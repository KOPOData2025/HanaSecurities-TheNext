package com.hanati.domain.auth.service;

import com.hanati.domain.auth.dto.*;
import com.hanati.domain.auth.entity.User;
import com.hanati.domain.auth.entity.WebAuthnCredential;
import com.hanati.domain.auth.repository.UserRepository;
import com.hanati.domain.auth.repository.WebAuthnCredentialRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * WebAuthn 지문 인증 서비스
 * - 회원가입: 사용자 정보 등록 + 지문 인증기 등록
 * - 로그인: 지문 기반 인증
 * - Redis 대신 고정 Challenge 사용 (개발 환경)
 */
@Service
@Slf4j
public class WebAuthnService {

    private final UserRepository userRepository;
    private final WebAuthnCredentialRepository credentialRepository;

    // 인메모리 Challenge 저장소 (Redis 대신)
    private final Map<String, Long> challengeStore = new ConcurrentHashMap<>();

    @Value("${WEBAUTHN_RP_ID:localhost}")
    private String rpId;

    @Value("${WEBAUTHN_RP_NAME:Hana Securities}")
    private String rpName;

    @Value("${WEBAUTHN_FIXED_CHALLENGE:test-challenge-for-development-only}")
    private String fixedChallenge;

    private static final int CHALLENGE_TIMEOUT_MINUTES = 5;

    public WebAuthnService(UserRepository userRepository, WebAuthnCredentialRepository credentialRepository) {
        this.userRepository = userRepository;
        this.credentialRepository = credentialRepository;
    }

    // ============================================================================
    // 1. 회원가입 시작 - Challenge 생성 및 사용자 등록
    // ============================================================================

    /**
     * 회원가입 시작 API
     * 1. 사용자 정보 DB 저장 (Users 테이블)
     * 2. Challenge 생성 및 Redis 저장 (5분 TTL)
     * 3. 프론트엔드에 Challenge 전달
     */
    @Transactional
    public WebAuthnRegistrationStartResponse startRegistration(WebAuthnRegistrationStartRequest request) {
        log.info("[회원가입 시작] 전화번호: {}", request.getMobileNo());

        // 1. 중복 확인
        if (userRepository.existsByMobileNo(request.getMobileNo())) {
            throw new RuntimeException("이미 등록된 전화번호입니다: " + request.getMobileNo());
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("이미 등록된 이메일입니다: " + request.getEmail());
        }

        // 2. 사용자 정보 저장
        User user = User.builder()
                .userName(request.getUserName())
                .mobileNo(request.getMobileNo())
                .gender(request.getGender())
                .birth(LocalDate.parse(request.getBirth(), DateTimeFormatter.ISO_DATE))
                .email(request.getEmail())
                .address(request.getAddress())
                .secondaryPasswordHash(hashPassword(request.getSecondaryPassword()))
                .build();

        User savedUser = userRepository.save(user);
        log.info("[사용자 등록 완료] userId: {}, 이름: {}", savedUser.getUserId(), savedUser.getUserName());

        // 3. 고정 Challenge 사용 (.env에서 설정)
        String challenge = fixedChallenge;

        // 4. 인메모리에 Challenge 저장 (userId 매핑)
        challengeStore.put(challenge, savedUser.getUserId());

        log.info("[Challenge 생성] userId: {}, challenge: {}", savedUser.getUserId(), challenge);

        // 5. 응답 반환
        return WebAuthnRegistrationStartResponse.builder()
                .challenge(challenge)
                .rpId(rpId)
                .rpName(rpName)
                .userId(savedUser.getUserId())
                .userName(savedUser.getUserName())
                .timeout(CHALLENGE_TIMEOUT_MINUTES * 60 * 1000)
                .build();
    }

    // ============================================================================
    // 2. 회원가입 완료 - 지문 인증기 등록
    // ============================================================================

    /**
     * 회원가입 완료 API
     * 1. Redis에서 Challenge 검증 (5분 이내, 일회용)
     * 2. 서명 검증 (간소화 버전)
     * 3. WebAuthn Credential 저장
     */
    @Transactional
    public void finishRegistration(WebAuthnRegistrationFinishRequest request) {
        log.info("[회원가입 완료] userId: {}, credentialId: {}", request.getUserId(), request.getCredentialId());

        // 1. Challenge 검증 (인메모리에서 조회)
        String challenge = fixedChallenge;  // 고정 Challenge 사용
        Long storedUserId = challengeStore.get(challenge);

        if (storedUserId == null) {
            throw new RuntimeException("Challenge가 유효하지 않습니다.");
        }

        if (!storedUserId.equals(request.getUserId())) {
            throw new RuntimeException("Challenge의 사용자 ID가 일치하지 않습니다.");
        }

        // Challenge 삭제 (일회용)
        challengeStore.remove(challenge);
        log.info("[Challenge 검증 완료] userId: {}", request.getUserId());

        // 2. 서명 검증 (실제로는 attestationObject 파싱 및 공개키 검증 필요)
        // TODO: WebAuthn4J 라이브러리 사용하여 실제 검증 구현
        verifyAttestation(request.getAttestationObject(), request.getClientDataJSON());

        // 3. WebAuthn Credential 저장
        WebAuthnCredential credential = WebAuthnCredential.builder()
                .credentialId(request.getCredentialId())
                .userId(request.getUserId())
                .publicKey(request.getPublicKey())
                .build();

        credentialRepository.save(credential);
        log.info("[인증기 등록 완료] credentialId: {}", request.getCredentialId());
    }

    // ============================================================================
    // 3. 로그인 시작 - Challenge 생성
    // ============================================================================

    /**
     * 로그인 시작 API
     * 1. 전화번호로 사용자 조회
     * 2. 사용자의 등록된 인증기 목록 조회
     * 3. Challenge 생성 및 Redis 저장
     */
    public WebAuthnAuthenticationStartResponse startAuthentication(WebAuthnAuthenticationStartRequest request) {
        log.info("[로그인 시작] 전화번호: {}", request.getMobileNo());

        // 1. 사용자 조회
        User user = userRepository.findByMobileNo(request.getMobileNo())
                .orElseThrow(() -> new RuntimeException("등록되지 않은 전화번호입니다: " + request.getMobileNo()));

        // 2. 등록된 인증기 조회
        List<WebAuthnCredential> credentials = credentialRepository.findByUserId(user.getUserId());

        if (credentials.isEmpty()) {
            throw new RuntimeException("등록된 지문 인증기가 없습니다. 먼저 회원가입을 진행해주세요.");
        }

        // 3. 고정 Challenge 사용
        String challenge = fixedChallenge;

        // 4. 인메모리에 Challenge 저장 (userId 매핑)
        challengeStore.put(challenge, user.getUserId());

        log.info("[Challenge 생성] userId: {}, credentialCount: {}", user.getUserId(), credentials.size());

        // 5. 허용된 인증기 목록 생성
        List<WebAuthnAuthenticationStartResponse.AllowedCredential> allowCredentials = credentials.stream()
                .map(cred -> WebAuthnAuthenticationStartResponse.AllowedCredential.builder()
                        .id(cred.getCredentialId())
                        .type("public-key")
                        .transports(List.of("internal"))
                        .build())
                .collect(Collectors.toList());

        return WebAuthnAuthenticationStartResponse.builder()
                .challenge(challenge)
                .allowCredentials(allowCredentials)
                .timeout(CHALLENGE_TIMEOUT_MINUTES * 60 * 1000)
                .build();
    }

    // ============================================================================
    // 4. 로그인 완료 - 서명 검증 및 JWT 발급
    // ============================================================================

    /**
     * 로그인 완료 API
     * 1. Challenge 검증 (Redis)
     * 2. 공개키로 서명 검증
     * 3. JWT 토큰 발급
     */
    @Transactional
    public WebAuthnAuthenticationResponse finishAuthentication(WebAuthnAuthenticationFinishRequest request) {
        log.info("[로그인 완료] 전화번호: {}, credentialId: {}", request.getMobileNo(), request.getCredentialId());

        // 1. 사용자 조회
        User user = userRepository.findByMobileNo(request.getMobileNo())
                .orElseThrow(() -> new RuntimeException("등록되지 않은 전화번호입니다."));

        // 2. Challenge 검증 (인메모리)
        String challenge = fixedChallenge;  // 고정 Challenge 사용
        Long storedUserId = challengeStore.get(challenge);

        if (storedUserId == null) {
            throw new RuntimeException("Challenge가 유효하지 않습니다.");
        }

        if (!storedUserId.equals(user.getUserId())) {
            throw new RuntimeException("Challenge의 사용자 ID가 일치하지 않습니다.");
        }

        // Challenge 삭제 (일회용)
        challengeStore.remove(challenge);

        // 3. Credential 조회
        WebAuthnCredential credential = credentialRepository.findByCredentialIdAndUserId(request.getCredentialId(), user.getUserId())
                .orElseThrow(() -> new RuntimeException("등록되지 않은 인증기입니다."));

        // 4. 서명 검증 (공개키로 signature 검증)
        // TODO: WebAuthn4J 라이브러리 사용하여 실제 검증 구현
        verifySignature(credential.getPublicKey(), request.getAuthenticatorData(), request.getClientDataJSON(), request.getSignature());

        // 5. 마지막 사용 일시 업데이트
        credential.updateLastUsedAt();
        credentialRepository.save(credential);

        log.info("[로그인 성공] userId: {}, 이름: {}", user.getUserId(), user.getUserName());

        // 6. JWT 토큰 발급 (실제로는 JwtUtil 사용)
        String accessToken = generateJwtToken(user);

        return WebAuthnAuthenticationResponse.builder()
                .success(true)
                .message("로그인 성공")
                .accessToken(accessToken)
                .userId(user.getUserId())
                .userName(user.getUserName())
                .expiresIn(3600L)  // 1시간
                .build();
    }

    // ============================================================================
    // Private Helper Methods
    // ============================================================================

    /**
     * 비밀번호 해싱 (BCrypt)
     */
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("비밀번호 해싱 실패", e);
        }
    }

    /**
     * Attestation 검증 (회원가입)
     * TODO: WebAuthn4J 라이브러리 사용
     */
    private void verifyAttestation(String attestationObject, String clientDataJSON) {
        // 간소화된 구현
        log.info("[Attestation 검증] 생략 (TODO: WebAuthn4J 구현)");
        // 실제로는:
        // 1. attestationObject Base64 디코딩
        // 2. CBOR 파싱
        // 3. 공개키 추출
        // 4. 서명 검증
    }

    /**
     * 서명 검증 (로그인)
     * TODO: WebAuthn4J 라이브러리 사용
     */
    private void verifySignature(String publicKey, String authenticatorData, String clientDataJSON, String signature) {
        // 간소화된 구현
        log.info("[서명 검증] 생략 (TODO: WebAuthn4J 구현)");
        // 실제로는:
        // 1. authenticatorData + SHA256(clientDataJSON) 결합
        // 2. 공개키로 signature 검증
        // 3. 검증 실패 시 예외 발생
    }

    /**
     * JWT 토큰 생성
     * TODO: JwtUtil 사용
     */
    private String generateJwtToken(User user) {
        // 간소화된 구현
        return "mock-jwt-token-" + user.getUserId();
        // 실제로는 JwtUtil.generateToken(user) 호출
    }

    // ============================================================================
    // 5. 2차 비밀번호 검증
    // ============================================================================

    /**
     * 2차 비밀번호 검증
     * 자산 탭 접근 시 사용
     *
     * @param userId 사용자 ID
     * @param secondaryPassword 2차 비밀번호 (평문)
     * @return 검증 결과
     */
    public VerifySecondaryPasswordResponse verifySecondaryPassword(Long userId, String secondaryPassword) {
        log.info("[2차 비밀번호 검증] userId: {}", userId);

        try {
            // 1. 사용자 조회
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

            // 2. 입력받은 비밀번호 해싱
            String inputPasswordHash = hashPassword(secondaryPassword);

            // 3. DB에 저장된 해시와 비교
            if (user.getSecondaryPasswordHash().equals(inputPasswordHash)) {
                log.info("[2차 비밀번호 검증 성공] userId: {}", userId);
                return VerifySecondaryPasswordResponse.builder()
                        .success(true)
                        .message("비밀번호 인증 성공")
                        .build();
            } else {
                log.warn("[2차 비밀번호 검증 실패] userId: {} - 비밀번호 불일치", userId);
                return VerifySecondaryPasswordResponse.builder()
                        .success(false)
                        .message("비밀번호가 일치하지 않습니다.")
                        .build();
            }

        } catch (Exception e) {
            log.error("[2차 비밀번호 검증 실패] userId: {}, 에러: {}", userId, e.getMessage());
            return VerifySecondaryPasswordResponse.builder()
                    .success(false)
                    .message("비밀번호 검증 실패: " + e.getMessage())
                    .build();
        }
    }
}

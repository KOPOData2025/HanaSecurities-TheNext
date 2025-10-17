package com.hanati.domain.auth.controller;

import com.hanati.domain.auth.dto.*;
import com.hanati.domain.auth.service.WebAuthnService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * WebAuthn 지문 인증 REST API Controller
 * - 회원가입: 사용자 정보 + 지문 등록
 * - 로그인: 지문 기반 인증
 */
@RestController
@RequestMapping("/api/v1/auth/webauthn")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
@Tag(name = "WebAuthn Authentication API", description = "지문 기반 회원가입/로그인 API")
public class WebAuthnController {

    private final WebAuthnService webAuthnService;

    // ============================================================================
    // 1. 회원가입 시작 (사용자 정보 등록 + Challenge 발급)
    // ============================================================================

    @Operation(
            summary = "회원가입 시작 - 사용자 정보 등록",
            description = "사용자 기본 정보를 등록하고 지문 등록을 위한 Challenge를 발급합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "등록 성공, Challenge 발급",
                    content = @Content(schema = @Schema(implementation = WebAuthnRegistrationStartResponse.class))),
            @ApiResponse(responseCode = "400", description = "중복된 전화번호 또는 이메일"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/register/start")
    public ResponseEntity<WebAuthnRegistrationStartResponse> startRegistration(
            @RequestBody WebAuthnRegistrationStartRequest request) {

        log.info("[API 호출] 회원가입 시작 - 전화번호: {}", request.getMobileNo());

        WebAuthnRegistrationStartResponse response = webAuthnService.startRegistration(request);

        return ResponseEntity.ok(response);
    }

    // ============================================================================
    // 2. 회원가입 완료 (지문 인증기 등록)
    // ============================================================================

    @Operation(
            summary = "회원가입 완료 - 지문 인증기 등록",
            description = "사용자의 지문 인증기(Credential)를 등록합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "등록 완료"),
            @ApiResponse(responseCode = "400", description = "Challenge 만료 또는 검증 실패"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/register/finish")
    public ResponseEntity<String> finishRegistration(
            @RequestBody WebAuthnRegistrationFinishRequest request) {

        log.info("[API 호출] 회원가입 완료 - userId: {}", request.getUserId());

        webAuthnService.finishRegistration(request);

        return ResponseEntity.ok("회원가입이 완료되었습니다.");
    }

    // ============================================================================
    // 3. 로그인 시작 (Challenge 발급)
    // ============================================================================

    @Operation(
            summary = "로그인 시작 - Challenge 발급",
            description = "등록된 지문으로 로그인하기 위한 Challenge를 발급합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Challenge 발급 성공",
                    content = @Content(schema = @Schema(implementation = WebAuthnAuthenticationStartResponse.class))),
            @ApiResponse(responseCode = "404", description = "등록되지 않은 전화번호"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/login/start")
    public ResponseEntity<WebAuthnAuthenticationStartResponse> startAuthentication(
            @RequestBody WebAuthnAuthenticationStartRequest request) {

        log.info("[API 호출] 로그인 시작 - 전화번호: {}", request.getMobileNo());

        WebAuthnAuthenticationStartResponse response = webAuthnService.startAuthentication(request);

        return ResponseEntity.ok(response);
    }

    // ============================================================================
    // 4. 로그인 완료 (서명 검증 및 JWT 발급)
    // ============================================================================

    @Operation(
            summary = "로그인 완료 - 지문 인증 및 JWT 발급",
            description = "지문으로 서명을 검증하고 JWT 액세스 토큰을 발급합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공, JWT 토큰 발급",
                    content = @Content(schema = @Schema(implementation = WebAuthnAuthenticationResponse.class))),
            @ApiResponse(responseCode = "400", description = "Challenge 만료 또는 서명 검증 실패"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/login/finish")
    public ResponseEntity<WebAuthnAuthenticationResponse> finishAuthentication(
            @RequestBody WebAuthnAuthenticationFinishRequest request) {

        log.info("[API 호출] 로그인 완료 - 전화번호: {}", request.getMobileNo());

        WebAuthnAuthenticationResponse response = webAuthnService.finishAuthentication(request);

        return ResponseEntity.ok(response);
    }

    // ============================================================================
    // 5. 2차 비밀번호 검증 (자산 탭 접근)
    // ============================================================================

    @Operation(
            summary = "2차 비밀번호 검증",
            description = "자산 탭 접근 시 2차 비밀번호를 검증합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "검증 완료",
                    content = @Content(schema = @Schema(implementation = VerifySecondaryPasswordResponse.class))),
            @ApiResponse(responseCode = "400", description = "검증 실패"),
            @ApiResponse(responseCode = "404", description = "사용자 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/verify-secondary-password")
    public ResponseEntity<VerifySecondaryPasswordResponse> verifySecondaryPassword(
            @RequestBody VerifySecondaryPasswordRequest request) {

        log.info("[API 호출] 2차 비밀번호 검증 - userId: {}", request.getUserId());

        VerifySecondaryPasswordResponse response = webAuthnService.verifySecondaryPassword(
                request.getUserId(),
                request.getSecondaryPassword()
        );

        return ResponseEntity.ok(response);
    }
}

package com.hanati.common.service;

import com.hanati.common.config.TokenConfig;
import com.hanati.common.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class KiwoomTokenService {

    private final TokenConfig tokenConfig;
    private final RestTemplate restTemplate = new RestTemplate();
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    // 토큰 저장소
    private KiwoomTokenResponse currentKiwoomToken;

    public KiwoomTokenResponse getKiwoomToken() {
        if (currentKiwoomToken == null || isTokenExpired()) {
            currentKiwoomToken = refreshKiwoomToken();
        }
        return currentKiwoomToken;
    }

    private boolean isTokenExpired() {
        if (currentKiwoomToken == null || currentKiwoomToken.getExpiresDt() == null) {
            return true;
        }
        try {
            LocalDateTime expireTime = LocalDateTime.parse(currentKiwoomToken.getExpiresDt(), formatter);
            return LocalDateTime.now().isAfter(expireTime);
        } catch (Exception e) {
            log.error("키움증권 토큰 만료 시간 파싱 실패: {}", e.getMessage());
            return true;
        }
    }

    public KiwoomTokenResponse refreshKiwoomToken() {
        log.info("키움증권 토큰 갱신 시작");
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // 키움증권 토큰 요청 생성
            KiwoomTokenRequest requestDto = KiwoomTokenRequest.builder()
                .grantType("client_credentials")
                .appKey(tokenConfig.getKiwoomAppKey())
                .secretKey(tokenConfig.getKiwoomSecretKey())
                .build();

            HttpEntity<KiwoomTokenRequest> request = new HttpEntity<>(requestDto, headers);

            ResponseEntity<KiwoomTokenResponse> response = restTemplate.postForEntity(
                tokenConfig.getKiwoomTokenUrl(),
                request,
                KiwoomTokenResponse.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                KiwoomTokenResponse token = response.getBody();
                currentKiwoomToken = token;


                log.info("키움증권 토큰 갱신 성공");
                log.debug("토큰 타입: {}, 만료 시간: {}",
                    token.getTokenType(),
                    token.getExpiresDt());

                return token;
            }
        } catch (RestClientException e) {
            log.error("키움증권 토큰 갱신 실패: {}", e.getMessage());
            throw new RuntimeException("키움증권 토큰 갱신 실패", e);
        }
        throw new RuntimeException("키움증권 토큰 갱신 실패");
    }

    // 토큰 상태 확인 메서드
    public boolean isKiwoomTokenValid() {
        return currentKiwoomToken != null && !isTokenExpired();
    }

    // 토큰 값 가져오기 메서드
    public String getKiwoomAccessToken() {
        KiwoomTokenResponse token = getKiwoomToken();
        return token != null ? token.getToken() : null;
    }
}
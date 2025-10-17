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
public class TokenService {

    private final TokenConfig tokenConfig;
    private final RestTemplate restTemplate = new RestTemplate();
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // 토큰 저장소
    private RestApiTokenResponse currentRestApiToken;
    private WebSocketTokenResponse currentWebSocketToken;
    private LocalDateTime webSocketTokenExpiry;

    public RestApiTokenResponse getRestApiToken() {
        if (currentRestApiToken == null || isRestApiTokenExpired()) {
            currentRestApiToken = refreshRestApiToken();
        }
        return currentRestApiToken;
    }

    public WebSocketTokenResponse getWebSocketToken() {
        if (currentWebSocketToken == null || isWebSocketTokenExpired()) {
            currentWebSocketToken = refreshWebSocketToken();
        }
        return currentWebSocketToken;
    }

    private boolean isRestApiTokenExpired() {
        if (currentRestApiToken == null || currentRestApiToken.getAccessTokenTokenExpired() == null) {
            return true;
        }
        LocalDateTime expireTime = LocalDateTime.parse(currentRestApiToken.getAccessTokenTokenExpired(), formatter);
        return LocalDateTime.now().isAfter(expireTime);
    }

    private boolean isWebSocketTokenExpired() {
        if (webSocketTokenExpiry == null) {
            return true;
        }
        return LocalDateTime.now().isAfter(webSocketTokenExpiry);
    }

    public RestApiTokenResponse refreshRestApiToken() {
        log.info("REST API 토큰 갱신 시작");
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // REST API 토큰 요청 생성
            RestApiTokenRequest requestDto = RestApiTokenRequest.builder()
                .grantType("client_credentials")
                .appKey(tokenConfig.getAppKey())
                .appSecret(tokenConfig.getAppSecret())
                .build();

            HttpEntity<RestApiTokenRequest> request = new HttpEntity<>(requestDto, headers);

            ResponseEntity<RestApiTokenResponse> response = restTemplate.postForEntity(
                tokenConfig.getTokenUrl(),
                request,
                RestApiTokenResponse.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                RestApiTokenResponse token = response.getBody();
                currentRestApiToken = token;


                log.info("REST API 토큰 갱신 성공");
                log.debug("토큰 타입: {}, 만료 시간: {}",
                    token.getTokenType(),
                    token.getAccessTokenTokenExpired());

                return token;
            }
        } catch (RestClientException e) {
            log.error("REST API 토큰 갱신 실패: {}", e.getMessage());
            throw new RuntimeException("REST API 토큰 갱신 실패", e);
        }
        throw new RuntimeException("REST API 토큰 갱신 실패");
    }

    public WebSocketTokenResponse refreshWebSocketToken() {
        log.info("WebSocket 토큰 갱신 시작");
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // WebSocket 토큰 요청 생성
            WebSocketTokenRequest requestDto = WebSocketTokenRequest.builder()
                .grantType("client_credentials")
                .appKey(tokenConfig.getAppKey())
                .secretKey(tokenConfig.getAppSecret())  // WebSocket은 secretkey 사용
                .build();

            HttpEntity<WebSocketTokenRequest> request = new HttpEntity<>(requestDto, headers);

            ResponseEntity<WebSocketTokenResponse> response = restTemplate.postForEntity(
                tokenConfig.getWebSocketTokenUrl(),
                request,
                WebSocketTokenResponse.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                WebSocketTokenResponse token = response.getBody();
                currentWebSocketToken = token;
                webSocketTokenExpiry = LocalDateTime.now().plusHours(23); // WebSocket 토큰은 대략 23시간 유효

                log.info("WebSocket 토큰 갱신 성공");
                log.debug("승인 키: {}...", token.getApprovalKey().substring(0, Math.min(10, token.getApprovalKey().length())));

                return token;
            }
        } catch (RestClientException e) {
            log.error("WebSocket 토큰 갱신 실패: {}", e.getMessage());
            throw new RuntimeException("WebSocket 토큰 갱신 실패", e);
        }
        throw new RuntimeException("WebSocket 토큰 갱신 실패");
    }

    public void refreshAllTokens() {
        log.info("모든 토큰 갱신 시작");
        try {
            refreshRestApiToken();
            log.info("REST API 토큰 갱신 완료");
        } catch (Exception e) {
            log.error("REST API 토큰 갱신 중 오류: {}", e.getMessage());
        }

        try {
            refreshWebSocketToken();
            log.info("WebSocket 토큰 갱신 완료");
        } catch (Exception e) {
            log.error("WebSocket 토큰 갱신 중 오류: {}", e.getMessage());
        }

        log.info("모든 토큰 갱신 완료");
    }

    // 토큰 상태 확인 메서드
    public boolean isRestApiTokenValid() {
        return currentRestApiToken != null && !isRestApiTokenExpired();
    }

    public boolean isWebSocketTokenValid() {
        return currentWebSocketToken != null && !isWebSocketTokenExpired();
    }

    // 토큰 값 가져오기 메서드
    public String getRestApiAccessToken() {
        RestApiTokenResponse token = getRestApiToken();
        return token != null ? token.getAccessToken() : null;
    }

    public String getWebSocketApprovalKey() {
        WebSocketTokenResponse token = getWebSocketToken();
        return token != null ? token.getApprovalKey() : null;
    }
}
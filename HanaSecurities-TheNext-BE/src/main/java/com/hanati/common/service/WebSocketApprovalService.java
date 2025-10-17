package com.hanati.common.service;

import com.hanati.common.config.TokenConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebSocketApprovalService {

    private final TokenConfig tokenConfig;
    private final RestTemplate restTemplate;

    private final AtomicReference<String> approvalKey = new AtomicReference<>();

    /**
     * WebSocket 접속키 발급
     * REST API 토큰과 다른 별도의 WebSocket 전용 접속키
     */
    public String getWebSocketApprovalKey() {
        if (approvalKey.get() != null) {
            return approvalKey.get();
        }

        return issueApprovalKey();
    }

    /**
     * WebSocket 접속키 신규 발급
     */
    private synchronized String issueApprovalKey() {
        log.info("WebSocket 접속키 발급 시작");

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, String> body = new HashMap<>();
            body.put("grant_type", "client_credentials");
            body.put("appkey", tokenConfig.getAppKey());
            body.put("secretkey", tokenConfig.getAppSecret());

            HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    tokenConfig.getWebSocketTokenUrl(),
                    HttpMethod.POST,
                    request,
                    Map.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                String key = (String) response.getBody().get("approval_key");
                approvalKey.set(key);
                log.info("WebSocket 접속키 발급 성공");
                return key;
            }

            throw new RuntimeException("WebSocket 접속키 발급 실패");
        } catch (Exception e) {
            log.error("WebSocket 접속키 발급 실패", e);
            throw new RuntimeException("WebSocket 접속키 발급 실패", e);
        }
    }

    /**
     * 접속키 강제 재발급
     */
    public String renewApprovalKey() {
        approvalKey.set(null);
        return issueApprovalKey();
    }
}

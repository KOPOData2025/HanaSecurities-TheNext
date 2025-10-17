package com.hanati.common.scheduler;

import com.hanati.common.service.TokenService;
import com.hanati.common.service.KiwoomTokenService;
import com.hanati.common.service.WebSocketApprovalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
@Slf4j
public class TokenScheduler {

    private final TokenService tokenService;
    private final KiwoomTokenService kiwoomTokenService;
    private final WebSocketApprovalService webSocketApprovalService;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @PostConstruct
    public void initializeTokens() {
        log.info("애플리케이션 시작 시 토큰 초기화");
        try {
            tokenService.refreshAllTokens();
            log.info("한국투자증권 토큰 초기화 완료");
        } catch (Exception e) {
            log.error("한국투자증권 토큰 초기화 실패: {}", e.getMessage());
        }

        try {
            kiwoomTokenService.refreshKiwoomToken();
            log.info("키움증권 토큰 초기화 완료");
        } catch (Exception e) {
            log.error("키움증권 토큰 초기화 실패: {}", e.getMessage());
        }

        try {
            String approvalKey = webSocketApprovalService.getWebSocketApprovalKey();
            log.info("한국투자증권 WebSocket 승인키 초기화 완료: {}", approvalKey.substring(0, 20) + "...");
        } catch (Exception e) {
            log.error("한국투자증권 WebSocket 승인키 초기화 실패: {}", e.getMessage());
        }
    }

    @Scheduled(cron = "0 0 8 * * *", zone = "Asia/Seoul")
    public void refreshTokensDaily() {
        LocalDateTime now = LocalDateTime.now();
        log.info("========================================");
        log.info("토큰 갱신 스케줄러 실행 시작");
        log.info("실행 시간: {}", now.format(formatter));
        log.info("========================================");

        // 한국투자증권 토큰 갱신
        try {
            tokenService.refreshRestApiToken();
            log.info("한국투자증권 REST API 토큰 갱신 완료");

            tokenService.refreshWebSocketToken();
            log.info("한국투자증권 WebSocket 토큰 갱신 완료");
        } catch (Exception e) {
            log.error("한국투자증권 토큰 갱신 실패: {}", e.getMessage(), e);
        }

        // 키움증권 토큰 갱신
        try {
            kiwoomTokenService.refreshKiwoomToken();
            log.info("키움증권 토큰 갱신 완료");
        } catch (Exception e) {
            log.error("키움증권 토큰 갱신 실패: {}", e.getMessage(), e);
        }

        log.info("========================================");
        log.info("모든 토큰 갱신 스케줄러 실행 완료");
        log.info("다음 실행 예정: 익일 오전 8시 (KST)");
        log.info("========================================");
    }

    @Scheduled(cron = "0 30 7 * * *", zone = "Asia/Seoul")
    public void preRefreshNotification() {
        log.info("30분 후 토큰 갱신 예정입니다. (오전 8시)");
    }

    @Scheduled(fixedDelay = 3600000)
    public void tokenHealthCheck() {
        try {
            // 한국투자증권 토큰 체크
            boolean restTokenValid = tokenService.isRestApiTokenValid();
            boolean wsTokenValid = tokenService.isWebSocketTokenValid();

            if (restTokenValid && wsTokenValid) {
                log.debug("한국투자증권 토큰 상태 정상 - REST: OK, WebSocket: OK");
            } else {
                log.warn("한국투자증권 토큰 상태 이상 감지 - REST: {}, WebSocket: {}",
                    restTokenValid ? "OK" : "EXPIRED",
                    wsTokenValid ? "OK" : "EXPIRED");

                if (!restTokenValid) {
                    tokenService.refreshRestApiToken();
                }
                if (!wsTokenValid) {
                    tokenService.refreshWebSocketToken();
                }
            }

            // 키움증권 토큰 체크
            boolean kiwoomTokenValid = kiwoomTokenService.isKiwoomTokenValid();
            if (kiwoomTokenValid) {
                log.debug("키움증권 토큰 상태 정상");
            } else {
                log.warn("키움증권 토큰 상태 이상 감지 - EXPIRED");
                kiwoomTokenService.refreshKiwoomToken();
            }
        } catch (Exception e) {
            log.error("토큰 헬스체크 실패: {}", e.getMessage());
        }
    }
}
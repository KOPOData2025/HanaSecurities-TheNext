package com.hanati.domain.gold.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanati.domain.gold.dto.GoldQuoteData;
import com.hanati.domain.gold.service.GoldQuoteCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.Set;
import java.util.concurrent.*;

/**
 * 금현물 실시간 호가 데이터 브로드캐스트 WebSocket 핸들러
 *
 * 엔드포인트: ws://localhost:8080/ws/gold-quote
 * 기능: 키움증권에서 수신한 실시간 호가 데이터(5단 호가)를 연결된 모든 클라이언트에게 브로드캐스트
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GoldQuoteWebSocketHandler extends TextWebSocketHandler {

    private final GoldQuoteCacheService cacheService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // 연결된 클라이언트 세션 관리 (스레드 안전)
    private final Set<WebSocketSession> sessions = ConcurrentHashMap.newKeySet();

    // 브로드캐스트 스케줄러
    private ScheduledExecutorService broadcastScheduler;
    private static final long BROADCAST_INTERVAL_MS = 200; // 200ms 간격

    // 금현물 상품 코드
    private static final String GOLD_1KG = "M04020000";
    private static final String GOLD_100G = "M04020100";

    @PostConstruct
    public void init() {
        startBroadcastScheduler();
        log.info("[금현물 호가 WebSocket] 브로드캐스트 핸들러 초기화 완료");
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        log.info("[금현물 호가 WebSocket] 클라이언트 연결: {} (총 {}명)",
                session.getId(), sessions.size());

        // 연결 즉시 현재 캐시된 데이터 전송
        sendCachedData(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
        log.info("[금현물 호가 WebSocket] 클라이언트 연결 해제: {} (총 {}명)",
                session.getId(), sessions.size());
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        // Connection reset 등 일반적인 연결 종료는 WARN 레벨로 처리
        String errorMessage = exception.getMessage();
        if (errorMessage != null && (errorMessage.contains("Connection reset") ||
            errorMessage.contains("Broken pipe") ||
            errorMessage.contains("Connection closed"))) {
            log.warn("[금현물 호가 WebSocket] 연결 종료 - 세션: {}, 원인: {}",
                    session.getId(), errorMessage);
        } else {
            log.error("[금현물 호가 WebSocket] 전송 오류 - 세션: {}", session.getId(), exception);
        }
        sessions.remove(session);
    }

    /**
     * 브로드캐스트 스케줄러 시작
     */
    private void startBroadcastScheduler() {
        broadcastScheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread thread = new Thread(r, "gold-quote-broadcast");
            thread.setDaemon(true);
            return thread;
        });

        // 200ms마다 캐시된 데이터를 모든 클라이언트에게 브로드캐스트
        broadcastScheduler.scheduleAtFixedRate(() -> {
            try {
                if (!sessions.isEmpty()) {
                    broadcastCachedData();
                }
            } catch (Exception e) {
                log.error("[금현물 호가 WebSocket] 브로드캐스트 오류", e);
            }
        }, 0, BROADCAST_INTERVAL_MS, TimeUnit.MILLISECONDS);

        log.info("[금현물 호가 WebSocket] 브로드캐스트 스케줄러 시작 ({}ms 간격)", BROADCAST_INTERVAL_MS);
    }

    /**
     * 캐시된 데이터를 모든 클라이언트에게 브로드캐스트
     */
    private void broadcastCachedData() {
        // 금 1kg 호가 데이터
        GoldQuoteData gold1kg = cacheService.getQuoteData(GOLD_1KG);
        if (gold1kg != null) {
            broadcastToAll(gold1kg);
        }

        // 미니금 100g 호가 데이터
        GoldQuoteData gold100g = cacheService.getQuoteData(GOLD_100G);
        if (gold100g != null) {
            broadcastToAll(gold100g);
        }
    }

    /**
     * 특정 세션에 캐시된 데이터 전송 (연결 직후)
     */
    private void sendCachedData(WebSocketSession session) {
        try {
            // 금 1kg 호가 데이터
            GoldQuoteData gold1kg = cacheService.getQuoteData(GOLD_1KG);
            if (gold1kg != null) {
                sendToSession(session, gold1kg);
            }

            // 미니금 100g 호가 데이터
            GoldQuoteData gold100g = cacheService.getQuoteData(GOLD_100G);
            if (gold100g != null) {
                sendToSession(session, gold100g);
            }
        } catch (Exception e) {
            log.error("[금현물 호가 WebSocket] 캐시 데이터 전송 실패 - 세션: {}", session.getId(), e);
        }
    }

    /**
     * 모든 세션에 데이터 브로드캐스트
     */
    private void broadcastToAll(GoldQuoteData data) {
        String message = toJson(data);
        if (message == null) {
            return;
        }

        Set<WebSocketSession> closedSessions = new CopyOnWriteArraySet<>();

        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                try {
                    synchronized (session) {
                        session.sendMessage(new TextMessage(message));
                    }
                } catch (Exception e) {
                    log.error("[금현물 호가 WebSocket] 메시지 전송 실패 - 세션: {}", session.getId(), e);
                    closedSessions.add(session);
                }
            } else {
                closedSessions.add(session);
            }
        }

        // 닫힌 세션 정리
        sessions.removeAll(closedSessions);
    }

    /**
     * 특정 세션에 데이터 전송
     */
    private void sendToSession(WebSocketSession session, GoldQuoteData data) {
        String message = toJson(data);
        if (message == null) {
            return;
        }

        try {
            if (session.isOpen()) {
                synchronized (session) {
                    session.sendMessage(new TextMessage(message));
                }
            }
        } catch (Exception e) {
            log.error("[금현물 호가 WebSocket] 메시지 전송 실패 - 세션: {}", session.getId(), e);
        }
    }

    /**
     * 객체를 JSON 문자열로 변환
     */
    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            log.error("[금현물 호가 WebSocket] JSON 변환 실패", e);
            return null;
        }
    }

    @PreDestroy
    public void destroy() {
        // 스케줄러 중지
        if (broadcastScheduler != null && !broadcastScheduler.isShutdown()) {
            broadcastScheduler.shutdown();
            try {
                if (!broadcastScheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                    broadcastScheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                broadcastScheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }

        // 모든 세션 종료
        for (WebSocketSession session : sessions) {
            try {
                if (session.isOpen()) {
                    session.close();
                }
            } catch (Exception e) {
                log.error("[금현물 호가 WebSocket] 세션 종료 실패", e);
            }
        }
        sessions.clear();

        log.info("[금현물 호가 WebSocket] 브로드캐스트 핸들러 종료");
    }
}

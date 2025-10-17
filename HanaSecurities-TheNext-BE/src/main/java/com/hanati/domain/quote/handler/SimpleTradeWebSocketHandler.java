package com.hanati.domain.quote.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanati.domain.quote.dto.RealtimeTradeData;
import com.hanati.domain.quote.service.KisWebSocketClient;
import com.hanati.domain.quote.service.TradeCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
@Slf4j
public class SimpleTradeWebSocketHandler extends TextWebSocketHandler {

    private final KisWebSocketClient kisWebSocketClient;
    private final TradeCacheService cacheService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // 세션별 구독 종목 관리
    private final Map<String, Set<String>> sessionSubscriptions = new ConcurrentHashMap<>();
    // 세션 관리
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        log.info("Trade WebSocket 연결 성공: {}", session.getId());
        sessions.put(session.getId(), session);
        sessionSubscriptions.put(session.getId(), ConcurrentHashMap.newKeySet());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        log.info("수신 메시지: {}", payload);

        try {
            Map<String, String> request = objectMapper.readValue(payload, Map.class);
            String action = request.get("action");
            String stockCode = request.get("stockCode");

            if ("subscribe".equals(action)) {
                handleSubscribe(session, stockCode);
            } else if ("unsubscribe".equals(action)) {
                handleUnsubscribe(session, stockCode);
            } else {
                sendError(session, "Unknown action: " + action);
            }

        } catch (Exception e) {
            log.error("메시지 처리 실패", e);
            sendError(session, "Invalid message format");
        }
    }

    private void handleSubscribe(WebSocketSession session, String stockCode) throws Exception {
        log.info("체결가 구독 요청 - 세션: {}, 종목: {}", session.getId(), stockCode);

        // KIS WebSocket에 체결가 구독
        kisWebSocketClient.subscribeTrade(stockCode);

        // 세션별 구독 종목 추가
        sessionSubscriptions.get(session.getId()).add(stockCode);

        // 확인 메시지 전송
        Map<String, String> response = Map.of(
                "type", "subscribe",
                "status", "success",
                "stockCode", stockCode,
                "message", "체결가 구독 완료"
        );
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(response)));
    }

    private void handleUnsubscribe(WebSocketSession session, String stockCode) throws Exception {
        log.info("체결가 구독 해제 - 세션: {}, 종목: {}", session.getId(), stockCode);

        // 세션별 구독 종목 제거
        Set<String> subscriptions = sessionSubscriptions.get(session.getId());
        if (subscriptions != null) {
            subscriptions.remove(stockCode);

            // 해당 종목을 구독하는 세션이 없으면 KIS 구독 해제
            if (sessionSubscriptions.values().stream().noneMatch(set -> set.contains(stockCode))) {
                kisWebSocketClient.unsubscribeTrade(stockCode);
            }
        }

        // 확인 메시지 전송
        Map<String, String> response = Map.of(
                "type", "unsubscribe",
                "status", "success",
                "stockCode", stockCode,
                "message", "체결가 구독 해제 완료"
        );
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(response)));
    }

    private void sendError(WebSocketSession session, String errorMessage) throws Exception {
        Map<String, String> error = Map.of(
                "type", "error",
                "message", errorMessage
        );
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(error)));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        log.info("Trade WebSocket 연결 종료: {}, 상태: {}", session.getId(), status);

        // 세션 제거
        sessions.remove(session.getId());

        // 해당 세션의 모든 구독 종목 정리
        Set<String> subscriptions = sessionSubscriptions.remove(session.getId());
        if (subscriptions != null) {
            for (String stockCode : subscriptions) {
                // 해당 종목을 구독하는 세션이 없으면 KIS 구독 해제
                if (sessionSubscriptions.values().stream().noneMatch(set -> set.contains(stockCode))) {
                    kisWebSocketClient.unsubscribeTrade(stockCode);
                }
            }
        }
    }

    /**
     * 200ms마다 캐시된 체결가 데이터를 구독 중인 클라이언트에게 브로드캐스트
     */
    @Scheduled(fixedRate = 200)
    public void broadcastTrades() {
        sessionSubscriptions.forEach((sessionId, stockCodes) -> {
            stockCodes.forEach(stockCode -> {
                RealtimeTradeData trade = cacheService.getTrade(stockCode);
                if (trade != null) {
                    try {
                        WebSocketSession session = findSessionById(sessionId);
                        if (session != null && session.isOpen()) {
                            Map<String, Object> message = Map.of(
                                    "type", "trade",
                                    "data", trade
                            );
                            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(message)));
                        }
                    } catch (Exception e) {
                        log.error("체결가 브로드캐스트 실패: {}", stockCode, e);
                    }
                }
            });
        });
    }

    private WebSocketSession findSessionById(String sessionId) {
        return sessions.get(sessionId);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        log.error("Trade WebSocket 전송 오류: {}", session.getId(), exception);
    }
}

package com.hanati.domain.foreignquote.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanati.domain.foreignquote.dto.ForeignQuoteData;
import com.hanati.domain.foreignquote.service.ForeignQuoteCacheService;
import com.hanati.domain.foreignquote.service.ForeignKisWebSocketClient;
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

/**
 * 해외주식 실시간 WebSocket Handler
 * - 통합 클라이언트로 모든 해외주식 데이터 처리
 * - 체결가: HDFSCNT0
 * - 호가(미국): HDFSASP0
 * - 호가(아시아): HDFSASP1
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ForeignQuoteWebSocketHandler extends TextWebSocketHandler {

    private final ForeignKisWebSocketClient kisClient;
    private final ForeignQuoteCacheService cacheService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // 세션별 구독 종목 관리 (key: sessionId, value: Set<exchangeCode:stockCode>)
    private final Map<String, Set<String>> sessionSubscriptions = new ConcurrentHashMap<>();
    // 세션 관리
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        log.info("[해외주식 WebSocket] 연결 성공: {}", session.getId());
        sessions.put(session.getId(), session);
        sessionSubscriptions.put(session.getId(), ConcurrentHashMap.newKeySet());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        log.info("[해외주식 WebSocket] 수신 메시지: {}", payload);

        try {
            @SuppressWarnings("unchecked")
            Map<String, String> request = objectMapper.readValue(payload, Map.class);
            String action = request.get("action");
            String exchangeCode = request.get("exchangeCode");
            String stockCode = request.get("stockCode");
            String dataType = request.getOrDefault("dataType", "trade");  // trade 또는 quote

            if ("subscribe".equals(action)) {
                handleSubscribe(session, exchangeCode, stockCode, dataType);
            } else if ("unsubscribe".equals(action)) {
                handleUnsubscribe(session, exchangeCode, stockCode, dataType);
            } else {
                sendError(session, "Unknown action: " + action);
            }

        } catch (Exception e) {
            log.error("[해외주식 WebSocket] 메시지 처리 실패", e);
            sendError(session, "Invalid message format");
        }
    }

    /**
     * 구독 처리
     * @param dataType "trade" 또는 "quote"
     */
    private void handleSubscribe(WebSocketSession session, String exchangeCode, String stockCode, String dataType) throws Exception {
        String key = exchangeCode + ":" + stockCode + ":" + dataType;
        log.info("[해외주식 구독] 세션: {}, 거래소: {}, 종목: {}, 타입: {}",
                session.getId(), exchangeCode, stockCode, dataType);

        if ("trade".equals(dataType)) {
            // 체결가 구독 (HDFSCNT0)
            kisClient.subscribeTrade(exchangeCode, stockCode);
        } else if ("quote".equals(dataType)) {
            // 호가 구독 (HDFSASP0 또는 HDFSASP1 - 자동 판별)
            kisClient.subscribeQuote(exchangeCode, stockCode);
        } else {
            sendError(session, "Invalid dataType: " + dataType);
            return;
        }

        // 세션별 구독 종목 추가
        sessionSubscriptions.get(session.getId()).add(key);

        // 확인 메시지 전송
        Map<String, String> response = Map.of(
                "type", "subscribe",
                "status", "success",
                "exchangeCode", exchangeCode,
                "stockCode", stockCode,
                "dataType", dataType,
                "message", "해외주식 " + (dataType.equals("trade") ? "체결가" : "호가") + " 구독 완료"
        );
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(response)));
    }

    /**
     * 구독 해제 처리
     */
    private void handleUnsubscribe(WebSocketSession session, String exchangeCode, String stockCode, String dataType) throws Exception {
        String key = exchangeCode + ":" + stockCode + ":" + dataType;
        log.info("[해외주식 구독 해제] 세션: {}, 거래소: {}, 종목: {}, 타입: {}",
                session.getId(), exchangeCode, stockCode, dataType);

        // 세션별 구독 종목 제거
        Set<String> subscriptions = sessionSubscriptions.get(session.getId());
        if (subscriptions != null) {
            subscriptions.remove(key);

            // 해당 종목을 구독하는 세션이 없으면 KIS 구독 해제
            if (sessionSubscriptions.values().stream().noneMatch(set -> set.contains(key))) {
                String trId = "trade".equals(dataType) ? "HDFSCNT0" :
                             (isUsMarket(exchangeCode) ? "HDFSASP0" : "HDFSASP1");
                kisClient.unsubscribe(exchangeCode, stockCode, trId);
            }
        }

        // 확인 메시지 전송
        Map<String, String> response = Map.of(
                "type", "unsubscribe",
                "status", "success",
                "exchangeCode", exchangeCode,
                "stockCode", stockCode,
                "dataType", dataType,
                "message", "구독 해제 완료"
        );
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(response)));
    }

    /**
     * 미국 시장 여부 확인
     */
    private boolean isUsMarket(String exchangeCode) {
        return exchangeCode.matches("(?i)(NYS|NYSE|NAS|NASD|AMS|AMEX)");
    }

    /**
     * 아시아 시장 여부 확인
     */
    private boolean isAsiaMarket(String exchangeCode) {
        return exchangeCode.matches("(?i)(HKS|SEHK|TSE|TKSE|SHS|SZS|HSX|HNX)");
    }

    /**
     * 에러 메시지 전송
     */
    private void sendError(WebSocketSession session, String errorMessage) {
        try {
            Map<String, String> error = Map.of(
                    "type", "error",
                    "message", errorMessage
            );
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(error)));
        } catch (Exception e) {
            log.error("[해외주식 WebSocket] 에러 메시지 전송 실패", e);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        log.info("[해외주식 WebSocket] 연결 종료: {}, 상태: {}", session.getId(), status);

        // 세션 제거
        sessions.remove(session.getId());

        // 해당 세션의 모든 구독 종목 정리
        Set<String> subscriptions = sessionSubscriptions.remove(session.getId());
        if (subscriptions != null) {
            for (String key : subscriptions) {
                String[] parts = key.split(":");
                if (parts.length == 3) {
                    String exchangeCode = parts[0];
                    String stockCode = parts[1];
                    String dataType = parts[2];

                    // 해당 종목을 구독하는 세션이 없으면 KIS 구독 해제
                    if (sessionSubscriptions.values().stream().noneMatch(set -> set.contains(key))) {
                        String trId = "trade".equals(dataType) ? "HDFSCNT0" :
                                     (isUsMarket(exchangeCode) ? "HDFSASP0" : "HDFSASP1");
                        kisClient.unsubscribe(exchangeCode, stockCode, trId);
                    }
                }
            }
        }
    }

    /**
     * 200ms마다 캐시된 데이터를 구독 중인 클라이언트에게 브로드캐스트
     */
    @Scheduled(fixedRate = 200)
    public void broadcastQuotes() {
        sessionSubscriptions.forEach((sessionId, keys) -> {
            keys.forEach(key -> {
                String[] parts = key.split(":");
                if (parts.length >= 3) {
                    String exchangeCode = parts[0];  // 거래소코드
                    String stockCode = parts[1];     // 종목코드
                    String dataType = parts[2];      // "trade" 또는 "quote"

                    ForeignQuoteData quote = cacheService.getQuote(exchangeCode, stockCode);

                    if (quote != null) {
                        try {
                            WebSocketSession session = sessions.get(sessionId);
                            if (session != null && session.isOpen()) {
                                Map<String, Object> message = Map.of(
                                        "type", dataType,  // "trade" 또는 "quote"
                                        "data", quote
                                );
                                session.sendMessage(new TextMessage(objectMapper.writeValueAsString(message)));
                            }
                        } catch (Exception e) {
                            log.error("[해외주식 WebSocket] 브로드캐스트 실패: {}:{}", exchangeCode, stockCode, e);
                        }
                    }
                }
            });
        });
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        log.error("[해외주식 WebSocket] 전송 오류: {}", session.getId(), exception);
    }
}

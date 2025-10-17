package com.hanati.domain.gold.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanati.common.config.TokenConfig;
import com.hanati.common.service.KiwoomTokenService;
import com.hanati.domain.gold.dto.GoldQuoteData;
import com.hanati.domain.gold.dto.GoldTradeData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import jakarta.annotation.PreDestroy;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 금현물 키움증권 WebSocket 클라이언트
 *
 * 2단계 인증 프로토콜:
 * 1. LOGIN: 액세스 토큰으로 인증
 * 2. REG: 금현물 상품 구독 (M04020000, M04020100)
 *
 * 데이터 타입:
 * - type "00": 체결 데이터 (GoldTradeData)
 * - type "01": 호가 데이터 (GoldQuoteData)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GoldKiwoomWebSocketClient extends TextWebSocketHandler {

    private final KiwoomTokenService kiwoomTokenService;
    private final TokenConfig tokenConfig;
    private final GoldQuoteCacheService cacheService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private WebSocketSession session;
    private final Set<String> subscribedProducts = ConcurrentHashMap.newKeySet();

    // 리스너 리스트 (스레드 안전)
    private final java.util.List<TradeDataListener> tradeDataListeners = new java.util.concurrent.CopyOnWriteArrayList<>();
    private final java.util.List<QuoteDataListener> quoteDataListeners = new java.util.concurrent.CopyOnWriteArrayList<>();

    // 재연결 관련
    private final AtomicInteger reconnectAttempts = new AtomicInteger(0);
    private static final int MAX_RECONNECT_ATTEMPTS = 5;
    private static final long[] BACKOFF_DELAYS = {3000, 6000, 12000, 24000, 48000}; // 밀리초
    private volatile boolean circuitBreakerOpen = false;

    // Ping/Pong 모니터링
    private ScheduledExecutorService pingScheduler;
    private volatile long lastPongTime = System.currentTimeMillis();
    private static final long PING_INTERVAL_MS = 30000; // 30초
    private static final long PONG_TIMEOUT_MS = 10000; // 10초

    // 연결 상태
    private enum ConnectionState {
        DISCONNECTED,
        CONNECTING,
        CONNECTED,
        AUTHENTICATED,
        SUBSCRIBED,
        FAILED
    }

    private volatile ConnectionState connectionState = ConnectionState.DISCONNECTED;

    // 키움증권 WebSocket URL
    private static final String KIWOOM_WS_URL = "wss://api.kiwoom.com:10000/api/dostk/websocket";

    /**
     * WebSocket 연결 및 LOGIN
     */
    public synchronized void connect() {
        try {
            if (session != null && session.isOpen()) {
                log.info("[금현물 WebSocket] 이미 연결되어 있음");
                return;
            }

            // Circuit Breaker 체크
            if (circuitBreakerOpen) {
                log.error("[금현물 WebSocket] Circuit Breaker 활성화 - 연결 차단");
                throw new RuntimeException("Circuit Breaker가 활성화되어 연결할 수 없습니다");
            }

            connectionState = ConnectionState.CONNECTING;
            log.info("[금현물 WebSocket] 연결 시도: {} (재연결 시도: {})",
                    KIWOOM_WS_URL, reconnectAttempts.get());

            StandardWebSocketClient client = new StandardWebSocketClient();
            session = client.execute(this, KIWOOM_WS_URL).get();

            connectionState = ConnectionState.CONNECTED;
            log.info("[금현물 WebSocket] 연결 성공");

            // 연결 성공 시 재연결 카운터 리셋
            reconnectAttempts.set(0);
            circuitBreakerOpen = false;

            // 연결 성공 후 즉시 LOGIN 메시지 전송
            sendLoginMessage();

            // Ping/Pong 모니터링 시작
            startPingMonitoring();

        } catch (Exception e) {
            connectionState = ConnectionState.FAILED;
            log.error("[금현물 WebSocket] 연결 실패", e);
            throw new RuntimeException("금현물 WebSocket 연결 실패", e);
        }
    }

    /**
     * LOGIN 메시지 전송 (1단계 인증)
     */
    private void sendLoginMessage() {
        try {
            String accessToken = kiwoomTokenService.getKiwoomAccessToken();
            if (accessToken == null) {
                throw new RuntimeException("키움증권 액세스 토큰을 가져올 수 없습니다.");
            }

            Map<String, Object> loginMessage = new HashMap<>();
            loginMessage.put("trnm", "LOGIN");
            loginMessage.put("token", accessToken);

            String message = objectMapper.writeValueAsString(loginMessage);
            log.info("[금현물 WebSocket] LOGIN 메시지 전송: {}", message);

            synchronized (session) {
                session.sendMessage(new TextMessage(message));
            }

        } catch (Exception e) {
            connectionState = ConnectionState.FAILED;
            log.error("[금현물 WebSocket] LOGIN 메시지 전송 실패", e);
            throw new RuntimeException("LOGIN 실패", e);
        }
    }

    /**
     * REG 메시지 전송 (2단계 구독)
     * @param productCode 금현물 상품 코드 (M04020000, M04020100)
     * @param type 데이터 타입 ("00": 체결, "01": 호가)
     */
    public void subscribe(String productCode, String type) {
        String key = productCode + ":" + type;

        if (subscribedProducts.contains(key)) {
            log.info("[금현물 WebSocket] 이미 구독 중: {}", key);
            return;
        }

        try {
            ensureAuthenticated();

            Map<String, Object> regMessage = new HashMap<>();
            regMessage.put("trnm", "REG");
            regMessage.put("grp_no", "1");
            regMessage.put("refresh", "0");

            Map<String, Object[]> dataItem = new HashMap<>();
            dataItem.put("item", new String[]{productCode});
            dataItem.put("type", new String[]{type});

            regMessage.put("data", new Object[]{dataItem});

            String message = objectMapper.writeValueAsString(regMessage);
            log.info("[금현물 WebSocket] REG 메시지 전송: {}", message);

            synchronized (session) {
                session.sendMessage(new TextMessage(message));
            }

            subscribedProducts.add(key);
            connectionState = ConnectionState.SUBSCRIBED;
            log.info("[금현물 WebSocket] 구독 완료: {}", key);

        } catch (Exception e) {
            log.error("[금현물 WebSocket] 구독 실패: {}", key, e);
            throw new RuntimeException("구독 실패", e);
        }
    }

    /**
     * 금현물 체결 데이터 구독
     */
    public void subscribeTrade(String productCode) {
        subscribe(productCode, "00");
    }

    /**
     * 금현물 호가 데이터 구독
     */
    public void subscribeQuote(String productCode) {
        subscribe(productCode, "01");
    }

    /**
     * 인증 상태 확인
     */
    private void ensureAuthenticated() {
        if (session == null || !session.isOpen()) {
            log.warn("[금현물 WebSocket] 연결이 끊어짐. 재연결 시도...");
            connect();
        }

        if (connectionState != ConnectionState.AUTHENTICATED &&
            connectionState != ConnectionState.SUBSCRIBED) {
            throw new IllegalStateException("WebSocket이 인증되지 않았습니다. 현재 상태: " + connectionState);
        }
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        try {
            String payload = message.getPayload();
            log.debug("[금현물 WebSocket 수신] {}", payload);

            // JSON 응답 처리 (LOGIN 응답 등)
            if (payload.startsWith("{")) {
                handleJsonResponse(payload);
                return;
            }

            // 실시간 데이터 처리 (파이프 구분자)
            if (payload.contains("|")) {
                handleRealtimeData(payload);
                return;
            }

            log.warn("[금현물 WebSocket] 알 수 없는 데이터 형식: {}", payload);

        } catch (Exception e) {
            log.error("[금현물 WebSocket] 메시지 처리 실패", e);
        }
    }

    /**
     * 실시간 데이터 처리
     * 형식: type|productCode|data1|data2|...
     */
    private void handleRealtimeData(String payload) {
        try {
            String[] fields = payload.split("\\|");

            if (fields.length < 2) {
                log.warn("[금현물 WebSocket] 데이터 필드 부족: {}", payload);
                return;
            }

            String type = fields[0];
            String productCode = fields[1];

            if ("00".equals(type)) {
                // 체결 데이터
                GoldTradeData tradeData = parseTradeData(productCode, fields);
                log.info("[금현물 체결] {}", tradeData);

                // 캐시에 저장
                cacheService.cacheTradeData(tradeData);

                // 등록된 리스너들에게 통지
                for (TradeDataListener listener : tradeDataListeners) {
                    try {
                        listener.onTradeData(tradeData);
                    } catch (Exception e) {
                        log.error("[금현물 WebSocket] 체결 데이터 리스너 오류", e);
                    }
                }

            } else if ("01".equals(type)) {
                // 호가 데이터
                GoldQuoteData quoteData = parseQuoteData(productCode, fields);
                log.info("[금현물 호가] {}", quoteData);

                // 캐시에 저장
                cacheService.cacheQuoteData(quoteData);

                // 등록된 리스너들에게 통지
                for (QuoteDataListener listener : quoteDataListeners) {
                    try {
                        listener.onQuoteData(quoteData);
                    } catch (Exception e) {
                        log.error("[금현물 WebSocket] 호가 데이터 리스너 오류", e);
                    }
                }

            } else {
                log.warn("[금현물 WebSocket] 알 수 없는 데이터 타입: {}", type);
            }

        } catch (Exception e) {
            log.error("[금현물 WebSocket] 실시간 데이터 파싱 실패: {}", payload, e);
        }
    }

    /**
     * 체결 데이터 파싱 (type: "00")
     * 형식: 00|productCode|price|quantity|changeAmount|changeRate|volume|timestamp
     */
    private GoldTradeData parseTradeData(String productCode, String[] fields) {
        try {
            return GoldTradeData.builder()
                    .productCode(productCode)
                    .price(parseDouble(fields, 2))
                    .quantity(parseLong(fields, 3))
                    .changeAmount(parseDouble(fields, 4))
                    .changeRate(parseDouble(fields, 5))
                    .volume(parseLong(fields, 6))
                    .timestamp(parseTimestamp(fields, 7))
                    .build();
        } catch (Exception e) {
            log.error("[금현물 WebSocket] 체결 데이터 파싱 실패: {}", String.join("|", fields), e);
            throw e;
        }
    }

    /**
     * 호가 데이터 파싱 (type: "01")
     * 형식: 01|productCode|bidPrice1|bidQty1|bidPrice2|bidQty2|...|bidPrice10|bidQty10|askPrice1|askQty1|...|askPrice10|askQty10|timestamp
     */
    private GoldQuoteData parseQuoteData(String productCode, String[] fields) {
        try {
            return GoldQuoteData.builder()
                    .productCode(productCode)
                    // 매수 호가 (10단계)
                    .bidPrice1(parseDouble(fields, 2))
                    .bidQuantity1(parseLong(fields, 3))
                    .bidPrice2(parseDouble(fields, 4))
                    .bidQuantity2(parseLong(fields, 5))
                    .bidPrice3(parseDouble(fields, 6))
                    .bidQuantity3(parseLong(fields, 7))
                    .bidPrice4(parseDouble(fields, 8))
                    .bidQuantity4(parseLong(fields, 9))
                    .bidPrice5(parseDouble(fields, 10))
                    .bidQuantity5(parseLong(fields, 11))
                    .bidPrice6(parseDouble(fields, 12))
                    .bidQuantity6(parseLong(fields, 13))
                    .bidPrice7(parseDouble(fields, 14))
                    .bidQuantity7(parseLong(fields, 15))
                    .bidPrice8(parseDouble(fields, 16))
                    .bidQuantity8(parseLong(fields, 17))
                    .bidPrice9(parseDouble(fields, 18))
                    .bidQuantity9(parseLong(fields, 19))
                    .bidPrice10(parseDouble(fields, 20))
                    .bidQuantity10(parseLong(fields, 21))
                    // 매도 호가 (10단계)
                    .askPrice1(parseDouble(fields, 22))
                    .askQuantity1(parseLong(fields, 23))
                    .askPrice2(parseDouble(fields, 24))
                    .askQuantity2(parseLong(fields, 25))
                    .askPrice3(parseDouble(fields, 26))
                    .askQuantity3(parseLong(fields, 27))
                    .askPrice4(parseDouble(fields, 28))
                    .askQuantity4(parseLong(fields, 29))
                    .askPrice5(parseDouble(fields, 30))
                    .askQuantity5(parseLong(fields, 31))
                    .askPrice6(parseDouble(fields, 32))
                    .askQuantity6(parseLong(fields, 33))
                    .askPrice7(parseDouble(fields, 34))
                    .askQuantity7(parseLong(fields, 35))
                    .askPrice8(parseDouble(fields, 36))
                    .askQuantity8(parseLong(fields, 37))
                    .askPrice9(parseDouble(fields, 38))
                    .askQuantity9(parseLong(fields, 39))
                    .askPrice10(parseDouble(fields, 40))
                    .askQuantity10(parseLong(fields, 41))
                    .timestamp(parseTimestamp(fields, 42))
                    .build();
        } catch (Exception e) {
            log.error("[금현물 WebSocket] 호가 데이터 파싱 실패: {}", String.join("|", fields), e);
            throw e;
        }
    }

    /**
     * Double 파싱 헬퍼
     */
    private Double parseDouble(String[] fields, int index) {
        if (index >= fields.length || fields[index] == null || fields[index].trim().isEmpty()) {
            return null;
        }
        try {
            return Double.parseDouble(fields[index].trim());
        } catch (NumberFormatException e) {
            log.warn("[금현물 WebSocket] Double 파싱 실패 - index: {}, value: {}", index, fields[index]);
            return null;
        }
    }

    /**
     * Long 파싱 헬퍼
     */
    private Long parseLong(String[] fields, int index) {
        if (index >= fields.length || fields[index] == null || fields[index].trim().isEmpty()) {
            return null;
        }
        try {
            return Long.parseLong(fields[index].trim());
        } catch (NumberFormatException e) {
            log.warn("[금현물 WebSocket] Long 파싱 실패 - index: {}, value: {}", index, fields[index]);
            return null;
        }
    }

    /**
     * Timestamp 파싱 헬퍼
     * 키움증권 형식이 확정되지 않아 현재 시각을 반환하거나, 있으면 그대로 사용
     */
    private String parseTimestamp(String[] fields, int index) {
        if (index >= fields.length || fields[index] == null || fields[index].trim().isEmpty()) {
            return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
        return fields[index].trim();
    }

    /**
     * JSON 응답 처리 (LOGIN 응답 등)
     */
    private void handleJsonResponse(String payload) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> response = objectMapper.readValue(payload, Map.class);

            String trnm = (String) response.get("trnm");

            if ("LOGIN".equals(trnm)) {
                // LOGIN 응답 처리
                String result = (String) response.get("result");
                if ("success".equals(result) || response.containsKey("success")) {
                    connectionState = ConnectionState.AUTHENTICATED;
                    log.info("[금현물 WebSocket] LOGIN 성공");
                } else {
                    connectionState = ConnectionState.FAILED;
                    log.error("[금현물 WebSocket] LOGIN 실패: {}", payload);
                }
            } else if ("REG".equals(trnm)) {
                // REG 응답 처리
                log.info("[금현물 WebSocket] REG 응답: {}", payload);
            } else {
                log.info("[금현물 WebSocket] JSON 응답: {}", payload);
            }

        } catch (Exception e) {
            log.error("[금현물 WebSocket] JSON 응답 처리 실패: {}", payload, e);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        log.warn("[금현물 WebSocket] 연결 종료: {}", status);
        this.session = null;
        connectionState = ConnectionState.DISCONNECTED;

        // Ping 스케줄러 중지
        stopPingMonitoring();

        // 재연결 시도
        attemptReconnect();
    }

    /**
     * 지수 백오프를 사용한 재연결 시도
     */
    private void attemptReconnect() {
        int currentAttempt = reconnectAttempts.getAndIncrement();

        if (currentAttempt >= MAX_RECONNECT_ATTEMPTS) {
            log.error("[금현물 WebSocket] 최대 재연결 시도 횟수({})를 초과했습니다. Circuit Breaker 활성화",
                    MAX_RECONNECT_ATTEMPTS);
            circuitBreakerOpen = true;
            return;
        }

        long delay = BACKOFF_DELAYS[Math.min(currentAttempt, BACKOFF_DELAYS.length - 1)];
        log.info("[금현물 WebSocket] {}초 후 재연결 시도 (시도 {}/{})",
                delay / 1000, currentAttempt + 1, MAX_RECONNECT_ATTEMPTS);

        // 백그라운드 스레드에서 지연 후 재연결
        CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(delay);
                log.info("[금현물 WebSocket] 재연결 시도 중...");
                connect();

                // 재연결 성공 시 이전 구독 재등록
                resubscribeAll();

            } catch (Exception e) {
                log.error("[금현물 WebSocket] 재연결 실패: {}", e.getMessage());
                // 재연결 실패 시 다시 시도
                if (reconnectAttempts.get() < MAX_RECONNECT_ATTEMPTS) {
                    attemptReconnect();
                }
            }
        });
    }

    /**
     * 재연결 후 모든 구독 재등록
     */
    private void resubscribeAll() {
        if (subscribedProducts.isEmpty()) {
            log.info("[금현물 WebSocket] 재구독할 상품이 없습니다");
            return;
        }

        log.info("[금현물 WebSocket] {} 개 상품 재구독 시작", subscribedProducts.size());

        // 기존 구독 목록 복사 (ConcurrentModificationException 방지)
        Set<String> toResubscribe = new java.util.HashSet<>(subscribedProducts);
        subscribedProducts.clear();

        // 각 상품 재구독
        for (String key : toResubscribe) {
            try {
                String[] parts = key.split(":");
                if (parts.length == 2) {
                    String productCode = parts[0];
                    String type = parts[1];
                    subscribe(productCode, type);
                    log.info("[금현물 WebSocket] 재구독 성공: {}", key);
                }
            } catch (Exception e) {
                log.error("[금현물 WebSocket] 재구독 실패: {}", key, e);
            }
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        log.error("[금현물 WebSocket] 전송 오류", exception);
        connectionState = ConnectionState.FAILED;
    }

    /**
     * WebSocket 연결 종료
     */
    @PreDestroy
    public void disconnect() {
        // Ping 스케줄러 중지
        stopPingMonitoring();

        if (session != null && session.isOpen()) {
            try {
                session.close();
                log.info("[금현물 WebSocket] 연결 종료");
            } catch (Exception e) {
                log.error("[금현물 WebSocket] 종료 실패", e);
            }
        }
        connectionState = ConnectionState.DISCONNECTED;
    }

    /**
     * Ping/Pong 모니터링 시작
     */
    private void startPingMonitoring() {
        stopPingMonitoring(); // 기존 스케줄러 정리

        pingScheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread thread = new Thread(r, "gold-websocket-ping");
            thread.setDaemon(true);
            return thread;
        });

        lastPongTime = System.currentTimeMillis();

        // 30초마다 연결 상태 확인
        pingScheduler.scheduleAtFixedRate(() -> {
            try {
                long timeSinceLastPong = System.currentTimeMillis() - lastPongTime;

                if (timeSinceLastPong > PONG_TIMEOUT_MS) {
                    log.warn("[금현물 WebSocket] Pong 응답 없음 ({}ms 경과) - 연결 끊김으로 간주",
                            timeSinceLastPong);

                    // 연결 끊김으로 간주하고 재연결 시도
                    if (session != null && session.isOpen()) {
                        session.close();
                    }
                } else {
                    // Ping 메시지 전송 (WebSocket Ping Frame 사용)
                    if (session != null && session.isOpen()) {
                        session.sendMessage(new org.springframework.web.socket.PingMessage());
                        log.debug("[금현물 WebSocket] Ping 전송");
                    }
                }

            } catch (Exception e) {
                log.error("[금현물 WebSocket] Ping 모니터링 오류", e);
            }
        }, PING_INTERVAL_MS, PING_INTERVAL_MS, TimeUnit.MILLISECONDS);

        log.info("[금현물 WebSocket] Ping/Pong 모니터링 시작 ({}초 주기)", PING_INTERVAL_MS / 1000);
    }

    /**
     * Ping/Pong 모니터링 중지
     */
    private void stopPingMonitoring() {
        if (pingScheduler != null && !pingScheduler.isShutdown()) {
            pingScheduler.shutdown();
            try {
                if (!pingScheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                    pingScheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                pingScheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }
            log.info("[금현물 WebSocket] Ping/Pong 모니터링 중지");
        }
    }

    @Override
    protected void handlePongMessage(WebSocketSession session, org.springframework.web.socket.PongMessage message) {
        lastPongTime = System.currentTimeMillis();
        log.debug("[금현물 WebSocket] Pong 수신");
    }

    /**
     * Circuit Breaker 수동 리셋 (관리자 기능)
     */
    public void resetCircuitBreaker() {
        circuitBreakerOpen = false;
        reconnectAttempts.set(0);
        log.info("[금현물 WebSocket] Circuit Breaker 리셋");
    }

    public Set<String> getSubscribedProducts() {
        return subscribedProducts;
    }

    public ConnectionState getConnectionState() {
        return connectionState;
    }

    public boolean isConnected() {
        return session != null && session.isOpen();
    }

    public boolean isAuthenticated() {
        return connectionState == ConnectionState.AUTHENTICATED ||
               connectionState == ConnectionState.SUBSCRIBED;
    }

    /**
     * 체결 데이터 리스너 등록
     */
    public void addTradeDataListener(TradeDataListener listener) {
        if (listener != null) {
            tradeDataListeners.add(listener);
            log.info("[금현물 WebSocket] 체결 데이터 리스너 등록");
        }
    }

    /**
     * 체결 데이터 리스너 제거
     */
    public void removeTradeDataListener(TradeDataListener listener) {
        if (listener != null) {
            tradeDataListeners.remove(listener);
            log.info("[금현물 WebSocket] 체결 데이터 리스너 제거");
        }
    }

    /**
     * 호가 데이터 리스너 등록
     */
    public void addQuoteDataListener(QuoteDataListener listener) {
        if (listener != null) {
            quoteDataListeners.add(listener);
            log.info("[금현물 WebSocket] 호가 데이터 리스너 등록");
        }
    }

    /**
     * 호가 데이터 리스너 제거
     */
    public void removeQuoteDataListener(QuoteDataListener listener) {
        if (listener != null) {
            quoteDataListeners.remove(listener);
            log.info("[금현물 WebSocket] 호가 데이터 리스너 제거");
        }
    }
}

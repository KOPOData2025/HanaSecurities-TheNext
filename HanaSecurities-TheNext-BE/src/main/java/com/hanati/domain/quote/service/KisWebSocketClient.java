package com.hanati.domain.quote.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanati.common.service.WebSocketApprovalService;
import com.hanati.domain.quote.dto.KisWebSocketRequest;
import com.hanati.domain.quote.dto.RealtimeQuoteResponse;
import com.hanati.domain.quote.dto.RealtimeTradeData;
import com.hanati.domain.stock.dto.StockInfoResponse;
import com.hanati.domain.stock.service.StockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class KisWebSocketClient extends TextWebSocketHandler {

    private final WebSocketApprovalService approvalService;
    private final QuoteCacheService quoteCacheService;
    private final TradeCacheService tradeCacheService;
    private final StockService stockService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private WebSocketSession session;
    private final Set<String> subscribedQuoteStocks = ConcurrentHashMap.newKeySet();
    private final Set<String> subscribedTradeStocks = ConcurrentHashMap.newKeySet();

    private static final String KIS_WS_URL = "ws://ops.koreainvestment.com:21000";

    /**
     * WebSocket 연결 (필요 시에만)
     */
    private synchronized void connect() {
        try {
            if (session != null && session.isOpen()) {
                return;
            }

            log.info("KIS WebSocket 연결 시도...");
            StandardWebSocketClient client = new StandardWebSocketClient();
            session = client.execute(this, KIS_WS_URL).get();
            log.info("KIS WebSocket 연결 성공");
        } catch (Exception e) {
            log.error("KIS WebSocket 연결 실패", e);
            throw new RuntimeException("KIS WebSocket 연결 실패", e);
        }
    }

    /**
     * 호가 구독
     */
    public void subscribeQuote(String stockCode) {
        if (subscribedQuoteStocks.contains(stockCode)) {
            log.info("이미 호가 구독 중인 종목: {}", stockCode);
            return;
        }

        try {
            ensureConnection();

            // NXT 지원 여부 확인
            StockInfoResponse stockInfo = stockService.getStockInfo(stockCode);
            String trId = stockInfo.isNxtSupported() ? "H0UNASP0" : "H0STASP0";

            log.info("종목 {} NXT 지원 여부: {} -> 호가 TR_ID: {}",
                    stockCode, stockInfo.isNxtSupported(), trId);

            // TokenScheduler에서 이미 발급받은 approval key 재사용
            String approvalKey = approvalService.getWebSocketApprovalKey();

            KisWebSocketRequest request = KisWebSocketRequest.builder()
                    .header(KisWebSocketRequest.Header.builder()
                            .approvalKey(approvalKey)
                            .custtype("P")
                            .trType("1")  // 1: 등록
                            .contentType("utf-8")
                            .build())
                    .body(KisWebSocketRequest.Body.builder()
                            .input(KisWebSocketRequest.Input.builder()
                                    .trId(trId)
                                    .trKey(stockCode)
                                    .build())
                            .build())
                    .build();

            String message = objectMapper.writeValueAsString(request);
            log.info("=== 호가 구독 요청 ===");
            log.info("메시지: {}", message);

            synchronized (session) {
                session.sendMessage(new TextMessage(message));
            }

            subscribedQuoteStocks.add(stockCode);
            log.info("호가 구독 완료: {}", stockCode);

        } catch (Exception e) {
            log.error("호가 구독 실패: {}", stockCode, e);
        }
    }

    /**
     * 체결가 구독
     */
    public void subscribeTrade(String stockCode) {
        if (subscribedTradeStocks.contains(stockCode)) {
            log.info("이미 체결가 구독 중인 종목: {}", stockCode);
            return;
        }

        try {
            ensureConnection();

            // NXT 지원 여부 확인
            StockInfoResponse stockInfo = stockService.getStockInfo(stockCode);
            String trId = stockInfo.isNxtSupported() ? "H0UNCNT0" : "H0STCNT0";

            log.info("종목 {} NXT 지원 여부: {} -> TR_ID: {}",
                    stockCode, stockInfo.isNxtSupported(), trId);

            // TokenScheduler에서 이미 발급받은 approval key 재사용
            String approvalKey = approvalService.getWebSocketApprovalKey();

            KisWebSocketRequest request = KisWebSocketRequest.builder()
                    .header(KisWebSocketRequest.Header.builder()
                            .approvalKey(approvalKey)
                            .custtype("P")
                            .trType("1")  // 1: 등록
                            .contentType("utf-8")
                            .build())
                    .body(KisWebSocketRequest.Body.builder()
                            .input(KisWebSocketRequest.Input.builder()
                                    .trId(trId)
                                    .trKey(stockCode)
                                    .build())
                            .build())
                    .build();

            String message = objectMapper.writeValueAsString(request);
            log.info("=== 체결가 구독 요청 ===");
            log.info("메시지: {}", message);

            synchronized (session) {
                session.sendMessage(new TextMessage(message));
            }

            subscribedTradeStocks.add(stockCode);
            log.info("체결가 구독 완료: {}", stockCode);

        } catch (Exception e) {
            log.error("체결가 구독 실패: {}", stockCode, e);
        }
    }

    /**
     * 호가 구독 해제
     */
    public void unsubscribeQuote(String stockCode) {
        if (!subscribedQuoteStocks.contains(stockCode)) {
            return;
        }

        try {
            String approvalKey = approvalService.getWebSocketApprovalKey();

            KisWebSocketRequest request = KisWebSocketRequest.builder()
                    .header(KisWebSocketRequest.Header.builder()
                            .approvalKey(approvalKey)
                            .custtype("P")
                            .trType("2")  // 2: 해제
                            .contentType("utf-8")
                            .build())
                    .body(KisWebSocketRequest.Body.builder()
                            .input(KisWebSocketRequest.Input.builder()
                                    .trId("H0UNASP0")
                                    .trKey(stockCode)
                                    .build())
                            .build())
                    .build();

            String message = objectMapper.writeValueAsString(request);
            synchronized (session) {
                session.sendMessage(new TextMessage(message));
            }

            subscribedQuoteStocks.remove(stockCode);
            log.info("호가 구독 해제: {}", stockCode);

        } catch (Exception e) {
            log.error("호가 구독 해제 실패: {}", stockCode, e);
        }
    }

    /**
     * 체결가 구독 해제
     */
    public void unsubscribeTrade(String stockCode) {
        if (!subscribedTradeStocks.contains(stockCode)) {
            return;
        }

        try {
            String approvalKey = approvalService.getWebSocketApprovalKey();

            KisWebSocketRequest request = KisWebSocketRequest.builder()
                    .header(KisWebSocketRequest.Header.builder()
                            .approvalKey(approvalKey)
                            .custtype("P")
                            .trType("2")  // 2: 해제
                            .contentType("utf-8")
                            .build())
                    .body(KisWebSocketRequest.Body.builder()
                            .input(KisWebSocketRequest.Input.builder()
                                    .trId("H0UNCNT0")
                                    .trKey(stockCode)
                                    .build())
                            .build())
                    .build();

            String message = objectMapper.writeValueAsString(request);
            synchronized (session) {
                session.sendMessage(new TextMessage(message));
            }

            subscribedTradeStocks.remove(stockCode);
            log.info("체결가 구독 해제: {}", stockCode);

        } catch (Exception e) {
            log.error("체결가 구독 해제 실패: {}", stockCode, e);
        }
    }

    /**
     * WebSocket 연결 확인
     */
    private void ensureConnection() {
        if (session == null || !session.isOpen()) {
            log.warn("WebSocket 연결이 끊어짐. 재연결 시도...");
            connect();
        }
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        try {
            String payload = message.getPayload();
            log.info("=== KIS WebSocket 수신 ===");
            log.info("메시지: {}", payload);

            // JSON 응답 (연결 확인)
            if (payload.startsWith("{")) {
                log.info("KIS WebSocket JSON 응답: {}", payload);
                return;
            }

            // 실시간 데이터 (| 구분)
            if (payload.contains("|")) {
                String[] parts = payload.split("\\|");
                if (parts.length >= 2) {
                    String trId = parts[1];
                    log.info("실시간 데이터 수신 - TR_ID: {}", trId);

                    if ("H0UNASP0".equals(trId) || "H0STASP0".equals(trId)) {
                        parseRealtimeQuoteData(payload);
                    } else if ("H0UNCNT0".equals(trId) || "H0STCNT0".equals(trId)) {
                        parseRealtimeTradeData(payload);
                    }
                }
            }

        } catch (Exception e) {
            log.error("메시지 처리 실패", e);
        }
    }

    /**
     * 실시간 호가 데이터 파싱 (H0UNASP0)
     */
    private void parseRealtimeQuoteData(String data) {
        String[] parts = data.split("\\|");
        if (parts.length < 4) {
            return;
        }

        String[] fields = parts[3].split("\\^");
        if (fields.length < 44) {
            log.warn("호가 데이터 필드 부족: {}", fields.length);
            return;
        }

        String stockCode = fields[0];

        List<Long> askPrices = Arrays.asList(
                parseLong(fields[3]), parseLong(fields[4]), parseLong(fields[5]), parseLong(fields[6]),
                parseLong(fields[7]), parseLong(fields[8]), parseLong(fields[9]), parseLong(fields[10]),
                parseLong(fields[11]), parseLong(fields[12])
        );

        List<Long> bidPrices = Arrays.asList(
                parseLong(fields[13]), parseLong(fields[14]), parseLong(fields[15]), parseLong(fields[16]),
                parseLong(fields[17]), parseLong(fields[18]), parseLong(fields[19]), parseLong(fields[20]),
                parseLong(fields[21]), parseLong(fields[22])
        );

        List<Long> askVolumes = Arrays.asList(
                parseLong(fields[23]), parseLong(fields[24]), parseLong(fields[25]), parseLong(fields[26]),
                parseLong(fields[27]), parseLong(fields[28]), parseLong(fields[29]), parseLong(fields[30]),
                parseLong(fields[31]), parseLong(fields[32])
        );

        List<Long> bidVolumes = Arrays.asList(
                parseLong(fields[33]), parseLong(fields[34]), parseLong(fields[35]), parseLong(fields[36]),
                parseLong(fields[37]), parseLong(fields[38]), parseLong(fields[39]), parseLong(fields[40]),
                parseLong(fields[41]), parseLong(fields[42])
        );

        Long totalAskVolume = parseLong(fields[43]);
        Long totalBidVolume = parseLong(fields[44]);

        RealtimeQuoteResponse quote = RealtimeQuoteResponse.builder()
                .stockCode(stockCode)
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .askPrices(askPrices)
                .bidPrices(bidPrices)
                .askVolumes(askVolumes)
                .bidVolumes(bidVolumes)
                .totalAskVolume(totalAskVolume)
                .totalBidVolume(totalBidVolume)
                .build();

        quoteCacheService.saveQuote(stockCode, quote);
        log.info("실시간 호가 캐시 저장 완료: {}", stockCode);
    }

    /**
     * 실시간 체결가 데이터 파싱 (H0UNCNT0)
     */
    private void parseRealtimeTradeData(String data) {
        String[] parts = data.split("\\|");
        if (parts.length < 4) {
            return;
        }

        String[] fields = parts[3].split("\\^");
        if (fields.length < 40) {
            log.warn("체결가 데이터 필드 부족: {}", fields.length);
            return;
        }

        RealtimeTradeData tradeData = RealtimeTradeData.builder()
                .stockCode(fields[0])
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .tradeTime(fields[1])
                .currentPrice(fields[2])
                .priceChangeSign(fields[3])
                .priceChange(fields[4])
                .changeRate(fields[5])
                .openPrice(fields[7])
                .highPrice(fields[8])
                .lowPrice(fields[9])
                .askPrice1(fields[10])
                .bidPrice1(fields[11])
                .tradeVolume(fields[12])
                .accumulatedVolume(fields[13])
                .accumulatedAmount(fields[14])
                .sellCount(fields[15])
                .buyCount(fields[16])
                .tradeStrength(fields[18])
                .totalAskRemain(fields.length > 38 ? fields[38] : "0")
                .totalBidRemain(fields.length > 39 ? fields[39] : "0")
                .build();

        tradeCacheService.saveTrade(tradeData.getStockCode(), tradeData);
        log.info("실시간 체결가 캐시 저장 완료: {}", tradeData.getStockCode());
    }

    private Long parseLong(String value) {
        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException e) {
            return 0L;
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        log.warn("KIS WebSocket 연결 종료: {}", status);
        this.session = null;
        // 자동 재연결 시도
        try {
            Thread.sleep(3000);
            connect();
        } catch (Exception e) {
            log.error("WebSocket 재연결 실패", e);
        }
    }

    /**
     * 애플리케이션 종료 시 WebSocket 연결 종료
     */
    public void disconnect() {
        if (session != null && session.isOpen()) {
            try {
                session.close();
                log.info("KIS WebSocket 연결 종료");
            } catch (Exception e) {
                log.error("WebSocket 종료 실패", e);
            }
        }
    }
}

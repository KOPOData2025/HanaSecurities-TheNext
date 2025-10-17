package com.hanati.domain.foreignquote.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanati.common.service.WebSocketApprovalService;
import com.hanati.domain.foreignquote.dto.ForeignQuoteData;
import com.hanati.domain.quote.dto.KisWebSocketRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 해외주식 통합 WebSocket 클라이언트
 * - 하나의 WebSocket 연결로 모든 해외주식 데이터 처리
 * - HDFSCNT0: 체결가 (미국/아시아 공통)
 * - HDFSASP0: 실시간 호가 (미국)
 * - HDFSASP1: 지연 호가 (아시아)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ForeignKisWebSocketClient extends TextWebSocketHandler {

    private final WebSocketApprovalService approvalService;
    private final ForeignQuoteCacheService cacheService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private WebSocketSession session;
    private final Set<String> subscribedStocks = ConcurrentHashMap.newKeySet();
    // stockCode -> exchangeCode 매핑
    private final Map<String, String> stockToExchangeMap = new ConcurrentHashMap<>();

    private static final String KIS_WS_URL = "ws://ops.koreainvestment.com:21000";

    /**
     * WebSocket 연결
     */
    private synchronized void connect() {
        try {
            if (session != null && session.isOpen()) {
                return;
            }

            log.info("[해외주식 통합 WebSocket] 연결 시도...");
            StandardWebSocketClient client = new StandardWebSocketClient();
            session = client.execute(this, KIS_WS_URL).get();
            log.info("[해외주식 통합 WebSocket] 연결 성공");
        } catch (Exception e) {
            log.error("[해외주식 통합 WebSocket] 연결 실패", e);
            throw new RuntimeException("해외주식 WebSocket 연결 실패", e);
        }
    }

    /**
     * 체결가 구독 (HDFSCNT0)
     */
    public void subscribeTrade(String exchangeCode, String stockCode) {
        subscribe(exchangeCode, stockCode, "HDFSCNT0", "trade");
    }

    /**
     * 호가 구독 (HDFSASP0 또는 HDFSASP1)
     */
    public void subscribeQuote(String exchangeCode, String stockCode) {
        // 미국/아시아 자동 판별
        String trId = isUsMarket(exchangeCode) ? "HDFSASP0" : "HDFSASP1";
        subscribe(exchangeCode, stockCode, trId, "quote");
    }

    /**
     * 통합 구독 메서드
     */
    private void subscribe(String exchangeCode, String stockCode, String trId, String type) {
        String key = exchangeCode + ":" + stockCode + ":" + trId;

        if (subscribedStocks.contains(key)) {
            log.info("[해외주식 {}] 이미 구독 중: {}", type, key);
            return;
        }

        try {
            ensureConnection();

            String trKey = buildTrKey(exchangeCode, stockCode, trId);
            log.info("[해외주식 {}] 구독 요청 - TR_ID: {}, TR_KEY: {}", type, trId, trKey);

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
                                    .trKey(trKey)
                                    .build())
                            .build())
                    .build();

            String message = objectMapper.writeValueAsString(request);
            log.info("[해외주식 구독 요청] {}", message);

            synchronized (session) {
                session.sendMessage(new TextMessage(message));
            }

            subscribedStocks.add(key);
            stockToExchangeMap.put(stockCode.toUpperCase(), exchangeCode.toUpperCase());
            log.info("[해외주식 {}] 구독 완료: {}", type, key);

        } catch (Exception e) {
            log.error("[해외주식 {}] 구독 실패: {}", type, key, e);
        }
    }

    /**
     * 구독 해제
     */
    public void unsubscribe(String exchangeCode, String stockCode, String trId) {
        String key = exchangeCode + ":" + stockCode + ":" + trId;

        if (!subscribedStocks.contains(key)) {
            return;
        }

        try {
            String trKey = buildTrKey(exchangeCode, stockCode, trId);
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
                                    .trId(trId)
                                    .trKey(trKey)
                                    .build())
                            .build())
                    .build();

            String message = objectMapper.writeValueAsString(request);
            synchronized (session) {
                session.sendMessage(new TextMessage(message));
            }

            subscribedStocks.remove(key);
            log.info("[해외주식] 구독 해제: {}", key);

        } catch (Exception e) {
            log.error("[해외주식] 구독 해제 실패: {}", key, e);
        }
    }

    /**
     * TR_KEY 생성
     */
    private String buildTrKey(String exchangeCode, String stockCode, String trId) {
        String prefix;
        String marketCode = getMarketCode(exchangeCode);

        if ("HDFSCNT0".equals(trId)) {
            // 체결가: 무료(D)
            prefix = "D";
        } else if ("HDFSASP0".equals(trId)) {
            // 미국 호가: 무료(D)
            prefix = "D";
        } else if ("HDFSASP1".equals(trId)) {
            // 아시아 지연호가: 무료(D)
            prefix = "D";
        } else {
            prefix = "D";
        }

        return prefix + marketCode + stockCode.toUpperCase();
    }

    /**
     * 거래소별 시장코드 매핑
     */
    private String getMarketCode(String exchangeCode) {
        switch (exchangeCode.toUpperCase()) {
            case "NYS":
            case "NYSE":
                return "NYS";
            case "NAS":
            case "NASD":
                return "NAS";
            case "AMS":
            case "AMEX":
                return "AMS";
            case "HKS":
            case "SEHK":
                return "HKS";
            case "TSE":
            case "TKSE":
                return "TSE";
            case "SHS":
                return "SHS";
            case "SZS":
                return "SZS";
            case "HSX":
                return "HSX";
            case "HNX":
                return "HNX";
            default:
                return exchangeCode.toUpperCase();
        }
    }

    /**
     * 미국 시장 여부
     */
    private boolean isUsMarket(String exchangeCode) {
        return exchangeCode.matches("(?i)(NYS|NYSE|NAS|NASD|AMS|AMEX)");
    }

    /**
     * WebSocket 연결 확인
     */
    private void ensureConnection() {
        if (session == null || !session.isOpen()) {
            log.warn("[해외주식 WebSocket] 연결이 끊어짐. 재연결 시도...");
            connect();
        }
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        try {
            String payload = message.getPayload();
            log.info("[해외주식 WebSocket 수신] {}", payload);

            // JSON 응답 (연결 확인)
            if (payload.startsWith("{")) {
                log.info("[해외주식 WebSocket] JSON 응답: {}", payload);
                return;
            }

            // 실시간 데이터 (| 구분)
            if (payload.contains("|")) {
                String[] parts = payload.split("\\|");
                if (parts.length >= 2) {
                    String trId = parts[1];
                    log.info("[해외주식 실시간 데이터 수신] TR_ID: {}, parts.length: {}", trId, parts.length);

                    if ("HDFSCNT0".equals(trId)) {
                        log.info("[해외주식] 체결가 데이터 파싱 시작");
                        parseTradeData(payload);
                    } else if ("HDFSASP0".equals(trId)) {
                        log.info("[해외주식] 미국 호가 데이터 파싱 시작");
                        parseUsQuoteData(payload);
                    } else if ("HDFSASP1".equals(trId)) {
                        log.info("[해외주식] 아시아 호가 데이터 파싱 시작");
                        parseAsiaQuoteData(payload);
                    } else {
                        log.warn("[해외주식] 알 수 없는 TR_ID: {}", trId);
                    }
                }
            }

        } catch (Exception e) {
            log.error("[해외주식 WebSocket] 메시지 처리 실패", e);
        }
    }

    /**
     * 체결가 데이터 파싱 (HDFSCNT0)
     */
    private void parseTradeData(String data) {
        String[] parts = data.split("\\|");
        if (parts.length < 4) {
            log.warn("[해외주식 체결] parts 부족: {}", parts.length);
            return;
        }

        String[] fields = parts[3].split("\\^");
        if (fields.length < 24) {
            log.warn("[해외주식 체결] 데이터 필드 부족: {}, 전체 데이터: {}", fields.length, data);
            return;
        }

        String stockCode = fields[1];
        String currentPrice = fields.length > 11 ? fields[11] : "0";
        String changeRate = fields.length > 14 ? fields[14] : "0";
        String volume = fields.length > 20 ? fields[20] : "0";
        String executionTime = fields.length > 7 ? fields[7] : "";
        String bidPrice1 = fields.length > 15 ? fields[15] : "0";
        String askPrice1 = fields.length > 16 ? fields[16] : "0";
        String bidQuantity1 = fields.length > 17 ? fields[17] : "0";
        String askQuantity1 = fields.length > 18 ? fields[18] : "0";

        log.info("[해외주식 체결] 파싱 완료 - stockCode: {}, currentPrice: {}, volume: {}",
                stockCode, currentPrice, volume);

        ForeignQuoteData quoteData = ForeignQuoteData.builder()
                .stockCode(stockCode)
                .currentPrice(currentPrice)
                .changeRate(changeRate)
                .volume(volume)
                .executionTime(executionTime)
                .bidPrice1(bidPrice1)
                .bidQuantity1(bidQuantity1)
                .askPrice1(askPrice1)
                .askQuantity1(askQuantity1)
                .build();

        String exchangeCode = stockToExchangeMap.get(stockCode.toUpperCase());
        log.info("[해외주식 체결] exchangeCode 조회: stockCode={}, found={}",
                stockCode.toUpperCase(), exchangeCode);

        if (exchangeCode != null) {
            cacheService.saveQuote(exchangeCode, stockCode, quoteData);
            log.info("[해외주식 체결] 캐시 저장 완료: {}:{}", exchangeCode, stockCode);
        } else {
            log.error("[해외주식 체결] exchangeCode를 찾을 수 없음. stockCode: {}, map: {}",
                    stockCode, stockToExchangeMap);
        }
    }

    /**
     * 미국 호가 데이터 파싱 (HDFSASP0)
     */
    private void parseUsQuoteData(String data) {
        String[] parts = data.split("\\|");
        if (parts.length < 4) {
            log.warn("[해외주식 미국 호가] parts 부족: {}", parts.length);
            return;
        }

        String[] fields = parts[3].split("\\^");
        if (fields.length < 20) {
            log.warn("[해외주식 미국 호가] 데이터 필드 부족: {}, 전체 데이터: {}", fields.length, data);
            return;
        }

        String stockCode = fields[1];
        String bidPrice1 = fields.length > 11 ? fields[11] : "0";
        String askPrice1 = fields.length > 12 ? fields[12] : "0";
        String bidQuantity1 = fields.length > 13 ? fields[13] : "0";
        String askQuantity1 = fields.length > 14 ? fields[14] : "0";
        String executionTime = fields.length > 6 ? fields[6] : "";

        String currentPrice = calculateMidPrice(bidPrice1, askPrice1);

        log.info("[해외주식 미국 호가] 파싱 완료 - stockCode: {}, bid: {}, ask: {}",
                stockCode, bidPrice1, askPrice1);

        ForeignQuoteData quoteData = ForeignQuoteData.builder()
                .stockCode(stockCode)
                .currentPrice(currentPrice)
                .bidPrice1(bidPrice1)
                .bidQuantity1(bidQuantity1)
                .askPrice1(askPrice1)
                .askQuantity1(askQuantity1)
                .executionTime(executionTime)
                .volume("0")
                .changeRate("0")
                .build();

        String exchangeCode = stockToExchangeMap.get(stockCode.toUpperCase());
        log.info("[해외주식 미국 호가] exchangeCode 조회: stockCode={}, found={}",
                stockCode.toUpperCase(), exchangeCode);

        if (exchangeCode != null) {
            cacheService.saveQuote(exchangeCode, stockCode, quoteData);
            log.info("[해외주식 미국 호가] 캐시 저장 완료: {}:{}", exchangeCode, stockCode);
        } else {
            log.error("[해외주식 미국 호가] exchangeCode를 찾을 수 없음. stockCode: {}, map: {}",
                    stockCode, stockToExchangeMap);
        }
    }

    /**
     * 아시아 지연 호가 데이터 파싱 (HDFSASP1)
     */
    private void parseAsiaQuoteData(String data) {
        String[] parts = data.split("\\|");
        if (parts.length < 4) {
            log.warn("[해외주식 아시아 호가] parts 부족: {}", parts.length);
            return;
        }

        String[] fields = parts[3].split("\\^");
        if (fields.length < 15) {
            log.warn("[해외주식 아시아 호가] 데이터 필드 부족: {}, 전체 데이터: {}", fields.length, data);
            return;
        }

        String stockCode = fields[1];
        String bidPrice1 = fields.length > 11 ? fields[11] : "0";
        String askPrice1 = fields.length > 12 ? fields[12] : "0";
        String bidQuantity1 = fields.length > 13 ? fields[13] : "0";
        String askQuantity1 = fields.length > 14 ? fields[14] : "0";
        String executionTime = fields.length > 6 ? fields[6] : "";

        String currentPrice = calculateMidPrice(bidPrice1, askPrice1);

        log.info("[해외주식 아시아 호가] 파싱 완료 - stockCode: {}, bid: {}, ask: {} (15분 지연)",
                stockCode, bidPrice1, askPrice1);

        ForeignQuoteData quoteData = ForeignQuoteData.builder()
                .stockCode(stockCode)
                .currentPrice(currentPrice)
                .bidPrice1(bidPrice1)
                .bidQuantity1(bidQuantity1)
                .askPrice1(askPrice1)
                .askQuantity1(askQuantity1)
                .executionTime(executionTime)
                .volume("0")
                .changeRate("0")
                .build();

        String exchangeCode = stockToExchangeMap.get(stockCode.toUpperCase());
        log.info("[해외주식 아시아 호가] exchangeCode 조회: stockCode={}, found={}",
                stockCode.toUpperCase(), exchangeCode);

        if (exchangeCode != null) {
            cacheService.saveQuote(exchangeCode, stockCode, quoteData);
            log.info("[해외주식 아시아 호가] 캐시 저장 완료: {}:{} (15분 지연)", exchangeCode, stockCode);
        } else {
            log.error("[해외주식 아시아 호가] exchangeCode를 찾을 수 없음. stockCode: {}, map: {}",
                    stockCode, stockToExchangeMap);
        }
    }

    /**
     * 중간가격 계산
     */
    private String calculateMidPrice(String bid, String ask) {
        try {
            double bidPrice = Double.parseDouble(bid);
            double askPrice = Double.parseDouble(ask);
            double midPrice = (bidPrice + askPrice) / 2.0;
            return String.format("%.4f", midPrice);
        } catch (Exception e) {
            return bid;
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        log.warn("[해외주식 WebSocket] 연결 종료: {}", status);
        this.session = null;
        // 자동 재연결 시도
        try {
            Thread.sleep(3000);
            connect();
        } catch (Exception e) {
            log.error("[해외주식 WebSocket] 재연결 실패", e);
        }
    }

    public void disconnect() {
        if (session != null && session.isOpen()) {
            try {
                session.close();
                log.info("[해외주식 WebSocket] 연결 종료");
            } catch (Exception e) {
                log.error("[해외주식 WebSocket] 종료 실패", e);
            }
        }
    }

    public Set<String> getSubscribedStocks() {
        return subscribedStocks;
    }
}

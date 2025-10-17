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
 * 아시아 해외주식 지연호가 WebSocket 클라이언트 (HDFSASP1)
 * - 아시아 국가: 15분 지연 무료시세
 * - 홍콩, 베트남, 중국, 일본 지원
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AsiaDelayedQuoteWebSocketClient extends TextWebSocketHandler {

    private final WebSocketApprovalService approvalService;
    private final ForeignQuoteCacheService cacheService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private WebSocketSession session;
    private final Set<String> subscribedStocks = ConcurrentHashMap.newKeySet();
    // stockCode -> exchangeCode 매핑 (캐시 저장용)
    private final Map<String, String> stockToExchangeMap = new ConcurrentHashMap<>();

    private static final String KIS_WS_URL = "ws://ops.koreainvestment.com:21000";
    private static final String TR_ID = "HDFSASP1";  // 지연호가(아시아)

    /**
     * WebSocket 연결
     */
    private synchronized void connect() {
        try {
            if (session != null && session.isOpen()) {
                return;
            }

            log.info("[아시아 지연호가 WebSocket] 연결 시도...");
            StandardWebSocketClient client = new StandardWebSocketClient();
            session = client.execute(this, KIS_WS_URL).get();
            log.info("[아시아 지연호가 WebSocket] 연결 성공");
        } catch (Exception e) {
            log.error("[아시아 지연호가 WebSocket] 연결 실패", e);
            throw new RuntimeException("아시아 지연호가 WebSocket 연결 실패", e);
        }
    }

    /**
     * 아시아 지연호가 구독
     * @param exchangeCode 거래소코드 (HKS, TSE, SHS, SZS, HSX, HNX)
     * @param stockCode 종목코드
     */
    public void subscribeDelayedQuote(String exchangeCode, String stockCode) {
        String key = exchangeCode + ":" + stockCode;

        if (subscribedStocks.contains(key)) {
            log.info("[아시아 지연호가 구독] 이미 구독 중: {}", key);
            return;
        }

        try {
            ensureConnection();

            String trKey = buildTrKey(exchangeCode, stockCode);
            log.info("[아시아 지연호가 구독] TR_KEY: {} (거래소: {}, 종목: {})",
                    trKey, exchangeCode, stockCode);

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
                                    .trId(TR_ID)
                                    .trKey(trKey)
                                    .build())
                            .build())
                    .build();

            String message = objectMapper.writeValueAsString(request);
            log.info("[아시아 지연호가 구독 요청] {}", message);

            synchronized (session) {
                session.sendMessage(new TextMessage(message));
            }

            subscribedStocks.add(key);
            stockToExchangeMap.put(stockCode.toUpperCase(), exchangeCode.toUpperCase());
            log.info("[아시아 지연호가 구독 완료] {}", key);

        } catch (Exception e) {
            log.error("[아시아 지연호가 구독 실패] {}", key, e);
        }
    }

    /**
     * 아시아 지연호가 구독 해제
     */
    public void unsubscribeDelayedQuote(String exchangeCode, String stockCode) {
        String key = exchangeCode + ":" + stockCode;

        if (!subscribedStocks.contains(key)) {
            return;
        }

        try {
            String trKey = buildTrKey(exchangeCode, stockCode);
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
                                    .trId(TR_ID)
                                    .trKey(trKey)
                                    .build())
                            .build())
                    .build();

            String message = objectMapper.writeValueAsString(request);
            synchronized (session) {
                session.sendMessage(new TextMessage(message));
            }

            subscribedStocks.remove(key);
            log.info("[아시아 지연호가 구독 해제] {}", key);

        } catch (Exception e) {
            log.error("[아시아 지연호가 구독 해제 실패] {}", key, e);
        }
    }

    /**
     * TR_KEY 생성 로직 (HDFSASP1)
     * 아시아 무료 지연시세: D+시장구분+종목코드 (예: DHKS00003)
     */
    private String buildTrKey(String exchangeCode, String stockCode) {
        String marketCode = getMarketCode(exchangeCode);
        return "D" + marketCode + stockCode.toUpperCase();
    }

    /**
     * 거래소별 시장코드 매핑
     */
    private String getMarketCode(String exchangeCode) {
        switch (exchangeCode.toUpperCase()) {
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

    private void ensureConnection() {
        if (session == null || !session.isOpen()) {
            log.warn("[아시아 지연호가 WebSocket] 연결이 끊어짐. 재연결 시도...");
            connect();
        }
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        try {
            String payload = message.getPayload();
            log.debug("[아시아 지연호가 WebSocket 수신] {}", payload);

            // JSON 응답 (연결 확인)
            if (payload.startsWith("{")) {
                log.info("[아시아 지연호가 WebSocket] JSON 응답: {}", payload);
                return;
            }

            // 실시간 데이터 (| 구분)
            if (payload.contains("|")) {
                String[] parts = payload.split("\\|");
                if (parts.length >= 2 && TR_ID.equals(parts[1])) {
                    parseDelayedQuoteData(payload);
                }
            }

        } catch (Exception e) {
            log.error("[아시아 지연호가 WebSocket] 메시지 처리 실패", e);
        }
    }

    /**
     * 아시아 지연호가 데이터 파싱 (HDFSASP1)
     * Response 형식: 0|HDFSASP1|001|RSYM^SYMB^ZDIV^XYMD^XHMS^KYMD^KHMS^BVOL^AVOL^BDVL^ADVL^PBID1^PASK1^VBID1^VASK1^DBID1^DASK1
     */
    private void parseDelayedQuoteData(String data) {
        String[] parts = data.split("\\|");
        if (parts.length < 4) {
            return;
        }

        String[] fields = parts[3].split("\\^");
        if (fields.length < 15) {
            log.warn("[아시아 지연호가] 데이터 필드 부족: {}", fields.length);
            return;
        }

        // 필드 파싱
        // 0:RSYM, 1:SYMB, 2:ZDIV, 3:XYMD, 4:XHMS, 5:KYMD, 6:KHMS,
        // 7:BVOL, 8:AVOL, 9:BDVL, 10:ADVL, 11:PBID1, 12:PASK1, 13:VBID1, 14:VASK1, 15:DBID1, 16:DASK1

        String stockCode = fields.length > 1 ? fields[1] : "";
        String executionTime = fields.length > 6 ? fields[6] : "";
        String bidPrice1 = fields.length > 11 ? fields[11] : "0";
        String askPrice1 = fields.length > 12 ? fields[12] : "0";
        String bidQuantity1 = fields.length > 13 ? fields[13] : "0";
        String askQuantity1 = fields.length > 14 ? fields[14] : "0";

        // 중간가격 계산
        String currentPrice = calculateMidPrice(bidPrice1, askPrice1);

        ForeignQuoteData quoteData = ForeignQuoteData.builder()
                .stockCode(stockCode)
                .currentPrice(currentPrice)
                .bidPrice1(bidPrice1)
                .bidQuantity1(bidQuantity1)
                .askPrice1(askPrice1)
                .askQuantity1(askQuantity1)
                .executionTime(executionTime)
                .volume("0")  // 호가 데이터에는 거래량 없음
                .changeRate("0")  // 호가 데이터에는 등락률 없음
                .build();

        // exchangeCode 조회 및 캐시 저장
        String exchangeCode = stockToExchangeMap.get(stockCode.toUpperCase());
        if (exchangeCode != null) {
            cacheService.saveQuote(exchangeCode, stockCode, quoteData);
            log.debug("[아시아 지연호가] 캐시 저장 완료: {}:{} (15분 지연)", exchangeCode, stockCode);
        } else {
            log.warn("[아시아 지연호가] exchangeCode를 찾을 수 없음: {}", stockCode);
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
        log.warn("[아시아 지연호가 WebSocket] 연결 종료: {}", status);
        this.session = null;
        // 자동 재연결 시도
        try {
            Thread.sleep(3000);
            connect();
        } catch (Exception e) {
            log.error("[아시아 지연호가 WebSocket] 재연결 실패", e);
        }
    }

    public void disconnect() {
        if (session != null && session.isOpen()) {
            try {
                session.close();
                log.info("[아시아 지연호가 WebSocket] 연결 종료");
            } catch (Exception e) {
                log.error("[아시아 지연호가 WebSocket] 종료 실패", e);
            }
        }
    }

    public Set<String> getSubscribedStocks() {
        return subscribedStocks;
    }
}

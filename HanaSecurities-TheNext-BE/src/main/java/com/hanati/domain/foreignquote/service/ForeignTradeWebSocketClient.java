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
 * 해외주식 실시간 체결가 WebSocket 클라이언트 (HDFSCNT0)
 * - 미국: 실시간 무료 (0분 지연)
 * - 아시아: 15분 지연 (무료) 또는 실시간 (유료)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ForeignTradeWebSocketClient extends TextWebSocketHandler {

    private final WebSocketApprovalService approvalService;
    private final ForeignQuoteCacheService cacheService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private WebSocketSession session;
    private final Set<String> subscribedStocks = ConcurrentHashMap.newKeySet();
    // stockCode -> exchangeCode 매핑 (캐시 저장용)
    private final Map<String, String> stockToExchangeMap = new ConcurrentHashMap<>();

    private static final String KIS_WS_URL = "ws://ops.koreainvestment.com:21000";
    private static final String TR_ID = "HDFSCNT0";  // 실시간지연체결가

    /**
     * WebSocket 연결
     */
    private synchronized void connect() {
        try {
            if (session != null && session.isOpen()) {
                return;
            }

            log.info("[해외주식 체결 WebSocket] 연결 시도...");
            StandardWebSocketClient client = new StandardWebSocketClient();
            session = client.execute(this, KIS_WS_URL).get();
            log.info("[해외주식 체결 WebSocket] 연결 성공");
        } catch (Exception e) {
            log.error("[해외주식 체결 WebSocket] 연결 실패", e);
            throw new RuntimeException("해외주식 체결 WebSocket 연결 실패", e);
        }
    }

    /**
     * 해외주식 체결가 구독
     * @param exchangeCode 거래소코드
     * @param stockCode 종목코드
     * @param isPaidService 유료 서비스 여부 (true: 실시간, false: 지연)
     */
    public void subscribeTrade(String exchangeCode, String stockCode, boolean isPaidService) {
        String key = exchangeCode + ":" + stockCode;

        if (subscribedStocks.contains(key)) {
            log.info("[해외주식 체결 구독] 이미 구독 중: {}", key);
            return;
        }

        try {
            ensureConnection();

            String trKey = buildTrKey(exchangeCode, stockCode, isPaidService);
            log.info("[해외주식 체결 구독] TR_KEY: {} (거래소: {}, 종목: {}, 유료: {})",
                    trKey, exchangeCode, stockCode, isPaidService);

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
            log.info("[해외주식 체결 구독 요청] {}", message);

            synchronized (session) {
                session.sendMessage(new TextMessage(message));
            }

            subscribedStocks.add(key);
            stockToExchangeMap.put(stockCode.toUpperCase(), exchangeCode.toUpperCase());
            log.info("[해외주식 체결 구독 완료] {}", key);

        } catch (Exception e) {
            log.error("[해외주식 체결 구독 실패] {}", key, e);
        }
    }

    /**
     * 해외주식 체결가 구독 해제
     */
    public void unsubscribeTrade(String exchangeCode, String stockCode, boolean isPaidService) {
        String key = exchangeCode + ":" + stockCode;

        if (!subscribedStocks.contains(key)) {
            return;
        }

        try {
            String trKey = buildTrKey(exchangeCode, stockCode, isPaidService);
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
            log.info("[해외주식 체결 구독 해제] {}", key);

        } catch (Exception e) {
            log.error("[해외주식 체결 구독 해제 실패] {}", key, e);
        }
    }

    /**
     * TR_KEY 생성 로직 (HDFSCNT0)
     * 무료시세: D+시장구분+종목코드 (예: DNASAAPL)
     * 유료시세: R+시장구분+종목코드 (예: RNASAAPL)
     * 미국 주간거래: R+BAQ/BAY/BAA+종목코드 (예: RBAQAAPL)
     */
    private String buildTrKey(String exchangeCode, String stockCode, boolean isPaidService) {
        String prefix = isPaidService ? "R" : "D";
        String marketCode = getMarketCode(exchangeCode);
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

    private void ensureConnection() {
        if (session == null || !session.isOpen()) {
            log.warn("[해외주식 체결 WebSocket] 연결이 끊어짐. 재연결 시도...");
            connect();
        }
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        try {
            String payload = message.getPayload();
            log.debug("[해외주식 체결 WebSocket 수신] {}", payload);

            // JSON 응답 (연결 확인)
            if (payload.startsWith("{")) {
                log.info("[해외주식 체결 WebSocket] JSON 응답: {}", payload);
                return;
            }

            // 실시간 데이터 (| 구분)
            if (payload.contains("|")) {
                String[] parts = payload.split("\\|");
                if (parts.length >= 2 && TR_ID.equals(parts[1])) {
                    parseRealtimeTradeData(payload);
                }
            }

        } catch (Exception e) {
            log.error("[해외주식 체결 WebSocket] 메시지 처리 실패", e);
        }
    }

    /**
     * 실시간 체결 데이터 파싱 (HDFSCNT0)
     * Response 형식: 0|HDFSCNT0|001|RSYM^SYMB^ZDIV^TYMD^XYMD^XHMS^KYMD^KHMS^OPEN^HIGH^LOW^LAST^SIGN^DIFF^RATE^PBID^PASK^VBID^VASK^EVOL^TVOL^TAMT^BIVL^ASVL^STRN^MTYP
     */
    private void parseRealtimeTradeData(String data) {
        String[] parts = data.split("\\|");
        if (parts.length < 4) {
            return;
        }

        String[] fields = parts[3].split("\\^");
        if (fields.length < 24) {
            log.warn("[해외주식 체결] 데이터 필드 부족: {}", fields.length);
            return;
        }

        // 필드 파싱
        // 0:RSYM, 1:SYMB, 2:ZDIV, 3:TYMD, 4:XYMD, 5:XHMS, 6:KYMD, 7:KHMS,
        // 8:OPEN, 9:HIGH, 10:LOW, 11:LAST, 12:SIGN, 13:DIFF, 14:RATE,
        // 15:PBID, 16:PASK, 17:VBID, 18:VASK, 19:EVOL, 20:TVOL, 21:TAMT, 22:BIVL, 23:ASVL, 24:STRN, 25:MTYP

        String stockCode = fields.length > 1 ? fields[1] : "";
        String currentPrice = fields.length > 11 ? fields[11] : "0";  // LAST
        String changeRate = fields.length > 14 ? fields[14] : "0";    // RATE
        String volume = fields.length > 20 ? fields[20] : "0";        // TVOL
        String executionTime = fields.length > 7 ? fields[7] : "";    // KHMS
        String bidPrice1 = fields.length > 15 ? fields[15] : "0";     // PBID
        String askPrice1 = fields.length > 16 ? fields[16] : "0";     // PASK
        String bidQuantity1 = fields.length > 17 ? fields[17] : "0";  // VBID
        String askQuantity1 = fields.length > 18 ? fields[18] : "0";  // VASK

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

        // exchangeCode 조회 및 캐시 저장
        String exchangeCode = stockToExchangeMap.get(stockCode.toUpperCase());
        if (exchangeCode != null) {
            cacheService.saveQuote(exchangeCode, stockCode, quoteData);
            log.debug("[해외주식 체결] 캐시 저장 완료: {}:{} - 현재가: {}, 등락률: {}",
                    exchangeCode, stockCode, currentPrice, changeRate);
        } else {
            log.warn("[해외주식 체결] exchangeCode를 찾을 수 없음: {}", stockCode);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        log.warn("[해외주식 체결 WebSocket] 연결 종료: {}", status);
        this.session = null;
        // 자동 재연결 시도
        try {
            Thread.sleep(3000);
            connect();
        } catch (Exception e) {
            log.error("[해외주식 체결 WebSocket] 재연결 실패", e);
        }
    }

    public void disconnect() {
        if (session != null && session.isOpen()) {
            try {
                session.close();
                log.info("[해외주식 체결 WebSocket] 연결 종료");
            } catch (Exception e) {
                log.error("[해외주식 체결 WebSocket] 종료 실패", e);
            }
        }
    }

    public Set<String> getSubscribedStocks() {
        return subscribedStocks;
    }
}

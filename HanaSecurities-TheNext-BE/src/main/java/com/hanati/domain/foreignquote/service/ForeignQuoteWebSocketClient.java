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
 * 해외주식 실시간 호가 WebSocket 클라이언트 (HDFSASP0)
 * - 미국: 실시간 무료 (매수/매도 각 1호가)
 * - 아시아: 실시간 유료 (10호가)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ForeignQuoteWebSocketClient extends TextWebSocketHandler {

    private final WebSocketApprovalService approvalService;
    private final ForeignQuoteCacheService cacheService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private WebSocketSession session;
    private final Set<String> subscribedStocks = ConcurrentHashMap.newKeySet();
    // stockCode -> exchangeCode 매핑 (캐시 저장용)
    private final Map<String, String> stockToExchangeMap = new ConcurrentHashMap<>();

    private static final String KIS_WS_URL = "ws://ops.koreainvestment.com:21000";
    private static final String TR_ID = "HDFSASP0";  // 실시간호가

    /**
     * WebSocket 연결 (필요 시에만)
     */
    private synchronized void connect() {
        try {
            if (session != null && session.isOpen()) {
                return;
            }

            log.info("[해외주식 WebSocket] 연결 시도...");
            StandardWebSocketClient client = new StandardWebSocketClient();
            session = client.execute(this, KIS_WS_URL).get();
            log.info("[해외주식 WebSocket] 연결 성공");
        } catch (Exception e) {
            log.error("[해외주식 WebSocket] 연결 실패", e);
            throw new RuntimeException("해외주식 WebSocket 연결 실패", e);
        }
    }

    /**
     * 해외주식 호가 구독
     * @param exchangeCode 거래소코드 (NYS, NAS, AMS, HKS, TSE 등)
     * @param stockCode 종목코드
     */
    public void subscribeQuote(String exchangeCode, String stockCode) {
        String key = exchangeCode + ":" + stockCode;

        if (subscribedStocks.contains(key)) {
            log.info("[해외주식 호가 구독] 이미 구독 중: {}", key);
            return;
        }

        try {
            ensureConnection();

            // tr_key 생성: 미국 야간거래(D), 주간거래(R), 아시아(R)
            String trKey = buildTrKey(exchangeCode, stockCode);

            log.info("[해외주식 호가 구독] TR_KEY: {} (거래소: {}, 종목: {})",
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
            log.info("[해외주식 호가 구독 요청] {}", message);

            synchronized (session) {
                session.sendMessage(new TextMessage(message));
            }

            subscribedStocks.add(key);
            stockToExchangeMap.put(stockCode.toUpperCase(), exchangeCode.toUpperCase());
            log.info("[해외주식 호가 구독 완료] {}", key);

        } catch (Exception e) {
            log.error("[해외주식 호가 구독 실패] {}", key, e);
        }
    }

    /**
     * 해외주식 호가 구독 해제
     * @param exchangeCode 거래소코드
     * @param stockCode 종목코드
     */
    public void unsubscribeQuote(String exchangeCode, String stockCode) {
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
            log.info("[해외주식 호가 구독 해제] {}", key);

        } catch (Exception e) {
            log.error("[해외주식 호가 구독 해제 실패] {}", key, e);
        }
    }

    /**
     * TR_KEY 생성 로직 (HDFSASP0)
     * 미국 야간거래 무료: D+시장구분+종목코드 (예: DNASAAPL)
     * 미국 주간거래: R+BAQ/BAY/BAA+종목코드 (예: RBAQAAPL)
     * 아시아 유료: R+시장구분+종목코드 (예: RHKS00003)
     */
    private String buildTrKey(String exchangeCode, String stockCode) {
        String prefix;
        String marketCode;

        // 거래소별 시장코드 매핑
        switch (exchangeCode.toUpperCase()) {
            case "NYS":
            case "NYSE":
                prefix = "D";  // 미국 야간거래 무료시세
                marketCode = "NYS";
                break;
            case "NAS":
            case "NASD":
                prefix = "D";
                marketCode = "NAS";
                break;
            case "AMS":
            case "AMEX":
                prefix = "D";
                marketCode = "AMS";
                break;
            case "HKS":
            case "SEHK":
                prefix = "R";  // 아시아 유료 실시간
                marketCode = "HKS";
                break;
            case "TSE":
            case "TKSE":
                prefix = "R";
                marketCode = "TSE";
                break;
            case "SHS":  // 상해
                prefix = "R";
                marketCode = "SHS";
                break;
            case "SZS":  // 심천
                prefix = "R";
                marketCode = "SZS";
                break;
            case "HSX":  // 호치민
                prefix = "R";
                marketCode = "HSX";
                break;
            case "HNX":  // 하노이
                prefix = "R";
                marketCode = "HNX";
                break;
            default:
                prefix = "D";
                marketCode = exchangeCode.toUpperCase();
        }

        return prefix + marketCode + stockCode.toUpperCase();
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
                    log.info("[해외주식 WebSocket] 실시간 데이터 수신 - TR_ID: {}", trId);

                    if (TR_ID.equals(trId)) {
                        parseRealtimeQuoteData(payload);
                    }
                }
            }

        } catch (Exception e) {
            log.error("[해외주식 WebSocket] 메시지 처리 실패", e);
        }
    }

    /**
     * 실시간 호가 데이터 파싱 (HDFSASP0)
     * Response 형식: 0|HDFSASP0|001|RSYM^SYMB^ZDIV^...^PBID1^PASK1^VBID1^VASK1^...
     */
    private void parseRealtimeQuoteData(String data) {
        String[] parts = data.split("\\|");
        if (parts.length < 4) {
            return;
        }

        String[] fields = parts[3].split("\\^");
        if (fields.length < 20) {
            log.warn("[해외주식 호가] 데이터 필드 부족: {}", fields.length);
            return;
        }

        // 주요 필드 파싱
        // 0:RSYM, 1:SYMB, 2:ZDIV, 3:XYMD, 4:XHMS, 5:KYMD, 6:KHMS,
        // 7:BVOL, 8:AVOL, 9:BDVL, 10:ADVL, 11:PBID1, 12:PASK1, 13:VBID1, 14:VASK1

        String stockCode = fields.length > 1 ? fields[1] : "";
        String executionTime = fields.length > 6 ? fields[6] : "";
        String bidPrice1 = fields.length > 11 ? fields[11] : "0";
        String askPrice1 = fields.length > 12 ? fields[12] : "0";
        String bidQuantity1 = fields.length > 13 ? fields[13] : "0";
        String askQuantity1 = fields.length > 14 ? fields[14] : "0";

        // 현재가는 매수/매도 중간값으로 추정
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
            log.info("[해외주식 호가] 캐시 저장 완료: {}:{}", exchangeCode, stockCode);
        } else {
            log.warn("[해외주식 호가] exchangeCode를 찾을 수 없음: {}", stockCode);
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
            return bid;  // 계산 실패 시 매수호가 반환
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

    /**
     * WebSocket 연결 종료
     */
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

    /**
     * 구독 중인 종목 목록 반환
     */
    public Set<String> getSubscribedStocks() {
        return subscribedStocks;
    }
}

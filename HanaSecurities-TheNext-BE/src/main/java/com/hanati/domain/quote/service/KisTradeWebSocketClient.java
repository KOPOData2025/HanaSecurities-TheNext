package com.hanati.domain.quote.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanati.common.service.WebSocketApprovalService;
import com.hanati.domain.quote.dto.KisWebSocketRequest;
import com.hanati.domain.quote.dto.RealtimeTradeData;
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
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class KisTradeWebSocketClient extends TextWebSocketHandler {

    private final WebSocketApprovalService approvalService;
    private final TradeCacheService cacheService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private WebSocketSession session;
    private final Set<String> subscribedStocks = ConcurrentHashMap.newKeySet();

    private static final String KIS_WS_URL = "ws://ops.koreainvestment.com:21000";

    /**
     * 종목 구독
     */
    public void subscribe(String stockCode) {
        if (subscribedStocks.contains(stockCode)) {
            log.info("이미 구독 중인 종목: {}", stockCode);
            return;
        }

        try {
            if (session == null || !session.isOpen()) {
                log.info("KIS Trade WebSocket 연결 시도...");
                connect();
            }

            String approvalKey = approvalService.getWebSocketApprovalKey();
            log.info("WebSocket 승인키 발급 완료");

            KisWebSocketRequest request = KisWebSocketRequest.builder()
                    .header(KisWebSocketRequest.Header.builder()
                            .approvalKey(approvalKey)
                            .custtype("P")
                            .trType("1")  // 1: 등록
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
            log.info("=== KIS Trade WebSocket 전송 ===");
            log.info("구독 요청 메시지: {}", message);

            session.sendMessage(new TextMessage(message));

            subscribedStocks.add(stockCode);
            log.info("종목 체결가 구독 완료: {}", stockCode);

        } catch (Exception e) {
            log.error("종목 체결가 구독 실패: {}", stockCode, e);
        }
    }

    /**
     * 종목 구독 해제
     */
    public void unsubscribe(String stockCode) {
        if (!subscribedStocks.contains(stockCode)) {
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
            session.sendMessage(new TextMessage(message));

            subscribedStocks.remove(stockCode);
            log.info("종목 체결가 구독 해제: {}", stockCode);

        } catch (Exception e) {
            log.error("종목 체결가 구독 해제 실패: {}", stockCode, e);
        }
    }

    /**
     * WebSocket 연결
     */
    private void connect() {
        try {
            StandardWebSocketClient client = new StandardWebSocketClient();
            session = client.execute(this, KIS_WS_URL).get();
            log.info("KIS Trade WebSocket 연결 성공");
        } catch (Exception e) {
            log.error("KIS Trade WebSocket 연결 실패", e);
            throw new RuntimeException("KIS Trade WebSocket 연결 실패", e);
        }
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        try {
            String payload = message.getPayload();
            log.info("=== KIS Trade WebSocket 수신 ===");
            log.info("메시지: {}", payload);

            // JSON 응답 (연결 확인)
            if (payload.startsWith("{")) {
                log.info("KIS Trade WebSocket JSON 응답: {}", payload);
                return;
            }

            // 실시간 데이터 (| 구분)
            if (payload.contains("|")) {
                log.info("KIS Trade WebSocket 실시간 체결가 데이터 수신");
                parseRealtimeTradeData(payload);
            }

        } catch (Exception e) {
            log.error("메시지 처리 실패", e);
        }
    }

    /**
     * 실시간 체결가 데이터 파싱
     * 형식: 암호화유무|TR_ID|데이터건수|데이터(^구분)
     */
    private void parseRealtimeTradeData(String data) {
        String[] parts = data.split("\\|");
        if (parts.length < 4) {
            return;
        }

        String[] fields = parts[3].split("\\^");
        if (fields.length < 40) {
            log.warn("데이터 필드 부족: {}", fields.length);
            return;
        }

        // CSV 명세서 순서대로 파싱
        RealtimeTradeData tradeData = RealtimeTradeData.builder()
                .stockCode(fields[0])                    // MKSC_SHRN_ISCD
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .tradeTime(fields[1])                    // STCK_CNTG_HOUR
                .currentPrice(fields[2])                 // STCK_PRPR
                .priceChangeSign(fields[3])              // PRDY_VRSS_SIGN
                .priceChange(fields[4])                  // PRDY_VRSS
                .changeRate(fields[5])                   // PRDY_CTRT
                .openPrice(fields[7])                    // STCK_OPRC
                .highPrice(fields[8])                    // STCK_HGPR
                .lowPrice(fields[9])                     // STCK_LWPR
                .askPrice1(fields[10])                   // ASKP1
                .bidPrice1(fields[11])                   // BIDP1
                .tradeVolume(fields[12])                 // CNTG_VOL
                .accumulatedVolume(fields[13])           // ACML_VOL
                .accumulatedAmount(fields[14])           // ACML_TR_PBMN
                .sellCount(fields[15])                   // SELN_CNTG_CSNU
                .buyCount(fields[16])                    // SHNU_CNTG_CSNU
                .tradeStrength(fields[18])               // CTTR
                .totalAskRemain(fields.length > 38 ? fields[38] : "0")  // TOTAL_ASKP_RSQN
                .totalBidRemain(fields.length > 39 ? fields[39] : "0")  // TOTAL_BIDP_RSQN
                .build();

        cacheService.saveTrade(tradeData.getStockCode(), tradeData);
        log.info("실시간 체결가 캐시 저장 완료: {}", tradeData.getStockCode());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        log.warn("KIS Trade WebSocket 연결 종료: {}", status);
        this.session = null;
    }

    /**
     * 애플리케이션 종료 시 WebSocket 연결 종료
     */
    public void disconnect() {
        if (session != null && session.isOpen()) {
            try {
                session.close();
                log.info("KIS Trade WebSocket 연결 종료");
            } catch (Exception e) {
                log.error("Trade WebSocket 종료 실패", e);
            }
        }
    }
}

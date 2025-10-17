package com.hanati.domain.quote.controller;

import com.hanati.domain.quote.dto.RealtimeQuoteResponse;
import com.hanati.domain.quote.service.KisWebSocketClient;
import com.hanati.domain.quote.service.QuoteCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

// STOMP 컨트롤러 - 순수 WebSocket 사용으로 인해 비활성화
//@Controller
//@RequiredArgsConstructor
//@Slf4j
//public class QuoteWebSocketController {
//
//    private final KisWebSocketClient kisWebSocketClient;
//    private final QuoteCacheService cacheService;
//    private final SimpMessagingTemplate messagingTemplate;
//
//    /**
//     * 클라이언트 구독 요청 처리
//     * /app/subscribe/{stockCode}
//     */
//    @MessageMapping("/subscribe/{stockCode}")
//    public void subscribeQuote(@DestinationVariable String stockCode) {
//        log.info("클라이언트 구독 요청: {}", stockCode);
//
//        // KIS WebSocket에 종목 구독
//        kisWebSocketClient.subscribe(stockCode);
//    }
//
//    /**
//     * 클라이언트 구독 해제
//     * /app/unsubscribe/{stockCode}
//     */
//    @MessageMapping("/unsubscribe/{stockCode}")
//    public void unsubscribeQuote(@DestinationVariable String stockCode) {
//        log.info("클라이언트 구독 해제: {}", stockCode);
//
//        // KIS WebSocket 구독 해제
//        kisWebSocketClient.unsubscribe(stockCode);
//    }
//
//    /**
//     * 100ms마다 캐시된 데이터를 클라이언트에게 브로드캐스트
//     */
//    @Scheduled(fixedRate = 100)
//    public void broadcastQuotes() {
//        // 구독 중인 모든 종목의 캐시 데이터를 브로드캐스트
//        // 실제로는 구독 중인 종목 목록을 관리해야 하지만
//        // 여기서는 캐시에서 자동으로 만료된 데이터는 제외됨
//    }
//
//    /**
//     * 10초마다 만료된 캐시 정리
//     */
//    @Scheduled(fixedRate = 10000)
//    public void cleanCache() {
//        cacheService.cleanExpiredCache();
//    }
//
//    /**
//     * 특정 종목의 실시간 호가 데이터를 클라이언트에게 전송
//     */
//    public void sendQuoteToClients(String stockCode, RealtimeQuoteResponse quote) {
//        messagingTemplate.convertAndSend("/topic/quote/" + stockCode, quote);
//    }
//}

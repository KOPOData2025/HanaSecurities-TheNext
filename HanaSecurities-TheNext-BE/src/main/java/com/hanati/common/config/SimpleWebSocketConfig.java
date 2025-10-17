package com.hanati.common.config;

import com.hanati.domain.foreignquote.controller.ForeignQuoteWebSocketHandler;
import com.hanati.domain.gold.controller.GoldQuoteWebSocketHandler;
import com.hanati.domain.gold.controller.GoldTradeWebSocketHandler;
import com.hanati.domain.quote.handler.SimpleQuoteWebSocketHandler;
import com.hanati.domain.quote.handler.SimpleTradeWebSocketHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class SimpleWebSocketConfig implements WebSocketConfigurer {

    private final SimpleQuoteWebSocketHandler simpleQuoteWebSocketHandler;
    private final SimpleTradeWebSocketHandler simpleTradeWebSocketHandler;
    private final ForeignQuoteWebSocketHandler foreignQuoteWebSocketHandler;
    private final GoldTradeWebSocketHandler goldTradeWebSocketHandler;
    private final GoldQuoteWebSocketHandler goldQuoteWebSocketHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // 국내주식 호가 WebSocket 엔드포인트
        registry.addHandler(simpleQuoteWebSocketHandler, "/ws/quote")
                .setAllowedOriginPatterns("*");

        // 국내주식 체결가 WebSocket 엔드포인트
        registry.addHandler(simpleTradeWebSocketHandler, "/ws/trade")
                .setAllowedOriginPatterns("*");

        // 해외주식 호가 WebSocket 엔드포인트
        registry.addHandler(foreignQuoteWebSocketHandler, "/ws/foreign-quote")
                .setAllowedOriginPatterns("*");

        // 금현물 체결 WebSocket 엔드포인트
        registry.addHandler(goldTradeWebSocketHandler, "/ws/gold-trade")
                .setAllowedOriginPatterns("*");

        // 금현물 호가 WebSocket 엔드포인트
        registry.addHandler(goldQuoteWebSocketHandler, "/ws/gold-quote")
                .setAllowedOriginPatterns("*");
    }
}

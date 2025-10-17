package com.hanati.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

// STOMP WebSocket 설정 - 순수 WebSocket 사용으로 인해 비활성화
//@Configuration
//@EnableWebSocketMessageBroker
//public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
//
//    @Override
//    public void configureMessageBroker(MessageBrokerRegistry config) {
//        // 클라이언트로 메시지를 보낼 때 사용할 prefix
//        config.enableSimpleBroker("/topic");
//
//        // 클라이언트에서 메시지를 받을 때 사용할 prefix
//        config.setApplicationDestinationPrefixes("/app");
//    }
//
//    @Override
//    public void registerStompEndpoints(StompEndpointRegistry registry) {
//        // WebSocket 연결 엔드포인트 (SockJS - 브라우저용)
//        registry.addEndpoint("/ws-quote")
//                .setAllowedOriginPatterns("*")
//                .withSockJS();
//
//        // WebSocket 연결 엔드포인트 (순수 WebSocket - Postman용)
//        registry.addEndpoint("/ws-quote")
//                .setAllowedOriginPatterns("*");
//    }
//}

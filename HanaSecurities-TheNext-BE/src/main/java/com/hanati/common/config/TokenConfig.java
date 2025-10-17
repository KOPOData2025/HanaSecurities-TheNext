package com.hanati.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Getter;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = "api.token")
@Getter
@Setter
public class TokenConfig {

    // 한국투자증권 설정
    private String appKey;
    private String appSecret;
    private String baseUrl;
    private String tokenPath;
    private String webSocketTokenPath;
    private String accountNumber;
    private String accountProductCode;

    // 키움증권 설정
    private String kiwoomAppKey;
    private String kiwoomSecretKey;
    private String kiwoomBaseUrl;
    private String kiwoomTokenPath;

    // 네이버 API 설정
    private String naverClientId;
    private String naverClientSecret;

    // 한국투자증권 URL
    public String getTokenUrl() {
        return baseUrl + tokenPath;
    }

    public String getWebSocketTokenUrl() {
        return baseUrl + webSocketTokenPath;
    }

    // 키움증권 URL
    public String getKiwoomTokenUrl() {
        return kiwoomBaseUrl + kiwoomTokenPath;
    }
}
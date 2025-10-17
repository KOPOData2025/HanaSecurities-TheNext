package com.hanati.common.dto;

import lombok.*;
import com.fasterxml.jackson.annotation.JsonProperty;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WebSocketTokenRequest {

    @JsonProperty("grant_type")
    private String grantType;  // 권한부여타입

    @JsonProperty("appkey")
    private String appKey;     // 앱키

    @JsonProperty("secretkey")
    private String secretKey;  // 시크릿키
}
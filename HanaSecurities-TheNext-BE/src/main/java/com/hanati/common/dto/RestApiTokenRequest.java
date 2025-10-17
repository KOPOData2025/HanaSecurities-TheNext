package com.hanati.common.dto;

import lombok.*;
import com.fasterxml.jackson.annotation.JsonProperty;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RestApiTokenRequest {

    @JsonProperty("grant_type")
    private String grantType;  // 권한부여 Type

    @JsonProperty("appkey")
    private String appKey;     // 앱키

    @JsonProperty("appsecret")
    private String appSecret;  // 앱시크릿키
}
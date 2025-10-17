package com.hanati.common.dto;

import lombok.*;
import com.fasterxml.jackson.annotation.JsonProperty;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KiwoomTokenRequest {

    @JsonProperty("grant_type")
    private String grantType;  // 권한부여 Type

    @JsonProperty("appkey")
    private String appKey;     // 앱키

    @JsonProperty("secretkey")
    private String secretKey;  // 시크릿키
}
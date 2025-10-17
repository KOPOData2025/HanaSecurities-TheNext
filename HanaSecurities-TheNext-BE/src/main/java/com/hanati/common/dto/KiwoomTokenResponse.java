package com.hanati.common.dto;

import lombok.*;
import com.fasterxml.jackson.annotation.JsonProperty;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class KiwoomTokenResponse {

    @JsonProperty("expires_dt")
    private String expiresDt;  // 만료일시

    @JsonProperty("token_type")
    private String tokenType;  // 토큰 타입

    @JsonProperty("token")
    private String token;      // 토큰
}
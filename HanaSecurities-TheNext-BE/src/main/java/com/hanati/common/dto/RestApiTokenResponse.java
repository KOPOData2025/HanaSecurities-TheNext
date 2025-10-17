package com.hanati.common.dto;

import lombok.*;
import com.fasterxml.jackson.annotation.JsonProperty;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class RestApiTokenResponse {

    @JsonProperty("access_token")
    private String accessToken;  // 접근토큰

    @JsonProperty("token_type")
    private String tokenType;    // 접근토큰유형

    @JsonProperty("expires_in")
    private Long expiresIn;      // 접근토큰 유효기간

    @JsonProperty("access_token_token_expired")
    private String accessTokenTokenExpired;  // 접근토큰 유효기간(일시표시)
}
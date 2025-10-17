package com.hanati.common.dto;

import lombok.*;
import com.fasterxml.jackson.annotation.JsonProperty;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class WebSocketTokenResponse {

    @JsonProperty("approval_key")
    private String approvalKey;  // 웹소켓 접속키
}
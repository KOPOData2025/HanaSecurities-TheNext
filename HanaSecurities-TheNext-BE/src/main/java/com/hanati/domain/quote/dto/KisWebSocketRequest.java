package com.hanati.domain.quote.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KisWebSocketRequest {

    @JsonProperty("header")
    private Header header;

    @JsonProperty("body")
    private Body body;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Header {
        @JsonProperty("approval_key")
        private String approvalKey;

        @JsonProperty("custtype")
        private String custtype;  // P: 개인

        @JsonProperty("tr_type")
        private String trType;  // 1: 등록, 2: 해제

        @JsonProperty("content-type")
        private String contentType;  // utf-8
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Body {
        @JsonProperty("input")
        private Input input;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Input {
        @JsonProperty("tr_id")
        private String trId;  // H0STASP0: 주식호가

        @JsonProperty("tr_key")
        private String trKey;  // 종목번호 (6자리)
    }
}

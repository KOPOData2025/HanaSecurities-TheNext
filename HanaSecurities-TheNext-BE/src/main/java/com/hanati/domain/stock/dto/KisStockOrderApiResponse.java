package com.hanati.domain.stock.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KisStockOrderApiResponse {

    @JsonProperty("rt_cd")
    private String rtCd;  // 성공 실패 여부

    @JsonProperty("msg_cd")
    private String msgCd;  // 응답코드

    @JsonProperty("msg1")
    private String msg1;  // 응답메시지

    @JsonProperty("output")
    private Output output;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Output {
        @JsonProperty("KRX_FWDG_ORD_ORGNO")
        private String krxFwdgOrdOrgno;  // 거래소코드

        @JsonProperty("ODNO")
        private String odno;  // 주문번호

        @JsonProperty("ORD_TMD")
        private String ordTmd;  // 주문시간
    }
}

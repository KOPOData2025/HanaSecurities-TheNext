package com.hanati.domain.stock.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KisStockInfoApiResponse {

    @JsonProperty("rt_cd")
    private String rtCd;

    @JsonProperty("msg_cd")
    private String msgCd;

    @JsonProperty("msg1")
    private String msg1;

    @JsonProperty("output")
    private Output output;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Output {

        @JsonProperty("pdno")
        private String pdno;  // 종목코드

        @JsonProperty("prdt_name")
        private String prdtName;  // 종목명

        @JsonProperty("cptt_trad_tr_psbl_yn")
        private String cpttTradTrPsblYn;  // NXT 거래종목여부 (Y/N)

        @JsonProperty("nxt_tr_stop_yn")
        private String nxtTrStopYn;  // NXT 거래정지여부 (Y/N)

        @JsonProperty("prdy_vrss_sign")
        private String prdyVrssSign;  // 전일대비부호

        @JsonProperty("prdy_vrss")
        private String prdyVrss;  // 전일대비

        @JsonProperty("prdy_ctrt")
        private String prdyCtrt;  // 전일대비율

        @JsonProperty("stck_prpr")
        private String stckPrpr;  // 현재가
    }
}

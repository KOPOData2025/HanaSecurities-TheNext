package com.hanati.domain.stock.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 한국투자증권 재무비율 API 응답 DTO (FHKST66430300)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KisFinancialRatioApiResponse {

    @JsonProperty("rt_cd")
    private String rtCd;

    @JsonProperty("msg_cd")
    private String msgCd;

    @JsonProperty("msg1")
    private String msg1;

    @JsonProperty("output")
    private List<FinancialRatioData> output;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FinancialRatioData {
        @JsonProperty("stac_yymm")
        private String stacYymm;              // 결산 년월

        @JsonProperty("grs")
        private String grs;                   // 매출액 증가율

        @JsonProperty("bsop_prfi_inrt")
        private String bsopPrfiInrt;          // 영업 이익 증가율

        @JsonProperty("ntin_inrt")
        private String ntinInrt;              // 순이익 증가율

        @JsonProperty("roe_val")
        private String roeVal;                // ROE 값

        @JsonProperty("eps")
        private String eps;                   // EPS

        @JsonProperty("sps")
        private String sps;                   // 주당매출액

        @JsonProperty("bps")
        private String bps;                   // BPS

        @JsonProperty("rsrv_rate")
        private String rsrvRate;              // 유보 비율

        @JsonProperty("lblt_rate")
        private String lbltRate;              // 부채 비율
    }
}

package com.hanati.domain.stock.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 한국투자증권 손익계산서 API 응답 DTO (FHKST66430200)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KisIncomeStatementApiResponse {

    @JsonProperty("rt_cd")
    private String rtCd;

    @JsonProperty("msg_cd")
    private String msgCd;

    @JsonProperty("msg1")
    private String msg1;

    @JsonProperty("output")
    private List<IncomeStatementData> output;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class IncomeStatementData {
        @JsonProperty("stac_yymm")
        private String stacYymm;              // 결산 년월

        @JsonProperty("sale_account")
        private String saleAccount;           // 매출액

        @JsonProperty("sale_cost")
        private String saleCost;              // 매출 원가

        @JsonProperty("sale_totl_prfi")
        private String saleTotlPrfi;          // 매출 총 이익

        @JsonProperty("depr_cost")
        private String deprCost;              // 감가상각비 (99.99 = 미제공)

        @JsonProperty("sell_mang")
        private String sellMang;              // 판매 및 관리비 (99.99 = 미제공)

        @JsonProperty("bsop_prti")
        private String bsopPrti;              // 영업 이익

        @JsonProperty("bsop_non_ernn")
        private String bsopNonErnn;           // 영업 외 수익 (99.99 = 미제공)

        @JsonProperty("bsop_non_expn")
        private String bsopNonExpn;           // 영업 외 비용 (99.99 = 미제공)

        @JsonProperty("op_prfi")
        private String opPrfi;                // 경상 이익

        @JsonProperty("spec_prfi")
        private String specPrfi;              // 특별 이익

        @JsonProperty("spec_loss")
        private String specLoss;              // 특별 손실

        @JsonProperty("thtr_ntin")
        private String thtrNtin;              // 당기순이익
    }
}

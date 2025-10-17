package com.hanati.domain.stock.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 한국투자증권 대차대조표 API 응답 DTO (FHKST66430100)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KisBalanceSheetApiResponse {

    @JsonProperty("rt_cd")
    private String rtCd;

    @JsonProperty("msg_cd")
    private String msgCd;

    @JsonProperty("msg1")
    private String msg1;

    @JsonProperty("output")
    private List<BalanceSheetData> output;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BalanceSheetData {
        @JsonProperty("stac_yymm")
        private String stacYymm;              // 결산 년월

        @JsonProperty("cras")
        private String cras;                  // 유동자산

        @JsonProperty("fxas")
        private String fxas;                  // 고정자산

        @JsonProperty("total_aset")
        private String totalAset;             // 자산총계

        @JsonProperty("flow_lblt")
        private String flowLblt;              // 유동부채

        @JsonProperty("fix_lblt")
        private String fixLblt;               // 고정부채

        @JsonProperty("total_lblt")
        private String totalLblt;             // 부채총계

        @JsonProperty("cpfn")
        private String cpfn;                  // 자본금

        @JsonProperty("cfp_surp")
        private String cfpSurp;               // 자본 잉여금 (99.99 = 미제공)

        @JsonProperty("prfi_surp")
        private String prfiSurp;              // 이익 잉여금 (99.99 = 미제공)

        @JsonProperty("total_cptl")
        private String totalCptl;             // 자본총계
    }
}

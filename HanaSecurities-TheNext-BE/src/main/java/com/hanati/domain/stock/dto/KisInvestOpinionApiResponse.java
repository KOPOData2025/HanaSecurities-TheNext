package com.hanati.domain.stock.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 한국투자증권 종목투자의견 API 응답 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KisInvestOpinionApiResponse {

    @JsonProperty("rt_cd")
    private String rtCd;

    @JsonProperty("msg_cd")
    private String msgCd;

    @JsonProperty("msg1")
    private String msg1;

    @JsonProperty("output")
    private List<InvestOpinionItem> output;

    /**
     * 투자의견 상세 항목
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InvestOpinionItem {
        @JsonProperty("stck_bsop_date")
        private String stckBsopDate; // 주식영업일자

        @JsonProperty("invt_opnn")
        private String invtOpnn; // 투자의견

        @JsonProperty("invt_opnn_cls_code")
        private String invtOpnnClsCode; // 투자의견구분코드

        @JsonProperty("rgbf_invt_opnn")
        private String rgbfInvtOpnn; // 직전투자의견

        @JsonProperty("rgbf_invt_opnn_cls_code")
        private String rgbfInvtOpnnClsCode; // 직전투자의견구분코드

        @JsonProperty("mbcr_name")
        private String mbcrName; // 회원사명

        @JsonProperty("hts_goal_prc")
        private String htsGoalPrc; // HTS목표가격

        @JsonProperty("stck_prdy_clpr")
        private String stckPrdyClpr; // 주식전일종가

        @JsonProperty("stck_nday_esdg")
        private String stckNdayEsdg; // 주식N일괴리도

        @JsonProperty("nday_dprt")
        private String ndayDprt; // N일괴리율

        @JsonProperty("stft_esdg")
        private String stftEsdg; // 주식선물괴리도

        @JsonProperty("dprt")
        private String dprt; // 괴리율
    }
}

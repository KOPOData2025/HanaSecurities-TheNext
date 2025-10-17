package com.hanati.domain.stock.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class KisIntradayChartApiResponse {

    @JsonProperty("rt_cd")
    private String rtCd;

    @JsonProperty("msg_cd")
    private String msgCd;

    @JsonProperty("msg1")
    private String msg1;

    @JsonProperty("output1")
    private Output1 output1;

    @JsonProperty("output2")
    private List<ChartData> output2;

    @Data
    public static class Output1 {

        @JsonProperty("prdy_vrss")
        private String prdyVrss;  // 전일 대비

        @JsonProperty("prdy_vrss_sign")
        private String prdyVrssSign;  // 전일 대비 부호

        @JsonProperty("prdy_ctrt")
        private String prdyCtrt;  // 전일 대비율

        @JsonProperty("stck_prdy_clpr")
        private String stckPrdyClpr;  // 전일대비 종가

        @JsonProperty("acml_vol")
        private String acmlVol;  // 누적 거래량

        @JsonProperty("acml_tr_pbmn")
        private String acmlTrPbmn;  // 누적 거래대금

        @JsonProperty("hts_kor_isnm")
        private String htsKorIsnm;  // 한글 종목명

        @JsonProperty("stck_prpr")
        private String stckPrpr;  // 주식 현재가
    }

    @Data
    public static class ChartData {

        @JsonProperty("stck_bsop_date")
        private String stckBsopDate;  // 주식 영업일자

        @JsonProperty("stck_cntg_hour")
        private String stckCntgHour;  // 주식 체결시간

        @JsonProperty("stck_prpr")
        private String stckPrpr;  // 주식 현재가

        @JsonProperty("stck_oprc")
        private String stckOprc;  // 주식 시가

        @JsonProperty("stck_hgpr")
        private String stckHgpr;  // 주식 최고가

        @JsonProperty("stck_lwpr")
        private String stckLwpr;  // 주식 최저가

        @JsonProperty("cntg_vol")
        private String cntgVol;  // 체결 거래량

        @JsonProperty("acml_tr_pbmn")
        private String acmlTrPbmn;  // 누적 거래대금
    }
}
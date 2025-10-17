package com.hanati.domain.ranking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class KisFluctuationRankApiResponse {

    @JsonProperty("rt_cd")
    private String rtCd;

    @JsonProperty("msg_cd")
    private String msgCd;

    @JsonProperty("msg1")
    private String msg1;

    @JsonProperty("output")
    private List<FluctuationRankData> output;

    @Data
    public static class FluctuationRankData {

        @JsonProperty("stck_shrn_iscd")
        private String stckShrnIscd;  // 주식 단축 종목코드

        @JsonProperty("data_rank")
        private String dataRank;  // 데이터 순위

        @JsonProperty("hts_kor_isnm")
        private String htsKorIsnm;  // HTS 한글 종목명

        @JsonProperty("stck_prpr")
        private String stckPrpr;  // 주식 현재가

        @JsonProperty("prdy_vrss")
        private String prdyVrss;  // 전일 대비

        @JsonProperty("prdy_vrss_sign")
        private String prdyVrssSign;  // 전일 대비 부호

        @JsonProperty("prdy_ctrt")
        private String prdyCtrt;  // 전일 대비율

        @JsonProperty("acml_vol")
        private String acmlVol;  // 누적 거래량

        @JsonProperty("stck_hgpr")
        private String stckHgpr;  // 주식 최고가

        @JsonProperty("hgpr_hour")
        private String hgprHour;  // 최고가 시간

        @JsonProperty("acml_hgpr_date")
        private String acmlHgprDate;  // 누적 최고가 일자

        @JsonProperty("stck_lwpr")
        private String stckLwpr;  // 주식 최저가

        @JsonProperty("lwpr_hour")
        private String lwprHour;  // 최저가 시간

        @JsonProperty("acml_lwpr_date")
        private String acmlLwprDate;  // 누적 최저가 일자

        @JsonProperty("lwpr_vrss_prpr_rate")
        private String lwprVrssPrprRate;  // 최저가 대비 현재가 비율

        @JsonProperty("dsgt_date_clpr_vrss_prpr_rate")
        private String dsgtDateClprVrssPrprRate;  // 지정 일자 종가 대비 현재가 비율

        @JsonProperty("cnnt_ascn_dynu")
        private String cnntAscnDynu;  // 연속 상승 일수

        @JsonProperty("hgpr_vrss_prpr_rate")
        private String hgprVrssPrprRate;  // 최고가 대비 현재가 비율

        @JsonProperty("cnnt_down_dynu")
        private String cnntDownDynu;  // 연속 하락 일수

        @JsonProperty("oprc_vrss_prpr_sign")
        private String oprcVrssPrprSign;  // 시가2 대비 현재가 부호

        @JsonProperty("oprc_vrss_prpr")
        private String oprcVrssPrpr;  // 시가2 대비 현재가

        @JsonProperty("oprc_vrss_prpr_rate")
        private String oprcVrssPrprRate;  // 시가2 대비 현재가 비율

        @JsonProperty("prd_rsfl")
        private String prdRsfl;  // 기간 등락

        @JsonProperty("prd_rsfl_rate")
        private String prdRsflRate;  // 기간 등락 비율
    }
}
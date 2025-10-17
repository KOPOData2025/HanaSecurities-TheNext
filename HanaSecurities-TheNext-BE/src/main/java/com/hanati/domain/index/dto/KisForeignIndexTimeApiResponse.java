package com.hanati.domain.index.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class KisForeignIndexTimeApiResponse {

    @JsonProperty("rt_cd")
    private String rtCd;

    @JsonProperty("msg_cd")
    private String msgCd;

    @JsonProperty("msg1")
    private String msg1;

    @JsonProperty("output1")
    private Output1 output1;

    @JsonProperty("output2")
    private List<TimeData> output2;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Output1 {
        @JsonProperty("ovrs_nmix_prdy_vrss")
        private String ovrsNmixPrdyVrss;  // 전일 대비 변동

        @JsonProperty("prdy_vrss_sign")
        private String prdyVrssSign;  // 전일 대비 부호

        @JsonProperty("prdy_ctrt")
        private String prdyCtrt;  // 전일 대비율

        @JsonProperty("ovrs_nmix_prdy_clpr")
        private String ovrsNmixPrdyClpr;  // 전일 종가

        @JsonProperty("acml_vol")
        private String acmlVol;  // 누적 거래량

        @JsonProperty("hts_kor_isnm")
        private String htsKorIsnm;  // 한글 종목명

        @JsonProperty("ovrs_nmix_prpr")
        private String ovrsNmixPrpr;  // 현재가

        @JsonProperty("stck_shrn_iscd")
        private String stckShrnIscd;  // 종목 단축 코드

        @JsonProperty("ovrs_prod_oprc")
        private String ovrsProdOprc;  // 시가

        @JsonProperty("ovrs_prod_hgpr")
        private String ovrsProdHgpr;  // 고가

        @JsonProperty("ovrs_prod_lwpr")
        private String ovrsProdLwpr;  // 저가
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class TimeData {

        @JsonProperty("stck_bsop_date")
        private String stckBsopDate;  // 영업일자

        @JsonProperty("stck_cntg_hour")
        private String stckCntgHour;  // 체결시간

        @JsonProperty("optn_prpr")
        private String optnPrpr;  // 현재가

        @JsonProperty("optn_oprc")
        private String optnOprc;  // 시가

        @JsonProperty("optn_hgpr")
        private String optnHgpr;  // 고가

        @JsonProperty("optn_lwpr")
        private String optnLwpr;  // 저가

        @JsonProperty("cntg_vol")
        private String cntgVol;  // 체결량
    }
}
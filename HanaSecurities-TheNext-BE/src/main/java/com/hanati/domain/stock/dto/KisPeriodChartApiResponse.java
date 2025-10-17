package com.hanati.domain.stock.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class KisPeriodChartApiResponse {

    @JsonProperty("rt_cd")
    private String rtCd;

    @JsonProperty("msg_cd")
    private String msgCd;

    @JsonProperty("msg1")
    private String msg1;

    @JsonProperty("output1")
    private Output1 output1;

    @JsonProperty("output2")
    private List<PeriodData> output2;

    @Data
    public static class Output1 {

        @JsonProperty("prdy_vrss")
        private String prdyVrss;  // 전일 대비

        @JsonProperty("prdy_vrss_sign")
        private String prdyVrssSign;  // 전일 대비 부호

        @JsonProperty("prdy_ctrt")
        private String prdyCtrt;  // 전일 대비율

        @JsonProperty("stck_prdy_clpr")
        private String stckPrdyClpr;  // 주식 전일 종가

        @JsonProperty("acml_vol")
        private String acmlVol;  // 누적 거래량

        @JsonProperty("acml_tr_pbmn")
        private String acmlTrPbmn;  // 누적 거래 대금

        @JsonProperty("hts_kor_isnm")
        private String htsKorIsnm;  // HTS 한글 종목명

        @JsonProperty("stck_prpr")
        private String stckPrpr;  // 주식 현재가

        @JsonProperty("stck_shrn_iscd")
        private String stckShrnIscd;  // 주식 단축 종목코드

        @JsonProperty("prdy_vol")
        private String prdyVol;  // 전일 거래량

        @JsonProperty("stck_mxpr")
        private String stckMxpr;  // 주식 상한가

        @JsonProperty("stck_llam")
        private String stckLlam;  // 주식 하한가

        @JsonProperty("stck_oprc")
        private String stckOprc;  // 주식 시가2

        @JsonProperty("stck_hgpr")
        private String stckHgpr;  // 주식 최고가

        @JsonProperty("stck_lwpr")
        private String stckLwpr;  // 주식 최저가

        @JsonProperty("stck_prdy_oprc")
        private String stckPrdyOprc;  // 주식 전일 시가

        @JsonProperty("stck_prdy_hgpr")
        private String stckPrdyHgpr;  // 주식 전일 최고가

        @JsonProperty("stck_prdy_lwpr")
        private String stckPrdyLwpr;  // 주식 전일 최저가

        @JsonProperty("askp")
        private String askp;  // 매도호가

        @JsonProperty("bidp")
        private String bidp;  // 매수호가

        @JsonProperty("prdy_vrss_vol")
        private String prdyVrssVol;  // 전일 대비 거래량

        @JsonProperty("vol_tnrt")
        private String volTnrt;  // 거래량 회전율

        @JsonProperty("stck_fcam")
        private String stckFcam;  // 주식 액면가

        @JsonProperty("lstn_stcn")
        private String lstnStcn;  // 상장 주수

        @JsonProperty("cpfn")
        private String cpfn;  // 자본금

        @JsonProperty("hts_avls")
        private String htsAvls;  // HTS 시가총액

        @JsonProperty("per")
        private String per;  // PER

        @JsonProperty("eps")
        private String eps;  // EPS

        @JsonProperty("pbr")
        private String pbr;  // PBR

        @JsonProperty("itewhol_loan_rmnd_ratem")
        private String itewholLoanRmndRatem;  // 전체 융자 잔고 비율
    }

    @Data
    public static class PeriodData {

        @JsonProperty("stck_bsop_date")
        private String stckBsopDate;  // 주식 영업 일자

        @JsonProperty("stck_clpr")
        private String stckClpr;  // 주식 종가

        @JsonProperty("stck_oprc")
        private String stckOprc;  // 주식 시가2

        @JsonProperty("stck_hgpr")
        private String stckHgpr;  // 주식 최고가

        @JsonProperty("stck_lwpr")
        private String stckLwpr;  // 주식 최저가

        @JsonProperty("acml_vol")
        private String acmlVol;  // 누적 거래량

        @JsonProperty("acml_tr_pbmn")
        private String acmlTrPbmn;  // 누적 거래 대금

        @JsonProperty("flng_cls_code")
        private String flngClsCode;  // 락 구분 코드

        @JsonProperty("prtt_rate")
        private String prttRate;  // 분할 비율

        @JsonProperty("mod_yn")
        private String modYn;  // 변경 여부

        @JsonProperty("prdy_vrss_sign")
        private String prdyVrssSign;  // 전일 대비 부호

        @JsonProperty("prdy_vrss")
        private String prdyVrss;  // 전일 대비

        @JsonProperty("revl_issu_reas")
        private String revlIssuReas;  // 재평가사유코드
    }
}
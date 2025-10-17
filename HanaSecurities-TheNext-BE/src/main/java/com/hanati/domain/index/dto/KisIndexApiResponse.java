package com.hanati.domain.index.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
public class KisIndexApiResponse {
    @JsonProperty("rt_cd")
    private String rtCd;  // 응답코드

    @JsonProperty("msg_cd")
    private String msgCd;  // 응답메시지코드

    @JsonProperty("msg1")
    private String msg1;  // 응답메시지

    @JsonProperty("output")
    private Output output;  // 응답 데이터

    @JsonProperty("output1")
    private List<TimeData> output1;  // 시간별 데이터 (timeprice API)

    @JsonProperty("output2")
    private List<TimeData> output2;  // 시간별 데이터 (일부 API는 output2 사용)

    public List<TimeData> getOutput2() {
        return output2;
    }

    @Data
    @NoArgsConstructor
    public static class Output {
        @JsonProperty("bstp_nmix_prpr")
        private String currentPrice;  // 현재가

        @JsonProperty("bstp_nmix_prdy_vrss")
        private String changePrice;  // 전일대비

        @JsonProperty("prdy_vrss_sign")
        private String changeSign;  // 전일대비부호

        @JsonProperty("bstp_nmix_prdy_ctrt")
        private String changeRate;  // 전일대비율

        @JsonProperty("bstp_nmix_oprc")
        private String openPrice;  // 시가

        @JsonProperty("bstp_nmix_hgpr")
        private String highPrice;  // 고가

        @JsonProperty("bstp_nmix_lwpr")
        private String lowPrice;  // 저가

        @JsonProperty("acml_vol")
        private String volume;  // 누적거래량

        @JsonProperty("acml_tr_pbmn")
        private String tradingValue;  // 누적거래대금

        @JsonProperty("bstp_nmix_avg_prpr")
        private String avgPrice;  // 평균가

        @JsonProperty("prdy_nmix")
        private String prevClose;  // 전일지수

        @JsonProperty("ascn_issu_cnt")
        private String riseCount;  // 상승종목수

        @JsonProperty("down_issu_cnt")
        private String fallCount;  // 하락종목수

        @JsonProperty("sady_issu_cnt")
        private String unchangedCount;  // 보합종목수

        @JsonProperty("uplm_issu_cnt")
        private String upperLimitCount;  // 상한종목수

        @JsonProperty("lslm_issu_cnt")
        private String lowerLimitCount;  // 하한종목수
    }

    @Data
    @NoArgsConstructor
    public static class TimeData {
        @JsonProperty("bsop_hour_cls_code")
        private String time;  // 시간 (HHMMSS)

        @JsonProperty("bstp_nmix_prpr")
        private String price;  // 지수

        @JsonProperty("bstp_nmix_prdy_vrss")
        private String changePrice;  // 전일대비

        @JsonProperty("prdy_vrss_sign")
        private String changeSign;  // 전일대비부호

        @JsonProperty("bstp_nmix_prdy_ctrt")
        private String changeRate;  // 전일대비율

        @JsonProperty("acml_vol")
        private String volume;  // 거래량

        @JsonProperty("acml_tr_pbmn")
        private String tradingValue;  // 거래대금
    }
}
package com.hanati.domain.index.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
public class KisIndexTimeApiResponse {
    @JsonProperty("rt_cd")
    private String rtCd;  // 응답코드

    @JsonProperty("msg_cd")
    private String msgCd;  // 응답메시지코드

    @JsonProperty("msg1")
    private String msg1;  // 응답메시지

    @JsonProperty("output")
    private List<TimeData> output;  // 시간별 데이터 배열

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

        @JsonProperty("bstp_nmix_oprc")
        private String openPrice;  // 시가

        @JsonProperty("bstp_nmix_hgpr")
        private String highPrice;  // 고가

        @JsonProperty("bstp_nmix_lwpr")
        private String lowPrice;  // 저가
    }
}
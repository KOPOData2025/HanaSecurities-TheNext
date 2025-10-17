package com.hanati.domain.ranking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KisForeignTradingValueRankApiResponse {

    @JsonProperty("rt_cd")
    private String rtCd;

    @JsonProperty("msg_cd")
    private String msgCd;

    @JsonProperty("msg1")
    private String msg1;

    @JsonProperty("output1")
    private Output1 output1;

    @JsonProperty("output2")
    private List<TradingValueRankData> output2;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Output1 {
        @JsonProperty("zdiv")
        private String zdiv;

        @JsonProperty("stat")
        private String stat;

        @JsonProperty("crec")
        private String crec;

        @JsonProperty("trec")
        private String trec;

        @JsonProperty("nrec")
        private String nrec;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TradingValueRankData {
        @JsonProperty("rsym")
        private String rsym;  // 실시간조회심볼

        @JsonProperty("excd")
        private String excd;  // 거래소코드

        @JsonProperty("symb")
        private String symb;  // 종목코드

        @JsonProperty("name")
        private String name;  // 종목명

        @JsonProperty("last")
        private String last;  // 현재가

        @JsonProperty("sign")
        private String sign;  // 기호

        @JsonProperty("diff")
        private String diff;  // 대비

        @JsonProperty("rate")
        private String rate;  // 등락율

        @JsonProperty("pask")
        private String pask;  // 매도호가

        @JsonProperty("pbid")
        private String pbid;  // 매수호가

        @JsonProperty("tvol")
        private String tvol;  // 거래량

        @JsonProperty("tamt")
        private String tamt;  // 거래대금

        @JsonProperty("a_tamt")
        private String aTamt;  // 평균거래대금

        @JsonProperty("rank")
        private String rank;  // 순위

        @JsonProperty("ename")
        private String ename;  // 영문종목명

        @JsonProperty("e_ordyn")
        private String eOrdyn;  // 매매가능
    }
}
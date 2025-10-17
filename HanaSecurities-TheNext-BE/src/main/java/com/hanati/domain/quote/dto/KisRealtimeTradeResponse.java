package com.hanati.domain.quote.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class KisRealtimeTradeResponse {
    @JsonProperty("MKSC_SHRN_ISCD")
    private String stockCode;           // 유가증권 단축 종목코드

    @JsonProperty("STCK_CNTG_HOUR")
    private String tradeTime;            // 주식 체결 시간

    @JsonProperty("STCK_PRPR")
    private String currentPrice;         // 주식 현재가

    @JsonProperty("PRDY_VRSS_SIGN")
    private String priceChangeSign;      // 전일 대비 부호

    @JsonProperty("PRDY_VRSS")
    private String priceChange;          // 전일 대비

    @JsonProperty("PRDY_CTRT")
    private String changeRate;           // 전일 대비율

    @JsonProperty("WGHN_AVRG_STCK_PRC")
    private String weightedAvgPrice;     // 가중 평균 주식 가격

    @JsonProperty("STCK_OPRC")
    private String openPrice;            // 주식 시가

    @JsonProperty("STCK_HGPR")
    private String highPrice;            // 주식 최고가

    @JsonProperty("STCK_LWPR")
    private String lowPrice;             // 주식 최저가

    @JsonProperty("ASKP1")
    private String askPrice1;            // 매도호가1

    @JsonProperty("BIDP1")
    private String bidPrice1;            // 매수호가1

    @JsonProperty("CNTG_VOL")
    private String tradeVolume;          // 체결 거래량

    @JsonProperty("ACML_VOL")
    private String accumulatedVolume;    // 누적 거래량

    @JsonProperty("ACML_TR_PBMN")
    private String accumulatedAmount;    // 누적 거래 대금

    @JsonProperty("SELN_CNTG_CSNU")
    private String sellCount;            // 매도 체결 건수

    @JsonProperty("SHNU_CNTG_CSNU")
    private String buyCount;             // 매수 체결 건수

    @JsonProperty("NTBY_CNTG_CSNU")
    private String netBuyCount;          // 순매수 체결 건수

    @JsonProperty("CTTR")
    private String tradeStrength;        // 체결강도

    @JsonProperty("SELN_CNTG_SMTN")
    private String totalSellVolume;      // 총 매도 수량

    @JsonProperty("SHNU_CNTG_SMTN")
    private String totalBuyVolume;       // 총 매수 수량

    @JsonProperty("TOTAL_ASKP_RSQN")
    private String totalAskRemain;       // 총 매도호가 잔량

    @JsonProperty("TOTAL_BIDP_RSQN")
    private String totalBidRemain;       // 총 매수호가 잔량
}

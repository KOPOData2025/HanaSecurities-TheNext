package com.hanati.domain.quote.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RealtimeQuoteResponse {

    @JsonProperty("stockCode")
    private String stockCode;

    @JsonProperty("timestamp")
    private String timestamp;

    @JsonProperty("askPrices")
    private List<Long> askPrices;  // 매도호가 1-10

    @JsonProperty("bidPrices")
    private List<Long> bidPrices;  // 매수호가 1-10

    @JsonProperty("askVolumes")
    private List<Long> askVolumes;  // 매도호가 잔량 1-10

    @JsonProperty("bidVolumes")
    private List<Long> bidVolumes;  // 매수호가 잔량 1-10

    @JsonProperty("totalAskVolume")
    private Long totalAskVolume;  // 총 매도호가 잔량

    @JsonProperty("totalBidVolume")
    private Long totalBidVolume;  // 총 매수호가 잔량
}

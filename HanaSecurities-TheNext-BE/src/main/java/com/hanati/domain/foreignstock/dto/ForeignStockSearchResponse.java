package com.hanati.domain.foreignstock.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "해외 주식 검색 응답")
public class ForeignStockSearchResponse {

    @Schema(description = "검색된 주식 목록")
    @JsonProperty("stocks")
    private List<StockInfo> stocks;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "주식 정보")
    public static class StockInfo {

        @Schema(description = "종목코드")
        @JsonProperty("stockCode")
        private String stockCode;

        @Schema(description = "종목명")
        @JsonProperty("stockName")
        private String stockName;

        @Schema(description = "거래소코드")
        @JsonProperty("exchangeCode")
        private String exchangeCode;

        @Schema(description = "통화")
        @JsonProperty("currency")
        private String currency;

        @Schema(description = "현재가")
        @JsonProperty("currentPrice")
        private String currentPrice;
    }
}

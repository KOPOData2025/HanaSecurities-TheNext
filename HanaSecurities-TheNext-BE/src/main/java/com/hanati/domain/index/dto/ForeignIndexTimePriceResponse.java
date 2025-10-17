package com.hanati.domain.index.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@Schema(description = "해외 지수 분봉 데이터 응답")
public class ForeignIndexTimePriceResponse {

    @Schema(description = "지수 심볼", example = "SPX")
    @JsonProperty("index_symbol")
    private String indexSymbol;

    @Schema(description = "지수명", example = "S&P 500")
    @JsonProperty("index_name")
    private String indexName;

    @Schema(description = "국가 코드", example = "US")
    @JsonProperty("country_code")
    private String countryCode;

    @Schema(description = "분봉 데이터 리스트")
    @JsonProperty("time_prices")
    private List<TimePrice> timePrices;

    @Getter
    @Builder
    @Schema(description = "분봉 데이터")
    public static class TimePrice {

        @Schema(description = "시간 (HHmmss)", example = "093000")
        @JsonProperty("time")
        private String time;

        @Schema(description = "현재가", example = "5123.45")
        @JsonProperty("price")
        private String price;

        @Schema(description = "전일 대비 변동가격", example = "+12.34")
        @JsonProperty("change_price")
        private String changePrice;

        @Schema(description = "전일 대비 변동률", example = "0.24")
        @JsonProperty("change_rate")
        private String changeRate;

        @Schema(description = "부호 (1:상한, 2:상승, 3:보합, 4:하한, 5:하락)", example = "2")
        @JsonProperty("change_sign")
        private String changeSign;

        @Schema(description = "거래량", example = "1234567")
        @JsonProperty("volume")
        private String volume;

        @Schema(description = "거래대금", example = "6345678901")
        @JsonProperty("trading_value")
        private String tradingValue;
    }
}
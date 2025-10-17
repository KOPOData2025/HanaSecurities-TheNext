package com.hanati.domain.foreignquote.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "해외 주식 실시간 호가 데이터")
public class ForeignQuoteData {

    @Schema(description = "종목코드")
    @JsonProperty("stockCode")
    private String stockCode;

    @Schema(description = "현재가")
    @JsonProperty("currentPrice")
    private String currentPrice;

    @Schema(description = "매수1호가")
    @JsonProperty("bidPrice1")
    private String bidPrice1;

    @Schema(description = "매수1잔량")
    @JsonProperty("bidQuantity1")
    private String bidQuantity1;

    @Schema(description = "매도1호가")
    @JsonProperty("askPrice1")
    private String askPrice1;

    @Schema(description = "매도1잔량")
    @JsonProperty("askQuantity1")
    private String askQuantity1;

    @Schema(description = "체결시간")
    @JsonProperty("executionTime")
    private String executionTime;

    @Schema(description = "거래량")
    @JsonProperty("volume")
    private String volume;

    @Schema(description = "등락률")
    @JsonProperty("changeRate")
    private String changeRate;
}

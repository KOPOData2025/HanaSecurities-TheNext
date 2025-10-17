package com.hanati.domain.foreignstock.dto;

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
@Schema(description = "해외 주식 매수가능 금액 조회 응답")
public class ForeignBuyableAmountResponse {

    @Schema(description = "매수가능금액")
    @JsonProperty("buyableAmount")
    private String buyableAmount;

    @Schema(description = "외화매수가능금액")
    @JsonProperty("foreignBuyableAmount")
    private String foreignBuyableAmount;

    @Schema(description = "거래통화코드")
    @JsonProperty("currencyCode")
    private String currencyCode;

    @Schema(description = "환율")
    @JsonProperty("exchangeRate")
    private String exchangeRate;
}

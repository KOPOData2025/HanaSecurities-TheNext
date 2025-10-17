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
@Schema(description = "해외 주식 매도가능 수량 조회 응답")
public class ForeignSellableQuantityResponse {

    @Schema(description = "매도가능수량")
    @JsonProperty("sellableQuantity")
    private String sellableQuantity;

    @Schema(description = "보유수량")
    @JsonProperty("holdingQuantity")
    private String holdingQuantity;

    @Schema(description = "종목코드")
    @JsonProperty("stockCode")
    private String stockCode;

    @Schema(description = "종목명")
    @JsonProperty("stockName")
    private String stockName;
}

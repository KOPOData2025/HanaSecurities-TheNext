package com.hanati.domain.stock.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "국내 주식 주문 응답")
public class StockOrderResponse {

    @Schema(description = "주문번호")
    @JsonProperty("orderNumber")
    private String orderNumber;

    @Schema(description = "주문시간")
    @JsonProperty("orderTime")
    private String orderTime;

    @Schema(description = "거래소코드")
    @JsonProperty("exchangeCode")
    private String exchangeCode;

    @Schema(description = "응답메시지")
    @JsonProperty("message")
    private String message;

    @Schema(description = "성공여부")
    @JsonProperty("success")
    private boolean success;
}

package com.hanati.domain.foreignstock.dto;

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
@Schema(description = "해외 주식 주문 응답")
public class ForeignStockOrderResponse {

    @Schema(description = "주문번호")
    @JsonProperty("orderNumber")
    private String orderNumber;

    @Schema(description = "주문시각")
    @JsonProperty("orderTime")
    private String orderTime;

    @Schema(description = "한국거래소전송주문조직번호")
    @JsonProperty("organizationNumber")
    private String organizationNumber;

    @Schema(description = "응답메시지")
    @JsonProperty("message")
    private String message;

    @Schema(description = "성공여부")
    @JsonProperty("success")
    private boolean success;
}

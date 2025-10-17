package com.hanati.domain.stock.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "국내 주식 주문 요청")
public class StockOrderRequest {

    @Schema(description = "상품번호 (종목코드)", example = "005930", required = true)
    private String pdno;

    @Schema(description = "주문구분 (00:지정가, 01:시장가)", example = "00", required = true)
    private String ordDvsn;

    @Schema(description = "주문수량", example = "10", required = true)
    private String ordQty;

    @Schema(description = "주문단가 (시장가는 0)", example = "70000", required = true)
    private String ordUnpr;
}

package com.hanati.domain.foreignstock.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "해외 주식 주문 요청")
public class ForeignStockOrderRequest {

    @Schema(description = "해외거래소코드 (NASD:나스닥, NYSE:뉴욕, AMEX:아멕스, SEHK:홍콩, TKSE:도쿄)", example = "NASD", required = true)
    private String ovrsExcgCd;

    @Schema(description = "상품번호(종목코드)", example = "AAPL", required = true)
    private String pdno;

    @Schema(description = "주문수량", example = "10", required = true)
    private String ordQty;

    @Schema(description = "해외주문단가 (시장가는 0)", example = "150.00", required = true)
    private String ovrsOrdUnpr;

    @Schema(description = "판매유형 (공백:매수, 00:매도)", example = "", required = false)
    private String sllType;

    @Schema(description = "주문서버구분코드", example = "0", required = true)
    private String ordSvrDvsnCd;

    @Schema(description = "주문구분 (00:지정가)", example = "00", required = true)
    private String ordDvsn;
}

package com.hanati.domain.stock.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KisStockOrderApiRequest {

    @JsonProperty("CANO")
    private String cano;  // 종합계좌번호

    @JsonProperty("ACNT_PRDT_CD")
    private String acntPrdtCd;  // 계좌상품코드

    @JsonProperty("PDNO")
    private String pdno;  // 상품번호(종목코드)

    @JsonProperty("ORD_DVSN")
    private String ordDvsn;  // 주문구분

    @JsonProperty("ORD_QTY")
    private String ordQty;  // 주문수량

    @JsonProperty("ORD_UNPR")
    private String ordUnpr;  // 주문단가
}

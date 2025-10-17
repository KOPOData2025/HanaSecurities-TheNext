package com.hanati.domain.stock.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 한국투자증권 매도가능수량조회 API 응답 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KisSellableQuantityApiResponse {

    @JsonProperty("rt_cd")
    private String rtCd;

    @JsonProperty("msg_cd")
    private String msgCd;

    @JsonProperty("msg1")
    private String msg1;

    @JsonProperty("output")
    private SellableQuantityOutput output;

    /**
     * 매도가능수량 출력 데이터
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SellableQuantityOutput {
        @JsonProperty("pdno")
        private String pdno; // 상품번호

        @JsonProperty("prdt_name")
        private String prdtName; // 상품명

        @JsonProperty("buy_qty")
        private String buyQty; // 매수수량

        @JsonProperty("sll_qty")
        private String sllQty; // 매도수량

        @JsonProperty("cblc_qty")
        private String cblcQty; // 잔고수량

        @JsonProperty("nsvg_qty")
        private String nsvgQty; // 비저축수량

        @JsonProperty("ord_psbl_qty")
        private String ordPsblQty; // 주문가능수량

        @JsonProperty("pchs_avg_pric")
        private String pchsAvgPric; // 매입평균가격

        @JsonProperty("pchs_amt")
        private String pchsAmt; // 매입금액

        @JsonProperty("now_pric")
        private String nowPric; // 현재가

        @JsonProperty("evlu_amt")
        private String evluAmt; // 평가금액

        @JsonProperty("evlu_pfls_amt")
        private String evluPflsAmt; // 평가손익금액

        @JsonProperty("evlu_pfls_rt")
        private String evluPflsRt; // 평가손익율
    }
}

package com.hanati.domain.stock.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 한국투자증권 장내채권 잔고조회 API 응답 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KisBondBalanceApiResponse {

    @JsonProperty("rt_cd")
    private String rtCd;

    @JsonProperty("msg_cd")
    private String msgCd;

    @JsonProperty("msg1")
    private String msg1;

    @JsonProperty("ctx_area_fk200")
    private String ctxAreaFk200;

    @JsonProperty("ctx_area_nk200")
    private String ctxAreaNk200;

    @JsonProperty("output")
    private List<BondBalanceItem> output;

    /**
     * 채권 잔고 상세 항목
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BondBalanceItem {
        @JsonProperty("pdno")
        private String pdno; // 상품번호

        @JsonProperty("prdt_name")
        private String prdtName; // 상품명

        @JsonProperty("buy_dt")
        private String buyDt; // 매수일자

        @JsonProperty("buy_sqno")
        private String buySqno; // 매수일련번호

        @JsonProperty("cblc_qty")
        private String cblcQty; // 잔고수량

        @JsonProperty("agrx_qty")
        private String agrxQty; // 종합과세수량

        @JsonProperty("sprx_qty")
        private String sprxQty; // 분리과세수량

        @JsonProperty("exdt")
        private String exdt; // 만기일

        @JsonProperty("buy_erng_rt")
        private String buyErngRt; // 매수수익율

        @JsonProperty("buy_unpr")
        private String buyUnpr; // 매수단가

        @JsonProperty("buy_amt")
        private String buyAmt; // 매수금액

        @JsonProperty("ord_psbl_qty")
        private String ordPsblQty; // 주문가능수량
    }
}

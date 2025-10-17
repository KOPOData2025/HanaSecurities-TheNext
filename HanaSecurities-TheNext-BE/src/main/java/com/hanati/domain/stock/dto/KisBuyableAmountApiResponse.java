package com.hanati.domain.stock.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 한국투자증권 매수가능조회 API 응답 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KisBuyableAmountApiResponse {

    @JsonProperty("rt_cd")
    private String rtCd;

    @JsonProperty("msg_cd")
    private String msgCd;

    @JsonProperty("msg1")
    private String msg1;

    @JsonProperty("output")
    private BuyableAmountOutput output;

    /**
     * 매수가능 출력 데이터
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BuyableAmountOutput {
        @JsonProperty("ord_psbl_cash")
        private String ordPsblCash; // 주문가능현금

        @JsonProperty("ord_psbl_sbst")
        private String ordPsblSbst; // 주문가능대용

        @JsonProperty("ruse_psbl_amt")
        private String rusePsblAmt; // 재사용가능금액

        @JsonProperty("fund_rpch_chgs")
        private String fundRpchChgs; // 펀드환매대금

        @JsonProperty("psbl_qty_calc_unpr")
        private String psblQtyCalcUnpr; // 가능수량계산단가

        @JsonProperty("nrcvb_buy_amt")
        private String nrcvbBuyAmt; // 미수없는매수금액

        @JsonProperty("nrcvb_buy_qty")
        private String nrcvbBuyQty; // 미수없는매수수량

        @JsonProperty("max_buy_amt")
        private String maxBuyAmt; // 최대매수금액

        @JsonProperty("max_buy_qty")
        private String maxBuyQty; // 최대매수수량

        @JsonProperty("cma_evlu_amt")
        private String cmaEvluAmt; // CMA평가금액

        @JsonProperty("ovrs_re_use_amt_wcrc")
        private String ovrsReUseAmtWcrc; // 해외재사용금액원화

        @JsonProperty("ord_psbl_frcr_amt_wcrc")
        private String ordPsblFrcrAmtWcrc; // 주문가능외화금액원화
    }
}

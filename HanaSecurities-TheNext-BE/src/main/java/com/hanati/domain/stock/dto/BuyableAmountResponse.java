package com.hanati.domain.stock.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 매수가능조회 클라이언트 응답 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BuyableAmountResponse {
    private boolean success;
    private String message;
    private BuyableAmountData data;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BuyableAmountData {
        private String orderableCash;           // 주문가능현금
        private String noCreditBuyAmount;       // 미수없는매수금액
        private String noCreditBuyQuantity;     // 미수없는매수수량
        private String maxBuyAmount;            // 최대매수금액
        private String maxBuyQuantity;          // 최대매수수량
        private String cmaEvaluationAmount;     // CMA평가금액
        private String overseasReuseAmount;     // 해외재사용금액원화
        private String orderableForeignAmount;  // 주문가능외화금액원화
    }
}

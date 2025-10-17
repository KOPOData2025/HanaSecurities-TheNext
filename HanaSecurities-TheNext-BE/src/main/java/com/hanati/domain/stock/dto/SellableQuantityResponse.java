package com.hanati.domain.stock.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 매도가능수량조회 클라이언트 응답 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SellableQuantityResponse {
    private boolean success;
    private String message;
    private SellableQuantityData data;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SellableQuantityData {
        private String stockCode;              // 상품번호
        private String stockName;              // 상품명
        private String buyQuantity;            // 매수수량
        private String sellQuantity;           // 매도수량
        private String balanceQuantity;        // 잔고수량
        private String nonSavingQuantity;      // 비저축수량
        private String orderableQuantity;      // 주문가능수량
        private String purchaseAveragePrice;   // 매입평균가격
        private String purchaseAmount;         // 매입금액
        private String currentPrice;           // 현재가
        private String evaluationAmount;       // 평가금액
        private String profitLossAmount;       // 평가손익금액
        private String profitLossRate;         // 평가손익율
    }
}

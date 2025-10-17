package com.hanati.domain.stock.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 주식 잔고조회 응답 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "주식 잔고조회 응답")
public class StockBalanceResponse {

    @Schema(description = "성공 여부", example = "true")
    private boolean success;

    @Schema(description = "응답 메시지", example = "조회가 완료되었습니다")
    private String message;

    @Schema(description = "보유 주식 목록")
    private List<StockHolding> holdings;

    @Schema(description = "계좌 요약 정보")
    private AccountSummary summary;

    /**
     * 보유 주식 정보
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "보유 주식 정보")
    public static class StockHolding {
        @Schema(description = "종목 코드", example = "005930")
        private String stockCode;

        @Schema(description = "종목명", example = "삼성전자")
        private String stockName;

        @Schema(description = "매매구분", example = "현금")
        private String tradeType;

        @Schema(description = "보유수량", example = "100")
        private String holdingQty;

        @Schema(description = "주문가능수량", example = "100")
        private String orderableQty;

        @Schema(description = "매입평균가격", example = "70000")
        private String avgPurchasePrice;

        @Schema(description = "매입금액", example = "7000000")
        private String purchaseAmount;

        @Schema(description = "현재가", example = "75000")
        private String currentPrice;

        @Schema(description = "평가금액", example = "7500000")
        private String evaluationAmount;

        @Schema(description = "평가손익금액", example = "500000")
        private String profitLossAmount;

        @Schema(description = "평가손익율", example = "7.14")
        private String profitLossRate;

        @Schema(description = "전일대비증감", example = "2000")
        private String priceChange;

        @Schema(description = "등락율", example = "2.74")
        private String changeRate;
    }

    /**
     * 계좌 요약 정보
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "계좌 요약 정보")
    public static class AccountSummary {
        @Schema(description = "예수금총금액 (D+2)", example = "10000000")
        private String depositAmount;

        @Schema(description = "총평가금액", example = "20000000")
        private String totalEvaluationAmount;

        @Schema(description = "순자산금액", example = "30000000")
        private String netAssetAmount;

        @Schema(description = "매입금액합계", example = "18000000")
        private String totalPurchaseAmount;

        @Schema(description = "평가금액합계", example = "20000000")
        private String totalCurrentAmount;

        @Schema(description = "평가손익합계", example = "2000000")
        private String totalProfitLoss;

        @Schema(description = "자산증감액", example = "500000")
        private String assetChangeAmount;
    }
}

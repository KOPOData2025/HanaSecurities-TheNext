package com.hanati.domain.stock.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 장내채권 잔고조회 응답 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "장내채권 잔고조회 응답")
public class BondBalanceResponse {

    @Schema(description = "성공 여부", example = "true")
    private boolean success;

    @Schema(description = "응답 메시지", example = "조회가 완료되었습니다")
    private String message;

    @Schema(description = "보유 채권 목록")
    private List<BondHolding> bonds;

    /**
     * 보유 채권 정보
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "보유 채권 정보")
    public static class BondHolding {
        @Schema(description = "상품번호", example = "KR101501D942")
        private String bondCode;

        @Schema(description = "상품명", example = "국민주택1종19-04")
        private String bondName;

        @Schema(description = "매수일자", example = "20240426")
        private String buyDate;

        @Schema(description = "매수일련번호", example = "1")
        private String buySequence;

        @Schema(description = "잔고수량", example = "4")
        private String balanceQty;

        @Schema(description = "종합과세수량", example = "4")
        private String comprehensiveTaxQty;

        @Schema(description = "분리과세수량", example = "0")
        private String separateTaxQty;

        @Schema(description = "만기일", example = "20240430")
        private String maturityDate;

        @Schema(description = "매수수익율", example = "0.00000000")
        private String buyReturnRate;

        @Schema(description = "매수단가", example = "0")
        private String buyUnitPrice;

        @Schema(description = "매수금액", example = "0")
        private String buyAmount;

        @Schema(description = "주문가능수량", example = "4")
        private String orderableQty;
    }
}

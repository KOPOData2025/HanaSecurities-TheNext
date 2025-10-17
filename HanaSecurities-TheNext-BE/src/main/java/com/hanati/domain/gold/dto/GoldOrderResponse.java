package com.hanati.domain.gold.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 금현물 주문 응답 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoldOrderResponse {

    /**
     * 주문 성공 여부
     */
    private Boolean success;

    /**
     * 주문번호
     */
    private String orderNumber;

    /**
     * 계좌번호
     */
    private String accountNumber;

    /**
     * 상품코드
     */
    private String productCode;

    /**
     * 상품명
     */
    private String productName;

    /**
     * 주문구분 (매수/매도)
     */
    private String orderSide;

    /**
     * 주문 수량 (개)
     */
    private Integer orderedQuantity;

    /**
     * 체결 수량 (개)
     */
    private Integer executedQuantity;

    /**
     * 주문 가격 (원)
     */
    private Long orderPrice;

    /**
     * 체결 가격 (원)
     */
    private Long executionPrice;

    /**
     * 총 주문 금액 (원)
     */
    private Long totalAmount;

    /**
     * 응답 메시지
     */
    private String message;

    /**
     * 오류 코드 (오류 발생 시)
     */
    private String errorCode;
}

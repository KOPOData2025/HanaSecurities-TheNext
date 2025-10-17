package com.hanati.domain.gold.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 금현물 주문 요청 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoldOrderRequest {

    /**
     * 계좌번호
     */
    private String accountNumber;

    /**
     * 상품코드 (M04020000: 금 1Kg, M04020100: 미니금 100g)
     */
    private String productCode;

    /**
     * 주문 수량 (개)
     */
    private Integer quantity;

    /**
     * 주문 가격 (원)
     */
    private Long price;

    /**
     * 주문 유형 (01: 지정가, 02: 시장가)
     */
    private String orderType;
}

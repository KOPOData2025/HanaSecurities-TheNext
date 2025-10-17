package com.hanati.domain.gold.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 금현물 실시간 체결 데이터 DTO (WebSocket)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoldTradeData {

    /**
     * 상품 코드
     */
    private String productCode;

    /**
     * 체결 가격
     */
    private Double price;

    /**
     * 체결 수량
     */
    private Long quantity;

    /**
     * 전일 대비 금액
     */
    private Double changeAmount;

    /**
     * 전일 대비 등락률 (%)
     */
    private Double changeRate;

    /**
     * 누적 거래량
     */
    private Long volume;

    /**
     * 체결 시각
     */
    private String timestamp;
}

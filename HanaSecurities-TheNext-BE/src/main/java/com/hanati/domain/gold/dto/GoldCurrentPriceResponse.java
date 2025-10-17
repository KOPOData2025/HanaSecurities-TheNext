package com.hanati.domain.gold.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 금현물 현재가 조회 응답 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoldCurrentPriceResponse {

    /**
     * 상품 코드 (M04020000, M04020100)
     */
    private String productCode;

    /**
     * 상품명
     */
    private String productName;

    /**
     * 현재가 (원/g)
     */
    private Double currentPrice;

    /**
     * 전일 대비 금액
     */
    private Double changeAmount;

    /**
     * 전일 대비 등락률 (%)
     */
    private Double changeRate;

    /**
     * 고가
     */
    private Double highPrice;

    /**
     * 저가
     */
    private Double lowPrice;

    /**
     * 시가
     */
    private Double openPrice;

    /**
     * 전일 종가
     */
    private Double previousClose;

    /**
     * 거래량
     */
    private Long volume;

    /**
     * 거래대금
     */
    private Long tradeAmount;

    /**
     * 조회 시각
     */
    private String timestamp;
}

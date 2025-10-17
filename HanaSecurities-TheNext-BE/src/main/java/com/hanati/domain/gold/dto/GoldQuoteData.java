package com.hanati.domain.gold.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 금현물 호가 데이터 DTO (WebSocket 실시간 데이터)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoldQuoteData {

    /**
     * 상품 코드
     */
    private String productCode;

    /**
     * 매수 1호가
     */
    private Double bidPrice1;

    /**
     * 매수 1호가 잔량
     */
    private Long bidQuantity1;

    /**
     * 매수 2호가
     */
    private Double bidPrice2;

    /**
     * 매수 2호가 잔량
     */
    private Long bidQuantity2;

    /**
     * 매수 3호가
     */
    private Double bidPrice3;

    /**
     * 매수 3호가 잔량
     */
    private Long bidQuantity3;

    /**
     * 매수 4호가
     */
    private Double bidPrice4;

    /**
     * 매수 4호가 잔량
     */
    private Long bidQuantity4;

    /**
     * 매수 5호가
     */
    private Double bidPrice5;

    /**
     * 매수 5호가 잔량
     */
    private Long bidQuantity5;

    /**
     * 매수 6호가
     */
    private Double bidPrice6;

    /**
     * 매수 6호가 잔량
     */
    private Long bidQuantity6;

    /**
     * 매수 7호가
     */
    private Double bidPrice7;

    /**
     * 매수 7호가 잔량
     */
    private Long bidQuantity7;

    /**
     * 매수 8호가
     */
    private Double bidPrice8;

    /**
     * 매수 8호가 잔량
     */
    private Long bidQuantity8;

    /**
     * 매수 9호가
     */
    private Double bidPrice9;

    /**
     * 매수 9호가 잔량
     */
    private Long bidQuantity9;

    /**
     * 매수 10호가
     */
    private Double bidPrice10;

    /**
     * 매수 10호가 잔량
     */
    private Long bidQuantity10;

    /**
     * 매도 1호가
     */
    private Double askPrice1;

    /**
     * 매도 1호가 잔량
     */
    private Long askQuantity1;

    /**
     * 매도 2호가
     */
    private Double askPrice2;

    /**
     * 매도 2호가 잔량
     */
    private Long askQuantity2;

    /**
     * 매도 3호가
     */
    private Double askPrice3;

    /**
     * 매도 3호가 잔량
     */
    private Long askQuantity3;

    /**
     * 매도 4호가
     */
    private Double askPrice4;

    /**
     * 매도 4호가 잔량
     */
    private Long askQuantity4;

    /**
     * 매도 5호가
     */
    private Double askPrice5;

    /**
     * 매도 5호가 잔량
     */
    private Long askQuantity5;

    /**
     * 매도 6호가
     */
    private Double askPrice6;

    /**
     * 매도 6호가 잔량
     */
    private Long askQuantity6;

    /**
     * 매도 7호가
     */
    private Double askPrice7;

    /**
     * 매도 7호가 잔량
     */
    private Long askQuantity7;

    /**
     * 매도 8호가
     */
    private Double askPrice8;

    /**
     * 매도 8호가 잔량
     */
    private Long askQuantity8;

    /**
     * 매도 9호가
     */
    private Double askPrice9;

    /**
     * 매도 9호가 잔량
     */
    private Long askQuantity9;

    /**
     * 매도 10호가
     */
    private Double askPrice10;

    /**
     * 매도 10호가 잔량
     */
    private Long askQuantity10;

    /**
     * 수신 시각
     */
    private String timestamp;
}

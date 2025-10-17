package com.hanati.domain.gold.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 금현물 캔들 데이터 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoldCandleData {

    /**
     * 시각 (ISO 8601 형식)
     */
    private String timestamp;

    /**
     * 시가
     */
    private Double open;

    /**
     * 고가
     */
    private Double high;

    /**
     * 저가
     */
    private Double low;

    /**
     * 종가
     */
    private Double close;

    /**
     * 거래량
     */
    private Long volume;
}

package com.hanati.domain.gold.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 금현물 차트 데이터 조회 응답 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoldChartResponse {

    /**
     * 차트 간격 (분봉 간격 또는 기간 타입)
     */
    private String interval;

    /**
     * 캔들 데이터 목록
     */
    private List<GoldCandleData> data;
}

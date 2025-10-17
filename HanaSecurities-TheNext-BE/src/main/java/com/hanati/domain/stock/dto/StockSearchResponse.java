package com.hanati.domain.stock.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 주식 종목 검색 응답 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockSearchResponse {

    private boolean success;
    private String message;
    private List<StockSearchItem> stocks;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StockSearchItem {
        private String stockCode;      // 종목 코드
        private String stockName;      // 종목명
        private String marketType;     // 시장구분 (KOSPI/KOSDAQ)
    }
}

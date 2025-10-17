package com.hanati.domain.stock.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PeriodChartResponse {

    private String stockCode;
    private String stockName;
    private String currentPrice;
    private String changeSign;
    private String changePrice;
    private String changeRate;
    private String volume;
    private String tradingValue;
    private String totalShares;  // 상장 주수
    private String periodType;  // D, W, M, Y
    private List<PeriodItem> chartData;
    private String timestamp;

    @Data
    @Builder
    public static class PeriodItem {
        private String date;  // 영업일자
        private String close;  // 종가
        private String open;  // 시가
        private String high;  // 최고가
        private String low;  // 최저가
        private String volume;  // 거래량
        private String tradingValue;  // 거래대금
        private String changeSign;  // 전일 대비 부호
        private String changePrice;  // 전일 대비
    }
}
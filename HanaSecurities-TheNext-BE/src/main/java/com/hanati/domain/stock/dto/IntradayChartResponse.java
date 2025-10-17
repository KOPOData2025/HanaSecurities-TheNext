package com.hanati.domain.stock.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class IntradayChartResponse {

    private String stockCode;
    private String stockName;
    private String currentPrice;
    private String changeSign;
    private String changePrice;
    private String changeRate;
    private String volume;
    private String tradingValue;
    private String totalShares;  // 상장 주수
    private List<ChartItem> chartData;
    private String timestamp;

    @Data
    @Builder
    public static class ChartItem {
        private String date;  // 영업일자
        private String time;  // 체결시간
        private String close;  // 현재가
        private String open;  // 시가
        private String high;  // 최고가
        private String low;  // 최저가
        private String volume;  // 체결 거래량
        private String tradingValue;  // 누적 거래대금
    }
}
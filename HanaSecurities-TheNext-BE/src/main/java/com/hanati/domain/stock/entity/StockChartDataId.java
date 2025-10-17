package com.hanati.domain.stock.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 주식 차트 데이터 복합키
 * 테이블: STOCK_CHART_DATA
 * 복합키: stock_code + period_type + trade_date
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockChartDataId implements Serializable {

    private String stockCode;       // 종목코드
    private String periodType;      // 기간구분 (D/W/M/Y)
    private String tradeDate;       // 거래일자 (YYYYMMDD)
}

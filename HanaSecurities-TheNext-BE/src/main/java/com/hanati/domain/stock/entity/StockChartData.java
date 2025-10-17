package com.hanati.domain.stock.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * 주식 차트 데이터 엔티티 (일봉/주봉/월봉/년봉 통합)
 * 테이블: STOCK_CHART_DATA
 * 용도: 한투 OpenAPI 기간별 시세 조회 데이터 캐싱
 * 복합키: stock_code + period_type + trade_date
 */
@Entity
@Table(name = "STOCK_CHART_DATA")
@IdClass(StockChartDataId.class)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockChartData {

    // 복합키 필드
    @Id
    @Column(name = "stock_code", nullable = false, length = 20)
    private String stockCode;                      // 종목코드 (PK, FK)

    @Id
    @Column(name = "period_type", nullable = false, length = 1)
    private String periodType;                     // 기간구분 (D:일봉, W:주봉, M:월봉, Y:년봉)

    @Id
    @Column(name = "trade_date", nullable = false, length = 8)
    private String tradeDate;                      // 거래일자 (YYYYMMDD)

    // 가격 데이터
    @Column(name = "open_price", length = 20)
    private String openPrice;                      // 시가

    @Column(name = "high_price", length = 20)
    private String highPrice;                      // 고가

    @Column(name = "low_price", length = 20)
    private String lowPrice;                       // 저가

    @Column(name = "close_price", length = 20)
    private String closePrice;                     // 종가

    // 거래 데이터
    @Column(name = "volume", length = 30)
    private String volume;                         // 거래량

    @Column(name = "trading_value", length = 30)
    private String tradingValue;                   // 거래대금

    // 전일 대비 데이터
    @Column(name = "change_sign", length = 1)
    private String changeSign;                     // 전일 대비 부호

    @Column(name = "change_price", length = 20)
    private String changePrice;                    // 전일 대비

    // 메타 데이터
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;               // 생성일시

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;               // 수정일시

    // 연관관계 (optional - Stock 데이터가 없어도 저장 가능)
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "stock_code", insertable = false, updatable = false)
    private Stock stock;
}

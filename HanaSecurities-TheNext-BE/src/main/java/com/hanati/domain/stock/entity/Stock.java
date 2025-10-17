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
 * 주식 종목 마스터 엔티티
 * 테이블: STOCK
 * 용도: 종목코드와 종목명 기반 검색용 최소 마스터 데이터
 */
@Entity
@Table(name = "STOCK")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Stock {

    @Id
    @Column(name = "stock_code", nullable = false, length = 20)
    private String stockCode;                      // 종목코드 (단축코드 6자리)

    @Column(name = "stock_name", nullable = false, length = 200)
    private String stockName;                      // 종목명 (한글)

    @Column(name = "market_type", length = 10)
    private String marketType;                     // 시장구분 (KOSPI/KOSDAQ)

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;               // 생성일시

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;               // 수정일시

    // 연관관계 (Optional)
    @OneToOne(mappedBy = "stock", cascade = CascadeType.ALL, orphanRemoval = true)
    private StockOverview overview;

    // 편의 메서드
    public void updateStockName(String stockName) {
        this.stockName = stockName;
    }

    public void updateMarketType(String marketType) {
        this.marketType = marketType;
    }
}

package com.hanati.domain.foreignwatchlist.entity;

import com.hanati.domain.auth.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 해외 관심종목 엔티티
 * 테이블: FOREIGN_WATCHLIST
 * 용도: 사용자별 해외 관심종목 관리
 */
@Entity
@Table(name = "FOREIGN_WATCHLIST")
@IdClass(ForeignWatchlistId.class)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ForeignWatchlist {

    @Id
    @Column(name = "user_id", nullable = false)
    private Long userId;                           // 사용자 ID (PK)

    @Id
    @Column(name = "exchange_code", nullable = false, length = 10)
    private String exchangeCode;                   // 거래소코드 (PK)

    @Id
    @Column(name = "stock_code", nullable = false, length = 20)
    private String stockCode;                      // 종목코드 (PK)

    @Column(name = "stock_name", length = 200)
    private String stockName;                      // 종목명

    @Column(name = "currency", length = 3)
    private String currency;                       // 통화 (USD, HKD, JPY 등)

    @Column(name = "created_at")
    private LocalDateTime createdAt;               // 등록일시

    // 연관관계 (Optional)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}

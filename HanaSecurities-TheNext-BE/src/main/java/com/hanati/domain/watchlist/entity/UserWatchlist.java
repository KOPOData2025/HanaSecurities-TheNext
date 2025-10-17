package com.hanati.domain.watchlist.entity;

import com.hanati.domain.auth.entity.User;
import com.hanati.domain.stock.entity.Stock;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 사용자 관심 종목 엔티티
 * 테이블: USER_WATCHLISTS
 * 용도: 사용자별 관심 종목 관리
 */
@Entity
@Table(name = "USER_WATCHLISTS")
@IdClass(UserWatchlistId.class)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserWatchlist {

    @Id
    @Column(name = "stock_code", nullable = false, length = 255)
    private String stockCode;                      // 종목코드 (PK)

    @Id
    @Column(name = "user_id", nullable = false)
    private Long userId;                           // 사용자 ID (PK)

    // 연관관계 (Optional)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_code", insertable = false, updatable = false)
    private Stock stock;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;
}

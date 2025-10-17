package com.hanati.domain.watchlist.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 관심 종목 복합키 클래스
 * 테이블: USER_WATCHLISTS
 * 용도: (stock_code, user_id) 복합 PK
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserWatchlistId implements Serializable {

    private String stockCode;      // 종목코드
    private Long userId;            // 사용자 ID

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserWatchlistId that = (UserWatchlistId) o;

        if (!stockCode.equals(that.stockCode)) return false;
        return userId.equals(that.userId);
    }

    @Override
    public int hashCode() {
        int result = stockCode.hashCode();
        result = 31 * result + userId.hashCode();
        return result;
    }
}

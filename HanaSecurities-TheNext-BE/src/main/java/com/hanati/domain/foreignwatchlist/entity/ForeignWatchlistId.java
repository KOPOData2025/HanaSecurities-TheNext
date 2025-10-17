package com.hanati.domain.foreignwatchlist.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 해외 관심종목 복합키 클래스
 * 테이블: FOREIGN_WATCHLIST
 * 용도: (user_id, exchange_code, stock_code) 복합 PK
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ForeignWatchlistId implements Serializable {

    private Long userId;               // 사용자 ID
    private String exchangeCode;       // 거래소코드
    private String stockCode;          // 종목코드

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ForeignWatchlistId that = (ForeignWatchlistId) o;

        if (!userId.equals(that.userId)) return false;
        if (!exchangeCode.equals(that.exchangeCode)) return false;
        return stockCode.equals(that.stockCode);
    }

    @Override
    public int hashCode() {
        int result = userId.hashCode();
        result = 31 * result + exchangeCode.hashCode();
        result = 31 * result + stockCode.hashCode();
        return result;
    }
}

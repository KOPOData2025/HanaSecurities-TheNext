package com.hanati.domain.watchlist.dto;

import com.hanati.domain.watchlist.entity.UserWatchlist;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 관심 종목 조회 응답 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WatchlistResponse {

    private String stockCode;          // 종목코드
    private String stockName;           // 종목명
    private String marketType;          // 시장구분 (KOSPI/KOSDAQ)

    /**
     * Entity → DTO 변환
     */
    public static WatchlistResponse from(UserWatchlist watchlist) {
        return WatchlistResponse.builder()
                .stockCode(watchlist.getStockCode())
                .stockName(watchlist.getStock() != null ? watchlist.getStock().getStockName() : null)
                .marketType(watchlist.getStock() != null ? watchlist.getStock().getMarketType() : null)
                .build();
    }

    /**
     * Entity List → DTO List 변환
     */
    public static List<WatchlistResponse> fromList(List<UserWatchlist> watchlists) {
        return watchlists.stream()
                .map(WatchlistResponse::from)
                .collect(Collectors.toList());
    }
}

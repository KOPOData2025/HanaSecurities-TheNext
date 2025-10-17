package com.hanati.domain.watchlist.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 관심 종목 추가 요청 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddWatchlistRequest {

    private String stockCode;      // 종목코드
    private Long userId;            // 사용자 ID
}

package com.hanati.domain.foreignwatchlist.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "해외 관심종목 등록 요청")
public class ForeignWatchlistRequest {

    @Schema(description = "종목코드", example = "AAPL", required = true)
    private String stockCode;

    @Schema(description = "거래소코드 (NASD, NYSE, HKS, TSE)", example = "NASD", required = true)
    private String exchangeCode;

    @Schema(description = "사용자 ID", example = "1", required = true)
    private Long userId;
}

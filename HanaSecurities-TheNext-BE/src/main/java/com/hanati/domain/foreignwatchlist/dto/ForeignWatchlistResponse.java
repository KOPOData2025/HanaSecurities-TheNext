package com.hanati.domain.foreignwatchlist.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "해외 관심종목 조회 응답")
public class ForeignWatchlistResponse {

    @Schema(description = "관심종목 목록")
    @JsonProperty("watchlist")
    private List<WatchlistItem> watchlist;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "관심종목 아이템")
    public static class WatchlistItem {

        @Schema(description = "종목코드")
        @JsonProperty("stockCode")
        private String stockCode;

        @Schema(description = "종목명")
        @JsonProperty("stockName")
        private String stockName;

        @Schema(description = "거래소코드")
        @JsonProperty("exchangeCode")
        private String exchangeCode;

        @Schema(description = "현재가")
        @JsonProperty("currentPrice")
        private String currentPrice;

        @Schema(description = "등락률")
        @JsonProperty("changeRate")
        private String changeRate;

        @Schema(description = "통화")
        @JsonProperty("currency")
        private String currency;
    }
}

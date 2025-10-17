package com.hanati.domain.ranking.dto;

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
@Schema(description = "거래량 순위 조회 응답")
public class VolumeRankResponse {

    @Schema(description = "종목 리스트")
    private List<StockVolume> stocks;

    @Schema(description = "조회 시간")
    private String timestamp;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "거래량 순위 종목 정보")
    public static class StockVolume {

        @Schema(description = "한글 종목명", example = "삼성전자")
        private String stockName;

        @Schema(description = "종목코드", example = "005930")
        private String stockCode;

        @Schema(description = "순위", example = "1")
        private String rank;

        @Schema(description = "현재가", example = "65100")
        private String currentPrice;

        @Schema(description = "전일 대비 부호", example = "2")
        private String changeSign;

        @Schema(description = "전일 대비", example = "-300")
        private String changePrice;

        @Schema(description = "전일 대비율", example = "-0.46")
        private String changeRate;

        @Schema(description = "누적 거래량", example = "8958147")
        private String accumulatedVolume;

        @Schema(description = "전일 거래량", example = "12334657")
        private String previousVolume;

        @Schema(description = "상장 주수", example = "5969782550")
        private String listedShares;

        @Schema(description = "평균 거래량", example = "8958147")
        private String averageVolume;

        @Schema(description = "N일전 종가 대비 현재가 대비율", example = "-0.46")
        private String nDayPriceRate;

        @Schema(description = "거래량 증가율", example = "72.63")
        private String volumeIncreaseRate;

        @Schema(description = "거래량 회전율", example = "0.15")
        private String volumeTurnoverRate;

        @Schema(description = "N일 거래량 회전율", example = "0.15")
        private String nDayVolumeTurnoverRate;

        @Schema(description = "평균 거래 대금", example = "584861890300")
        private String averageTradingValue;

        @Schema(description = "거래대금 회전율", example = "0.15")
        private String tradingValueTurnoverRate;

        @Schema(description = "N일 거래대금 회전율", example = "0.15")
        private String nDayTradingValueTurnoverRate;

        @Schema(description = "누적 거래 대금", example = "584861890300")
        private String accumulatedTradingValue;
    }
}
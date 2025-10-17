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
@Schema(description = "주식 랭킹 통합 응답")
public class StockRankingResponse {

    @Schema(description = "랭킹 타입", example = "VOLUME")
    private String rankingType;

    @Schema(description = "종목 리스트")
    private List<StockRankItem> stocks;

    @Schema(description = "조회 시간")
    private String timestamp;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "랭킹 종목 정보")
    public static class StockRankItem {

        @Schema(description = "순위", example = "1")
        private String rank;

        @Schema(description = "한글 종목명", example = "삼성전자")
        private String stockName;

        @Schema(description = "종목코드", example = "005930")
        private String stockCode;

        @Schema(description = "현재가", example = "65100")
        private String currentPrice;

        @Schema(description = "전일 대비 부호 (1:상한, 2:상승, 3:보합, 4:하한, 5:하락)", example = "2")
        private String changeSign;

        @Schema(description = "전일 대비", example = "-300")
        private String changePrice;

        @Schema(description = "전일 대비율", example = "-0.46")
        private String changeRate;

        @Schema(description = "누적 거래량", example = "8958147")
        private String volume;

        @Schema(description = "누적 거래 대금", example = "584861890300")
        private String tradingValue;

        // 거래량 순위 관련 추가 필드
        @Schema(description = "평균 거래량", example = "8958147")
        private String averageVolume;

        @Schema(description = "거래량 증가율", example = "72.63")
        private String volumeIncreaseRate;

        @Schema(description = "거래량 회전율", example = "0.15")
        private String volumeTurnoverRate;

        @Schema(description = "평균 거래 대금", example = "584861890300")
        private String averageTradingValue;

        @Schema(description = "거래대금 회전율", example = "0.15")
        private String tradingValueTurnoverRate;

        // 등락률 순위 관련 추가 필드
        @Schema(description = "주식 최고가", example = "65500")
        private String highPrice;

        @Schema(description = "주식 최저가", example = "64800")
        private String lowPrice;

        @Schema(description = "시가 대비 현재가", example = "100")
        private String openPriceChange;

        @Schema(description = "시가 대비 현재가 비율", example = "0.15")
        private String openPriceChangeRate;

        @Schema(description = "최고가 대비 현재가 비율", example = "-0.61")
        private String highPriceRatio;

        @Schema(description = "최저가 대비 현재가 비율", example = "0.46")
        private String lowPriceRatio;
    }
}
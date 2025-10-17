package com.hanati.domain.ranking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ForeignStockRankingResponse {

    private String rankingType;  // VOLUME, TRADING_VALUE, RISE, FALL
    private String exchangeCode;  // NYS, NAS, AMS, HKS, SHS, SZS, HSX, HNX, TSE
    private List<ForeignStockRankItem> stocks;
    private String timestamp;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ForeignStockRankItem {
        private String rank;              // 순위
        private String rsym;              // 실시간조회심볼
        private String stockCode;         // 종목코드
        private String stockName;         // 종목명
        private String englishName;       // 영문종목명
        private String currentPrice;      // 현재가
        private String changeSign;        // 기호
        private String changePrice;       // 대비
        private String changeRate;        // 등락율
        private String volume;            // 거래량
        private String tradingValue;      // 거래대금
        private String askPrice;          // 매도호가
        private String bidPrice;          // 매수호가
        private String tradeable;         // 매매가능여부

        // 상승율/하락율 전용 필드
        private String basePrice;         // 기준가격
        private String baseDiff;          // 기준가격대비
        private String baseRate;          // 기준가격대비율

        // 거래량 전용 필드
        private String averageVolume;     // 평균거래량

        // 거래대금 전용 필드
        private String averageTradingValue; // 평균거래대금
    }
}
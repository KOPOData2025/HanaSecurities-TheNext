package com.hanati.domain.stock.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * [국내주식] 통합 재무정보 조회 클라이언트 응답 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinancialInfoResponse {
    private boolean success;
    private String message;
    private FinancialData data;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FinancialData {
        private List<FinancialPeriodInfo> periods;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FinancialPeriodInfo {
        // 공통 필드
        private String period;                    // 결산 년월 (YYYYMM)

        // 재무비율
        private String salesGrowthRate;           // 매출액 증가율
        private String operatingProfitGrowthRate; // 영업 이익 증가율
        private String netIncomeGrowthRate;       // 순이익 증가율
        private String roe;                       // ROE (자기자본이익률)
        private String eps;                       // EPS (주당순이익)
        private String sps;                       // SPS (주당매출액)
        private String bps;                       // BPS (주당순자산)
        private String reserveRatio;              // 유보 비율
        private String debtRatio;                 // 부채 비율

        // 손익계산서
        private String sales;                     // 매출액
        private String salesCost;                 // 매출 원가
        private String grossProfit;               // 매출 총 이익
        private String operatingProfit;           // 영업 이익
        private String ordinaryProfit;            // 경상 이익
        private String extraordinaryGain;         // 특별 이익
        private String extraordinaryLoss;         // 특별 손실
        private String netIncome;                 // 당기순이익

        // 대차대조표
        private String currentAssets;             // 유동자산
        private String fixedAssets;               // 고정자산
        private String totalAssets;               // 자산총계
        private String currentLiabilities;        // 유동부채
        private String fixedLiabilities;          // 고정부채
        private String totalLiabilities;          // 부채총계
        private String capital;                   // 자본금
        private String totalEquity;               // 자본총계
    }
}

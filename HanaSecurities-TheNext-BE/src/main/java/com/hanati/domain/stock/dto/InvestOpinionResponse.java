package com.hanati.domain.stock.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 종목투자의견 조회 응답 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "종목투자의견 조회 응답")
public class InvestOpinionResponse {

    @Schema(description = "성공 여부", example = "true")
    private boolean success;

    @Schema(description = "응답 메시지", example = "조회가 완료되었습니다")
    private String message;

    @Schema(description = "투자의견 목록")
    private List<InvestOpinionItem> opinions;

    /**
     * 투자의견 항목
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "투자의견 항목")
    public static class InvestOpinionItem {
        @Schema(description = "영업일자", example = "20240527")
        private String businessDate;

        @Schema(description = "투자의견", example = "매수")
        private String opinion;

        @Schema(description = "투자의견구분코드", example = "2")
        private String opinionCode;

        @Schema(description = "직전투자의견", example = "매수")
        private String previousOpinion;

        @Schema(description = "직전투자의견구분코드", example = "3")
        private String previousOpinionCode;

        @Schema(description = "증권사명", example = "SK")
        private String brokerage;

        @Schema(description = "목표가격", example = "105000")
        private String targetPrice;

        @Schema(description = "전일종가", example = "75900")
        private String previousClose;

        @Schema(description = "목표가괴리도", example = "-29100")
        private String priceGap;

        @Schema(description = "괴리율", example = "-27.71")
        private String gapRate;

        @Schema(description = "선물괴리도", example = "-27400")
        private String futuresGap;

        @Schema(description = "선물괴리율", example = "-26.10")
        private String futuresGapRate;
    }
}

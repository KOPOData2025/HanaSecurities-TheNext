package com.hanati.domain.foreignstock.dto;

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
@Schema(description = "해외 주식 잔고 응답")
public class ForeignStockBalanceResponse {

    @Schema(description = "보유 주식 목록")
    @JsonProperty("holdings")
    private List<HoldingData> holdings;

    @Schema(description = "총 평가손익금액")
    @JsonProperty("totEvluPflsAmt")
    private String totEvluPflsAmt;

    @Schema(description = "총 수익률")
    @JsonProperty("totPftrt")
    private String totPftrt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "보유 주식 데이터")
    public static class HoldingData {

        @Schema(description = "해외상품번호(종목코드)")
        @JsonProperty("ovrsPdno")
        private String ovrsPdno;

        @Schema(description = "해외종목명")
        @JsonProperty("ovrsItemName")
        private String ovrsItemName;

        @Schema(description = "해외잔고수량")
        @JsonProperty("ovrsCblcQty")
        private String ovrsCblcQty;

        @Schema(description = "매입평균가격")
        @JsonProperty("pchsAvgPric")
        private String pchsAvgPric;

        @Schema(description = "현재가격")
        @JsonProperty("nowPric2")
        private String nowPric2;

        @Schema(description = "외화평가손익금액")
        @JsonProperty("frcrEvluPflsAmt")
        private String frcrEvluPflsAmt;

        @Schema(description = "평가손익율")
        @JsonProperty("evluPflsRt")
        private String evluPflsRt;

        @Schema(description = "외화매입금액")
        @JsonProperty("frcrPchsAmt1")
        private String frcrPchsAmt1;

        @Schema(description = "해외주식평가금액")
        @JsonProperty("ovrsStckEvluAmt")
        private String ovrsStckEvluAmt;

        @Schema(description = "주문가능수량")
        @JsonProperty("ordPsblQty")
        private String ordPsblQty;

        @Schema(description = "거래통화코드")
        @JsonProperty("trCrcyCd")
        private String trCrcyCd;

        @Schema(description = "해외거래소코드")
        @JsonProperty("ovrsExcgCd")
        private String ovrsExcgCd;
    }
}

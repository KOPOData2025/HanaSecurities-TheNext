package com.hanati.domain.index.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "지수 현재가 응답")
public class IndexPriceResponse {

    @Schema(description = "지수 코드", example = "U001", allowableValues = {"U001", "U201", "U180"})
    private String indexCode;

    @Schema(description = "지수 이름", example = "코스피")
    private String indexName;

    @Schema(description = "현재가", example = "2550.32")
    private String currentPrice;

    @Schema(description = "전일 대비", example = "+15.20")
    private String changePrice;

    @Schema(description = "전일 대비율", example = "0.60")
    private String changeRate;

    @Schema(description = "부호 (1: 상한, 2: 상승, 3: 보합, 4: 하한, 5: 하락)", example = "2")
    private String changeSign;

    @Schema(description = "시가", example = "2535.10")
    private String openPrice;

    @Schema(description = "고가", example = "2560.50")
    private String highPrice;

    @Schema(description = "저가", example = "2530.00")
    private String lowPrice;

    @Schema(description = "거래량", example = "500000000")
    private String volume;

    @Schema(description = "거래대금", example = "10000000000")
    private String tradingValue;

    @Schema(description = "시가총액", nullable = true)
    private String marketCap;

    @Schema(description = "조회 시간", example = "2024-01-01 15:30:00")
    private String timestamp;
}
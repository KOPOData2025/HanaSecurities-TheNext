package com.hanati.domain.index.dto;

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
@Schema(description = "지수 시간별 가격 응답")
public class IndexTimePriceResponse {

    @Schema(description = "지수 코드", example = "U001")
    private String indexCode;

    @Schema(description = "지수 이름", example = "코스피")
    private String indexName;

    @Schema(description = "시간별 가격 리스트")
    private List<TimePrice> timePrices;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "시간별 가격 정보")
    public static class TimePrice {

        @Schema(description = "시간 (HHMMSS)", example = "093000")
        private String time;

        @Schema(description = "지수", example = "2550.32")
        private String price;

        @Schema(description = "전일 대비", example = "+15.20")
        private String changePrice;

        @Schema(description = "전일 대비율", example = "0.60")
        private String changeRate;

        @Schema(description = "부호 (1: 상한, 2: 상승, 3: 보합, 4: 하한, 5: 하락)", example = "2")
        private String changeSign;

        @Schema(description = "거래량", example = "50000000")
        private String volume;

        @Schema(description = "거래대금", example = "1000000000")
        private String tradingValue;
    }
}
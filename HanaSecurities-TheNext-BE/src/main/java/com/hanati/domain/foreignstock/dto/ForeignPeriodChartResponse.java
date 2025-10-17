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
@Schema(description = "해외 주식 기간별(일/주/월/년) 차트 응답")
public class ForeignPeriodChartResponse {

    @Schema(description = "실시간종목코드")
    @JsonProperty("rsym")
    private String rsym;

    @Schema(description = "차트 데이터 목록")
    @JsonProperty("chartData")
    private List<ChartData> chartData;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "기간별 차트 데이터")
    public static class ChartData {

        @Schema(description = "일자")
        @JsonProperty("xymd")
        private String xymd;

        @Schema(description = "시가")
        @JsonProperty("open")
        private String open;

        @Schema(description = "고가")
        @JsonProperty("high")
        private String high;

        @Schema(description = "저가")
        @JsonProperty("low")
        private String low;

        @Schema(description = "종가")
        @JsonProperty("clos")
        private String clos;

        @Schema(description = "거래량")
        @JsonProperty("tvol")
        private String tvol;

        @Schema(description = "거래대금")
        @JsonProperty("tamt")
        private String tamt;
    }
}

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
@Schema(description = "해외 주식 분봉 차트 응답")
public class ForeignIntradayChartResponse {

    @Schema(description = "실시간종목코드")
    @JsonProperty("rsym")
    private String rsym;

    @Schema(description = "다음가능여부")
    @JsonProperty("next")
    private String next;

    @Schema(description = "추가데이타여부")
    @JsonProperty("more")
    private String more;

    @Schema(description = "차트 데이터 목록")
    @JsonProperty("chartData")
    private List<ChartData> chartData;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "분봉 차트 데이터")
    public static class ChartData {

        @Schema(description = "한국기준일자")
        @JsonProperty("kymd")
        private String kymd;

        @Schema(description = "한국기준시간")
        @JsonProperty("khms")
        private String khms;

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
        @JsonProperty("last")
        private String last;

        @Schema(description = "체결량")
        @JsonProperty("evol")
        private String evol;

        @Schema(description = "체결대금")
        @JsonProperty("eamt")
        private String eamt;
    }
}

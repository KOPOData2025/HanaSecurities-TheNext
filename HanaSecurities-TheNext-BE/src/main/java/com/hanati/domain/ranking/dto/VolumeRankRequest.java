package com.hanati.domain.ranking.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "거래량 순위 조회 요청")
public class VolumeRankRequest {

    @Schema(description = "조건 시장 분류 코드", example = "J", allowableValues = {"J", "NX"})
    private String fidCondMrktDivCode;

    @Schema(description = "조건 화면 분류 코드", example = "20171")
    private String fidCondScrDivCode;

    @Schema(description = "입력 종목코드 (0000:전체, 기타:업종코드)", example = "0000")
    private String fidInputIscd;

    @Schema(description = "분류 구분 코드 (0:전체, 1:보통주, 2:우선주)", example = "0", allowableValues = {"0", "1", "2"})
    private String fidDivClsCode;

    @Schema(description = "소속 구분 코드", example = "0", allowableValues = {"0", "1", "2", "3", "4"})
    private String fidBlngClsCode;

    @Schema(description = "대상 구분 코드 (9자리 1 or 0)", example = "111111111")
    private String fidTrgtClsCode;

    @Schema(description = "대상 제외 구분 코드 (10자리 1 or 0)", example = "0000000000")
    private String fidTrgtExlsClsCode;

    @Schema(description = "입력 가격1 (가격 ~)", example = "0")
    private String fidInputPrice1;

    @Schema(description = "입력 가격2 (~ 가격)", example = "1000000")
    private String fidInputPrice2;

    @Schema(description = "거래량 수", example = "100000")
    private String fidVolCnt;

    @Schema(description = "입력 날짜1", example = "")
    private String fidInputDate1;
}
package com.hanati.domain.foreignstock.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "해외 주식 기본정보 응답")
public class ForeignStockBasicInfoResponse {

    @Schema(description = "종목코드")
    @JsonProperty("stockCode")
    private String stockCode;

    @Schema(description = "종목명")
    @JsonProperty("stockName")
    private String stockName;

    @Schema(description = "상장주수")
    @JsonProperty("shar")
    private String shar;

    @Schema(description = "자본금")
    @JsonProperty("mcap")
    private String mcap;

    @Schema(description = "업종(섹터)")
    @JsonProperty("sector")
    private String sector;

    @Schema(description = "액면가")
    @JsonProperty("parValue")
    private String parValue;

    @Schema(description = "통화")
    @JsonProperty("currency")
    private String currency;

    @Schema(description = "거래소코드")
    @JsonProperty("exchangeCode")
    private String exchangeCode;
}

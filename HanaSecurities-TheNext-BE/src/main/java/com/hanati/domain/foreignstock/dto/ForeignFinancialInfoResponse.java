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
@Schema(description = "해외 주식 재무정보 응답")
public class ForeignFinancialInfoResponse {

    @Schema(description = "PER (주가수익비율)")
    @JsonProperty("per")
    private String per;

    @Schema(description = "PBR (주가순자산비율)")
    @JsonProperty("pbr")
    private String pbr;

    @Schema(description = "EPS (주당순이익)")
    @JsonProperty("eps")
    private String eps;

    @Schema(description = "BPS (주당순자산가치)")
    @JsonProperty("bps")
    private String bps;

    @Schema(description = "ROE (자기자본이익률)")
    @JsonProperty("roe")
    private String roe;

    @Schema(description = "ROA (총자산이익률)")
    @JsonProperty("roa")
    private String roa;

    @Schema(description = "시가총액")
    @JsonProperty("marketCap")
    private String marketCap;

    @Schema(description = "매출액")
    @JsonProperty("revenue")
    private String revenue;

    @Schema(description = "영업이익")
    @JsonProperty("operatingProfit")
    private String operatingProfit;

    @Schema(description = "당기순이익")
    @JsonProperty("netIncome")
    private String netIncome;
}

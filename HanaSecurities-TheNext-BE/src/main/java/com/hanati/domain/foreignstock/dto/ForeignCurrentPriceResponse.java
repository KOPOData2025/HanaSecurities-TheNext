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
@Schema(description = "해외 주식 현재가 상세 응답")
public class ForeignCurrentPriceResponse {

    @Schema(description = "실시간조회종목코드")
    @JsonProperty("rsym")
    private String rsym;

    @Schema(description = "전일거래량")
    @JsonProperty("pvol")
    private String pvol;

    @Schema(description = "시가")
    @JsonProperty("open")
    private String open;

    @Schema(description = "고가")
    @JsonProperty("high")
    private String high;

    @Schema(description = "저가")
    @JsonProperty("low")
    private String low;

    @Schema(description = "현재가")
    @JsonProperty("last")
    private String last;

    @Schema(description = "전일종가")
    @JsonProperty("base")
    private String base;

    @Schema(description = "시가총액")
    @JsonProperty("tomv")
    private String tomv;

    @Schema(description = "전일거래대금")
    @JsonProperty("pamt")
    private String pamt;

    @Schema(description = "상한가")
    @JsonProperty("uplp")
    private String uplp;

    @Schema(description = "하한가")
    @JsonProperty("dnlp")
    private String dnlp;

    @Schema(description = "52주최고가")
    @JsonProperty("h52p")
    private String h52p;

    @Schema(description = "52주최고일자")
    @JsonProperty("h52d")
    private String h52d;

    @Schema(description = "52주최저가")
    @JsonProperty("l52p")
    private String l52p;

    @Schema(description = "52주최저일자")
    @JsonProperty("l52d")
    private String l52d;

    @Schema(description = "PER")
    @JsonProperty("perx")
    private String perx;

    @Schema(description = "PBR")
    @JsonProperty("pbrx")
    private String pbrx;

    @Schema(description = "EPS")
    @JsonProperty("epsx")
    private String epsx;

    @Schema(description = "BPS")
    @JsonProperty("bpsx")
    private String bpsx;

    @Schema(description = "상장주수")
    @JsonProperty("shar")
    private String shar;

    @Schema(description = "자본금")
    @JsonProperty("mcap")
    private String mcap;

    @Schema(description = "통화")
    @JsonProperty("curr")
    private String curr;

    @Schema(description = "소수점자리수")
    @JsonProperty("zdiv")
    private String zdiv;

    @Schema(description = "매매단위")
    @JsonProperty("vnit")
    private String vnit;

    @Schema(description = "원환산당일가격")
    @JsonProperty("t_xprc")
    private String tXprc;

    @Schema(description = "원환산당일대비")
    @JsonProperty("t_xdif")
    private String tXdif;

    @Schema(description = "원환산당일등락")
    @JsonProperty("t_xrat")
    private String tXrat;

    @Schema(description = "원환산전일가격")
    @JsonProperty("p_xprc")
    private String pXprc;

    @Schema(description = "원환산전일대비")
    @JsonProperty("p_xdif")
    private String pXdif;

    @Schema(description = "원환산전일등락")
    @JsonProperty("p_xrat")
    private String pXrat;

    @Schema(description = "당일환율")
    @JsonProperty("t_rate")
    private String tRate;

    @Schema(description = "전일환율")
    @JsonProperty("p_rate")
    private String pRate;

    @Schema(description = "원환산당일기호")
    @JsonProperty("t_xsgn")
    private String tXsgn;

    @Schema(description = "원환산전일기호")
    @JsonProperty("p_xsng")
    private String pXsng;

    @Schema(description = "거래가능여부")
    @JsonProperty("e_ordyn")
    private String eOrdyn;

    @Schema(description = "호가단위")
    @JsonProperty("e_hogau")
    private String eHogau;

    @Schema(description = "업종(섹터)")
    @JsonProperty("e_icod")
    private String eIcod;

    @Schema(description = "액면가")
    @JsonProperty("e_parp")
    private String eParp;

    @Schema(description = "거래량")
    @JsonProperty("tvol")
    private String tvol;

    @Schema(description = "거래대금")
    @JsonProperty("tamt")
    private String tamt;

    @Schema(description = "ETP분류명")
    @JsonProperty("etyp_nm")
    private String etypNm;
}

package com.hanati.domain.stock.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * [국내주식] 주식 기본 정보 조회 클라이언트 응답 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockBasicInfoResponse {
    private boolean success;
    private String message;
    private StockBasicInfoData data;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StockBasicInfoData {
        private String pdno;
        private String prdtTypeCd;
        private String prdtName;
        private String prdtAbrvName;
        private String prdtEngName;
        private String stdPdno;
        private String mketIdCd;
        private String sctyGrpIdCd;
        private String excgDvsnCd;
        private String lstgStqt;
        private String lstgCptlAmt;
        private String cpta;
        private String papr;
        private String issuPric;
        private String kospi200ItemYn;
        private String sctsMketLstgDt;
        private String sctsMketLstgAbolDt;
        private String kosdaqMketLstgDt;
        private String kosdaqMketLstgAbolDt;
        private String frbdMketLstgDt;
        private String frbdMketLstgAbolDt;
        private String reitsKindCd;
        private String etfDvsnCd;
        private String oilfFundYn;
        private String idxBztpLclsCd;
        private String idxBztpMclsCd;
        private String idxBztpSclsCd;
        private String stckKindCd;
        private String mfndOpngDt;
        private String mfndEndDt;
        private String dpsiErlmCnclDt;
        private String etfCuQty;
        private String prdtName120;
        private String prdtEngName120;
        private String prdtEngAbrvName;
        private String dpsiAptmErlmYn;
        private String etfTxtnTypeCd;
        private String etfTypeCd;
        private String lstgAbolDt;
        private String nwstOdstDvsnCd;
        private String sbstPric;
        private String thcoSbstPric;
        private String thcoSbstPricChngDt;
        private String trStopYn;
        private String admnItemYn;
        private String thdtClpr;
        private String bfdyClpr;
        private String clprChngDt;
        private String stdIdstClsfCd;
        private String stdIdstClsfCdName;
        private String idxBztpLclsCdName;
        private String idxBztpMclsCdName;
        private String idxBztpSclsCdName;
        private String ocrNo;
        private String crfdItemYn;
        private String elecSctyYn;
        private String issuIsttCd;
        private String etfChasErngRtDbnb;
        private String etfEtnIvstHeedItemYn;
        private String stlnIntRtDvsnCd;
        private String frnrPsnlLmtRt;
        private String lstgRqsrIssuIsttCd;
        private String lstgRqsrItemCd;
        private String trstIsttIssuIsttCd;
        private String cpttTradTrPsblYn;
        private String nxtTrStopYn;
        private String setlMmdd;
    }
}

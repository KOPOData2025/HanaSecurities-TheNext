package com.hanati.domain.stock.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 한국투자증권 주식기본조회 API 응답 DTO (CTPF1002R)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KisStockBasicInfoApiResponse {

    @JsonProperty("rt_cd")
    private String rtCd;

    @JsonProperty("msg_cd")
    private String msgCd;

    @JsonProperty("msg1")
    private String msg1;

    @JsonProperty("output")
    private StockBasicInfoOutput output;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StockBasicInfoOutput {
        @JsonProperty("pdno")
        private String pdno;
        @JsonProperty("prdt_type_cd")
        private String prdtTypeCd;
        @JsonProperty("mket_id_cd")
        private String mketIdCd;
        @JsonProperty("scty_grp_id_cd")
        private String sctyGrpIdCd;
        @JsonProperty("excg_dvsn_cd")
        private String excgDvsnCd;
        @JsonProperty("setl_mmdd")
        private String setlMmdd;
        @JsonProperty("lstg_stqt")
        private String lstgStqt;
        @JsonProperty("lstg_cptl_amt")
        private String lstgCptlAmt;
        @JsonProperty("cpta")
        private String cpta;
        @JsonProperty("papr")
        private String papr;
        @JsonProperty("issu_pric")
        private String issuPric;
        @JsonProperty("kospi200_item_yn")
        private String kospi200ItemYn;
        @JsonProperty("scts_mket_lstg_dt")
        private String sctsMketLstgDt;
        @JsonProperty("scts_mket_lstg_abol_dt")
        private String sctsMketLstgAbolDt;
        @JsonProperty("kosdaq_mket_lstg_dt")
        private String kosdaqMketLstgDt;
        @JsonProperty("kosdaq_mket_lstg_abol_dt")
        private String kosdaqMketLstgAbolDt;
        @JsonProperty("frbd_mket_lstg_dt")
        private String frbdMketLstgDt;
        @JsonProperty("frbd_mket_lstg_abol_dt")
        private String frbdMketLstgAbolDt;
        @JsonProperty("reits_kind_cd")
        private String reitsKindCd;
        @JsonProperty("etf_dvsn_cd")
        private String etfDvsnCd;
        @JsonProperty("oilf_fund_yn")
        private String oilfFundYn;
        @JsonProperty("idx_bztp_lcls_cd")
        private String idxBztpLclsCd;
        @JsonProperty("idx_bztp_mcls_cd")
        private String idxBztpMclsCd;
        @JsonProperty("idx_bztp_scls_cd")
        private String idxBztpSclsCd;
        @JsonProperty("stck_kind_cd")
        private String stckKindCd;
        @JsonProperty("mfnd_opng_dt")
        private String mfndOpngDt;
        @JsonProperty("mfnd_end_dt")
        private String mfndEndDt;
        @JsonProperty("dpsi_erlm_cncl_dt")
        private String dpsiErlmCnclDt;
        @JsonProperty("etf_cu_qty")
        private String etfCuQty;
        @JsonProperty("prdt_name")
        private String prdtName;
        @JsonProperty("prdt_name120")
        private String prdtName120;
        @JsonProperty("prdt_abrv_name")
        private String prdtAbrvName;
        @JsonProperty("std_pdno")
        private String stdPdno;
        @JsonProperty("prdt_eng_name")
        private String prdtEngName;
        @JsonProperty("prdt_eng_name120")
        private String prdtEngName120;
        @JsonProperty("prdt_eng_abrv_name")
        private String prdtEngAbrvName;
        @JsonProperty("dpsi_aptm_erlm_yn")
        private String dpsiAptmErlmYn;
        @JsonProperty("etf_txtn_type_cd")
        private String etfTxtnTypeCd;
        @JsonProperty("etf_type_cd")
        private String etfTypeCd;
        @JsonProperty("lstg_abol_dt")
        private String lstgAbolDt;
        @JsonProperty("nwst_odst_dvsn_cd")
        private String nwstOdstDvsnCd;
        @JsonProperty("sbst_pric")
        private String sbstPric;
        @JsonProperty("thco_sbst_pric")
        private String thcoSbstPric;
        @JsonProperty("thco_sbst_pric_chng_dt")
        private String thcoSbstPricChngDt;
        @JsonProperty("tr_stop_yn")
        private String trStopYn;
        @JsonProperty("admn_item_yn")
        private String admnItemYn;
        @JsonProperty("thdt_clpr")
        private String thdtClpr;
        @JsonProperty("bfdy_clpr")
        private String bfdyClpr;
        @JsonProperty("clpr_chng_dt")
        private String clprChngDt;
        @JsonProperty("std_idst_clsf_cd")
        private String stdIdstClsfCd;
        @JsonProperty("std_idst_clsf_cd_name")
        private String stdIdstClsfCdName;
        @JsonProperty("idx_bztp_lcls_cd_name")
        private String idxBztpLclsCdName;
        @JsonProperty("idx_bztp_mcls_cd_name")
        private String idxBztpMclsCdName;
        @JsonProperty("idx_bztp_scls_cd_name")
        private String idxBztpSclsCdName;
        @JsonProperty("ocr_no")
        private String ocrNo;
        @JsonProperty("crfd_item_yn")
        private String crfdItemYn;
        @JsonProperty("elec_scty_yn")
        private String elecSctyYn;
        @JsonProperty("issu_istt_cd")
        private String issuIsttCd;
        @JsonProperty("etf_chas_erng_rt_dbnb")
        private String etfChasErngRtDbnb;
        @JsonProperty("etf_etn_ivst_heed_item_yn")
        private String etfEtnIvstHeedItemYn;
        @JsonProperty("stln_int_rt_dvsn_cd")
        private String stlnIntRtDvsnCd;
        @JsonProperty("frnr_psnl_lmt_rt")
        private String frnrPsnlLmtRt;
        @JsonProperty("lstg_rqsr_issu_istt_cd")
        private String lstgRqsrIssuIsttCd;
        @JsonProperty("lstg_rqsr_item_cd")
        private String lstgRqsrItemCd;
        @JsonProperty("trst_istt_issu_istt_cd")
        private String trstIsttIssuIsttCd;
        @JsonProperty("cptt_trad_tr_psbl_yn")
        private String cpttTradTrPsblYn;
        @JsonProperty("nxt_tr_stop_yn")
        private String nxtTrStopYn;
    }
}

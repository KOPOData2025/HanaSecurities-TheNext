package com.hanati.domain.stock.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 한국투자증권 주식잔고조회 API 응답 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KisStockBalanceApiResponse {

    @JsonProperty("rt_cd")
    private String rtCd;

    @JsonProperty("msg_cd")
    private String msgCd;

    @JsonProperty("msg1")
    private String msg1;

    @JsonProperty("ctx_area_fk100")
    private String ctxAreaFk100;

    @JsonProperty("ctx_area_nk100")
    private String ctxAreaNk100;

    @JsonProperty("output1")
    private List<StockBalanceItem> output1;

    @JsonProperty("output2")
    private List<StockBalanceSummary> output2;

    /**
     * 주식 잔고 상세 항목
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StockBalanceItem {
        @JsonProperty("pdno")
        private String pdno; // 종목번호

        @JsonProperty("prdt_name")
        private String prdtName; // 종목명

        @JsonProperty("trad_dvsn_name")
        private String tradDvsnName; // 매매구분명

        @JsonProperty("bfdy_buy_qty")
        private String bfdyBuyQty; // 전일매수수량

        @JsonProperty("bfdy_sll_qty")
        private String bfdySllQty; // 전일매도수량

        @JsonProperty("thdt_buyqty")
        private String thdtBuyqty; // 금일매수수량

        @JsonProperty("thdt_sll_qty")
        private String thdtSllQty; // 금일매도수량

        @JsonProperty("hldg_qty")
        private String hldgQty; // 보유수량

        @JsonProperty("ord_psbl_qty")
        private String ordPsblQty; // 주문가능수량

        @JsonProperty("pchs_avg_pric")
        private String pchsAvgPric; // 매입평균가격

        @JsonProperty("pchs_amt")
        private String pchsAmt; // 매입금액

        @JsonProperty("prpr")
        private String prpr; // 현재가

        @JsonProperty("evlu_amt")
        private String evluAmt; // 평가금액

        @JsonProperty("evlu_pfls_amt")
        private String evluPflsAmt; // 평가손익금액

        @JsonProperty("evlu_pfls_rt")
        private String evluPflsRt; // 평가손익율

        @JsonProperty("evlu_erng_rt")
        private String evluErngRt; // 평가수익율

        @JsonProperty("loan_dt")
        private String loanDt; // 대출일자

        @JsonProperty("loan_amt")
        private String loanAmt; // 대출금액

        @JsonProperty("stln_slng_chgs")
        private String stlnSlngChgs; // 대주매각대금

        @JsonProperty("expd_dt")
        private String expdDt; // 만기일자

        @JsonProperty("fltt_rt")
        private String flttRt; // 등락율

        @JsonProperty("bfdy_cprs_icdc")
        private String bfdyCprsIcdc; // 전일대비증감

        @JsonProperty("item_mgna_rt_name")
        private String itemMgnaRtName; // 종목증거금율명

        @JsonProperty("grta_rt_name")
        private String grtaRtName; // 보증금율명

        @JsonProperty("sbst_pric")
        private String sbstPric; // 대용가격

        @JsonProperty("stck_loan_unpr")
        private String stckLoanUnpr; // 주식대출단가
    }

    /**
     * 주식 잔고 요약 정보
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StockBalanceSummary {
        @JsonProperty("dnca_tot_amt")
        private String dncaTotAmt; // 예수금총금액

        @JsonProperty("nxdy_excc_amt")
        private String nxdyExccAmt; // 익일정산금액 (D+1)

        @JsonProperty("prvs_rcdl_excc_amt")
        private String prvsRcdlExccAmt; // 가수도정산금액 (D+2)

        @JsonProperty("cma_evlu_amt")
        private String cmaEvluAmt; // CMA평가금액

        @JsonProperty("bfdy_buy_amt")
        private String bfdyBuyAmt; // 전일매수금액

        @JsonProperty("thdt_buy_amt")
        private String thdtBuyAmt; // 금일매수금액

        @JsonProperty("nxdy_auto_rdpt_amt")
        private String nxdyAutoRdptAmt; // 익일자동상환금액

        @JsonProperty("bfdy_sll_amt")
        private String bfdySllAmt; // 전일매도금액

        @JsonProperty("thdt_sll_amt")
        private String thdtSllAmt; // 금일매도금액

        @JsonProperty("d2_auto_rdpt_amt")
        private String d2AutoRdptAmt; // D+2자동상환금액

        @JsonProperty("bfdy_tlex_amt")
        private String bfdyTlexAmt; // 전일제비용금액

        @JsonProperty("thdt_tlex_amt")
        private String thdtTlexAmt; // 금일제비용금액

        @JsonProperty("tot_loan_amt")
        private String totLoanAmt; // 총대출금액

        @JsonProperty("scts_evlu_amt")
        private String sctsEvluAmt; // 유가평가금액

        @JsonProperty("tot_evlu_amt")
        private String totEvluAmt; // 총평가금액

        @JsonProperty("nass_amt")
        private String nassAmt; // 순자산금액

        @JsonProperty("fncg_gld_auto_rdpt_yn")
        private String fncgGldAutoRdptYn; // 융자금자동상환여부

        @JsonProperty("pchs_amt_smtl_amt")
        private String pchsAmtSmtlAmt; // 매입금액합계금액

        @JsonProperty("evlu_amt_smtl_amt")
        private String evluAmtSmtlAmt; // 평가금액합계금액

        @JsonProperty("evlu_pfls_smtl_amt")
        private String evluPflsSmtlAmt; // 평가손익합계금액

        @JsonProperty("tot_stln_slng_chgs")
        private String totStlnSlngChgs; // 총대주매각대금

        @JsonProperty("bfdy_tot_asst_evlu_amt")
        private String bfdyTotAsstEvluAmt; // 전일총자산평가금액

        @JsonProperty("asst_icdc_amt")
        private String asstIcdcAmt; // 자산증감액

        @JsonProperty("asst_icdc_erng_rt")
        private String asstIcdcErngRt; // 자산증감수익율
    }
}

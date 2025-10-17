package com.hanati.domain.stock.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 주식 재무정보 엔티티 (재무비율 + 손익계산서 + 대차대조표 통합)
 * 테이블: STOCK_FINANCIAL_INFO
 * 용도: 한투 OpenAPI 재무정보 조회 응답 데이터 저장 (기간별 이력)
 */
@Entity
@Table(name = "STOCK_FINANCIAL_INFO",
       uniqueConstraints = @UniqueConstraint(columnNames = {"stock_code", "stac_yymm"}))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockFinancialInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_stock_financial")
    @SequenceGenerator(name = "seq_stock_financial", sequenceName = "SEQ_STOCK_FINANCIAL", allocationSize = 1)
    @Column(name = "financial_id", nullable = false)
    private Long financialId;                      // PK (시퀀스)

    @Column(name = "stock_code", nullable = false, length = 20)
    private String stockCode;                      // FK to STOCK

    // 기준 정보
    @Column(name = "stac_yymm", nullable = false, length = 8)
    private String stacYymm;                       // 결산년월 (YYYYMM)

    @Column(name = "division_code", length = 1)
    private String divisionCode;                   // 분류구분 (0: 년, 1: 분기)

    // 재무비율 (KisFinancialRatioApiResponse)
    @Column(name = "grs", length = 20)
    private String grs;                            // 매출액증가율

    @Column(name = "bsop_prfi_inrt", length = 20)
    private String bsopPrfiInrt;                   // 영업이익증가율

    @Column(name = "ntin_inrt", length = 20)
    private String ntinInrt;                       // 순이익증가율

    @Column(name = "roe_val", length = 20)
    private String roeVal;                         // ROE 자기자본이익률

    @Column(name = "eps", length = 20)
    private String eps;                            // EPS 주당순이익

    @Column(name = "sps", length = 20)
    private String sps;                            // SPS 주당매출액

    @Column(name = "bps", length = 20)
    private String bps;                            // BPS 주당순자산

    @Column(name = "rsrv_rate", length = 20)
    private String rsrvRate;                       // 유보비율

    @Column(name = "lblt_rate", length = 20)
    private String lbltRate;                       // 부채비율

    // 손익계산서 (KisIncomeStatementApiResponse)
    @Column(name = "sale_account", length = 30)
    private String saleAccount;                    // 매출액

    @Column(name = "sale_cost", length = 30)
    private String saleCost;                       // 매출원가

    @Column(name = "sale_totl_prfi", length = 30)
    private String saleTotlPrfi;                   // 매출총이익

    @Column(name = "depr_cost", length = 30)
    private String deprCost;                       // 감가상각비

    @Column(name = "sell_mang", length = 30)
    private String sellMang;                       // 판매및관리비

    @Column(name = "bsop_prti", length = 30)
    private String bsopPrti;                       // 영업이익

    @Column(name = "bsop_non_ernn", length = 30)
    private String bsopNonErnn;                    // 영업외수익

    @Column(name = "bsop_non_expn", length = 30)
    private String bsopNonExpn;                    // 영업외비용

    @Column(name = "op_prfi", length = 30)
    private String opPrfi;                         // 경상이익

    @Column(name = "spec_prfi", length = 30)
    private String specPrfi;                       // 특별이익

    @Column(name = "spec_loss", length = 30)
    private String specLoss;                       // 특별손실

    @Column(name = "thtr_ntin", length = 30)
    private String thtrNtin;                       // 당기순이익

    // 대차대조표 (KisBalanceSheetApiResponse)
    @Column(name = "cras", length = 30)
    private String cras;                           // 유동자산

    @Column(name = "fxas", length = 30)
    private String fxas;                           // 고정자산

    @Column(name = "total_aset", length = 30)
    private String totalAset;                      // 자산총계

    @Column(name = "flow_lblt", length = 30)
    private String flowLblt;                       // 유동부채

    @Column(name = "fix_lblt", length = 30)
    private String fixLblt;                        // 고정부채

    @Column(name = "total_lblt", length = 30)
    private String totalLblt;                      // 부채총계

    @Column(name = "cpfn", length = 30)
    private String cpfn;                           // 자본금

    @Column(name = "cfp_surp", length = 30)
    private String cfpSurp;                        // 자본잉여금

    @Column(name = "prfi_surp", length = 30)
    private String prfiSurp;                       // 이익잉여금

    @Column(name = "total_cptl", length = 30)
    private String totalCptl;                      // 자본총계

    // 캐시 최적화용 필드
    @Column(name = "last_synced_date")
    private LocalDate lastSyncedDate;              // 마지막 API 동기화 일자

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;               // 생성일시

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;               // 수정일시

    // 연관관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_code", insertable = false, updatable = false)
    private Stock stock;

    // 비즈니스 로직 - API 동기화 필요 여부 판단
    public boolean needsSync() {
        if (lastSyncedDate == null) {
            return true;
        }
        return !lastSyncedDate.isEqual(LocalDate.now());
    }

    // 비즈니스 로직 - 동기화 완료 표시
    public void markSynced() {
        this.lastSyncedDate = LocalDate.now();
    }
}

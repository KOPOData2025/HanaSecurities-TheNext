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
 * 주식 종목 개요 정보 엔티티 (프론트엔드 필수 필드만)
 * 테이블: STOCK_OVERVIEW
 * 용도: 한투 OpenAPI 주식기본조회(CTPF1002R) 응답에서 필요한 필드만 저장
 */
@Entity
@Table(name = "STOCK_OVERVIEW")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockOverview {

    @Id
    @Column(name = "stock_code", nullable = false, length = 20)
    private String stockCode;                      // 종목코드 (PK, FK)

    // 프론트엔드 필수 필드
    @Column(name = "mket_id_cd", length = 10)
    private String mketIdCd;                       // 시장 (시장ID코드)

    @Column(name = "scty_grp_id_cd", length = 10)
    private String sctyGrpIdCd;                    // 증권그룹 (증권그룹ID코드)

    @Column(name = "excg_dvsn_cd", length = 10)
    private String excgDvsnCd;                     // 거래소구분 (거래소구분코드)

    @Column(name = "setl_mmdd", length = 4)
    private String setlMmdd;                       // 결산월일 (MMDD)

    @Column(name = "lstg_stqt", length = 30)
    private String lstgStqt;                       // 상장주수

    @Column(name = "lstg_cptl_amt", length = 30)
    private String lstgCptlAmt;                    // 상장자본금액

    @Column(name = "cpta", length = 30)
    private String cpta;                           // 자본금

    @Column(name = "papr", length = 30)
    private String papr;                           // 액면가

    @Column(name = "issu_pric", length = 30)
    private String issuPric;                       // 발행가

    @Column(name = "kospi200_item_yn", length = 1)
    private String kospi200ItemYn;                 // 코스피200종목여부

    @Column(name = "scts_mket_lstg_dt", length = 8)
    private String sctsMketLstgDt;                 // 유가증권시장상장일자

    @Column(name = "stck_kind_cd", length = 10)
    private String stckKindCd;                     // 주식종류

    @Column(name = "std_idst_clsf_cd", length = 10)
    private String stdIdstClsfCd;                  // 산업분류코드 (표준산업분류코드)

    @Column(name = "nxt_tr_stop_yn", length = 1)
    private String nxtTrStopYn;                    // NXT거래정지여부 (익일거래정지여부)

    // 캐시 최적화용 필드
    @Column(name = "last_synced_date")
    private LocalDate lastSyncedDate;              // 마지막 API 동기화 일자

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;               // 생성일시

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;               // 수정일시

    // 연관관계 (optional - Stock 데이터가 없어도 저장 가능)
    @OneToOne(fetch = FetchType.LAZY, optional = true)
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

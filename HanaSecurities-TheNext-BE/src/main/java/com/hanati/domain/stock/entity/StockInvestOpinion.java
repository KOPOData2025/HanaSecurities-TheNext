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
 * 종목 투자의견 엔티티 (프론트엔드 필수 필드만)
 * 테이블: STOCK_INVEST_OPINION
 * 용도: 한투 OpenAPI 종목투자의견 조회 응답에서 필요한 필드만 저장
 */
@Entity
@Table(name = "STOCK_INVEST_OPINION")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockInvestOpinion {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_stock_opinion")
    @SequenceGenerator(name = "seq_stock_opinion", sequenceName = "SEQ_STOCK_OPINION", allocationSize = 1)
    @Column(name = "opinion_id", nullable = false)
    private Long opinionId;                        // PK (시퀀스)

    @Column(name = "stock_code", nullable = false, length = 20)
    private String stockCode;                      // FK to STOCK

    // 프론트엔드 필수 필드
    @Column(name = "stck_bsop_date", nullable = false, length = 8)
    private String stckBsopDate;                   // 발표일 (YYYYMMDD, 프론트는 yyyy.mm.dd로 변환)

    @Column(name = "invt_opnn", length = 50)
    private String invtOpnn;                       // 현재 의견 (투자의견)

    @Column(name = "rgbf_invt_opnn", length = 50)
    private String rgbfInvtOpnn;                   // 직전 의견 (직전투자의견)

    @Column(name = "hts_goal_prc", length = 30)
    private String htsGoalPrc;                     // 목표가 (HTS목표가격)

    @Column(name = "mbcr_name", length = 100)
    private String mbcrName;                       // 증권사 (회원사명)

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

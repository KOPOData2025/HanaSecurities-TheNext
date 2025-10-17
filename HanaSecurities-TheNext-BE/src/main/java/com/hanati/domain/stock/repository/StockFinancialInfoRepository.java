package com.hanati.domain.stock.repository;

import com.hanati.domain.stock.entity.StockFinancialInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 주식 재무정보 Repository
 */
@Repository
public interface StockFinancialInfoRepository extends JpaRepository<StockFinancialInfo, Long> {

    /**
     * 종목코드로 재무정보 목록 조회 (최신순)
     */
    List<StockFinancialInfo> findByStockCodeOrderByStacYymmDesc(String stockCode);

    /**
     * 종목코드와 결산년월로 재무정보 조회
     */
    Optional<StockFinancialInfo> findByStockCodeAndStacYymm(String stockCode, String stacYymm);

    /**
     * 종목코드로 재무정보 조회 (분류구분 필터)
     */
    List<StockFinancialInfo> findByStockCodeAndDivisionCodeOrderByStacYymmDesc(
            String stockCode, String divisionCode
    );

    /**
     * 최신 재무정보 1건 조회
     */
    @Query("SELECT f FROM StockFinancialInfo f " +
           "WHERE f.stockCode = :stockCode " +
           "ORDER BY f.stacYymm DESC LIMIT 1")
    Optional<StockFinancialInfo> findLatestByStockCode(@Param("stockCode") String stockCode);

    /**
     * 오늘 동기화된 데이터 존재 여부 확인
     */
    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END " +
           "FROM StockFinancialInfo f " +
           "WHERE f.stockCode = :stockCode AND f.lastSyncedDate = :today")
    boolean isSyncedToday(@Param("stockCode") String stockCode, @Param("today") LocalDate today);

    /**
     * 종목코드와 분류구분으로 최근 N개 조회
     */
    @Query("SELECT f FROM StockFinancialInfo f " +
           "WHERE f.stockCode = :stockCode AND f.divisionCode = :divisionCode " +
           "ORDER BY f.stacYymm DESC LIMIT :limit")
    List<StockFinancialInfo> findTopNByStockCodeAndDivisionCode(
            @Param("stockCode") String stockCode,
            @Param("divisionCode") String divisionCode,
            @Param("limit") int limit
    );
}

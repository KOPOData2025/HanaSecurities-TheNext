package com.hanati.domain.stock.repository;

import com.hanati.domain.stock.entity.StockOverview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

/**
 * 주식 종목 개요 정보 Repository
 */
@Repository
public interface StockOverviewRepository extends JpaRepository<StockOverview, String> {

    /**
     * 종목코드로 개요 정보 조회
     */
    Optional<StockOverview> findByStockCode(String stockCode);

    /**
     * 오늘 동기화된 데이터인지 확인
     */
    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END " +
           "FROM StockOverview s " +
           "WHERE s.stockCode = :stockCode AND s.lastSyncedDate = :today")
    boolean isSyncedToday(@Param("stockCode") String stockCode, @Param("today") LocalDate today);

    /**
     * 동기화가 필요한 데이터 조회 (오늘 동기화되지 않은 데이터)
     */
    @Query("SELECT s FROM StockOverview s " +
           "WHERE s.stockCode = :stockCode AND (s.lastSyncedDate IS NULL OR s.lastSyncedDate < :today)")
    Optional<StockOverview> findNeedsSyncData(@Param("stockCode") String stockCode, @Param("today") LocalDate today);
}

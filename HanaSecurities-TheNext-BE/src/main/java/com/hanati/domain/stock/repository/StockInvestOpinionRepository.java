package com.hanati.domain.stock.repository;

import com.hanati.domain.stock.entity.StockInvestOpinion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * 종목 투자의견 Repository
 */
@Repository
public interface StockInvestOpinionRepository extends JpaRepository<StockInvestOpinion, Long> {

    /**
     * 종목코드로 투자의견 목록 조회 (최신순)
     */
    List<StockInvestOpinion> findByStockCodeOrderByStckBsopDateDesc(String stockCode);

    /**
     * 종목코드와 영업일자로 투자의견 목록 조회
     */
    List<StockInvestOpinion> findByStockCodeAndStckBsopDate(String stockCode, String stckBsopDate);

    /**
     * 종목코드와 증권사로 투자의견 조회
     */
    List<StockInvestOpinion> findByStockCodeAndMbcrName(String stockCode, String mbcrName);

    /**
     * 특정 종목의 최근 N개 투자의견 조회
     */
    @Query("SELECT o FROM StockInvestOpinion o " +
           "WHERE o.stockCode = :stockCode " +
           "ORDER BY o.stckBsopDate DESC LIMIT :limit")
    List<StockInvestOpinion> findTopNByStockCode(
            @Param("stockCode") String stockCode,
            @Param("limit") int limit
    );

    /**
     * 특정 기간 이후의 투자의견 조회
     */
    @Query("SELECT o FROM StockInvestOpinion o " +
           "WHERE o.stockCode = :stockCode AND o.stckBsopDate >= :startDate " +
           "ORDER BY o.stckBsopDate DESC")
    List<StockInvestOpinion> findByStockCodeAndDateAfter(
            @Param("stockCode") String stockCode,
            @Param("startDate") String startDate
    );

    /**
     * 오늘 동기화된 데이터 존재 여부 확인
     */
    @Query("SELECT CASE WHEN COUNT(o) > 0 THEN true ELSE false END " +
           "FROM StockInvestOpinion o " +
           "WHERE o.stockCode = :stockCode AND o.lastSyncedDate = :today")
    boolean isSyncedToday(@Param("stockCode") String stockCode, @Param("today") LocalDate today);

    /**
     * 종목코드별 최근 3개월 투자의견 조회
     */
    @Query("SELECT o FROM StockInvestOpinion o " +
           "WHERE o.stockCode = :stockCode AND o.stckBsopDate >= :threeMonthsAgo " +
           "ORDER BY o.stckBsopDate DESC, o.mbcrName")
    List<StockInvestOpinion> findRecentThreeMonths(
            @Param("stockCode") String stockCode,
            @Param("threeMonthsAgo") String threeMonthsAgo
    );
}

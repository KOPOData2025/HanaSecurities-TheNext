package com.hanati.domain.stock.repository;

import com.hanati.domain.stock.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 주식 종목 마스터 Repository
 */
@Repository
public interface StockRepository extends JpaRepository<Stock, String> {

    /**
     * 종목명으로 검색 (LIKE 검색)
     */
    List<Stock> findByStockNameContaining(String stockName);

    /**
     * 시장구분으로 검색
     */
    List<Stock> findByMarketType(String marketType);

    /**
     * 종목코드로 조회 (편의 메서드)
     */
    Optional<Stock> findByStockCode(String stockCode);

    /**
     * 종목명 또는 종목코드로 검색
     */
    @Query("SELECT s FROM Stock s WHERE s.stockName LIKE %:keyword% OR s.stockCode LIKE %:keyword%")
    List<Stock> searchByKeyword(@Param("keyword") String keyword);

    /**
     * 시장구분과 종목명으로 검색
     */
    @Query("SELECT s FROM Stock s WHERE s.marketType = :marketType AND s.stockName LIKE %:keyword%")
    List<Stock> searchByMarketTypeAndKeyword(
            @Param("marketType") String marketType,
            @Param("keyword") String keyword
    );
}

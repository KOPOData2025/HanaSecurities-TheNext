package com.hanati.domain.stock.repository;

import com.hanati.domain.stock.entity.StockChartData;
import com.hanati.domain.stock.entity.StockChartDataId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 주식 차트 데이터 Repository
 * 테이블: STOCK_CHART_DATA
 * 용도: 일봉/주봉/월봉/년봉 데이터 조회 및 관리
 */
@Repository
public interface StockChartDataRepository extends JpaRepository<StockChartData, StockChartDataId> {

    /**
     * 특정 종목 + 기간구분의 모든 데이터 조회 (최신순)
     *
     * @param stockCode  종목코드
     * @param periodType 기간구분 (D/W/M/Y)
     * @return 차트 데이터 리스트 (trade_date DESC)
     */
    List<StockChartData> findByStockCodeAndPeriodTypeOrderByTradeDateDesc(String stockCode, String periodType);

    /**
     * 특정 종목 + 기간구분의 가장 최근 데이터 1개 조회
     * 용도: 동기화 필요 여부 체크
     *
     * @param stockCode  종목코드
     * @param periodType 기간구분 (D/W/M/Y)
     * @return 가장 최근 차트 데이터
     */
    Optional<StockChartData> findTopByStockCodeAndPeriodTypeOrderByTradeDateDesc(String stockCode, String periodType);

    /**
     * 특정 종목 + 기간구분의 특정 기간 범위 데이터 조회
     *
     * @param stockCode  종목코드
     * @param periodType 기간구분 (D/W/M/Y)
     * @param startDate  시작일자 (YYYYMMDD)
     * @param endDate    종료일자 (YYYYMMDD)
     * @return 차트 데이터 리스트 (trade_date DESC)
     */
    List<StockChartData> findByStockCodeAndPeriodTypeAndTradeDateBetweenOrderByTradeDateDesc(
            String stockCode, String periodType, String startDate, String endDate);

    /**
     * 특정 종목 + 기간구분의 데이터 존재 여부 확인
     *
     * @param stockCode  종목코드
     * @param periodType 기간구분 (D/W/M/Y)
     * @return 데이터 존재 여부
     */
    boolean existsByStockCodeAndPeriodType(String stockCode, String periodType);
}

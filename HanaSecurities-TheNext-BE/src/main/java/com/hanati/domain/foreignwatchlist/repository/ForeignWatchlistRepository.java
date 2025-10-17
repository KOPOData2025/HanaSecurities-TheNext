package com.hanati.domain.foreignwatchlist.repository;

import com.hanati.domain.foreignwatchlist.entity.ForeignWatchlist;
import com.hanati.domain.foreignwatchlist.entity.ForeignWatchlistId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 해외 관심종목 Repository
 */
@Repository
public interface ForeignWatchlistRepository extends JpaRepository<ForeignWatchlist, ForeignWatchlistId> {

    /**
     * 사용자 ID로 관심종목 목록 조회
     * @param userId 사용자 ID
     * @return 관심종목 목록
     */
    List<ForeignWatchlist> findByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * 관심종목 존재 여부 확인
     * @param userId 사용자 ID
     * @param exchangeCode 거래소코드
     * @param stockCode 종목코드
     * @return 존재 여부
     */
    boolean existsByUserIdAndExchangeCodeAndStockCode(Long userId, String exchangeCode, String stockCode);

    /**
     * 관심종목 삭제
     * @param userId 사용자 ID
     * @param exchangeCode 거래소코드
     * @param stockCode 종목코드
     */
    void deleteByUserIdAndExchangeCodeAndStockCode(Long userId, String exchangeCode, String stockCode);
}

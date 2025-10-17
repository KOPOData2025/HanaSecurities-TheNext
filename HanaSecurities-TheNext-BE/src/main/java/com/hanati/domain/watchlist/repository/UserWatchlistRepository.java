package com.hanati.domain.watchlist.repository;

import com.hanati.domain.watchlist.entity.UserWatchlist;
import com.hanati.domain.watchlist.entity.UserWatchlistId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 관심 종목 Repository
 * 테이블: USER_WATCHLISTS
 */
@Repository
public interface UserWatchlistRepository extends JpaRepository<UserWatchlist, UserWatchlistId> {

    /**
     * 사용자 ID로 관심 종목 목록 조회
     *
     * @param userId 사용자 ID
     * @return 관심 종목 목록
     */
    @Query("SELECT uw FROM UserWatchlist uw JOIN FETCH uw.stock WHERE uw.userId = :userId")
    List<UserWatchlist> findByUserId(@Param("userId") Long userId);

    /**
     * 관심 종목 삭제
     *
     * @param stockCode 종목코드
     * @param userId 사용자 ID
     */
    void deleteByStockCodeAndUserId(String stockCode, Long userId);

    /**
     * 관심 종목 존재 여부 확인
     *
     * @param stockCode 종목코드
     * @param userId 사용자 ID
     * @return 존재 여부
     */
    boolean existsByStockCodeAndUserId(String stockCode, Long userId);
}

package com.hanati.domain.bnpl.repository;

import com.hanati.domain.bnpl.entity.BnplUsageHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 후불결제 이용내역 Repository
 */
@Repository
public interface BnplUsageHistoryRepository extends JpaRepository<BnplUsageHistory, Long> {

    /**
     * 사용자 ID로 이용내역 조회 (최근 날짜순 정렬)
     */
    List<BnplUsageHistory> findByUserIdOrderByUsageDateDesc(String userId);
}

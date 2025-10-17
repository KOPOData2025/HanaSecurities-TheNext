package com.hanati.domain.watchlist.service;

import com.hanati.domain.auth.repository.UserRepository;
import com.hanati.domain.stock.repository.StockRepository;
import com.hanati.domain.watchlist.dto.AddWatchlistRequest;
import com.hanati.domain.watchlist.dto.WatchlistResponse;
import com.hanati.domain.watchlist.entity.UserWatchlist;
import com.hanati.domain.watchlist.repository.UserWatchlistRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 관심 종목 서비스
 * - 관심 종목 추가
 * - 관심 종목 조회
 * - 관심 종목 삭제
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WatchlistService {

    private final UserWatchlistRepository watchlistRepository;
    private final UserRepository userRepository;
    private final StockRepository stockRepository;

    // ============================================================================
    // 1. 관심 종목 추가
    // ============================================================================

    /**
     * 관심 종목 추가
     *
     * @param request 추가 요청 (stockCode, userId)
     */
    @Transactional
    public void addWatchlist(AddWatchlistRequest request) {
        log.info("[관심 종목 추가] userId: {}, stockCode: {}", request.getUserId(), request.getStockCode());

        // 1. 사용자 존재 여부 확인
        if (!userRepository.existsById(request.getUserId())) {
            throw new RuntimeException("존재하지 않는 사용자입니다: " + request.getUserId());
        }

        // 2. 종목 존재 여부 확인
        if (!stockRepository.existsById(request.getStockCode())) {
            throw new RuntimeException("존재하지 않는 종목코드입니다: " + request.getStockCode());
        }

        // 3. 중복 확인
        if (watchlistRepository.existsByStockCodeAndUserId(request.getStockCode(), request.getUserId())) {
            throw new RuntimeException("이미 관심 종목에 추가된 종목입니다.");
        }

        // 4. 관심 종목 추가
        UserWatchlist watchlist = UserWatchlist.builder()
                .stockCode(request.getStockCode())
                .userId(request.getUserId())
                .build();

        watchlistRepository.save(watchlist);
        log.info("[관심 종목 추가 완료] userId: {}, stockCode: {}", request.getUserId(), request.getStockCode());
    }

    // ============================================================================
    // 2. 관심 종목 조회
    // ============================================================================

    /**
     * 사용자의 관심 종목 목록 조회
     *
     * @param userId 사용자 ID
     * @return 관심 종목 목록
     */
    @Transactional(readOnly = true)
    public List<WatchlistResponse> getUserWatchlist(Long userId) {
        log.info("[관심 종목 조회] userId: {}", userId);

        // 1. 사용자 존재 여부 확인
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("존재하지 않는 사용자입니다: " + userId);
        }

        // 2. 관심 종목 조회 (Stock 정보 포함)
        List<UserWatchlist> watchlists = watchlistRepository.findByUserId(userId);

        log.info("[관심 종목 조회 완료] userId: {}, count: {}", userId, watchlists.size());

        return WatchlistResponse.fromList(watchlists);
    }

    // ============================================================================
    // 3. 관심 종목 삭제
    // ============================================================================

    /**
     * 관심 종목 삭제
     *
     * @param userId 사용자 ID
     * @param stockCode 종목코드
     */
    @Transactional
    public void removeWatchlist(Long userId, String stockCode) {
        log.info("[관심 종목 삭제] userId: {}, stockCode: {}", userId, stockCode);

        // 1. 사용자 존재 여부 확인
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("존재하지 않는 사용자입니다: " + userId);
        }

        // 2. 관심 종목 존재 여부 확인
        if (!watchlistRepository.existsByStockCodeAndUserId(stockCode, userId)) {
            throw new RuntimeException("관심 종목에 없는 종목입니다.");
        }

        // 3. 삭제
        watchlistRepository.deleteByStockCodeAndUserId(stockCode, userId);
        log.info("[관심 종목 삭제 완료] userId: {}, stockCode: {}", userId, stockCode);
    }
}

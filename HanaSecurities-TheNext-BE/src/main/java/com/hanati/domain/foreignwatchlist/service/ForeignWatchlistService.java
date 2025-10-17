package com.hanati.domain.foreignwatchlist.service;

import com.hanati.domain.foreignstock.service.ForeignStockService;
import com.hanati.domain.foreignwatchlist.dto.ForeignWatchlistRequest;
import com.hanati.domain.foreignwatchlist.dto.ForeignWatchlistResponse;
import com.hanati.domain.foreignwatchlist.entity.ForeignWatchlist;
import com.hanati.domain.foreignwatchlist.repository.ForeignWatchlistRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 해외 관심종목 Service
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ForeignWatchlistService {

    private final ForeignWatchlistRepository repository;
    private final ForeignStockService foreignStockService;

    /**
     * 관심종목 추가
     * @param request 관심종목 추가 요청
     * @return 성공 여부
     */
    public boolean addWatchlist(ForeignWatchlistRequest request) {
        log.info("[해외 관심종목 추가] 사용자: {}, 거래소: {}, 종목: {}",
                request.getUserId(), request.getExchangeCode(), request.getStockCode());

        try {
            // 중복 체크
            if (repository.existsByUserIdAndExchangeCodeAndStockCode(
                    request.getUserId(),
                    request.getExchangeCode(),
                    request.getStockCode())) {
                log.warn("[해외 관심종목 추가] 이미 존재하는 종목: {}", request.getStockCode());
                return false;
            }

            // 종목명과 통화 정보 조회 (기본값 설정)
            String stockName = request.getStockCode();  // 기본값
            String currency = getCurrencyByExchange(request.getExchangeCode());

            ForeignWatchlist watchlist = ForeignWatchlist.builder()
                    .userId(request.getUserId())
                    .exchangeCode(request.getExchangeCode())
                    .stockCode(request.getStockCode())
                    .stockName(stockName)
                    .currency(currency)
                    .build();

            repository.save(watchlist);
            log.info("[해외 관심종목 추가 성공] {}", request.getStockCode());
            return true;

        } catch (Exception e) {
            log.error("[해외 관심종목 추가 실패]", e);
            return false;
        }
    }

    /**
     * 관심종목 목록 조회
     * @param userId 사용자 ID
     * @return 관심종목 목록
     */
    @Transactional(readOnly = true)
    public ForeignWatchlistResponse getWatchlist(Long userId) {
        log.info("[해외 관심종목 조회] 사용자: {}", userId);

        try {
            List<ForeignWatchlist> watchlists = repository.findByUserIdOrderByCreatedAtDesc(userId);
            List<ForeignWatchlistResponse.WatchlistItem> items = new ArrayList<>();

            for (ForeignWatchlist watchlist : watchlists) {
                // 현재가 조회 시도 (실패 시 기본값 사용)
                String currentPrice = "0";
                String changeRate = "0";

                try {
                    var priceResponse = foreignStockService.getCurrentPrice(
                            watchlist.getExchangeCode(),
                            watchlist.getStockCode()
                    );
                    currentPrice = priceResponse.getLast();
                    // 등락률 계산 (간단히 현재가-기준가/기준가*100)
                    if (priceResponse.getBase() != null && !priceResponse.getBase().isEmpty()) {
                        double current = Double.parseDouble(priceResponse.getLast());
                        double base = Double.parseDouble(priceResponse.getBase());
                        changeRate = String.format("%.2f", ((current - base) / base * 100));
                    }
                } catch (Exception e) {
                    log.warn("[현재가 조회 실패] {}: {}", watchlist.getStockCode(), e.getMessage());
                }

                ForeignWatchlistResponse.WatchlistItem item = ForeignWatchlistResponse.WatchlistItem.builder()
                        .stockCode(watchlist.getStockCode())
                        .stockName(watchlist.getStockName())
                        .exchangeCode(watchlist.getExchangeCode())
                        .currentPrice(currentPrice)
                        .changeRate(changeRate)
                        .currency(watchlist.getCurrency())
                        .build();

                items.add(item);
            }

            log.info("[해외 관심종목 조회 성공] 사용자: {}, 종목수: {}", userId, items.size());
            return ForeignWatchlistResponse.builder()
                    .watchlist(items)
                    .build();

        } catch (Exception e) {
            log.error("[해외 관심종목 조회 실패]", e);
            return ForeignWatchlistResponse.builder()
                    .watchlist(new ArrayList<>())
                    .build();
        }
    }

    /**
     * 관심종목 삭제
     * @param userId 사용자 ID
     * @param exchangeCode 거래소코드
     * @param stockCode 종목코드
     * @return 성공 여부
     */
    public boolean removeWatchlist(Long userId, String exchangeCode, String stockCode) {
        log.info("[해외 관심종목 삭제] 사용자: {}, 거래소: {}, 종목: {}",
                userId, exchangeCode, stockCode);

        try {
            if (!repository.existsByUserIdAndExchangeCodeAndStockCode(userId, exchangeCode, stockCode)) {
                log.warn("[해외 관심종목 삭제] 존재하지 않는 종목: {}", stockCode);
                return false;
            }

            repository.deleteByUserIdAndExchangeCodeAndStockCode(userId, exchangeCode, stockCode);
            log.info("[해외 관심종목 삭제 성공] {}", stockCode);
            return true;

        } catch (Exception e) {
            log.error("[해외 관심종목 삭제 실패]", e);
            return false;
        }
    }

    /**
     * 거래소별 통화 코드 반환
     * @param exchangeCode 거래소코드
     * @return 통화코드
     */
    private String getCurrencyByExchange(String exchangeCode) {
        return switch (exchangeCode.toUpperCase()) {
            case "NYS", "NYSE", "NAS", "NASD", "AMS", "AMEX" -> "USD";
            case "HKS", "SEHK" -> "HKD";
            case "TSE", "TKSE" -> "JPY";
            case "SHS", "SZS" -> "CNY";
            case "HSX", "HNX" -> "VND";
            default -> "USD";
        };
    }
}

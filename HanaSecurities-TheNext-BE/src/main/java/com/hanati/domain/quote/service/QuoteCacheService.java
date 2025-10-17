package com.hanati.domain.quote.service;

import com.hanati.domain.quote.dto.RealtimeQuoteResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class QuoteCacheService {

    // 종목별 실시간 호가 데이터 캐시
    private final Map<String, CachedQuote> quoteCache = new ConcurrentHashMap<>();

    /**
     * 실시간 호가 데이터 저장 (2초 TTL)
     */
    public void saveQuote(String stockCode, RealtimeQuoteResponse quote) {
        quoteCache.put(stockCode, new CachedQuote(quote, LocalDateTime.now()));
        log.debug("실시간 호가 캐시 저장: {}", stockCode);
    }

    /**
     * 실시간 호가 데이터 조회 (2초 이내 데이터만 반환)
     */
    public RealtimeQuoteResponse getQuote(String stockCode) {
        CachedQuote cached = quoteCache.get(stockCode);

        if (cached == null) {
            return null;
        }

        // 2초 TTL 체크
        if (cached.isExpired()) {
            quoteCache.remove(stockCode);
            log.debug("만료된 호가 데이터 삭제: {}", stockCode);
            return null;
        }

        return cached.getQuote();
    }

    /**
     * 캐시된 데이터 클래스
     */
    private static class CachedQuote {
        private final RealtimeQuoteResponse quote;
        private final LocalDateTime createdAt;

        public CachedQuote(RealtimeQuoteResponse quote, LocalDateTime createdAt) {
            this.quote = quote;
            this.createdAt = createdAt;
        }

        public RealtimeQuoteResponse getQuote() {
            return quote;
        }

        public boolean isExpired() {
            return LocalDateTime.now().isAfter(createdAt.plusSeconds(2));
        }
    }

    /**
     * 만료된 캐시 정리 (주기적으로 호출)
     */
    public void cleanExpiredCache() {
        quoteCache.entrySet().removeIf(entry -> entry.getValue().isExpired());
        log.debug("만료된 캐시 정리 완료");
    }
}

package com.hanati.domain.foreignquote.service;

import com.hanati.domain.foreignquote.dto.ForeignQuoteData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 해외주식 실시간 호가 캐시 서비스
 */
@Service
@Slf4j
public class ForeignQuoteCacheService {

    // 종목별 실시간 호가 데이터 캐시
    private final Map<String, CachedQuote> quoteCache = new ConcurrentHashMap<>();

    /**
     * 실시간 호가 데이터 저장 (2초 TTL)
     * @param exchangeCode 거래소코드
     * @param stockCode 종목코드
     * @param quote 호가 데이터
     */
    public void saveQuote(String exchangeCode, String stockCode, ForeignQuoteData quote) {
        String key = exchangeCode + ":" + stockCode;
        quoteCache.put(key, new CachedQuote(quote, LocalDateTime.now()));
        log.debug("[해외주식 호가 캐시] 저장: {}", key);
    }

    /**
     * 실시간 호가 데이터 조회 (2초 이내 데이터만 반환)
     * @param exchangeCode 거래소코드
     * @param stockCode 종목코드
     * @return 호가 데이터
     */
    public ForeignQuoteData getQuote(String exchangeCode, String stockCode) {
        String key = exchangeCode + ":" + stockCode;
        CachedQuote cached = quoteCache.get(key);

        if (cached == null) {
            return null;
        }

        // 2초 TTL 체크
        if (cached.isExpired()) {
            quoteCache.remove(key);
            log.debug("[해외주식 호가 캐시] 만료된 데이터 삭제: {}", key);
            return null;
        }

        return cached.getQuote();
    }

    /**
     * 캐시된 데이터 클래스
     */
    private static class CachedQuote {
        private final ForeignQuoteData quote;
        private final LocalDateTime createdAt;

        public CachedQuote(ForeignQuoteData quote, LocalDateTime createdAt) {
            this.quote = quote;
            this.createdAt = createdAt;
        }

        public ForeignQuoteData getQuote() {
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
        log.debug("[해외주식 호가 캐시] 만료된 캐시 정리 완료");
    }

    /**
     * 모든 캐시 삭제
     */
    public void clearAll() {
        quoteCache.clear();
        log.info("[해외주식 호가 캐시] 전체 삭제");
    }
}

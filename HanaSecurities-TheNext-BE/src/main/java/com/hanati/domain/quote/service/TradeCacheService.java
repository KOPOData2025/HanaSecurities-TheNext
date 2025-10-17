package com.hanati.domain.quote.service;

import com.hanati.domain.quote.dto.RealtimeTradeData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class TradeCacheService {

    private final Map<String, CachedTradeData> cache = new ConcurrentHashMap<>();
    private static final long TTL_SECONDS = 2;

    /**
     * 체결가 데이터 저장
     */
    public void saveTrade(String stockCode, RealtimeTradeData data) {
        cache.put(stockCode, new CachedTradeData(data, LocalDateTime.now()));
        log.debug("체결가 캐시 저장: {}", stockCode);
    }

    /**
     * 체결가 데이터 조회
     */
    public RealtimeTradeData getTrade(String stockCode) {
        CachedTradeData cached = cache.get(stockCode);

        if (cached == null) {
            return null;
        }

        // TTL 체크
        if (cached.getCachedAt().plusSeconds(TTL_SECONDS).isBefore(LocalDateTime.now())) {
            cache.remove(stockCode);
            log.debug("체결가 캐시 만료: {}", stockCode);
            return null;
        }

        return cached.getData();
    }

    /**
     * 체결가 데이터 삭제
     */
    public void removeTrade(String stockCode) {
        cache.remove(stockCode);
        log.debug("체결가 캐시 삭제: {}", stockCode);
    }

    /**
     * 만료된 캐시 정리
     */
    public void cleanupExpiredCache() {
        LocalDateTime now = LocalDateTime.now();
        cache.entrySet().removeIf(entry -> {
            boolean expired = entry.getValue().getCachedAt().plusSeconds(TTL_SECONDS).isBefore(now);
            if (expired) {
                log.debug("체결가 캐시 자동 정리: {}", entry.getKey());
            }
            return expired;
        });
    }

    /**
     * 캐시된 체결가 데이터
     */
    private static class CachedTradeData {
        private final RealtimeTradeData data;
        private final LocalDateTime cachedAt;

        public CachedTradeData(RealtimeTradeData data, LocalDateTime cachedAt) {
            this.data = data;
            this.cachedAt = cachedAt;
        }

        public RealtimeTradeData getData() {
            return data;
        }

        public LocalDateTime getCachedAt() {
            return cachedAt;
        }
    }
}

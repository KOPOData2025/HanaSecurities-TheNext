package com.hanati.domain.gold.service;

import com.hanati.domain.gold.dto.GoldQuoteData;
import com.hanati.domain.gold.dto.GoldTradeData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 금현물 실시간 데이터 캐시 서비스
 *
 * WebSocket으로 수신한 실시간 체결/호가 데이터를 캐싱합니다.
 * TTL: 2초 (오래된 데이터 자동 제거)
 */
@Slf4j
@Service
public class GoldQuoteCacheService {

    private static final long TTL_MILLIS = 2000; // 2초

    // 체결 데이터 캐시 (productCode -> CachedData<GoldTradeData>)
    private final Map<String, CachedData<GoldTradeData>> tradeCache = new ConcurrentHashMap<>();

    // 호가 데이터 캐시 (productCode -> CachedData<GoldQuoteData>)
    private final Map<String, CachedData<GoldQuoteData>> quoteCache = new ConcurrentHashMap<>();

    /**
     * 체결 데이터 캐시 저장
     */
    public void cacheTradeData(GoldTradeData tradeData) {
        if (tradeData == null || tradeData.getProductCode() == null) {
            log.warn("[금현물 캐시] 체결 데이터가 null이거나 상품 코드가 없습니다");
            return;
        }

        tradeCache.put(tradeData.getProductCode(), new CachedData<>(tradeData));
        log.debug("[금현물 캐시] 체결 데이터 저장: {}", tradeData.getProductCode());
    }

    /**
     * 호가 데이터 캐시 저장
     */
    public void cacheQuoteData(GoldQuoteData quoteData) {
        if (quoteData == null || quoteData.getProductCode() == null) {
            log.warn("[금현물 캐시] 호가 데이터가 null이거나 상품 코드가 없습니다");
            return;
        }

        quoteCache.put(quoteData.getProductCode(), new CachedData<>(quoteData));
        log.debug("[금현물 캐시] 호가 데이터 저장: {}", quoteData.getProductCode());
    }

    /**
     * 체결 데이터 조회 (TTL 체크)
     */
    public GoldTradeData getTradeData(String productCode) {
        CachedData<GoldTradeData> cached = tradeCache.get(productCode);

        if (cached == null) {
            return null;
        }

        if (cached.isExpired()) {
            tradeCache.remove(productCode);
            log.debug("[금현물 캐시] 체결 데이터 만료: {}", productCode);
            return null;
        }

        return cached.getData();
    }

    /**
     * 호가 데이터 조회 (TTL 체크)
     */
    public GoldQuoteData getQuoteData(String productCode) {
        CachedData<GoldQuoteData> cached = quoteCache.get(productCode);

        if (cached == null) {
            return null;
        }

        if (cached.isExpired()) {
            quoteCache.remove(productCode);
            log.debug("[금현물 캐시] 호가 데이터 만료: {}", productCode);
            return null;
        }

        return cached.getData();
    }

    /**
     * 캐시 전체 클리어
     */
    public void clearAll() {
        tradeCache.clear();
        quoteCache.clear();
        log.info("[금현물 캐시] 전체 캐시 클리어");
    }

    /**
     * 만료된 캐시 정리
     */
    public void cleanupExpired() {
        tradeCache.entrySet().removeIf(entry -> entry.getValue().isExpired());
        quoteCache.entrySet().removeIf(entry -> entry.getValue().isExpired());
        log.debug("[금현물 캐시] 만료된 캐시 정리 완료");
    }

    /**
     * 캐시 데이터 래퍼 (타임스탬프 포함)
     */
    private class CachedData<T> {
        private final T data;
        private final long timestamp;

        public CachedData(T data) {
            this.data = data;
            this.timestamp = System.currentTimeMillis();
        }

        public T getData() {
            return data;
        }

        public boolean isExpired() {
            return (System.currentTimeMillis() - timestamp) > TTL_MILLIS;
        }
    }
}

package com.hanati.domain.gold.service;

import com.hanati.domain.gold.dto.GoldQuoteData;

/**
 * 금현물 실시간 호가 데이터 리스너 인터페이스
 *
 * WebSocket으로부터 호가 데이터를 수신했을 때 호출됩니다.
 */
@FunctionalInterface
public interface QuoteDataListener {

    /**
     * 호가 데이터 수신 시 호출
     *
     * @param quoteData 호가 데이터
     */
    void onQuoteData(GoldQuoteData quoteData);
}

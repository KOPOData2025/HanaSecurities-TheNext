package com.hanati.domain.gold.service;

import com.hanati.domain.gold.dto.GoldTradeData;

/**
 * 금현물 실시간 체결 데이터 리스너 인터페이스
 *
 * WebSocket으로부터 체결 데이터를 수신했을 때 호출됩니다.
 */
@FunctionalInterface
public interface TradeDataListener {

    /**
     * 체결 데이터 수신 시 호출
     *
     * @param tradeData 체결 데이터
     */
    void onTradeData(GoldTradeData tradeData);
}

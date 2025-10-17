package com.hanati.domain.quote.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RealtimeTradeData {
    private String stockCode;           // 종목코드
    private String timestamp;           // 타임스탬프
    private String tradeTime;           // 체결 시간

    // 가격 정보
    private String currentPrice;        // 현재가
    private String priceChangeSign;     // 전일 대비 부호 (1:상한, 2:상승, 3:보합, 4:하한, 5:하락)
    private String priceChange;         // 전일 대비
    private String changeRate;          // 전일 대비율

    // 시고저 정보
    private String openPrice;           // 시가
    private String highPrice;           // 고가
    private String lowPrice;            // 저가

    // 거래량 정보
    private String tradeVolume;         // 체결 거래량
    private String accumulatedVolume;   // 누적 거래량
    private String accumulatedAmount;   // 누적 거래 대금

    // 호가 정보
    private String askPrice1;           // 매도호가1
    private String bidPrice1;           // 매수호가1
    private String totalAskRemain;      // 총 매도호가 잔량
    private String totalBidRemain;      // 총 매수호가 잔량

    // 체결 강도
    private String tradeStrength;       // 체결강도
    private String sellCount;           // 매도 체결 건수
    private String buyCount;            // 매수 체결 건수
}

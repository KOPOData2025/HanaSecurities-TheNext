package com.hanati.domain.stock.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockInfoResponse {

    private String stockCode;
    private String stockName;
    private boolean nxtSupported;  // NXT 지원 여부
    private boolean nxtTradeStopped;  // NXT 거래정지 여부
    private String currentPrice;
    private String changeSign;
    private String changePrice;
    private String changeRate;
}

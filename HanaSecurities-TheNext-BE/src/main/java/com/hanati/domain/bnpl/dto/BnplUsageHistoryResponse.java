package com.hanati.domain.bnpl.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 후불결제 이용내역 조회 응답 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BnplUsageHistoryResponse {

    private boolean success;
    private String message;
    private List<UsageItem> usageHistory;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UsageItem {
        private String usageDate;      // 이용날짜 (MM.DD 형식)
        private String merchantName;   // 사용처
        private Long amount;           // 금액
    }
}

package com.hanati.domain.bnpl.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 후불결제 정보 조회 응답 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BnplInfoResponse {

    private boolean success;
    private String message;
    private BnplInfoData data;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BnplInfoData {
        private Integer paymentDay;        // 납부일
        private String paymentAccount;     // 납부계좌
        private Long usageAmount;          // 이용금액
        private Long creditLimit;          // 한도
        private String applicationDate;    // 신청일
        private String approvalStatus;     // 승인여부
    }
}

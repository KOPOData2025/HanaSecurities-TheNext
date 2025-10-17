package com.hanati.domain.bnpl.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 후불결제 신청 응답 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BnplApplicationResponse {

    private boolean success;
    private String message;
    private Long creditLimit;  // 이용한도 (300,000원 고정)
    private String approvalStatus;  // 승인여부
}

package com.hanati.domain.bnpl.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 후불결제 신청 요청 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BnplApplicationRequest {

    private String userId;          // 사용자 ID
    private Integer paymentDay;     // 납부일 (5, 15, 25)
    private String paymentAccount;  // 납부계좌
}

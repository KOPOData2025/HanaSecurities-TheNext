package com.hanati.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 2차 비밀번호 검증 요청 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerifySecondaryPasswordRequest {

    private Long userId;                    // 사용자 ID
    private String secondaryPassword;        // 2차 비밀번호 (평문 4자리)
}

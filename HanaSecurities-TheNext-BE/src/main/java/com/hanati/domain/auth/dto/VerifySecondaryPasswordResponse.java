package com.hanati.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 2차 비밀번호 검증 응답 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerifySecondaryPasswordResponse {

    private boolean success;        // 검증 성공 여부
    private String message;          // 응답 메시지
}

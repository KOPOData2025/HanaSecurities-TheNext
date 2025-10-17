package com.hanati.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * WebAuthn 로그인 시작 요청 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebAuthnAuthenticationStartRequest {

    private String mobileNo;          // 전화번호 (사용자 식별용)
}

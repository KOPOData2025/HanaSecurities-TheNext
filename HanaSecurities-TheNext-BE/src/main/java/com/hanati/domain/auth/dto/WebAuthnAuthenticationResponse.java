package com.hanati.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * WebAuthn 로그인 완료 응답 DTO (JWT 토큰 포함)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebAuthnAuthenticationResponse {

    private boolean success;              // 성공 여부
    private String message;               // 메시지
    private String accessToken;           // JWT Access Token
    private Long userId;                  // 사용자 ID
    private String userName;              // 사용자 이름
    private Long expiresIn;               // 토큰 만료 시간 (초)
}

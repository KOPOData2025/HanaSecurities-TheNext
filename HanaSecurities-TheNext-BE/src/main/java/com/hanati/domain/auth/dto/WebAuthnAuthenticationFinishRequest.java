package com.hanati.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * WebAuthn 로그인 완료 요청 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebAuthnAuthenticationFinishRequest {

    private String mobileNo;              // 전화번호 (사용자 식별용)
    private String credentialId;          // Base64 인코딩된 Credential ID
    private String authenticatorData;     // Base64 인코딩된 Authenticator Data
    private String clientDataJSON;        // Base64 인코딩된 Client Data JSON
    private String signature;             // Base64 인코딩된 서명
}

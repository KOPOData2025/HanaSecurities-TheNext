package com.hanati.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * WebAuthn 등록 완료 요청 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebAuthnRegistrationFinishRequest {

    private Long userId;                  // 사용자 ID
    private String credentialId;          // Base64 인코딩된 Credential ID
    private String publicKey;             // Base64 인코딩된 공개키 (COSE 형식)
    private String attestationObject;     // Base64 인코딩된 Attestation Object
    private String clientDataJSON;        // Base64 인코딩된 Client Data JSON
}

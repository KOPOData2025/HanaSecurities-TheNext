package com.hanati.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * WebAuthn 로그인 시작 응답 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebAuthnAuthenticationStartResponse {

    private String challenge;                      // Base64 인코딩된 난수
    private List<AllowedCredential> allowCredentials;  // 허용된 인증기 목록
    private Integer timeout;                       // 타임아웃 (밀리초)

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AllowedCredential {
        private String id;                         // Credential ID (Base64)
        private String type;                       // "public-key"
        private List<String> transports;           // ["internal"] 등
    }
}

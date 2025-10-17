package com.hanati.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * WebAuthn 등록 시작 응답 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebAuthnRegistrationStartResponse {

    private String challenge;         // Base64 인코딩된 난수
    private String rpId;             // Relying Party ID (도메인)
    private String rpName;           // 서비스 이름
    private Long userId;             // 생성된 사용자 ID
    private String userName;         // 사용자 이름
    private Integer timeout;         // 타임아웃 (밀리초)
}

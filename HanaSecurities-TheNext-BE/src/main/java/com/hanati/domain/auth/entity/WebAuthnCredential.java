package com.hanati.domain.auth.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * WebAuthn 지문 인증 정보 엔티티
 * 테이블: WEBAUTHN_CREDENTIALS
 * 용도: 비밀번호 없는 지문 기반 로그인/회원가입
 */
@Entity
@Table(name = "WEBAUTHN_CREDENTIALS")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebAuthnCredential {

    @Id
    @Column(name = "credential_id", nullable = false, length = 200)
    private String credentialId;                   // 인증기 고유 ID (Base64)

    @Column(name = "user_id", nullable = false)
    private Long userId;                           // 사용자 ID (FK)

    @Lob
    @Column(name = "public_key", nullable = false)
    private String publicKey;                      // 공개키 (서명 검증용, Base64)

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;               // 등록 일시

    @Column(name = "last_used_at")
    private LocalDateTime lastUsedAt;              // 마지막 사용 일시

    /**
     * 마지막 사용 일시 업데이트
     */
    public void updateLastUsedAt() {
        this.lastUsedAt = LocalDateTime.now();
    }
}

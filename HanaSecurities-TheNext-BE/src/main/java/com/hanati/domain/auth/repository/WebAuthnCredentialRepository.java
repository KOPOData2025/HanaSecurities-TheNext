package com.hanati.domain.auth.repository;

import com.hanati.domain.auth.entity.WebAuthnCredential;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * WebAuthn 인증 정보 Repository
 * 테이블: WEBAUTHN_CREDENTIALS
 */
@Repository
public interface WebAuthnCredentialRepository extends JpaRepository<WebAuthnCredential, String> {

    /**
     * 사용자 ID로 등록된 모든 인증기 조회
     *
     * @param userId 사용자 ID
     * @return 인증기 리스트
     */
    List<WebAuthnCredential> findByUserId(Long userId);

    /**
     * 사용자 ID와 Credential ID로 조회
     *
     * @param credentialId 인증기 ID
     * @param userId       사용자 ID
     * @return 인증기 정보
     */
    Optional<WebAuthnCredential> findByCredentialIdAndUserId(String credentialId, Long userId);

    /**
     * 사용자가 등록한 인증기 존재 여부 확인
     *
     * @param userId 사용자 ID
     * @return 존재 여부
     */
    boolean existsByUserId(Long userId);

    /**
     * Credential ID로 존재 여부 확인
     *
     * @param credentialId 인증기 ID
     * @return 존재 여부
     */
    boolean existsByCredentialId(String credentialId);
}

package com.hanati.domain.auth.repository;

import com.hanati.domain.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 사용자 Repository
 * 테이블: USERS
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 전화번호로 사용자 조회
     *
     * @param mobileNo 전화번호
     * @return 사용자 정보
     */
    Optional<User> findByMobileNo(String mobileNo);

    /**
     * 이메일로 사용자 조회
     *
     * @param email 이메일
     * @return 사용자 정보
     */
    Optional<User> findByEmail(String email);

    /**
     * 전화번호 존재 여부 확인
     *
     * @param mobileNo 전화번호
     * @return 존재 여부
     */
    boolean existsByMobileNo(String mobileNo);

    /**
     * 이메일 존재 여부 확인
     *
     * @param email 이메일
     * @return 존재 여부
     */
    boolean existsByEmail(String email);
}

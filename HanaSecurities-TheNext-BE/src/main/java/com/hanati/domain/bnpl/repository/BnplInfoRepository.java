package com.hanati.domain.bnpl.repository;

import com.hanati.domain.bnpl.entity.BnplInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 후불결제 정보 Repository
 */
@Repository
public interface BnplInfoRepository extends JpaRepository<BnplInfo, String> {

    /**
     * 사용자 ID로 후불결제 정보 조회
     */
    Optional<BnplInfo> findByUserId(String userId);

    /**
     * 사용자 ID로 후불결제 정보 존재 여부 확인
     */
    boolean existsByUserId(String userId);
}

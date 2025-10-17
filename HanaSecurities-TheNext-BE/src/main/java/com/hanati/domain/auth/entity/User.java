package com.hanati.domain.auth.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 사용자 엔티티
 * 테이블: USERS
 * 용도: 회원 기본 정보 저장
 */
@Entity
@Table(name = "USERS")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_seq_gen")
    @SequenceGenerator(name = "users_seq_gen", sequenceName = "USERS_SEQ", allocationSize = 1)
    @Column(name = "user_id", nullable = false)
    private Long userId;                           // 사용자 ID (PK, 자동 증가)

    @Column(name = "user_name", nullable = false, length = 100)
    private String userName;                       // 사용자 이름

    @Column(name = "mobile_no", nullable = false, unique = true, length = 20)
    private String mobileNo;                       // 전화번호 (중복 불가)

    @Column(name = "gender", nullable = false, length = 10)
    private String gender;                         // 성별

    @Column(name = "birth", nullable = false, unique = true)
    private LocalDate birth;                       // 생년월일 (중복 불가)

    @Column(name = "email", nullable = false, length = 255)
    private String email;                          // 이메일

    @Column(name = "address", length = 500)
    private String address;                        // 주소

    @Column(name = "secondary_password_hash", nullable = false, length = 128)
    private String secondaryPasswordHash;          // 2차 비밀번호 해시
}

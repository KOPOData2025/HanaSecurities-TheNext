package com.hanati.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * WebAuthn 등록 시작 요청 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebAuthnRegistrationStartRequest {

    private String userName;          // 사용자 이름
    private String mobileNo;          // 전화번호
    private String gender;            // 성별
    private String birth;             // 생년월일 (YYYY-MM-DD)
    private String email;             // 이메일
    private String address;           // 주소
    private String secondaryPassword; // 2차 비밀번호
}

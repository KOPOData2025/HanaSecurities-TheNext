package com.hanati.domain.bnpl.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 후불결제 정보 엔티티
 */
@Entity
@Table(name = "BNPL_INFO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BnplInfo {

    @Id
    @Column(name = "user_id", length = 50)
    private String userId;

    @Column(name = "payment_day", nullable = false)
    private Integer paymentDay;  // 5, 15, 25

    @Column(name = "payment_account", length = 50, nullable = false)
    private String paymentAccount;

    @Column(name = "usage_amount")
    private Long usageAmount;

    @Column(name = "credit_limit")
    private Long creditLimit;  // 300,000원 고정

    @Column(name = "application_date")
    private LocalDate applicationDate;

    @Column(name = "approval_status", length = 20)
    private String approvalStatus;  // APPROVED, PENDING, REJECTED

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
        if (applicationDate == null) {
            applicationDate = LocalDate.now();
        }
        if (approvalStatus == null) {
            approvalStatus = "APPROVED";
        }
        if (creditLimit == null) {
            creditLimit = 300000L;
        }
        if (usageAmount == null) {
            usageAmount = 0L;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

package com.hanati.domain.bnpl.service;

import com.hanati.domain.bnpl.dto.*;
import com.hanati.domain.bnpl.entity.BnplInfo;
import com.hanati.domain.bnpl.entity.BnplUsageHistory;
import com.hanati.domain.bnpl.repository.BnplInfoRepository;
import com.hanati.domain.bnpl.repository.BnplUsageHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 후불결제(BNPL) 서비스
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BnplService {

    private final BnplInfoRepository bnplInfoRepository;
    private final BnplUsageHistoryRepository bnplUsageHistoryRepository;

    private static final Long FIXED_CREDIT_LIMIT = 300000L;  // 고정 한도 300,000원

    /**
     * 후불결제 신청 (납부일, 납부계좌 선택 → 한도 산정)
     */
    @Transactional
    public BnplApplicationResponse applyBnpl(BnplApplicationRequest request) {
        log.info("[후불결제 신청] 사용자: {}, 납부일: {}, 계좌: {}",
                request.getUserId(), request.getPaymentDay(), request.getPaymentAccount());

        try {
            // 입력 검증
            if (!isValidPaymentDay(request.getPaymentDay())) {
                return BnplApplicationResponse.builder()
                        .success(false)
                        .message("납부일은 5, 15, 25 중 하나여야 합니다.")
                        .build();
            }

            // 이미 신청한 사용자인지 확인
            if (bnplInfoRepository.existsByUserId(request.getUserId())) {
                return BnplApplicationResponse.builder()
                        .success(false)
                        .message("이미 후불결제에 가입되어 있습니다.")
                        .build();
            }

            // 후불결제 정보 저장
            BnplInfo bnplInfo = BnplInfo.builder()
                    .userId(request.getUserId())
                    .paymentDay(request.getPaymentDay())
                    .paymentAccount(request.getPaymentAccount())
                    .usageAmount(0L)
                    .creditLimit(FIXED_CREDIT_LIMIT)
                    .applicationDate(LocalDate.now())
                    .approvalStatus("APPROVED")  // 무조건 승인
                    .build();

            bnplInfoRepository.save(bnplInfo);

            log.info("[후불결제 신청 성공] 사용자: {}, 한도: {}원", request.getUserId(), FIXED_CREDIT_LIMIT);

            return BnplApplicationResponse.builder()
                    .success(true)
                    .message("후불결제 신청이 승인되었습니다.")
                    .creditLimit(FIXED_CREDIT_LIMIT)
                    .approvalStatus("APPROVED")
                    .build();

        } catch (Exception e) {
            log.error("[후불결제 신청 실패] 사용자: {}, 에러: {}", request.getUserId(), e.getMessage(), e);
            return BnplApplicationResponse.builder()
                    .success(false)
                    .message("후불결제 신청 중 오류가 발생했습니다: " + e.getMessage())
                    .build();
        }
    }

    /**
     * 후불결제 이용내역 조회
     */
    @Transactional(readOnly = true)
    public BnplUsageHistoryResponse getUsageHistory(String userId) {
        log.info("[후불결제 이용내역 조회] 사용자: {}", userId);

        try {
            List<BnplUsageHistory> historyList = bnplUsageHistoryRepository
                    .findByUserIdOrderByUsageDateDesc(userId);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM.dd");

            List<BnplUsageHistoryResponse.UsageItem> items = historyList.stream()
                    .map(history -> BnplUsageHistoryResponse.UsageItem.builder()
                            .usageDate(history.getUsageDate().format(formatter))
                            .merchantName(history.getMerchantName())
                            .amount(history.getAmount())
                            .build())
                    .collect(Collectors.toList());

            log.info("[후불결제 이용내역 조회 성공] 사용자: {}, 건수: {}", userId, items.size());

            return BnplUsageHistoryResponse.builder()
                    .success(true)
                    .message("이용내역 조회 성공")
                    .usageHistory(items)
                    .build();

        } catch (Exception e) {
            log.error("[후불결제 이용내역 조회 실패] 사용자: {}, 에러: {}", userId, e.getMessage(), e);
            return BnplUsageHistoryResponse.builder()
                    .success(false)
                    .message("이용내역 조회 실패: " + e.getMessage())
                    .usageHistory(List.of())
                    .build();
        }
    }

    /**
     * 후불결제 정보 조회
     */
    @Transactional(readOnly = true)
    public BnplInfoResponse getBnplInfo(String userId) {
        log.info("[후불결제 정보 조회] 사용자: {}", userId);

        try {
            BnplInfo bnplInfo = bnplInfoRepository.findByUserId(userId)
                    .orElseThrow(() -> new RuntimeException("후불결제 정보가 없습니다."));

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            BnplInfoResponse.BnplInfoData data = BnplInfoResponse.BnplInfoData.builder()
                    .paymentDay(bnplInfo.getPaymentDay())
                    .paymentAccount(bnplInfo.getPaymentAccount())
                    .usageAmount(bnplInfo.getUsageAmount())
                    .creditLimit(bnplInfo.getCreditLimit())
                    .applicationDate(bnplInfo.getApplicationDate().format(formatter))
                    .approvalStatus(bnplInfo.getApprovalStatus())
                    .build();

            log.info("[후불결제 정보 조회 성공] 사용자: {}", userId);

            return BnplInfoResponse.builder()
                    .success(true)
                    .message("후불결제 정보 조회 성공")
                    .data(data)
                    .build();

        } catch (Exception e) {
            log.error("[후불결제 정보 조회 실패] 사용자: {}, 에러: {}", userId, e.getMessage(), e);
            return BnplInfoResponse.builder()
                    .success(false)
                    .message("후불결제 정보 조회 실패: " + e.getMessage())
                    .build();
        }
    }

    /**
     * 납부일 유효성 검증
     */
    private boolean isValidPaymentDay(Integer paymentDay) {
        return paymentDay != null && (paymentDay == 5 || paymentDay == 15 || paymentDay == 25);
    }
}

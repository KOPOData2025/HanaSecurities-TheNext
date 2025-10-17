package com.hanati.domain.gold.service;

import com.hanati.domain.gold.dto.GoldOrderRequest;
import com.hanati.domain.gold.dto.GoldOrderResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.UUID;

/**
 * 금현물 주문 서비스
 * 
 * 주문 처리 및 잔고 조회 기능 제공
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GoldOrderService {

    private final Random random = new Random();

    /**
     * 금현물 매수 주문
     */
    public Object buyOrder(GoldOrderRequest request) {
        log.info("[금현물 Order Service] 매수 주문 처리 - 계좌: {}, 상품: {}, 수량: {}, 가격: {}",
                request.getAccountNumber(), request.getProductCode(), request.getQuantity(), request.getPrice());

        try {
            // 주문 검증
            validateOrder(request);

            // 실제로는 키움 API를 호출하여 주문 전송
            // 여기서는 시뮬레이션
            String orderNumber = generateOrderNumber();
            String productName = getProductName(request.getProductCode());

            // 주문 성공 응답 생성
            return GoldOrderResponse.builder()
                    .success(true)
                    .orderNumber(orderNumber)
                    .accountNumber(request.getAccountNumber())
                    .productCode(request.getProductCode())
                    .productName(productName)
                    .orderSide("매수")
                    .orderedQuantity(request.getQuantity())
                    .executedQuantity(request.getQuantity()) // 시뮬레이션: 전량 체결
                    .orderPrice(request.getPrice())
                    .executionPrice(request.getPrice())
                    .totalAmount(request.getPrice() * request.getQuantity())
                    .message("주문이 체결되었습니다")
                    .build();

        } catch (Exception e) {
            log.error("[금현물 Order Service] 매수 주문 실패", e);
            return GoldOrderResponse.builder()
                    .success(false)
                    .message("주문 처리 중 오류가 발생했습니다")
                    .errorCode("ORDER_ERROR")
                    .build();
        }
    }

    /**
     * 금현물 매도 주문
     */
    public Object sellOrder(GoldOrderRequest request) {
        log.info("[금현물 Order Service] 매도 주문 처리 - 계좌: {}, 상품: {}, 수량: {}, 가격: {}",
                request.getAccountNumber(), request.getProductCode(), request.getQuantity(), request.getPrice());

        try {
            // 주문 검증
            validateOrder(request);

            // 실제로는 키움 API를 호출하여 주문 전송
            // 여기서는 시뮬레이션
            String orderNumber = generateOrderNumber();
            String productName = getProductName(request.getProductCode());

            // 주문 성공 응답 생성
            return GoldOrderResponse.builder()
                    .success(true)
                    .orderNumber(orderNumber)
                    .accountNumber(request.getAccountNumber())
                    .productCode(request.getProductCode())
                    .productName(productName)
                    .orderSide("매도")
                    .orderedQuantity(request.getQuantity())
                    .executedQuantity(request.getQuantity()) // 시뮬레이션: 전량 체결
                    .orderPrice(request.getPrice())
                    .executionPrice(request.getPrice())
                    .totalAmount(request.getPrice() * request.getQuantity())
                    .message("주문이 체결되었습니다")
                    .build();

        } catch (Exception e) {
            log.error("[금현물 Order Service] 매도 주문 실패", e);
            return GoldOrderResponse.builder()
                    .success(false)
                    .message("주문 처리 중 오류가 발생했습니다")
                    .errorCode("ORDER_ERROR")
                    .build();
        }
    }

    /**
     * 금현물 잔고 조회
     */
    public Object getBalance(String accountNumber) {
        log.info("[금현물 Order Service] 잔고 조회 - 계좌: {}", accountNumber);
        
        // 실제로는 키움 API를 호출하여 잔고 조회
        // 여기서는 시뮬레이션 데이터 반환
        return "잔고 조회 기능은 추후 구현 예정";
    }

    /**
     * 주문 검증
     */
    private void validateOrder(GoldOrderRequest request) {
        if (request.getAccountNumber() == null || request.getAccountNumber().isEmpty()) {
            throw new IllegalArgumentException("계좌번호가 필요합니다");
        }
        if (request.getProductCode() == null || request.getProductCode().isEmpty()) {
            throw new IllegalArgumentException("상품코드가 필요합니다");
        }
        if (request.getQuantity() == null || request.getQuantity() <= 0) {
            throw new IllegalArgumentException("수량은 1개 이상이어야 합니다");
        }
        if (request.getPrice() == null || request.getPrice() <= 0) {
            throw new IllegalArgumentException("가격은 0원보다 커야 합니다");
        }
    }

    /**
     * 주문번호 생성
     */
    private String generateOrderNumber() {
        return "ORD" + System.currentTimeMillis() + random.nextInt(1000);
    }

    /**
     * 상품명 조회
     */
    private String getProductName(String productCode) {
        return switch (productCode) {
            case "M04020000" -> "금 99.99% 1Kg";
            case "M04020100" -> "미니금 99.99% 100g";
            default -> "금현물";
        };
    }
}

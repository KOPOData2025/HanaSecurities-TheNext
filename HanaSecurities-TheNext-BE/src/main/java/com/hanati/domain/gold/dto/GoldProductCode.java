package com.hanati.domain.gold.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 금현물 상품 코드 enum
 *
 * 키움증권 금현물 상품:
 * - M04020000: 금 99.99_1Kg (1000g)
 * - M04020100: 미니금 99.99_100g (100g)
 */
@Getter
@RequiredArgsConstructor
public enum GoldProductCode {

    /**
     * 금 99.99_1Kg
     * - 거래 단위: 1Kg (1000g)
     * - 최소 주문 수량: 1
     * - 가격 단위: 원/g
     */
    GOLD_1KG("M04020000", "금 99.99_1Kg", 1000, "1Kg"),

    /**
     * 미니금 99.99_100g
     * - 거래 단위: 100g
     * - 최소 주문 수량: 1
     * - 가격 단위: 원/g
     */
    MINI_GOLD_100G("M04020100", "미니금 99.99_100g", 100, "100g");

    /**
     * 상품 코드 (키움증권 API 사용)
     */
    private final String code;

    /**
     * 상품명
     */
    private final String name;

    /**
     * 거래 단위 (g)
     */
    private final int unitWeight;

    /**
     * 거래 단위 표시
     */
    private final String unitDisplay;

    /**
     * 상품 코드로 enum 찾기
     * @param code 상품 코드
     * @return GoldProductCode enum
     */
    public static GoldProductCode fromCode(String code) {
        for (GoldProductCode product : values()) {
            if (product.getCode().equals(code)) {
                return product;
            }
        }
        throw new IllegalArgumentException("유효하지 않은 금현물 상품 코드입니다: " + code);
    }

    /**
     * 상품 코드가 유효한지 검증
     * @param code 상품 코드
     * @return 유효 여부
     */
    public static boolean isValidCode(String code) {
        try {
            fromCode(code);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}

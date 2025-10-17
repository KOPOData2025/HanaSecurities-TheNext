package com.hanati.domain.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * 상품 목록 조회 응답 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductListResponse {

    private boolean success;
    private String message;
    private List<ProductItem> products;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductItem {
        private Long productId;              // 상품 ID
        private String productName;          // 상품명
        private String productImageUrl;      // 상품 이미지 URL
        private Integer price;               // 판매가
        private Integer originalPrice;       // 정가
        private BigDecimal discountRate;     // 할인율
        private String seller;               // 판매처
        private BigDecimal rating;           // 평점
        private Integer reviewCount;         // 리뷰 수
    }
}

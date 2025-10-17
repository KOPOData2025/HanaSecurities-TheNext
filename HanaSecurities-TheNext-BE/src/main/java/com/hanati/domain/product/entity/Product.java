package com.hanati.domain.product.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 쇼핑 상품 엔티티
 * 테이블: PRODUCTS
 * 용도: 하나증권 쇼핑 서비스 상품 데이터
 */
@Entity
@Table(name = "PRODUCTS")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "products_seq")
    @SequenceGenerator(name = "products_seq", sequenceName = "PRODUCTS_SEQ", allocationSize = 1)
    @Column(name = "product_id", nullable = false)
    private Long productId;                          // 상품 ID (PK)

    @Column(name = "product_name", nullable = false, length = 200)
    private String productName;                      // 상품명

    @Column(name = "product_image_url", length = 500)
    private String productImageUrl;                  // 상품 이미지 URL

    @Column(name = "price", nullable = false)
    private Integer price;                           // 판매가 (원)

    @Column(name = "original_price")
    private Integer originalPrice;                   // 정가 (원)

    @Column(name = "discount_rate", precision = 5, scale = 2)
    private BigDecimal discountRate;                 // 할인율 (%)

    @Column(name = "seller", length = 100)
    private String seller;                           // 판매처

    @Column(name = "rating", precision = 3, scale = 2)
    private BigDecimal rating;                       // 평점 (0.00 ~ 5.00)

    @Column(name = "review_count")
    private Integer reviewCount;                     // 리뷰 수

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;                 // 등록일시

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;                 // 수정일시

    // 편의 메서드
    public void updateProductInfo(String productName, Integer price, Integer originalPrice, BigDecimal discountRate) {
        this.productName = productName;
        this.price = price;
        this.originalPrice = originalPrice;
        this.discountRate = discountRate;
    }
}

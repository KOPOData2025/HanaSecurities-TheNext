package com.hanati.domain.product.service;

import com.hanati.domain.product.dto.ProductDetailResponse;
import com.hanati.domain.product.dto.ProductListResponse;
import com.hanati.domain.product.entity.Product;
import com.hanati.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 상품 서비스
 * - 상품 목록 조회
 * - 상품 상세 조회
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;

    /**
     * 상품 목록 조회
     * @return 전체 상품 목록 (최신순)
     */
    @Transactional(readOnly = true)
    public ProductListResponse getProductList() {
        log.info("[상품 목록 조회] 시작");

        try {
            // DB에서 전체 상품 조회 (최신순)
            List<Product> products = productRepository.findAllByOrderByCreatedAtDesc();

            log.info("[상품 목록 조회] 성공 - 조회된 상품 수: {}", products.size());

            // Entity -> DTO 변환
            List<ProductListResponse.ProductItem> productItems = products.stream()
                    .map(this::convertToProductItem)
                    .collect(Collectors.toList());

            return ProductListResponse.builder()
                    .success(true)
                    .message("상품 목록 조회 성공")
                    .products(productItems)
                    .build();

        } catch (Exception e) {
            log.error("[상품 목록 조회] 실패 - 에러: {}", e.getMessage(), e);
            return ProductListResponse.builder()
                    .success(false)
                    .message("상품 목록 조회 실패: " + e.getMessage())
                    .products(List.of())
                    .build();
        }
    }

    /**
     * 상품 상세 조회
     * @param productId 상품 ID
     * @return 상품 상세 정보
     */
    @Transactional(readOnly = true)
    public ProductDetailResponse getProductDetail(Long productId) {
        log.info("[상품 상세 조회] 시작 - 상품 ID: {}", productId);

        try {
            // DB에서 상품 조회
            Optional<Product> productOpt = productRepository.findById(productId);

            if (productOpt.isEmpty()) {
                log.warn("[상품 상세 조회] 상품을 찾을 수 없음 - 상품 ID: {}", productId);
                return ProductDetailResponse.builder()
                        .success(false)
                        .message("상품을 찾을 수 없습니다.")
                        .product(null)
                        .build();
            }

            Product product = productOpt.get();
            log.info("[상품 상세 조회] 성공 - 상품명: {}", product.getProductName());

            // Entity -> DTO 변환
            ProductDetailResponse.ProductDetail productDetail = convertToProductDetail(product);

            return ProductDetailResponse.builder()
                    .success(true)
                    .message("상품 상세 조회 성공")
                    .product(productDetail)
                    .build();

        } catch (Exception e) {
            log.error("[상품 상세 조회] 실패 - 상품 ID: {}, 에러: {}", productId, e.getMessage(), e);
            return ProductDetailResponse.builder()
                    .success(false)
                    .message("상품 상세 조회 실패: " + e.getMessage())
                    .product(null)
                    .build();
        }
    }

    /**
     * Product Entity -> ProductItem DTO 변환
     */
    private ProductListResponse.ProductItem convertToProductItem(Product product) {
        return ProductListResponse.ProductItem.builder()
                .productId(product.getProductId())
                .productName(product.getProductName())
                .productImageUrl(product.getProductImageUrl())
                .price(product.getPrice())
                .originalPrice(product.getOriginalPrice())
                .discountRate(product.getDiscountRate())
                .seller(product.getSeller())
                .rating(product.getRating())
                .reviewCount(product.getReviewCount())
                .build();
    }

    /**
     * Product Entity -> ProductDetail DTO 변환
     */
    private ProductDetailResponse.ProductDetail convertToProductDetail(Product product) {
        return ProductDetailResponse.ProductDetail.builder()
                .productId(product.getProductId())
                .productName(product.getProductName())
                .productImageUrl(product.getProductImageUrl())
                .price(product.getPrice())
                .originalPrice(product.getOriginalPrice())
                .discountRate(product.getDiscountRate())
                .seller(product.getSeller())
                .rating(product.getRating())
                .reviewCount(product.getReviewCount())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}

package com.hanati.domain.product.repository;

import com.hanati.domain.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 상품 Repository
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * 상품명으로 검색 (LIKE 검색)
     */
    List<Product> findByProductNameContaining(String productName);

    /**
     * 판매처로 검색
     */
    List<Product> findBySeller(String seller);

    /**
     * 상품 ID로 조회
     */
    Optional<Product> findByProductId(Long productId);

    /**
     * 모든 상품 조회 (최신순 정렬)
     */
    List<Product> findAllByOrderByCreatedAtDesc();

    /**
     * 모든 상품 조회 (평점 높은 순 정렬)
     */
    List<Product> findAllByOrderByRatingDesc();

    /**
     * 모든 상품 조회 (할인율 높은 순 정렬)
     */
    List<Product> findAllByOrderByDiscountRateDesc();
}

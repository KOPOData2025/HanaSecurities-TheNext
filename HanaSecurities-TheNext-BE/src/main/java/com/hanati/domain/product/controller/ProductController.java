package com.hanati.domain.product.controller;

import com.hanati.domain.product.dto.ProductDetailResponse;
import com.hanati.domain.product.dto.ProductListResponse;
import com.hanati.domain.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 쇼핑 상품 REST API Controller
 * - 상품 목록 조회
 * - 상품 상세 조회
 */
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
@Tag(name = "Shopping Product API", description = "쇼핑 상품 조회 API")
public class ProductController {

    private final ProductService productService;

    @Operation(
            summary = "상품 목록 조회",
            description = "전체 상품 목록을 조회합니다. 최신 등록순으로 정렬됩니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = ProductListResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping
    public ResponseEntity<ProductListResponse> getProductList() {
        log.info("[API 호출] 상품 목록 조회");

        try {
            ProductListResponse response = productService.getProductList();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("[API 호출] 상품 목록 조회 실패", e);
            return ResponseEntity.internalServerError()
                    .body(ProductListResponse.builder()
                            .success(false)
                            .message("상품 목록 조회 중 오류가 발생했습니다.")
                            .build());
        }
    }

    @Operation(
            summary = "상품 상세 조회",
            description = "특정 상품의 상세 정보를 조회합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = ProductDetailResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "상품을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/{productId}")
    public ResponseEntity<ProductDetailResponse> getProductDetail(
            @Parameter(description = "상품 ID", required = true, example = "1")
            @PathVariable Long productId
    ) {
        log.info("[API 호출] 상품 상세 조회 - 상품 ID: {}", productId);

        try {
            // 입력 검증
            if (productId == null || productId <= 0) {
                log.error("잘못된 상품 ID: {}", productId);
                return ResponseEntity.badRequest()
                        .body(ProductDetailResponse.builder()
                                .success(false)
                                .message("잘못된 상품 ID입니다.")
                                .build());
            }

            ProductDetailResponse response = productService.getProductDetail(productId);

            // 상품을 찾지 못한 경우
            if (!response.isSuccess()) {
                return ResponseEntity.status(404).body(response);
            }

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.error("잘못된 요청 파라미터", e);
            return ResponseEntity.badRequest()
                    .body(ProductDetailResponse.builder()
                            .success(false)
                            .message("잘못된 요청입니다.")
                            .build());
        } catch (Exception e) {
            log.error("[API 호출] 상품 상세 조회 실패", e);
            return ResponseEntity.internalServerError()
                    .body(ProductDetailResponse.builder()
                            .success(false)
                            .message("상품 상세 조회 중 오류가 발생했습니다.")
                            .build());
        }
    }
}

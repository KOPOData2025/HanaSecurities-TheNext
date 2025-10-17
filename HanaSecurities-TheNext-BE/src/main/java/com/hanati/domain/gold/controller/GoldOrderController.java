package com.hanati.domain.gold.controller;

import com.hanati.domain.gold.dto.GoldOrderRequest;
import com.hanati.domain.gold.dto.GoldOrderResponse;
import com.hanati.domain.gold.service.GoldOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 금현물 주문 관련 REST API 컨트롤러
 *
 * 기능:
 * - 금현물 매수 주문
 * - 금현물 매도 주문
 * - 금현물 잔고 조회
 */
@Slf4j
@RestController
@RequestMapping("/api/gold/order")
@RequiredArgsConstructor
@Tag(name = "Gold Order", description = "금현물 주문 API")
public class GoldOrderController {

    private final GoldOrderService goldOrderService;

    /**
     * 금현물 매수 주문
     * @param request 매수 주문 요청 정보
     * @return 주문 결과
     */
    @Operation(summary = "금현물 매수 주문", description = "금현물 매수 주문을 실행합니다.")
    @PostMapping("/buy")
    public ResponseEntity<GoldOrderResponse> buyOrder(@RequestBody GoldOrderRequest request) {
        log.info("[금현물 Order Controller] 매수 주문 요청 - 상품: {}, 수량: {}", request.getProductCode(), request.getQuantity());
        GoldOrderResponse response = (GoldOrderResponse) goldOrderService.buyOrder(request);
        return ResponseEntity.ok(response);
    }

    /**
     * 금현물 매도 주문
     * @param request 매도 주문 요청 정보
     * @return 주문 결과
     */
    @Operation(summary = "금현물 매도 주문", description = "금현물 매도 주문을 실행합니다.")
    @PostMapping("/sell")
    public ResponseEntity<GoldOrderResponse> sellOrder(@RequestBody GoldOrderRequest request) {
        log.info("[금현물 Order Controller] 매도 주문 요청 - 상품: {}, 수량: {}", request.getProductCode(), request.getQuantity());
        GoldOrderResponse response = (GoldOrderResponse) goldOrderService.sellOrder(request);
        return ResponseEntity.ok(response);
    }

    /**
     * 금현물 잔고 조회
     * @param accountNumber 계좌번호
     * @return 금현물 보유 잔고 정보
     */
    @Operation(summary = "금현물 잔고 조회", description = "계좌의 금현물 보유 잔고를 조회합니다.")
    @GetMapping("/balance")
    public ResponseEntity<?> getBalance(
            @Parameter(description = "계좌번호") @RequestParam String accountNumber) {
        log.info("[금현물 Order Controller] 잔고 조회 요청 - 계좌: {}", accountNumber);
        Object response = goldOrderService.getBalance(accountNumber);
        return ResponseEntity.ok(response);
    }
}

package com.hanati.domain.gold.controller;

import com.hanati.domain.gold.dto.GoldChartResponse;
import com.hanati.domain.gold.dto.GoldCurrentPriceResponse;
import com.hanati.domain.gold.dto.GoldQuoteData;
import com.hanati.domain.gold.service.GoldService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 금현물 조회 관련 REST API 컨트롤러
 *
 * 기능:
 * - 금현물 현재가 조회
 * - 금현물 호가 조회
 * - 금현물 차트 데이터 조회 (분봉, 일봉, 주봉, 월봉)
 */
@Slf4j
@RestController
@RequestMapping("/api/gold")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Gold", description = "금현물 조회 API")
public class GoldController {

    private final GoldService goldService;

    /**
     * 금현물 현재가 조회
     * @param productCode 상품 코드 (M04020000: 금 1Kg, M04020100: 미니금 100g)
     * @return 금현물 현재가 정보
     */
    @Operation(summary = "금현물 현재가 조회", description = "금현물의 현재가 및 시세 정보를 조회합니다.")
    @GetMapping("/current-price")
    public ResponseEntity<GoldCurrentPriceResponse> getCurrentPrice(
            @Parameter(description = "상품 코드 (M04020000, M04020100)") @RequestParam(defaultValue = "M04020000") String productCode) {
        log.info("[금현물 Controller] 현재가 조회 요청 - productCode: {}", productCode);
        GoldCurrentPriceResponse response = goldService.getCurrentPrice(productCode);
        return ResponseEntity.ok(response);
    }

    /**
     * 금현물 호가 조회 (REST API 제한사항: 1단 호가만 제공)
     * @param productCode 상품 코드 (M04020000: 금 1Kg, M04020100: 미니금 100g)
     * @return 금현물 호가 정보 (REST API는 1단 호가만, 전체 10단 호가는 WebSocket 사용 필요)
     */
    @Operation(
        summary = "금현물 호가 조회",
        description = "금현물의 호가 정보를 조회합니다. " +
                      "REST API는 최근 체결 시점의 1단 호가만 제공합니다. " +
                      "전체 10단 호가 정보는 WebSocket(/ws/gold-quote)을 사용하세요."
    )
    @GetMapping("/orderbook")
    public ResponseEntity<GoldQuoteData> getOrderBook(
            @Parameter(description = "상품 코드 (M04020000, M04020100)") @RequestParam(defaultValue = "M04020000") String productCode) {
        log.info("[금현물 Controller] 호가 조회 요청 - productCode: {}", productCode);
        GoldQuoteData response = goldService.getOrderBook(productCode);
        return ResponseEntity.ok(response);
    }

    /**
     * 금현물 분봉 데이터 조회
     * @param productCode 상품 코드
     * @param interval 분봉 간격 (1, 3, 5, 10, 15, 30, 60)
     * @param count 조회할 데이터 개수
     * @return 분봉 차트 데이터
     */
    @Operation(summary = "금현물 분봉 조회", description = "금현물의 분봉 차트 데이터를 조회합니다.")
    @GetMapping("/chart/minute")
    public ResponseEntity<GoldChartResponse> getMinuteChart(
            @Parameter(description = "상품 코드 (M04020000, M04020100)") @RequestParam(defaultValue = "M04020000") String productCode,
            @Parameter(description = "분봉 간격 (1, 3, 5, 10, 15, 30, 60분)") @RequestParam(defaultValue = "5") int interval,
            @Parameter(description = "조회할 데이터 개수") @RequestParam(defaultValue = "100") int count) {
        log.info("[금현물 Controller] 분봉 데이터 조회 요청 - productCode: {}, interval: {}, count: {}", productCode, interval, count);
        GoldChartResponse response = goldService.getMinuteChart(productCode, interval, count);
        return ResponseEntity.ok(response);
    }

    /**
     * 금현물 일/주/월봉 데이터 조회
     * @param period 기간 타입 (day, week, month)
     * @param productCode 상품 코드
     * @param count 조회할 데이터 개수
     * @return 차트 데이터
     */
    @Operation(summary = "금현물 기간별 차트 조회", description = "금현물의 일/주/월봉 차트 데이터를 조회합니다.")
    @GetMapping("/chart/{period}")
    public ResponseEntity<GoldChartResponse> getPeriodChart(
            @Parameter(description = "기간 타입 (day, week, month)") @PathVariable String period,
            @Parameter(description = "상품 코드 (M04020000, M04020100)") @RequestParam(defaultValue = "M04020000") String productCode,
            @Parameter(description = "조회할 데이터 개수") @RequestParam(defaultValue = "100") int count) {
        log.info("[금현물 Controller] {}봉 데이터 조회 요청 - productCode: {}, count: {}", period, productCode, count);
        GoldChartResponse response = goldService.getPeriodChart(productCode, period, count);
        return ResponseEntity.ok(response);
    }
}

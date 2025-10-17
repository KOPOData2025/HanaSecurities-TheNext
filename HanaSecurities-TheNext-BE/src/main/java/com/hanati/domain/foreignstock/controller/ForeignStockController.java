package com.hanati.domain.foreignstock.controller;

import com.hanati.domain.foreignstock.dto.*;
import com.hanati.domain.foreignstock.service.ForeignStockService;
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

@RestController
@RequestMapping("/api/v1/foreign-stock")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
@Tag(name = "Foreign Stock API", description = "해외 주식 가격 및 거래 API")
public class ForeignStockController {

    private final ForeignStockService foreignStockService;

    @Operation(
            summary = "해외주식 현재가 상세 조회",
            description = "해외주식의 현재가 상세 정보를 조회합니다. 지원 거래소: NYS(뉴욕), NAS(나스닥), HKS(홍콩), TSE(도쿄)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = ForeignCurrentPriceResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/current-price")
    public ResponseEntity<ForeignCurrentPriceResponse> getCurrentPrice(
            @Parameter(description = "거래소코드 (NYS: 뉴욕, NAS: 나스닥, HKS: 홍콩, TSE: 도쿄)",
                    required = true,
                    example = "NAS")
            @RequestParam String exchangeCode,

            @Parameter(description = "종목코드 (ex: AAPL, TSLA)",
                    required = true,
                    example = "AAPL")
            @RequestParam String stockCode
    ) {
        log.info("해외주식 현재가 조회 요청 - 거래소: {}, 종목: {}", exchangeCode, stockCode);

        try {
            if (exchangeCode == null || exchangeCode.trim().isEmpty()) {
                log.error("거래소코드가 비어있습니다.");
                return ResponseEntity.badRequest().build();
            }

            if (stockCode == null || stockCode.trim().isEmpty()) {
                log.error("종목코드가 비어있습니다.");
                return ResponseEntity.badRequest().build();
            }

            ForeignCurrentPriceResponse response = foreignStockService.getCurrentPrice(exchangeCode, stockCode);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("해외주식 현재가 조회 실패", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(
            summary = "해외주식 분봉 조회",
            description = "해외주식의 분봉 차트 데이터를 조회합니다. 최대 120건까지 조회 가능합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = ForeignIntradayChartResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/intraday")
    public ResponseEntity<ForeignIntradayChartResponse> getIntradayChart(
            @Parameter(description = "거래소코드 (NYS: 뉴욕, NAS: 나스닥, HKS: 홍콩, TSE: 도쿄)",
                    required = true,
                    example = "NAS")
            @RequestParam String exchangeCode,

            @Parameter(description = "종목코드 (ex: AAPL, TSLA)",
                    required = true,
                    example = "AAPL")
            @RequestParam String stockCode,

            @Parameter(description = "분갭 (1: 1분봉, 5: 5분봉, 10: 10분봉 등)",
                    required = true,
                    example = "1")
            @RequestParam String minuteGap
    ) {
        log.info("해외주식 분봉 조회 요청 - 거래소: {}, 종목: {}, 분갭: {}", exchangeCode, stockCode, minuteGap);

        try {
            if (exchangeCode == null || exchangeCode.trim().isEmpty()) {
                log.error("거래소코드가 비어있습니다.");
                return ResponseEntity.badRequest().build();
            }

            if (stockCode == null || stockCode.trim().isEmpty()) {
                log.error("종목코드가 비어있습니다.");
                return ResponseEntity.badRequest().build();
            }

            if (minuteGap == null || minuteGap.trim().isEmpty()) {
                log.error("분갭이 비어있습니다.");
                return ResponseEntity.badRequest().build();
            }

            ForeignIntradayChartResponse response = foreignStockService.getIntradayChart(exchangeCode, stockCode, minuteGap);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("해외주식 분봉 조회 실패", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(
            summary = "해외주식 기간별 시세 조회",
            description = "해외주식의 기간별(일/주/월/년) 시세 데이터를 조회합니다. 최대 100건까지 조회 가능합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = ForeignPeriodChartResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/period")
    public ResponseEntity<ForeignPeriodChartResponse> getPeriodChart(
            @Parameter(description = "거래소코드 (NYS: 뉴욕, NAS: 나스닥, HKS: 홍콩, TSE: 도쿄)",
                    required = true,
                    example = "NAS")
            @RequestParam String exchangeCode,

            @Parameter(description = "종목코드 (ex: AAPL, TSLA)",
                    required = true,
                    example = "AAPL")
            @RequestParam String stockCode,

            @Parameter(description = "기간분류코드 (D: 일봉, W: 주봉, M: 월봉, Y: 년봉)",
                    required = true,
                    example = "D",
                    schema = @Schema(allowableValues = {"D", "W", "M", "Y"}))
            @RequestParam String periodCode
    ) {
        log.info("해외주식 기간별 시세 조회 요청 - 거래소: {}, 종목: {}, 기간: {}", exchangeCode, stockCode, periodCode);

        try {
            if (exchangeCode == null || exchangeCode.trim().isEmpty()) {
                log.error("거래소코드가 비어있습니다.");
                return ResponseEntity.badRequest().build();
            }

            if (stockCode == null || stockCode.trim().isEmpty()) {
                log.error("종목코드가 비어있습니다.");
                return ResponseEntity.badRequest().build();
            }

            if (!isValidPeriodCode(periodCode)) {
                log.error("잘못된 기간분류코드: {}", periodCode);
                return ResponseEntity.badRequest().build();
            }

            ForeignPeriodChartResponse response = foreignStockService.getPeriodChart(exchangeCode, stockCode, periodCode);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("해외주식 기간별 시세 조회 실패", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(
            summary = "해외주식 매수 주문",
            description = "해외주식 매수 주문을 실행합니다. 주문구분: 00(지정가), 01-36(시장가 등)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "주문 성공",
                    content = @Content(schema = @Schema(implementation = ForeignStockOrderResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/order/buy")
    public ResponseEntity<ForeignStockOrderResponse> buyStock(@RequestBody ForeignStockOrderRequest request) {
        log.info("해외주식 매수 요청 - 거래소: {}, 종목: {}, 수량: {}, 단가: {}",
                request.getOvrsExcgCd(), request.getPdno(), request.getOrdQty(), request.getOvrsOrdUnpr());

        try {
            if (request.getOvrsExcgCd() == null || request.getOvrsExcgCd().trim().isEmpty()) {
                log.error("거래소코드가 비어있습니다.");
                return ResponseEntity.badRequest().build();
            }

            if (request.getPdno() == null || request.getPdno().trim().isEmpty()) {
                log.error("종목코드가 비어있습니다.");
                return ResponseEntity.badRequest().build();
            }

            if (request.getOrdQty() == null || request.getOrdQty().trim().isEmpty()) {
                log.error("주문수량이 비어있습니다.");
                return ResponseEntity.badRequest().build();
            }

            if (request.getOvrsOrdUnpr() == null || request.getOvrsOrdUnpr().trim().isEmpty()) {
                log.error("주문단가가 비어있습니다.");
                return ResponseEntity.badRequest().build();
            }

            ForeignStockOrderResponse response = foreignStockService.buyStock(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("해외주식 매수 실패", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(
            summary = "해외주식 매도 주문",
            description = "해외주식 매도 주문을 실행합니다. 주문구분: 00(지정가), 01-36(시장가 등)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "주문 성공",
                    content = @Content(schema = @Schema(implementation = ForeignStockOrderResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/order/sell")
    public ResponseEntity<ForeignStockOrderResponse> sellStock(@RequestBody ForeignStockOrderRequest request) {
        log.info("해외주식 매도 요청 - 거래소: {}, 종목: {}, 수량: {}, 단가: {}",
                request.getOvrsExcgCd(), request.getPdno(), request.getOrdQty(), request.getOvrsOrdUnpr());

        try {
            if (request.getOvrsExcgCd() == null || request.getOvrsExcgCd().trim().isEmpty()) {
                log.error("거래소코드가 비어있습니다.");
                return ResponseEntity.badRequest().build();
            }

            if (request.getPdno() == null || request.getPdno().trim().isEmpty()) {
                log.error("종목코드가 비어있습니다.");
                return ResponseEntity.badRequest().build();
            }

            if (request.getOrdQty() == null || request.getOrdQty().trim().isEmpty()) {
                log.error("주문수량이 비어있습니다.");
                return ResponseEntity.badRequest().build();
            }

            if (request.getOvrsOrdUnpr() == null || request.getOvrsOrdUnpr().trim().isEmpty()) {
                log.error("주문단가가 비어있습니다.");
                return ResponseEntity.badRequest().build();
            }

            ForeignStockOrderResponse response = foreignStockService.sellStock(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("해외주식 매도 실패", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(
            summary = "해외주식 잔고 조회",
            description = "보유 중인 해외주식 잔고를 조회합니다. 거래소별로 필터링하여 조회 가능합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = ForeignStockBalanceResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/balance")
    public ResponseEntity<ForeignStockBalanceResponse> getBalance(
            @Parameter(description = "거래소코드 (NASD: 미국전체, NYS: 뉴욕, NAS: 나스닥, HKS: 홍콩, TSE: 도쿄)",
                    required = true,
                    example = "NASD")
            @RequestParam String exchangeCode,

            @Parameter(description = "거래통화코드 (USD: 미국달러, HKD: 홍콩달러, JPY: 일본엔화)",
                    required = true,
                    example = "USD")
            @RequestParam String currencyCode
    ) {
        log.info("해외주식 잔고 조회 요청 - 거래소: {}, 통화: {}", exchangeCode, currencyCode);

        try {
            if (exchangeCode == null || exchangeCode.trim().isEmpty()) {
                log.error("거래소코드가 비어있습니다.");
                return ResponseEntity.badRequest().build();
            }

            if (currencyCode == null || currencyCode.trim().isEmpty()) {
                log.error("거래통화코드가 비어있습니다.");
                return ResponseEntity.badRequest().build();
            }

            ForeignStockBalanceResponse response = foreignStockService.getBalance(exchangeCode, currencyCode);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("해외주식 잔고 조회 실패", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(
            summary = "해외주식 매수가능금액 조회",
            description = "특정 해외주식에 대한 매수가능 금액을 조회합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = ForeignBuyableAmountResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/buyable-amount")
    public ResponseEntity<ForeignBuyableAmountResponse> getBuyableAmount(
            @Parameter(description = "거래소코드 (NYS: 뉴욕, NAS: 나스닥, HKS: 홍콩, TSE: 도쿄)",
                    required = true,
                    example = "NAS")
            @RequestParam String exchangeCode,

            @Parameter(description = "종목코드 (ex: AAPL, TSLA)",
                    required = true,
                    example = "AAPL")
            @RequestParam String stockCode,

            @Parameter(description = "거래통화코드 (USD: 미국달러, HKD: 홍콩달러, JPY: 일본엔화)",
                    required = true,
                    example = "USD")
            @RequestParam String currencyCode
    ) {
        log.info("해외주식 매수가능금액 조회 요청 - 거래소: {}, 종목: {}, 통화: {}", exchangeCode, stockCode, currencyCode);

        try {
            if (exchangeCode == null || exchangeCode.trim().isEmpty()) {
                log.error("거래소코드가 비어있습니다.");
                return ResponseEntity.badRequest().build();
            }

            if (stockCode == null || stockCode.trim().isEmpty()) {
                log.error("종목코드가 비어있습니다.");
                return ResponseEntity.badRequest().build();
            }

            if (currencyCode == null || currencyCode.trim().isEmpty()) {
                log.error("거래통화코드가 비어있습니다.");
                return ResponseEntity.badRequest().build();
            }

            ForeignBuyableAmountResponse response = foreignStockService.getBuyableAmount(exchangeCode, stockCode, currencyCode);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("해외주식 매수가능금액 조회 실패", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(
            summary = "해외주식 매도가능수량 조회",
            description = "특정 해외주식에 대한 매도가능 수량을 조회합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = ForeignSellableQuantityResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/sellable-quantity")
    public ResponseEntity<ForeignSellableQuantityResponse> getSellableQuantity(
            @Parameter(description = "거래소코드 (NYS: 뉴욕, NAS: 나스닥, HKS: 홍콩, TSE: 도쿄)",
                    required = true,
                    example = "NAS")
            @RequestParam String exchangeCode,

            @Parameter(description = "종목코드 (ex: AAPL, TSLA)",
                    required = true,
                    example = "AAPL")
            @RequestParam String stockCode,

            @Parameter(description = "거래통화코드 (USD: 미국달러, HKD: 홍콩달러, JPY: 일본엔화)",
                    required = true,
                    example = "USD")
            @RequestParam String currencyCode
    ) {
        log.info("해외주식 매도가능수량 조회 요청 - 거래소: {}, 종목: {}, 통화: {}", exchangeCode, stockCode, currencyCode);

        try {
            if (exchangeCode == null || exchangeCode.trim().isEmpty()) {
                log.error("거래소코드가 비어있습니다.");
                return ResponseEntity.badRequest().build();
            }

            if (stockCode == null || stockCode.trim().isEmpty()) {
                log.error("종목코드가 비어있습니다.");
                return ResponseEntity.badRequest().build();
            }

            if (currencyCode == null || currencyCode.trim().isEmpty()) {
                log.error("거래통화코드가 비어있습니다.");
                return ResponseEntity.badRequest().build();
            }

            ForeignSellableQuantityResponse response = foreignStockService.getSellableQuantity(exchangeCode, stockCode, currencyCode);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("해외주식 매도가능수량 조회 실패", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(
            summary = "해외주식 기본정보 조회",
            description = "해외주식의 기본정보(종목명, 상장주수, 업종 등)를 조회합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = ForeignStockBasicInfoResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/basic-info")
    public ResponseEntity<ForeignStockBasicInfoResponse> getBasicInfo(
            @Parameter(description = "상품유형코드 (512: 나스닥, 513: 뉴욕, 515: 일본, 501: 홍콩)",
                    required = true,
                    example = "512")
            @RequestParam String productTypeCode,

            @Parameter(description = "종목코드 (ex: AAPL, TSLA)",
                    required = true,
                    example = "AAPL")
            @RequestParam String stockCode
    ) {
        log.info("해외주식 기본정보 조회 요청 - 상품유형: {}, 종목: {}", productTypeCode, stockCode);

        try {
            if (productTypeCode == null || productTypeCode.trim().isEmpty()) {
                log.error("상품유형코드가 비어있습니다.");
                return ResponseEntity.badRequest().build();
            }

            if (stockCode == null || stockCode.trim().isEmpty()) {
                log.error("종목코드가 비어있습니다.");
                return ResponseEntity.badRequest().build();
            }

            ForeignStockBasicInfoResponse response = foreignStockService.getBasicInfo(productTypeCode, stockCode);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("해외주식 기본정보 조회 실패", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(
            summary = "해외주식 검색",
            description = "종목코드 또는 회사명으로 해외주식을 검색합니다. 최대 20개 결과를 반환합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "검색 성공",
                    content = @Content(schema = @Schema(implementation = ForeignStockSearchResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/search")
    public ResponseEntity<ForeignStockSearchResponse> searchStocks(
            @Parameter(description = "검색 키워드 (종목코드 또는 회사명)",
                    required = true,
                    example = "Apple")
            @RequestParam String keyword,

            @Parameter(description = "거래소코드 (선택사항, NYS: 뉴욕, NAS: 나스닥, HKS: 홍콩, TSE: 도쿄)",
                    required = false,
                    example = "NAS")
            @RequestParam(required = false) String exchangeCode
    ) {
        log.info("해외주식 검색 요청 - 키워드: {}, 거래소: {}", keyword, exchangeCode);

        try {
            if (keyword == null || keyword.trim().isEmpty()) {
                log.error("검색 키워드가 비어있습니다.");
                return ResponseEntity.badRequest().build();
            }

            ForeignStockSearchResponse response = foreignStockService.searchStocks(keyword, exchangeCode);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("해외주식 검색 실패", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 기간분류코드 유효성 검증
     */
    private boolean isValidPeriodCode(String periodCode) {
        return "D".equals(periodCode) || "W".equals(periodCode) ||
               "M".equals(periodCode) || "Y".equals(periodCode);
    }
}

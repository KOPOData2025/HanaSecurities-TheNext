package com.hanati.domain.stock.controller;

import com.hanati.domain.stock.dto.*;
import com.hanati.domain.stock.service.StockService;
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

import java.util.List;

@RestController
@RequestMapping("/api/v1/stock")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
@Tag(name = "Domestic Stock Price API", description = "국내 주식 가격 데이터 조회 API")
public class StockController {

    private final StockService stockService;

    @Operation(
            summary = "국내 주식 당일 분봉 조회",
            description = "국내 주식의 당일 분봉 데이터를 조회합니다. 최대 30건까지 확인 가능합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = IntradayChartResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/intraday")
    public ResponseEntity<IntradayChartResponse> getIntradayChart(
            @Parameter(description = "종목 코드 (ex: 005930 삼성전자)",
                    required = true,
                    example = "005930")
            @RequestParam String stockCode,

            @Parameter(description = "입력시간 (HHMMSS 형식, ex: 100000은 10시)",
                    required = true,
                    example = "100000")
            @RequestParam String inputTime
    ) {
        log.info("주식 당일 분봉 조회 요청 - 종목코드: {}, 입력시간: {}", stockCode, inputTime);

        try {
            // 입력 검증
            if (stockCode == null || stockCode.trim().isEmpty()) {
                log.error("종목 코드가 비어있습니다.");
                return ResponseEntity.badRequest().build();
            }

            if (inputTime == null || inputTime.trim().isEmpty() || inputTime.length() != 6) {
                log.error("입력시간 형식이 잘못되었습니다: {}", inputTime);
                return ResponseEntity.badRequest().build();
            }

            IntradayChartResponse response = stockService.getIntradayChart(stockCode, inputTime);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.error("잘못된 요청 파라미터", e);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("주식 당일 분봉 조회 실패", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(
            summary = "국내주식 기간별 시세 조회 (DB 캐싱)",
            description = "국내 주식의 기간별 시세(일/주/월/년) 데이터를 조회합니다. DB 캐싱을 사용하여 성능을 최적화합니다. 최대 100건까지 확인 가능합니다. 원주가 기준입니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = PeriodChartResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/period")
    public ResponseEntity<PeriodChartResponse> getPeriodChart(
            @Parameter(description = "종목 코드 (ex: 005930 삼성전자)",
                    required = true,
                    example = "005930")
            @RequestParam String stockCode,

            @Parameter(description = "조회 시작일자 (YYYYMMDD 형식)",
                    required = true,
                    example = "20240101")
            @RequestParam String startDate,

            @Parameter(description = "조회 종료일자 (YYYYMMDD 형식)",
                    required = true,
                    example = "20240131")
            @RequestParam String endDate,

            @Parameter(description = "기간분류코드 (D:일봉, W:주봉, M:월봉, Y:년봉)",
                    required = true,
                    example = "D",
                    schema = @Schema(allowableValues = {"D", "W", "M", "Y"}))
            @RequestParam String periodCode
    ) {
        log.info("국내주식 기간별 시세 조회 요청 (DB 캐싱) - 종목코드: {}, 시작일: {}, 종료일: {}, 기간: {}",
                stockCode, startDate, endDate, periodCode);

        try {
            // 입력 검증
            if (stockCode == null || stockCode.trim().isEmpty()) {
                log.error("종목 코드가 비어있습니다.");
                return ResponseEntity.badRequest().build();
            }

            if (startDate == null || startDate.trim().isEmpty() || startDate.length() != 8) {
                log.error("시작일자 형식이 잘못되었습니다: {}", startDate);
                return ResponseEntity.badRequest().build();
            }

            if (endDate == null || endDate.trim().isEmpty() || endDate.length() != 8) {
                log.error("종료일자 형식이 잘못되었습니다: {}", endDate);
                return ResponseEntity.badRequest().build();
            }

            if (!isValidPeriodCode(periodCode)) {
                log.error("잘못된 기간분류코드: {}", periodCode);
                return ResponseEntity.badRequest().build();
            }

            PeriodChartResponse response = stockService.getPeriodChart(stockCode, startDate, endDate, periodCode);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.error("잘못된 요청 파라미터", e);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("국내주식 기간별 시세 조회 실패", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(
            summary = "국내주식 기간별 시세 조회 (Direct API, 성능 비교용)",
            description = "국내 주식의 기간별 시세(일/주/월/년) 데이터를 매번 한투 API로 직접 조회합니다. DB 캐싱을 사용하지 않으며, DB에 저장하지도 않습니다. 성능 비교 목적으로 사용됩니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = PeriodChartResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/period/direct")
    public ResponseEntity<PeriodChartResponse> getPeriodChartDirect(
            @Parameter(description = "종목 코드 (ex: 005930 삼성전자)",
                    required = true,
                    example = "005930")
            @RequestParam String stockCode,

            @Parameter(description = "조회 시작일자 (YYYYMMDD 형식)",
                    required = true,
                    example = "20240101")
            @RequestParam String startDate,

            @Parameter(description = "조회 종료일자 (YYYYMMDD 형식)",
                    required = true,
                    example = "20240131")
            @RequestParam String endDate,

            @Parameter(description = "기간분류코드 (D:일봉, W:주봉, M:월봉, Y:년봉)",
                    required = true,
                    example = "D",
                    schema = @Schema(allowableValues = {"D", "W", "M", "Y"}))
            @RequestParam String periodCode
    ) {
        log.info("국내주식 기간별 시세 직접 조회 요청 (No Cache) - 종목코드: {}, 시작일: {}, 종료일: {}, 기간: {}",
                stockCode, startDate, endDate, periodCode);

        try {
            // 입력 검증
            if (stockCode == null || stockCode.trim().isEmpty()) {
                log.error("종목 코드가 비어있습니다.");
                return ResponseEntity.badRequest().build();
            }

            if (startDate == null || startDate.trim().isEmpty() || startDate.length() != 8) {
                log.error("시작일자 형식이 잘못되었습니다: {}", startDate);
                return ResponseEntity.badRequest().build();
            }

            if (endDate == null || endDate.trim().isEmpty() || endDate.length() != 8) {
                log.error("종료일자 형식이 잘못되었습니다: {}", endDate);
                return ResponseEntity.badRequest().build();
            }

            if (!isValidPeriodCode(periodCode)) {
                log.error("잘못된 기간분류코드: {}", periodCode);
                return ResponseEntity.badRequest().build();
            }

            PeriodChartResponse response = stockService.getPeriodChartDirect(stockCode, startDate, endDate, periodCode);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.error("잘못된 요청 파라미터", e);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("국내주식 기간별 시세 직접 조회 실패", e);
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

    @Operation(
            summary = "종목투자의견 조회",
            description = "국내 주식의 증권사 투자의견을 조회합니다. 최근 3개월 데이터를 제공합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = InvestOpinionResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/invest-opinion")
    public ResponseEntity<InvestOpinionResponse> getInvestOpinion(
            @Parameter(description = "종목 코드 (ex: 005930 삼성전자)",
                    required = true,
                    example = "005930")
            @RequestParam String stockCode
    ) {
        log.info("종목투자의견 조회 요청 - 종목코드: {}", stockCode);

        try {
            // 입력 검증
            if (stockCode == null || stockCode.trim().isEmpty()) {
                log.error("종목 코드가 비어있습니다.");
                return ResponseEntity.badRequest().build();
            }

            InvestOpinionResponse response = stockService.getInvestOpinion(stockCode);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.error("잘못된 요청 파라미터", e);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("종목투자의견 조회 실패", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(
            summary = "국내 주식 현금 매수",
            description = "국내 주식을 현금으로 매수합니다. 주문구분: 00(지정가), 01(시장가)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "주문 성공",
                    content = @Content(schema = @Schema(implementation = StockOrderResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/order/buy")
    public ResponseEntity<StockOrderResponse> buyStock(@RequestBody StockOrderRequest request) {
        log.info("국내 주식 현금 매수 요청 - 종목코드: {}, 수량: {}, 단가: {}",
                request.getPdno(), request.getOrdQty(), request.getOrdUnpr());

        try {
            // 입력 검증
            if (request.getPdno() == null || request.getPdno().trim().isEmpty()) {
                log.error("종목 코드가 비어있습니다.");
                return ResponseEntity.badRequest().build();
            }

            if (request.getOrdDvsn() == null || request.getOrdDvsn().trim().isEmpty()) {
                log.error("주문구분이 비어있습니다.");
                return ResponseEntity.badRequest().build();
            }

            if (request.getOrdQty() == null || request.getOrdQty().trim().isEmpty()) {
                log.error("주문수량이 비어있습니다.");
                return ResponseEntity.badRequest().build();
            }

            if (request.getOrdUnpr() == null || request.getOrdUnpr().trim().isEmpty()) {
                log.error("주문단가가 비어있습니다.");
                return ResponseEntity.badRequest().build();
            }

            StockOrderResponse response = stockService.buyStock(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("국내 주식 현금 매수 실패", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(
            summary = "국내 주식 현금 매도",
            description = "국내 주식을 현금으로 매도합니다. 주문구분: 00(지정가), 01(시장가)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "주문 성공",
                    content = @Content(schema = @Schema(implementation = StockOrderResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/order/sell")
    public ResponseEntity<StockOrderResponse> sellStock(@RequestBody StockOrderRequest request) {
        log.info("국내 주식 현금 매도 요청 - 종목코드: {}, 수량: {}, 단가: {}",
                request.getPdno(), request.getOrdQty(), request.getOrdUnpr());

        try {
            // 입력 검증
            if (request.getPdno() == null || request.getPdno().trim().isEmpty()) {
                log.error("종목 코드가 비어있습니다.");
                return ResponseEntity.badRequest().build();
            }

            if (request.getOrdDvsn() == null || request.getOrdDvsn().trim().isEmpty()) {
                log.error("주문구분이 비어있습니다.");
                return ResponseEntity.badRequest().build();
            }

            if (request.getOrdQty() == null || request.getOrdQty().trim().isEmpty()) {
                log.error("주문수량이 비어있습니다.");
                return ResponseEntity.badRequest().build();
            }

            if (request.getOrdUnpr() == null || request.getOrdUnpr().trim().isEmpty()) {
                log.error("주문단가가 비어있습니다.");
                return ResponseEntity.badRequest().build();
            }

            StockOrderResponse response = stockService.sellStock(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("국내 주식 현금 매도 실패", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(
            summary = "주식 잔고 조회",
            description = "보유 중인 주식 잔고를 조회합니다. 계좌번호는 환경변수에서 자동으로 가져옵니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = StockBalanceResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/balance")
    public ResponseEntity<StockBalanceResponse> getStockBalance() {
        log.info("주식 잔고 조회 요청");

        try {
            StockBalanceResponse response = stockService.getStockBalance();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("주식 잔고 조회 실패", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(
            summary = "장내채권 잔고 조회",
            description = "보유 중인 장내채권 잔고를 조회합니다. 계좌번호는 환경변수에서 자동으로 가져옵니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = BondBalanceResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/bond/balance")
    public ResponseEntity<BondBalanceResponse> getBondBalance(
            @Parameter(description = "조회조건 (00: 전체, 01: 상품번호단위)",
                    required = true,
                    example = "00")
            @RequestParam String inqrCndt
    ) {
        log.info("장내채권 잔고 조회 요청 - 조회조건: {}", inqrCndt);

        try {
            // 입력 검증
            if (!isValidInqrCndt(inqrCndt)) {
                log.error("잘못된 조회조건: {}", inqrCndt);
                return ResponseEntity.badRequest().build();
            }

            BondBalanceResponse response = stockService.getBondBalance(inqrCndt);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("장내채권 잔고 조회 실패", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 조회조건 유효성 검증
     */
    private boolean isValidInqrCndt(String inqrCndt) {
        return "00".equals(inqrCndt) || "01".equals(inqrCndt);
    }

    @Operation(
            summary = "[국내주식] 현금 매수가능조회",
            description = "특정 종목에 대한 매수가능 금액 및 수량을 조회합니다. 계좌번호는 환경변수에서 자동으로 가져옵니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = BuyableAmountResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/buyable-amount")
    public ResponseEntity<BuyableAmountResponse> getBuyableAmount(
            @Parameter(description = "종목 코드 (ex: 005930 삼성전자)",
                    required = true,
                    example = "005930")
            @RequestParam String stockCode
    ) {
        log.info("매수가능조회 요청 - 종목코드: {}", stockCode);

        try {
            // 입력 검증
            if (stockCode == null || stockCode.trim().isEmpty()) {
                log.error("종목 코드가 비어있습니다.");
                return ResponseEntity.badRequest().build();
            }

            BuyableAmountResponse response = stockService.getBuyableAmount(stockCode);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("매수가능조회 실패", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(
            summary = "[국내주식] 현금 매도가능수량조회",
            description = "특정 종목에 대한 매도가능 수량 및 보유 정보를 조회합니다. 계좌번호는 환경변수에서 자동으로 가져옵니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = SellableQuantityResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/sellable-quantity")
    public ResponseEntity<SellableQuantityResponse> getSellableQuantity(
            @Parameter(description = "종목 코드 (ex: 005930 삼성전자)",
                    required = true,
                    example = "005930")
            @RequestParam String stockCode
    ) {
        log.info("매도가능수량조회 요청 - 종목코드: {}", stockCode);

        try {
            // 입력 검증
            if (stockCode == null || stockCode.trim().isEmpty()) {
                log.error("종목 코드가 비어있습니다.");
                return ResponseEntity.badRequest().build();
            }

            SellableQuantityResponse response = stockService.getSellableQuantity(stockCode);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("매도가능수량조회 실패", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(
            summary = "[국내주식] 주식 기본 정보 조회",
            description = "국내 주식의 기본 정보를 조회합니다. 시장구분, 시가총액, 상장주식수 등의 정보를 제공합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = StockBasicInfoResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/basic-info")
    public ResponseEntity<StockBasicInfoResponse> getStockBasicInfo(
            @Parameter(description = "상품유형코드 (300: 주식, ETF, ETN, ELW / 301: 선물옵션 / 302: 채권 / 306: ELS)",
                    required = true,
                    example = "300")
            @RequestParam String productTypeCode,

            @Parameter(description = "종목코드 (6자리, ETN의 경우 Q로 시작)",
                    required = true,
                    example = "005930")
            @RequestParam String stockCode
    ) {
        log.info("[국내주식] 주식 기본 정보 조회 요청 - 상품유형코드: {}, 종목코드: {}", productTypeCode, stockCode);

        try {
            // 입력 검증
            if (productTypeCode == null || productTypeCode.trim().isEmpty()) {
                log.error("상품유형코드가 비어있습니다.");
                return ResponseEntity.badRequest().build();
            }

            if (stockCode == null || stockCode.trim().isEmpty()) {
                log.error("종목 코드가 비어있습니다.");
                return ResponseEntity.badRequest().build();
            }

            StockBasicInfoResponse response = stockService.getStockBasicInfo(productTypeCode, stockCode);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("[국내주식] 주식 기본 정보 조회 실패", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(
            summary = "주식 종목 검색",
            description = "종목명 또는 종목코드로 주식을 검색합니다. STOCK 테이블에서 실데이터를 조회합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "검색 성공",
                    content = @Content(schema = @Schema(implementation = StockSearchResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/search")
    public ResponseEntity<StockSearchResponse> searchStocks(
            @Parameter(description = "검색 키워드 (종목명 또는 종목코드)",
                    required = true,
                    example = "삼성")
            @RequestParam String keyword
    ) {
        log.info("주식 종목 검색 요청 - 키워드: {}", keyword);

        try {
            // 입력 검증
            if (keyword == null || keyword.trim().isEmpty()) {
                log.error("검색 키워드가 비어있습니다.");
                return ResponseEntity.badRequest()
                    .body(StockSearchResponse.builder()
                        .success(false)
                        .message("검색 키워드를 입력해주세요.")
                        .stocks(List.of())
                        .build());
            }

            StockSearchResponse response = stockService.searchStocks(keyword.trim());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("주식 종목 검색 실패", e);
            return ResponseEntity.internalServerError()
                .body(StockSearchResponse.builder()
                    .success(false)
                    .message("검색 실패: " + e.getMessage())
                    .stocks(List.of())
                    .build());
        }
    }
}
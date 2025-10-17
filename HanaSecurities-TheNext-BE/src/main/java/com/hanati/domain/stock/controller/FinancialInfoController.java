package com.hanati.domain.stock.controller;

import com.hanati.domain.stock.dto.FinancialInfoResponse;
import com.hanati.domain.stock.service.FinancialInfoService;
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
 * [국내주식] 재무정보 조회 API 컨트롤러
 */
@RestController
@RequestMapping("/api/v1/stock")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
@Tag(name = "Domestic Stock Financial API", description = "국내 주식 재무정보 조회 API")
public class FinancialInfoController {

    private final FinancialInfoService financialInfoService;

    @Operation(
            summary = "통합 재무정보 조회",
            description = "국내 주식의 재무비율, 손익계산서, 대차대조표를 통합 조회합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = FinancialInfoResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/financial-info")
    public ResponseEntity<FinancialInfoResponse> getFinancialInfo(
            @Parameter(description = "종목 코드 (ex: 005930 삼성전자)",
                    required = true,
                    example = "005930")
            @RequestParam String stockCode,

            @Parameter(description = "분류 구분 코드 (0: 년, 1: 분기)",
                    required = false,
                    example = "1")
            @RequestParam(defaultValue = "1") String divisionCode
    ) {
        log.info("재무정보 조회 요청 - 종목코드: {}, 분류구분: {}", stockCode, divisionCode);

        FinancialInfoResponse response = financialInfoService.getFinancialInfo(stockCode, divisionCode);

        return ResponseEntity.ok(response);
    }
}

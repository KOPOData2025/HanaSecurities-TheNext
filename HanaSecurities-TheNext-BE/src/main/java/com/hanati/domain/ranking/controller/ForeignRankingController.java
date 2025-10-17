package com.hanati.domain.ranking.controller;

import com.hanati.domain.ranking.dto.ForeignStockRankingResponse;
import com.hanati.domain.ranking.service.ForeignRankingService;
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
@RequestMapping("/api/v1/foreign-ranking")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
@Tag(name = "Foreign Ranking API", description = "해외 주식 실시간 랭킹 조회 API")
public class ForeignRankingController {

    private final ForeignRankingService foreignRankingService;

    @Operation(
            summary = "해외 주식 랭킹 조회",
            description = "해외 주식 랭킹을 조회합니다. 상승률, 하락률, 거래량, 거래대금 순위를 확인할 수 있습니다. " +
                    "거래소별로 조회 가능합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = ForeignStockRankingResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping
    public ResponseEntity<ForeignStockRankingResponse> getForeignStockRanking(
            @Parameter(description = "랭킹 타입",
                    required = true,
                    example = "VOLUME",
                    schema = @Schema(allowableValues = {"VOLUME", "TRADING_VALUE", "RISE", "FALL"}))
            @RequestParam String type,

            @Parameter(description = "거래소 코드",
                    required = true,
                    example = "NYS",
                    schema = @Schema(allowableValues = {"NYS", "NAS", "AMS", "HKS", "SHS", "SZS", "HSX", "HNX", "TSE"}))
            @RequestParam String exchange
    ) {
        log.info("해외 주식 랭킹 조회 요청 - type: {}, exchange: {}", type, exchange);

        try {
            // 랭킹 타입 검증
            if (!isValidRankingType(type)) {
                log.error("잘못된 랭킹 타입: {}", type);
                return ResponseEntity.badRequest().build();
            }

            // 거래소 코드 검증
            if (!isValidExchangeCode(exchange)) {
                log.error("잘못된 거래소 코드: {}", exchange);
                return ResponseEntity.badRequest().build();
            }

            ForeignStockRankingResponse response = foreignRankingService.getForeignStockRanking(type, exchange);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.error("잘못된 요청 파라미터", e);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("해외 주식 랭킹 조회 실패", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 랭킹 타입 유효성 검증
     */
    private boolean isValidRankingType(String type) {
        return ForeignRankingService.RANKING_VOLUME.equals(type) ||
                ForeignRankingService.RANKING_TRADING_VALUE.equals(type) ||
                ForeignRankingService.RANKING_RISE.equals(type) ||
                ForeignRankingService.RANKING_FALL.equals(type);
    }

    /**
     * 거래소 코드 유효성 검증
     */
    private boolean isValidExchangeCode(String exchange) {
        return "NYS".equals(exchange) ||  // 뉴욕
                "NAS".equals(exchange) ||  // 나스닥
                "AMS".equals(exchange) ||  // 아멕스
                "HKS".equals(exchange) ||  // 홍콩
                "SHS".equals(exchange) ||  // 상해
                "SZS".equals(exchange) ||  // 심천
                "HSX".equals(exchange) ||  // 호치민
                "HNX".equals(exchange) ||  // 하노이
                "TSE".equals(exchange);    // 도쿄
    }
}
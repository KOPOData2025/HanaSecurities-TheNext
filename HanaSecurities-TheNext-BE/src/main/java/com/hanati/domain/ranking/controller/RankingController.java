package com.hanati.domain.ranking.controller;

import com.hanati.domain.ranking.dto.StockRankingResponse;
import com.hanati.domain.ranking.service.RankingService;
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
@RequestMapping("/api/v1/ranking")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
@Tag(name = "Ranking API", description = "국내 주식 실시간 랭킹 조회 API")
public class RankingController {

    private final RankingService rankingService;

    @Operation(
            summary = "주식 랭킹 조회",
            description = "국내 주식 랭킹을 조회합니다. 상승률, 하락률, 거래량, 거래대금 순위를 확인할 수 있습니다. 최대 30개 종목까지 조회 가능합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = StockRankingResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping
    public ResponseEntity<StockRankingResponse> getStockRanking(
            @Parameter(description = "랭킹 타입",
                      required = true,
                      example = "VOLUME",
                      schema = @Schema(allowableValues = {"VOLUME", "TRADING_VALUE", "RISE", "FALL"}))
            @RequestParam String type,

            @Parameter(description = "시장 구분 코드 (J:KRX, NX:NXT)",
                      example = "J",
                      schema = @Schema(allowableValues = {"J", "NX"}))
            @RequestParam(required = false, defaultValue = "J") String market
    ) {
        log.info("주식 랭킹 조회 요청 - type: {}, market: {}", type, market);

        try {
            // 랭킹 타입 검증
            if (!isValidRankingType(type)) {
                log.error("잘못된 랭킹 타입: {}", type);
                return ResponseEntity.badRequest().build();
            }

            // 시장 코드 검증
            if (!"J".equals(market) && !"NX".equals(market)) {
                log.error("잘못된 시장 코드: {}", market);
                return ResponseEntity.badRequest().build();
            }

            StockRankingResponse response = rankingService.getStockRanking(type, market);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.error("잘못된 요청 파라미터", e);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("주식 랭킹 조회 실패", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 랭킹 타입 유효성 검증
     */
    private boolean isValidRankingType(String type) {
        return RankingService.RANKING_VOLUME.equals(type) ||
               RankingService.RANKING_TRADING_VALUE.equals(type) ||
               RankingService.RANKING_RISE.equals(type) ||
               RankingService.RANKING_FALL.equals(type);
    }

    @Operation(
            summary = "지원 랭킹 타입 조회",
            description = "사용 가능한 랭킹 타입 목록을 조회합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @GetMapping("/types")
    public ResponseEntity<RankingTypes> getRankingTypes() {
        return ResponseEntity.ok(new RankingTypes());
    }

    @Schema(description = "랭킹 타입 목록")
    public static class RankingTypes {
        @Schema(description = "거래량 순위", example = "VOLUME")
        public final String volume = RankingService.RANKING_VOLUME;

        @Schema(description = "거래대금 순위", example = "TRADING_VALUE")
        public final String tradingValue = RankingService.RANKING_TRADING_VALUE;

        @Schema(description = "상승률 순위", example = "RISE")
        public final String rise = RankingService.RANKING_RISE;

        @Schema(description = "하락률 순위", example = "FALL")
        public final String fall = RankingService.RANKING_FALL;
    }
}
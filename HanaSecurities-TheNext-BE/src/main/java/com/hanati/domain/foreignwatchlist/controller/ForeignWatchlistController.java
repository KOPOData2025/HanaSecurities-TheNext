package com.hanati.domain.foreignwatchlist.controller;

import com.hanati.domain.foreignwatchlist.dto.ForeignWatchlistRequest;
import com.hanati.domain.foreignwatchlist.dto.ForeignWatchlistResponse;
import com.hanati.domain.foreignwatchlist.service.ForeignWatchlistService;
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

import java.util.Map;

/**
 * 해외 관심종목 Controller
 */
@RestController
@RequestMapping("/api/v1/foreign-watchlist")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
@Tag(name = "Foreign Watchlist API", description = "해외 관심종목 관리 API")
public class ForeignWatchlistController {

    private final ForeignWatchlistService watchlistService;

    @Operation(
            summary = "해외 관심종목 추가",
            description = "사용자의 해외 관심종목 목록에 종목을 추가합니다. 중복된 종목은 추가되지 않습니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "추가 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 또는 중복된 종목"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping
    public ResponseEntity<Map<String, Object>> addWatchlist(@RequestBody ForeignWatchlistRequest request) {
        log.info("해외 관심종목 추가 요청 - 사용자: {}, 거래소: {}, 종목: {}",
                request.getUserId(), request.getExchangeCode(), request.getStockCode());

        try {
            if (request.getUserId() == null) {
                log.error("사용자 ID가 없습니다.");
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "사용자 ID가 필요합니다."));
            }

            if (request.getExchangeCode() == null || request.getExchangeCode().trim().isEmpty()) {
                log.error("거래소코드가 없습니다.");
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "거래소코드가 필요합니다."));
            }

            if (request.getStockCode() == null || request.getStockCode().trim().isEmpty()) {
                log.error("종목코드가 없습니다.");
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "종목코드가 필요합니다."));
            }

            boolean success = watchlistService.addWatchlist(request);

            if (success) {
                return ResponseEntity.ok(Map.of(
                        "success", true,
                        "message", "관심종목이 추가되었습니다."
                ));
            } else {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "이미 등록된 관심종목입니다."));
            }

        } catch (Exception e) {
            log.error("해외 관심종목 추가 실패", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "message", "관심종목 추가에 실패했습니다."));
        }
    }

    @Operation(
            summary = "해외 관심종목 목록 조회",
            description = "사용자의 해외 관심종목 목록을 조회합니다. 각 종목의 현재가와 등락률이 포함됩니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = ForeignWatchlistResponse.class))
            ),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/{userId}")
    public ResponseEntity<ForeignWatchlistResponse> getWatchlist(
            @Parameter(description = "사용자 ID", required = true, example = "1")
            @PathVariable Long userId
    ) {
        log.info("해외 관심종목 조회 요청 - 사용자: {}", userId);

        try {
            ForeignWatchlistResponse response = watchlistService.getWatchlist(userId);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("해외 관심종목 조회 실패", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(
            summary = "해외 관심종목 삭제",
            description = "사용자의 해외 관심종목 목록에서 특정 종목을 삭제합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "404", description = "종목을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @DeleteMapping("/{userId}/{exchangeCode}/{stockCode}")
    public ResponseEntity<Map<String, Object>> removeWatchlist(
            @Parameter(description = "사용자 ID", required = true, example = "1")
            @PathVariable Long userId,

            @Parameter(description = "거래소코드", required = true, example = "NAS")
            @PathVariable String exchangeCode,

            @Parameter(description = "종목코드", required = true, example = "AAPL")
            @PathVariable String stockCode
    ) {
        log.info("해외 관심종목 삭제 요청 - 사용자: {}, 거래소: {}, 종목: {}",
                userId, exchangeCode, stockCode);

        try {
            boolean success = watchlistService.removeWatchlist(userId, exchangeCode, stockCode);

            if (success) {
                return ResponseEntity.ok(Map.of(
                        "success", true,
                        "message", "관심종목이 삭제되었습니다."
                ));
            } else {
                return ResponseEntity.status(404)
                        .body(Map.of("success", false, "message", "관심종목을 찾을 수 없습니다."));
            }

        } catch (Exception e) {
            log.error("해외 관심종목 삭제 실패", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "message", "관심종목 삭제에 실패했습니다."));
        }
    }
}

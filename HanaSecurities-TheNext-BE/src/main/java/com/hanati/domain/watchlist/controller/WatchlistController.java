package com.hanati.domain.watchlist.controller;

import com.hanati.domain.watchlist.dto.AddWatchlistRequest;
import com.hanati.domain.watchlist.dto.WatchlistResponse;
import com.hanati.domain.watchlist.service.WatchlistService;
import io.swagger.v3.oas.annotations.Operation;
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

/**
 * 관심 종목 REST API Controller
 * - 관심 종목 추가
 * - 관심 종목 조회
 * - 관심 종목 삭제
 */
@RestController
@RequestMapping("/api/v1/watchlist")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
@Tag(name = "Watchlist API", description = "관심 종목 관리 API")
public class WatchlistController {

    private final WatchlistService watchlistService;

    // ============================================================================
    // 1. 관심 종목 추가
    // ============================================================================

    @Operation(
            summary = "관심 종목 추가",
            description = "사용자의 관심 종목에 특정 종목을 추가합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "추가 성공"),
            @ApiResponse(responseCode = "400", description = "이미 추가된 종목 또는 존재하지 않는 종목"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 사용자"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping
    public ResponseEntity<String> addWatchlist(@RequestBody AddWatchlistRequest request) {
        log.info("[API 호출] 관심 종목 추가 - userId: {}, stockCode: {}", request.getUserId(), request.getStockCode());

        watchlistService.addWatchlist(request);

        return ResponseEntity.ok("관심 종목에 추가되었습니다.");
    }

    // ============================================================================
    // 2. 관심 종목 조회
    // ============================================================================

    @Operation(
            summary = "관심 종목 조회",
            description = "사용자의 관심 종목 목록을 조회합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = WatchlistResponse.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 사용자"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/{userId}")
    public ResponseEntity<List<WatchlistResponse>> getUserWatchlist(@PathVariable Long userId) {
        log.info("[API 호출] 관심 종목 조회 - userId: {}", userId);

        List<WatchlistResponse> watchlist = watchlistService.getUserWatchlist(userId);

        return ResponseEntity.ok(watchlist);
    }

    // ============================================================================
    // 3. 관심 종목 삭제
    // ============================================================================

    @Operation(
            summary = "관심 종목 삭제",
            description = "사용자의 관심 종목에서 특정 종목을 삭제합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "400", description = "관심 종목에 없는 종목"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 사용자"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @DeleteMapping("/{userId}/{stockCode}")
    public ResponseEntity<String> removeWatchlist(
            @PathVariable Long userId,
            @PathVariable String stockCode) {

        log.info("[API 호출] 관심 종목 삭제 - userId: {}, stockCode: {}", userId, stockCode);

        watchlistService.removeWatchlist(userId, stockCode);

        return ResponseEntity.ok("관심 종목에서 삭제되었습니다.");
    }
}

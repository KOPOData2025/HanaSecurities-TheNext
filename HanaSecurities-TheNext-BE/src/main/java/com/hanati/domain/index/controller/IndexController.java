package com.hanati.domain.index.controller;

import com.hanati.domain.index.dto.IndexPriceResponse;
import com.hanati.domain.index.dto.IndexTimePriceResponse;
import com.hanati.domain.index.service.IndexService;
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

@RestController
@RequestMapping("/api/v1/index")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
@Tag(name = "Index API", description = "국내 주요 지수(코스피, 코스닥, 코스피200) 조회 API")
public class IndexController {

    private final IndexService indexService;

    @Operation(
            summary = "지수 현재가 조회",
            description = "지정된 지수(KOSPI, KOSDAQ, KOSPI200)의 현재가 정보를 조회합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = IndexPriceResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 지수 타입"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/price")
    public ResponseEntity<IndexPriceResponse> getIndexPrice(
            @RequestParam @Schema(description = "지수 타입", allowableValues = {"kospi", "kosdaq", "kospi200"}) String indexType) {
        log.info("{} 현재가 조회 요청", indexType);
        try {
            IndexPriceResponse response = indexService.getIndexPrice(indexType.toUpperCase());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.error("잘못된 지수 타입: {}", indexType);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("{} 현재가 조회 실패", indexType, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(
            summary = "지수 1분별 시계열 데이터 조회",
            description = "지정된 지수(KOSPI, KOSDAQ, KOSPI200)의 1분 단위 시계열 데이터를 조회합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = IndexTimePriceResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 지수 타입"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/timeprice")
    public ResponseEntity<IndexTimePriceResponse> getIndexTimePrice(
            @RequestParam @Schema(description = "지수 타입", allowableValues = {"kospi", "kosdaq", "kospi200"}) String indexType) {
        log.info("{} 1분별 지수 조회 요청", indexType);
        try {
            IndexTimePriceResponse response = indexService.getIndexTimePrice(indexType.toUpperCase());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.error("잘못된 지수 타입: {}", indexType);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("{} 1분별 지수 조회 실패", indexType, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(
            summary = "모든 지수 현재가 조회",
            description = "코스피, 코스닥, 코스피200 3개 지수의 현재가를 한번에 조회합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = AllIndexPricesResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/all/price")
    public ResponseEntity<?> getAllIndexPrices() {
        log.info("모든 지수 현재가 조회 요청");
        try {
            return ResponseEntity.ok(new AllIndexPricesResponse(
                    indexService.getKospiPrice(),
                    indexService.getKosdaqPrice(),
                    indexService.getKospi200Price()
            ));
        } catch (Exception e) {
            log.error("모든 지수 현재가 조회 실패", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @Schema(description = "모든 지수 현재가 응답")
    public record AllIndexPricesResponse(
            @Schema(description = "코스피 지수 정보") IndexPriceResponse kospi,
            @Schema(description = "코스닥 지수 정보") IndexPriceResponse kosdaq,
            @Schema(description = "코스피200 지수 정보") IndexPriceResponse kospi200
    ) {}
}
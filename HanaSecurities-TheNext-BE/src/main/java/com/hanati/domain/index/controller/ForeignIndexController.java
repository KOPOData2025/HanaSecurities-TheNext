package com.hanati.domain.index.controller;

import com.hanati.domain.index.dto.ForeignIndexTimePriceResponse;
import com.hanati.domain.index.service.ForeignIndexService;
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
@RequestMapping("/api/v1/foreign-index")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
@Tag(name = "Foreign Index API", description = "해외 주요 지수(S&P500, 상해종합, 유로STOXX50, 홍콩H지수) 분봉 조회 API")
public class ForeignIndexController {

    private final ForeignIndexService foreignIndexService;

    @Operation(
            summary = "해외 지수 분봉 조회",
            description = "지정된 해외 지수의 1분 단위 시계열 데이터를 조회합니다. " +
                    "지원 지수: SPX(S&P500), SHANG(상해종합), SX5E(유로STOXX50), HSCE(홍콩H지수)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = ForeignIndexTimePriceResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 지수 타입"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/timeprice")
    public ResponseEntity<ForeignIndexTimePriceResponse> getForeignIndexTimePrice(
            @Parameter(
                    description = "지수 타입",
                    required = true,
                    example = "SPX",
                    schema = @Schema(allowableValues = {"SPX", "SHANG", "SX5E", "HSCE"})
            )
            @RequestParam String indexType) {

        log.info("해외 지수 {} 분봉 조회 요청", indexType);

        try {
            ForeignIndexTimePriceResponse response = foreignIndexService.getForeignIndexTimePrice(indexType);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.error("잘못된 해외 지수 타입: {}", indexType);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("해외 지수 {} 분봉 조회 실패", indexType, e);
            return ResponseEntity.internalServerError().build();
        }
    }

}
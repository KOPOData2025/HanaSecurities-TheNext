package com.hanati.domain.bnpl.controller;

import com.hanati.domain.bnpl.dto.*;
import com.hanati.domain.bnpl.service.BnplService;
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
 * 후불결제(BNPL) API Controller
 */
@RestController
@RequestMapping("/api/v1/bnpl")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
@Tag(name = "BNPL API", description = "후불결제 관련 API")
public class BnplController {

    private final BnplService bnplService;

    @Operation(
            summary = "후불결제 신청",
            description = "납부일과 납부계좌를 선택하여 후불결제를 신청합니다. 한도는 무조건 300,000원으로 승인됩니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "신청 성공",
                    content = @Content(schema = @Schema(implementation = BnplApplicationResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/apply")
    public ResponseEntity<BnplApplicationResponse> applyBnpl(
            @RequestBody BnplApplicationRequest request
    ) {
        log.info("후불결제 신청 요청 - 사용자: {}, 납부일: {}, 계좌: {}",
                request.getUserId(), request.getPaymentDay(), request.getPaymentAccount());

        try {
            // 입력 검증
            if (request.getUserId() == null || request.getUserId().trim().isEmpty()) {
                log.error("사용자 ID가 비어있습니다.");
                return ResponseEntity.badRequest()
                        .body(BnplApplicationResponse.builder()
                                .success(false)
                                .message("사용자 ID를 입력해주세요.")
                                .build());
            }

            if (request.getPaymentDay() == null) {
                log.error("납부일이 비어있습니다.");
                return ResponseEntity.badRequest()
                        .body(BnplApplicationResponse.builder()
                                .success(false)
                                .message("납부일을 선택해주세요.")
                                .build());
            }

            if (request.getPaymentAccount() == null || request.getPaymentAccount().trim().isEmpty()) {
                log.error("납부계좌가 비어있습니다.");
                return ResponseEntity.badRequest()
                        .body(BnplApplicationResponse.builder()
                                .success(false)
                                .message("납부계좌를 입력해주세요.")
                                .build());
            }

            BnplApplicationResponse response = bnplService.applyBnpl(request);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("후불결제 신청 실패", e);
            return ResponseEntity.internalServerError()
                    .body(BnplApplicationResponse.builder()
                            .success(false)
                            .message("후불결제 신청 중 오류가 발생했습니다: " + e.getMessage())
                            .build());
        }
    }

    @Operation(
            summary = "후불결제 이용내역 조회",
            description = "사용자의 후불결제 이용내역을 조회합니다. 날짜, 사용처, 금액 정보를 최근 순으로 반환합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = BnplUsageHistoryResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/usage-history")
    public ResponseEntity<BnplUsageHistoryResponse> getUsageHistory(
            @Parameter(description = "사용자 ID", required = true, example = "test_user")
            @RequestParam String userId
    ) {
        log.info("후불결제 이용내역 조회 요청 - 사용자: {}", userId);

        try {
            // 입력 검증
            if (userId == null || userId.trim().isEmpty()) {
                log.error("사용자 ID가 비어있습니다.");
                return ResponseEntity.badRequest()
                        .body(BnplUsageHistoryResponse.builder()
                                .success(false)
                                .message("사용자 ID를 입력해주세요.")
                                .build());
            }

            BnplUsageHistoryResponse response = bnplService.getUsageHistory(userId);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("후불결제 이용내역 조회 실패", e);
            return ResponseEntity.internalServerError()
                    .body(BnplUsageHistoryResponse.builder()
                            .success(false)
                            .message("이용내역 조회 중 오류가 발생했습니다: " + e.getMessage())
                            .build());
        }
    }

    @Operation(
            summary = "후불결제 정보 조회",
            description = "사용자의 후불결제 정보를 조회합니다. 납부일, 납부계좌, 이용금액, 한도, 신청일, 승인여부를 반환합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = BnplInfoResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/info")
    public ResponseEntity<BnplInfoResponse> getBnplInfo(
            @Parameter(description = "사용자 ID", required = true, example = "test_user")
            @RequestParam String userId
    ) {
        log.info("후불결제 정보 조회 요청 - 사용자: {}", userId);

        try {
            // 입력 검증
            if (userId == null || userId.trim().isEmpty()) {
                log.error("사용자 ID가 비어있습니다.");
                return ResponseEntity.badRequest()
                        .body(BnplInfoResponse.builder()
                                .success(false)
                                .message("사용자 ID를 입력해주세요.")
                                .build());
            }

            BnplInfoResponse response = bnplService.getBnplInfo(userId);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("후불결제 정보 조회 실패", e);
            return ResponseEntity.internalServerError()
                    .body(BnplInfoResponse.builder()
                            .success(false)
                            .message("후불결제 정보 조회 중 오류가 발생했습니다: " + e.getMessage())
                            .build());
        }
    }
}

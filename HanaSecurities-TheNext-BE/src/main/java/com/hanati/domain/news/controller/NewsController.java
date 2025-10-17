package com.hanati.domain.news.controller;

import com.hanati.domain.news.dto.NewsResponse;
import com.hanati.domain.news.dto.NewsSummaryResponse;
import com.hanati.domain.news.service.NewsService;
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
@RequestMapping("/api/v1/news")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
@Tag(name = "News API", description = "뉴스 검색 API")
public class NewsController {

    private final NewsService newsService;

    @Operation(
            summary = "뉴스 목록 조회",
            description = "네이버 뉴스 검색 API를 통해 뉴스를 검색합니다. 네이버 뉴스 링크(https://n.news.naver.com)만 필터링하여 반환합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = NewsResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping
    public ResponseEntity<NewsResponse> searchNews(
            @Parameter(description = "검색어 (예: 하나금융)",
                    required = true,
                    example = "하나금융")
            @RequestParam String query,

            @Parameter(description = "검색 결과 출력 건수 (기본값: 30, 최대: 100)",
                    example = "30")
            @RequestParam(required = false, defaultValue = "30") Integer display
    ) {
        log.info("뉴스 검색 요청 - 검색어: {}, 출력 건수: {}", query, display);

        try {
            // 입력 검증
            if (query == null || query.trim().isEmpty()) {
                log.error("검색어가 비어있습니다.");
                return ResponseEntity.badRequest().build();
            }

            if (display != null && (display < 1 || display > 100)) {
                log.error("출력 건수가 유효하지 않습니다: {}", display);
                return ResponseEntity.badRequest().build();
            }

            NewsResponse response = newsService.searchNews(query, display);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("뉴스 검색 실패", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(
            summary = "뉴스 요약 조회",
            description = "네이버 뉴스 링크를 기반으로 뉴스 요약 정보를 조회합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = NewsSummaryResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/summary")
    public ResponseEntity<NewsSummaryResponse> getNewsSummary(
            @Parameter(description = "네이버 뉴스 링크 (예: https://n.news.naver.com/article/016/0002538484?sid=101)",
                    required = true,
                    example = "https://n.news.naver.com/article/016/0002538484?sid=101")
            @RequestParam String link
    ) {
        log.info("뉴스 요약 조회 요청 - 링크: {}", link);

        try {
            // 입력 검증
            if (link == null || link.trim().isEmpty()) {
                log.error("뉴스 링크가 비어있습니다.");
                return ResponseEntity.badRequest().build();
            }

            NewsSummaryResponse response = newsService.getNewsSummary(link);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("뉴스 요약 조회 실패", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}

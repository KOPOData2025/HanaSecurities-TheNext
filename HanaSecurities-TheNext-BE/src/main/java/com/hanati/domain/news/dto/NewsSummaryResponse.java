package com.hanati.domain.news.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 뉴스 요약 조회 클라이언트 응답 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewsSummaryResponse {
    private boolean success;
    private String message;
    private SummaryData data;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SummaryData {
        private String title;       // 뉴스 제목
        private String summary;     // 뉴스 요약 내용
    }
}

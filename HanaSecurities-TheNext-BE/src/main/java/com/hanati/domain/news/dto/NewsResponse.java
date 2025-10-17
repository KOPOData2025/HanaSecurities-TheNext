package com.hanati.domain.news.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 뉴스 목록 조회 클라이언트 응답 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewsResponse {
    private boolean success;
    private String message;
    private NewsData data;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NewsData {
        private int total;          // 전체 검색 결과 개수
        private int display;        // 반환된 뉴스 개수
        private List<NewsItem> items;

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class NewsItem {
            private String title;           // 뉴스 제목
            private String originalLink;    // 원본 링크
            private String link;            // 네이버 뉴스 링크
            private String description;     // 뉴스 요약
            private String pubDate;         // 발행일시
        }
    }
}

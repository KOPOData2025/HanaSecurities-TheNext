package com.hanati.domain.news.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 네이버 뉴스 검색 API 응답 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NaverNewsApiResponse {

    @JsonProperty("lastBuildDate")
    private String lastBuildDate;

    @JsonProperty("total")
    private int total;

    @JsonProperty("start")
    private int start;

    @JsonProperty("display")
    private int display;

    @JsonProperty("items")
    private List<NewsItem> items;

    /**
     * 뉴스 아이템
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NewsItem {
        @JsonProperty("title")
        private String title;

        @JsonProperty("originallink")
        private String originalLink;

        @JsonProperty("link")
        private String link;

        @JsonProperty("description")
        private String description;

        @JsonProperty("pubDate")
        private String pubDate;
    }
}

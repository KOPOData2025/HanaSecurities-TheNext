package com.hanati.domain.news.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanati.common.config.TokenConfig;
import com.hanati.domain.news.dto.NaverNewsApiResponse;
import com.hanati.domain.news.dto.NewsResponse;
import com.hanati.domain.news.dto.NewsSummaryResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NewsService {

    private final TokenConfig tokenConfig;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    private static final String NAVER_NEWS_API_URL = "https://openapi.naver.com/v1/search/news.json";
    private static final String FILTER_PREFIX = "https://n.news.naver.com";
    private static final String SUMMARY_URL_TEMPLATE = "https://tts.news.naver.com/article/%s/%s/summary?callback=callback&JSON";

    /**
     * 뉴스 검색
     * @param query 검색어
     * @param display 검색 결과 출력 건수 (기본값: 30, 최대: 100)
     * @return 뉴스 검색 결과
     */
    public NewsResponse searchNews(String query, Integer display) {
        log.info("뉴스 검색 시작 - 검색어: {}, 출력 건수: {}", query, display);

        try {
            // 헤더 설정
            HttpHeaders headers = createNewsApiHeaders();

            // URL 구성 (start=1, sort=date 고정)
            String url = UriComponentsBuilder.fromUriString(NAVER_NEWS_API_URL)
                    .queryParam("query", query)
                    .queryParam("display", display != null ? display : 30)
                    .queryParam("start", 1)
                    .queryParam("sort", "date")
                    .build()
                    .toUriString();

            log.info("요청 URL: {}", url);

            HttpEntity<Void> httpRequest = new HttpEntity<>(headers);

            // API 호출
            ResponseEntity<NaverNewsApiResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    httpRequest,
                    NaverNewsApiResponse.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                NaverNewsApiResponse apiResponse = response.getBody();

                // items가 null인 경우 처리
                if (apiResponse.getItems() == null || apiResponse.getItems().isEmpty()) {
                    log.warn("뉴스 검색 결과가 없습니다.");
                    return NewsResponse.builder()
                            .success(true)
                            .message("검색 결과가 없습니다.")
                            .data(NewsResponse.NewsData.builder()
                                    .total(0)
                                    .display(0)
                                    .items(List.of())
                                    .build())
                            .build();
                }

                // 네이버 뉴스 링크만 필터링 (link 필드가 https://n.news.naver.com으로 시작하는 것만)
                List<NaverNewsApiResponse.NewsItem> filteredItems = apiResponse.getItems().stream()
                        .filter(item -> {
                            if (item.getLink() == null) {
                                log.debug("link가 null인 항목 필터링");
                                return false;
                            }
                            boolean isNaverNews = item.getLink().startsWith(FILTER_PREFIX);
                            log.debug("link: {} -> 네이버뉴스: {}", item.getLink(), isNaverNews);
                            return isNaverNews;
                        })
                        .collect(Collectors.toList());

                log.info("뉴스 검색 완료 - 전체: {}건, 필터링 후: {}건",
                        apiResponse.getItems().size(), filteredItems.size());

                // 응답 데이터 변환
                return convertToNewsResponse(apiResponse, filteredItems);
            }

        } catch (RestClientException e) {
            log.error("뉴스 검색 실패: {}", e.getMessage());
            return NewsResponse.builder()
                    .success(false)
                    .message("뉴스 검색 실패: " + e.getMessage())
                    .build();
        }

        return NewsResponse.builder()
                .success(false)
                .message("뉴스 검색 실패")
                .build();
    }

    /**
     * 네이버 뉴스 API 헤더 생성
     */
    private HttpHeaders createNewsApiHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Naver-Client-Id", tokenConfig.getNaverClientId());
        headers.set("X-Naver-Client-Secret", tokenConfig.getNaverClientSecret());
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    /**
     * 네이버 뉴스 API 응답을 클라이언트 응답으로 변환
     */
    private NewsResponse convertToNewsResponse(NaverNewsApiResponse apiResponse,
                                               List<NaverNewsApiResponse.NewsItem> filteredItems) {
        List<NewsResponse.NewsData.NewsItem> newsItems = filteredItems.stream()
                .map(item -> NewsResponse.NewsData.NewsItem.builder()
                        .title(item.getTitle())
                        .originalLink(item.getOriginalLink())
                        .link(item.getLink())
                        .description(item.getDescription())
                        .pubDate(item.getPubDate())
                        .build())
                .collect(Collectors.toList());

        NewsResponse.NewsData data = NewsResponse.NewsData.builder()
                .total(apiResponse.getTotal())
                .display(filteredItems.size())
                .items(newsItems)
                .build();

        return NewsResponse.builder()
                .success(true)
                .message("뉴스 검색 성공")
                .data(data)
                .build();
    }

    /**
     * 뉴스 요약 조회
     * @param newsLink 네이버 뉴스 링크 (예: https://n.news.naver.com/article/016/0002538484?sid=101)
     * @return 뉴스 요약 결과
     */
    public NewsSummaryResponse getNewsSummary(String newsLink) {
        log.info("뉴스 요약 조회 시작 - 링크: {}", newsLink);

        try {
            // URL에서 언론사 코드와 기사 번호 추출
            // 예: https://n.news.naver.com/article/016/0002538484?sid=101
            Pattern pattern = Pattern.compile("article/(\\d+)/(\\d+)");
            Matcher matcher = pattern.matcher(newsLink);

            if (!matcher.find()) {
                log.error("유효하지 않은 뉴스 링크 형식: {}", newsLink);
                return NewsSummaryResponse.builder()
                        .success(false)
                        .message("유효하지 않은 뉴스 링크 형식입니다.")
                        .build();
            }

            String pressCode = matcher.group(1);  // 언론사 코드
            String articleId = matcher.group(2);   // 기사 번호

            // 요약 API URL 생성
            String summaryUrl = String.format(SUMMARY_URL_TEMPLATE, pressCode, articleId);
            log.info("요약 API URL: {}", summaryUrl);

            // API 호출
            ResponseEntity<String> response = restTemplate.getForEntity(summaryUrl, String.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                String jsonpResponse = response.getBody();
                log.debug("JSONP 응답: {}", jsonpResponse);

                // JSONP 응답에서 JSON 추출 (/**/callback({ ... }); 형태)
                String json = jsonpResponse
                        .replaceFirst("^/\\*\\*/callback\\(", "")
                        .replaceFirst("\\);?$", "");

                // Jackson을 사용한 JSON 파싱 (큰따옴표 등 특수문자 자동 처리)
                try {
                    JsonNode jsonNode = objectMapper.readTree(json);
                    String title = jsonNode.get("title").asText();
                    String summary = jsonNode.get("summary").asText()
                            .replace("<br/>", "\n")
                            .replace("<br>", "\n");

                    log.info("뉴스 요약 조회 완료 - 제목: {}", title);

                    return NewsSummaryResponse.builder()
                            .success(true)
                            .message("뉴스 요약 조회 성공")
                            .data(NewsSummaryResponse.SummaryData.builder()
                                    .title(title)
                                    .summary(summary)
                                    .build())
                            .build();
                } catch (Exception e) {
                    log.error("JSON 파싱 실패: {}", e.getMessage());
                    return NewsSummaryResponse.builder()
                            .success(false)
                            .message("뉴스 요약 파싱 실패")
                            .build();
                }
            }

        } catch (RestClientException e) {
            log.error("뉴스 요약 조회 실패: {}", e.getMessage());
            return NewsSummaryResponse.builder()
                    .success(false)
                    .message("뉴스 요약 조회 실패: " + e.getMessage())
                    .build();
        }

        return NewsSummaryResponse.builder()
                .success(false)
                .message("뉴스 요약 조회 실패")
                .build();
    }
}

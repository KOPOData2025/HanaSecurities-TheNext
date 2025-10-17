package com.hanati.domain.index.service;

import com.hanati.common.config.TokenConfig;
import com.hanati.common.service.TokenService;
import com.hanati.domain.index.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class IndexService {

    private final TokenConfig tokenConfig;
    private final TokenService tokenService;
    private final RestTemplate restTemplate;

    // 지수 코드 매핑
    private static final Map<String, String> INDEX_CODES = new HashMap<>() {{
        put("KOSPI", "0001");
        put("KOSDAQ", "1001");
        put("KOSPI200", "2001");
    }};

    // 지수 이름 매핑
    private static final Map<String, String> INDEX_NAMES = new HashMap<>() {{
        put("0001", "코스피");
        put("1001", "코스닥");
        put("2001", "코스피 200");
    }};

    /**
     * 코스피 현재가 조회
     */
    public IndexPriceResponse getKospiPrice() {
        return getIndexPriceInternal("KOSPI");
    }

    /**
     * 코스닥 현재가 조회
     */
    public IndexPriceResponse getKosdaqPrice() {
        return getIndexPriceInternal("KOSDAQ");
    }

    /**
     * 코스피200 현재가 조회
     */
    public IndexPriceResponse getKospi200Price() {
        return getIndexPriceInternal("KOSPI200");
    }

    /**
     * 지수 현재가 조회 (공통 메서드)
     * @param indexType KOSPI, KOSDAQ, KOSPI200
     */
    public IndexPriceResponse getIndexPrice(String indexType) {
        if (!INDEX_CODES.containsKey(indexType)) {
            throw new IllegalArgumentException("Invalid index type: " + indexType);
        }
        return getIndexPriceInternal(indexType);
    }

    /**
     * 코스피 1분별 지수 조회
     */
    public IndexTimePriceResponse getKospiTimePrice() {
        return getIndexTimePriceInternal("KOSPI");
    }

    /**
     * 코스닥 1분별 지수 조회
     */
    public IndexTimePriceResponse getKosdaqTimePrice() {
        return getIndexTimePriceInternal("KOSDAQ");
    }

    /**
     * 코스피200 1분별 지수 조회
     */
    public IndexTimePriceResponse getKospi200TimePrice() {
        return getIndexTimePriceInternal("KOSPI200");
    }

    /**
     * 지수 시간별 가격 조회 (공통 메서드)
     * @param indexType KOSPI, KOSDAQ, KOSPI200
     */
    public IndexTimePriceResponse getIndexTimePrice(String indexType) {
        if (!INDEX_CODES.containsKey(indexType)) {
            throw new IllegalArgumentException("Invalid index type: " + indexType);
        }
        return getIndexTimePriceInternal(indexType);
    }

    /**
     * 지수 현재가 조회 (내부 메서드)
     */
    private IndexPriceResponse getIndexPriceInternal(String indexType) {
        String indexCode = INDEX_CODES.get(indexType);
        if (indexCode == null) {
            throw new IllegalArgumentException("Invalid index type: " + indexType);
        }

        log.info("{} 지수 현재가 조회 시작", indexType);

        try {
            // 헤더 설정
            HttpHeaders headers = createHeaders("FHPUP02100000");  // 지수현재가 조회 거래ID

            // URL 구성
            String url = UriComponentsBuilder.fromUriString(tokenConfig.getBaseUrl())
                    .path("/uapi/domestic-stock/v1/quotations/inquire-index-price")
                    .queryParam("fid_cond_mrkt_div_code", "U")  // 업종 구분 코드
                    .queryParam("fid_input_iscd", indexCode)
                    .build()
                    .toUriString();

            HttpEntity<Void> request = new HttpEntity<>(headers);

            // API 호출
            ResponseEntity<KisIndexApiResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    request,
                    KisIndexApiResponse.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                KisIndexApiResponse apiResponse = response.getBody();

                if (!"0".equals(apiResponse.getRtCd())) {
                    log.error("{} 지수 조회 실패: {}", indexType, apiResponse.getMsg1());
                    throw new RuntimeException("지수 조회 실패: " + apiResponse.getMsg1());
                }

                // 응답 데이터 변환
                return convertToIndexPriceResponse(apiResponse, indexCode, indexType);
            }

        } catch (RestClientException e) {
            log.error("{} 지수 조회 실패: {}", indexType, e.getMessage());
            throw new RuntimeException(indexType + " 지수 조회 실패", e);
        }

        throw new RuntimeException(indexType + " 지수 조회 실패");
    }

    /**
     * 지수 시간별 가격 조회 (내부 메서드)
     */
    private IndexTimePriceResponse getIndexTimePriceInternal(String indexType) {
        String indexCode = INDEX_CODES.get(indexType);
        if (indexCode == null) {
            throw new IllegalArgumentException("Invalid index type: " + indexType);
        }

        log.info("{} 1분별 지수 조회 시작", indexType);

        try {
            // 헤더 설정
            HttpHeaders headers = createHeaders("FHPUP02110200");  // 국내업종 시간별지수(분) 조회 거래ID

            // URL 구성
            String currentDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String url = UriComponentsBuilder.fromUriString(tokenConfig.getBaseUrl())
                    .path("/uapi/domestic-stock/v1/quotations/inquire-index-timeprice")
                    .queryParam("fid_cond_mrkt_div_code", "U")  // 업종 구분 코드
                    .queryParam("fid_input_iscd", indexCode)
                    .queryParam("fid_input_hour_1", "60")
                    .build()
                    .toUriString();

            HttpEntity<Void> request = new HttpEntity<>(headers);

            // API 호출
            ResponseEntity<KisIndexTimeApiResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    request,
                    KisIndexTimeApiResponse.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                KisIndexTimeApiResponse apiResponse = response.getBody();

                if (!"0".equals(apiResponse.getRtCd())) {
                    log.error("{} 시간별 지수 조회 실패: {}", indexType, apiResponse.getMsg1());
                    throw new RuntimeException("시간별 지수 조회 실패: " + apiResponse.getMsg1());
                }

                // 디버깅용 로그 추가
                log.debug("API Response - output size: {}",
                    apiResponse.getOutput() != null ? apiResponse.getOutput().size() : "null");

                // 응답 데이터 변환
                return convertToIndexTimePriceResponse(apiResponse, indexCode, indexType);
            }

        } catch (RestClientException e) {
            log.error("{} 시간별 지수 조회 실패: {}", indexType, e.getMessage());
            throw new RuntimeException(indexType + " 시간별 지수 조회 실패", e);
        }

        throw new RuntimeException(indexType + " 시간별 지수 조회 실패");
    }

    /**
     * API 요청 헤더 생성
     */
    private HttpHeaders createHeaders(String trId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("authorization", "Bearer " + tokenService.getRestApiAccessToken());
        headers.set("appkey", tokenConfig.getAppKey());
        headers.set("appsecret", tokenConfig.getAppSecret());
        headers.set("tr_id", trId);  // API별 거래ID
        headers.set("custtype", "P");  // 고객타입 (개인)

        return headers;
    }

    /**
     * KIS API 응답을 IndexPriceResponse로 변환
     */
    private IndexPriceResponse convertToIndexPriceResponse(KisIndexApiResponse apiResponse, String indexCode, String indexType) {
        KisIndexApiResponse.Output output = apiResponse.getOutput();

        return IndexPriceResponse.builder()
                .indexCode(indexCode)
                .indexName(INDEX_NAMES.get(indexCode))
                .currentPrice(output.getCurrentPrice())
                .changePrice(output.getChangePrice())
                .changeRate(output.getChangeRate())
                .changeSign(output.getChangeSign())
                .openPrice(output.getOpenPrice())
                .highPrice(output.getHighPrice())
                .lowPrice(output.getLowPrice())
                .volume(output.getVolume())
                .tradingValue(output.getTradingValue())
                .marketCap(null)  // 지수는 시가총액 없음
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();
    }

    /**
     * KIS API 응답을 IndexTimePriceResponse로 변환 (시간별 지수용)
     */
    private IndexTimePriceResponse convertToIndexTimePriceResponse(KisIndexTimeApiResponse apiResponse, String indexCode, String indexType) {
        List<IndexTimePriceResponse.TimePrice> timePrices = new ArrayList<>();

        if (apiResponse.getOutput() != null && !apiResponse.getOutput().isEmpty()) {
            for (KisIndexTimeApiResponse.TimeData timeData : apiResponse.getOutput()) {
                IndexTimePriceResponse.TimePrice timePrice = IndexTimePriceResponse.TimePrice.builder()
                        .time(timeData.getTime())
                        .price(timeData.getPrice())
                        .changePrice(timeData.getChangePrice())
                        .changeRate(timeData.getChangeRate())
                        .changeSign(timeData.getChangeSign())
                        .volume(timeData.getVolume())
                        .tradingValue(timeData.getTradingValue())
                        .build();
                timePrices.add(timePrice);
            }
            log.debug("Converted {} time price data for {}", timePrices.size(), indexType);
        } else {
            log.warn("No time price data found in output for {}", indexType);
        }

        return IndexTimePriceResponse.builder()
                .indexCode(indexCode)
                .indexName(INDEX_NAMES.get(indexCode))
                .timePrices(timePrices)
                .build();
    }
}
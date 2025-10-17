package com.hanati.domain.gold.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanati.common.config.TokenConfig;
import com.hanati.common.service.KiwoomTokenService;
import com.hanati.domain.gold.dto.GoldCandleData;
import com.hanati.domain.gold.dto.GoldChartResponse;
import com.hanati.domain.gold.dto.GoldCurrentPriceResponse;
import com.hanati.domain.gold.dto.GoldQuoteData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 금현물 조회 서비스
 *
 * 기능:
 * - 키움증권 API를 통한 금현물 시세 조회
 * - 금현물 호가 정보 조회
 * - 금현물 차트 데이터 조회
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GoldService {

    private final KiwoomTokenService kiwoomTokenService;
    private final TokenConfig tokenConfig;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    // 키움증권 API 엔드포인트
    private static final String MARKET_COND_ENDPOINT = "/api/dostk/mrkcond";
    private static final String CHART_ENDPOINT = "/api/dostk/chart";

    // 금현물 상품 코드
    private static final String GOLD_1KG = "M04020000";
    private static final String GOLD_100G = "M04020100";

    /**
     * 키움증권 API 요청 헤더 생성
     * @param apiId API ID (TR_ID)
     * @return HttpHeaders
     */
    private HttpHeaders createHeaders(String apiId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 키움증권 액세스 토큰 가져오기
        String accessToken = kiwoomTokenService.getKiwoomAccessToken();
        if (accessToken == null) {
            throw new RuntimeException("키움증권 액세스 토큰을 가져올 수 없습니다.");
        }

        // Authorization 헤더 (Bearer 토큰)
        headers.set("authorization", "Bearer " + accessToken);

        // API ID 헤더
        headers.set("api-id", apiId);

        log.debug("[금현물 Service] API 헤더 생성 완료 - api-id: {}", apiId);
        return headers;
    }

    /**
     * 키움증권 API 기본 URL 가져오기
     * @return 키움증권 API Base URL
     */
    private String getKiwoomApiBaseUrl() {
        return tokenConfig.getKiwoomBaseUrl();
    }

    /**
     * 금현물 현재가 조회 (ka50100)
     * @param productCode 상품 코드 (M04020000, M04020100)
     * @return 금현물 현재가 정보
     */
    public GoldCurrentPriceResponse getCurrentPrice(String productCode) {
        log.info("[금현물 Service] 현재가 조회 - productCode: {}", productCode);

        try {
            // 헤더 설정
            HttpHeaders headers = createHeaders("ka50100");

            // 요청 바디 구성
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("stk_cd", productCode);

            HttpEntity<Map<String, Object>> httpRequest = new HttpEntity<>(requestBody, headers);

            // URL 구성
            String url = getKiwoomApiBaseUrl() + MARKET_COND_ENDPOINT;

            // API 호출
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    httpRequest,
                    String.class
            );

            // 응답 파싱
            @SuppressWarnings("unchecked")
            Map<String, Object> responseMap = objectMapper.readValue(response.getBody(), Map.class);

            // 응답 데이터 추출 및 변환
            return GoldCurrentPriceResponse.builder()
                    .productCode(productCode)
                    .productName(getProductName(productCode))
                    .currentPrice(parseDouble(responseMap.get("cur_pric")))
                    .changeAmount(parseDouble(responseMap.get("pred_pre")))
                    .changeRate(parseDouble(responseMap.get("flu_rt")))
                    .highPrice(parseDouble(responseMap.get("high_pric")))
                    .lowPrice(parseDouble(responseMap.get("low_pric")))
                    .openPrice(parseDouble(responseMap.get("open_pric")))
                    .previousClose(parseDouble(responseMap.get("pred_close_pric")))
                    .volume(parseLong(responseMap.get("trde_qty")))
                    .timestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                    .build();

        } catch (Exception e) {
            log.error("[금현물 현재가 조회] 실패 - productCode: {}", productCode, e);
            throw new RuntimeException("금현물 현재가 조회 실패", e);
        }
    }

    /**
     * 금현물 호가 조회 (ka50101)
     * @param productCode 상품 코드
     * @return 금현물 호가 정보
     */
    public GoldQuoteData getOrderBook(String productCode) {
        log.info("[금현물 Service] 호가 조회 - productCode: {}", productCode);

        try {
            // 헤더 설정
            HttpHeaders headers = createHeaders("ka50101");

            // 요청 바디 구성
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("stk_cd", productCode);
            requestBody.put("tic_scope", "1"); // 1틱

            HttpEntity<Map<String, Object>> httpRequest = new HttpEntity<>(requestBody, headers);

            // URL 구성
            String url = getKiwoomApiBaseUrl() + MARKET_COND_ENDPOINT;

            // API 호출
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    httpRequest,
                    String.class
            );

            // 응답 파싱
            @SuppressWarnings("unchecked")
            Map<String, Object> responseMap = objectMapper.readValue(response.getBody(), Map.class);

            // gold_bid 리스트 추출
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> goldBidList = (List<Map<String, Object>>) responseMap.get("gold_bid");

            // 최신 데이터 (첫 번째 항목) 사용
            if (goldBidList == null || goldBidList.isEmpty()) {
                throw new RuntimeException("호가 데이터가 없습니다.");
            }

            Map<String, Object> latestQuote = goldBidList.get(0);

            // ka50101은 체결 데이터를 반환함 (10단 호가 데이터는 WebSocket으로만 제공됨)
            // 체결 시점의 매도/매수 호가만 제공 (단일 가격)
            Double askPrice = parseDouble(latestQuote.get("pri_sel_bid_unit"));
            Double bidPrice = parseDouble(latestQuote.get("pri_buy_bid_unit"));

            // REST API로는 전체 10단 호가 정보를 얻을 수 없으므로
            // WebSocket을 사용하는 것을 권장함
            // 여기서는 최근 체결가 기준으로 1단 호가만 반환
            return GoldQuoteData.builder()
                    .productCode(productCode)
                    .bidPrice1(bidPrice)
                    .bidQuantity1(0L)  // ka50101에서는 호가 수량 정보 미제공
                    .askPrice1(askPrice)
                    .askQuantity1(0L)  // ka50101에서는 호가 수량 정보 미제공
                    .timestamp(parseTimestamp(latestQuote.get("tm")))
                    .build();

        } catch (Exception e) {
            log.error("[금현물 호가 조회] 실패 - productCode: {}", productCode, e);
            throw new RuntimeException("금현물 호가 조회 실패", e);
        }
    }

    /**
     * 금현물 분봉 데이터 조회 (ka50080)
     * @param productCode 상품 코드
     * @param interval 분봉 간격
     * @param count 조회할 데이터 개수
     * @return 분봉 차트 데이터
     */
    public GoldChartResponse getMinuteChart(String productCode, int interval, int count) {
        log.info("[금현물 Service] 분봉 데이터 조회 - productCode: {}, interval: {}, count: {}",
                productCode, interval, count);

        try {
            // 분봉 간격 유효성 검증
            if (!isValidInterval(interval)) {
                throw new RuntimeException("유효하지 않은 분봉 간격: " + interval);
            }

            // 헤더 설정
            HttpHeaders headers = createHeaders("ka50080");

            // 요청 바디 구성
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("stk_cd", productCode);
            requestBody.put("tic_scope", String.valueOf(interval));
            requestBody.put("upd_stkpc_tp", "1");

            HttpEntity<Map<String, Object>> httpRequest = new HttpEntity<>(requestBody, headers);

            // URL 구성
            String url = getKiwoomApiBaseUrl() + CHART_ENDPOINT;

            // API 호출
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    httpRequest,
                    String.class
            );

            // 응답 파싱
            @SuppressWarnings("unchecked")
            Map<String, Object> responseMap = objectMapper.readValue(response.getBody(), Map.class);

            // gds_min_chart_qry 리스트 추출
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> chartDataList = (List<Map<String, Object>>) responseMap.get("gds_min_chart_qry");

            if (chartDataList == null) {
                chartDataList = new ArrayList<>();
            }

            // 차트 데이터 변환
            List<GoldCandleData> candles = new ArrayList<>();
            for (Map<String, Object> item : chartDataList) {
                GoldCandleData candle = GoldCandleData.builder()
                        .timestamp(parseTimestamp(item.get("cntr_tm")))
                        .open(parseDouble(item.get("open_pric")))
                        .high(parseDouble(item.get("high_pric")))
                        .low(parseDouble(item.get("low_pric")))
                        .close(parseDouble(item.get("cur_prc")))
                        .volume(parseLong(item.get("trde_qty")))
                        .build();
                candles.add(candle);
            }

            return GoldChartResponse.builder()
                    .interval(interval + "분")
                    .data(candles)
                    .build();

        } catch (Exception e) {
            log.error("[금현물 분봉 조회] 실패 - productCode: {}, interval: {}", productCode, interval, e);
            throw new RuntimeException("금현물 분봉 조회 실패", e);
        }
    }

    /**
     * 금현물 일/주/월봉 데이터 조회 (ka50081/82/83)
     * @param productCode 상품 코드
     * @param period 기간 타입 (day, week, month)
     * @param count 조회할 데이터 개수
     * @return 차트 데이터
     */
    public GoldChartResponse getPeriodChart(String productCode, String period, int count) {
        log.info("[금현물 Service] {}봉 데이터 조회 - productCode: {}, count: {}", period, productCode, count);

        try {
            // 기간 타입 유효성 검증
            if (!isValidPeriod(period)) {
                throw new RuntimeException("유효하지 않은 기간 타입: " + period);
            }

            // API ID 결정
            String apiId = getApiIdForPeriod(period);

            // 헤더 설정
            HttpHeaders headers = createHeaders(apiId);

            // 요청 바디 구성
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("stk_cd", productCode);
            requestBody.put("base_dt", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
            requestBody.put("upd_stkpc_tp", "1");

            HttpEntity<Map<String, Object>> httpRequest = new HttpEntity<>(requestBody, headers);

            // URL 구성
            String url = getKiwoomApiBaseUrl() + CHART_ENDPOINT;

            // API 호출
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    httpRequest,
                    String.class
            );

            // 응답 파싱
            @SuppressWarnings("unchecked")
            Map<String, Object> responseMap = objectMapper.readValue(response.getBody(), Map.class);

            // 차트 데이터 리스트 추출 (기간별로 키 이름이 다름)
            String chartDataKey = getChartDataKeyForPeriod(period);
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> chartDataList = (List<Map<String, Object>>) responseMap.get(chartDataKey);

            if (chartDataList == null) {
                chartDataList = new ArrayList<>();
            }

            // 차트 데이터 변환
            List<GoldCandleData> candles = new ArrayList<>();
            for (Map<String, Object> item : chartDataList) {
                GoldCandleData candle = GoldCandleData.builder()
                        .timestamp(parseDate(item.get("dt")))
                        .open(parseDouble(item.get("open_pric")))
                        .high(parseDouble(item.get("high_pric")))
                        .low(parseDouble(item.get("low_pric")))
                        .close(parseDouble(item.get("cur_pric")))
                        .volume(parseLong(item.get("acc_trde_qty")))
                        .build();
                candles.add(candle);
            }

            return GoldChartResponse.builder()
                    .interval(getPeriodName(period))
                    .data(candles)
                    .build();

        } catch (Exception e) {
            log.error("[금현물 기간별 차트 조회] 실패 - productCode: {}, period: {}", productCode, period, e);
            throw new RuntimeException("금현물 기간별 차트 조회 실패", e);
        }
    }

    /**
     * 상품명 가져오기
     */
    private String getProductName(String productCode) {
        if (GOLD_1KG.equals(productCode)) {
            return "금 99.99% 1Kg";
        } else if (GOLD_100G.equals(productCode)) {
            return "미니금 99.99% 100g";
        }
        return productCode;
    }

    /**
     * 기간에 따른 API ID 반환
     */
    private String getApiIdForPeriod(String period) {
        switch (period) {
            case "day":
                return "ka50081";
            case "week":
                return "ka50082";
            case "month":
                return "ka50083";
            default:
                throw new RuntimeException("유효하지 않은 기간 타입: " + period);
        }
    }

    /**
     * 기간에 따른 차트 데이터 키 반환
     */
    private String getChartDataKeyForPeriod(String period) {
        switch (period) {
            case "day":
                return "gds_day_chart_qry";
            case "week":
                return "gds_week_chart_qry";
            case "month":
                return "gds_month_chart_qry";
            default:
                throw new RuntimeException("유효하지 않은 기간 타입: " + period);
        }
    }

    /**
     * 기간명 반환
     */
    private String getPeriodName(String period) {
        switch (period) {
            case "day":
                return "일봉";
            case "week":
                return "주봉";
            case "month":
                return "월봉";
            default:
                return period;
        }
    }

    /**
     * 분봉 간격 유효성 검증
     */
    private boolean isValidInterval(int interval) {
        return interval == 1 || interval == 3 || interval == 5 ||
               interval == 10 || interval == 15 || interval == 30 ||
               interval == 45 || interval == 60;
    }

    /**
     * 기간 타입 유효성 검증
     */
    private boolean isValidPeriod(String period) {
        return period != null &&
               (period.equals("day") || period.equals("week") || period.equals("month"));
    }

    /**
     * Double 파싱 헬퍼
     */
    private Double parseDouble(Object value) {
        if (value == null) {
            return null;
        }
        try {
            String str = value.toString().trim().replace("+", "");
            return Double.parseDouble(str);
        } catch (NumberFormatException e) {
            log.warn("[금현물 Service] Double 파싱 실패: {}", value);
            return null;
        }
    }

    /**
     * Long 파싱 헬퍼
     */
    private Long parseLong(Object value) {
        if (value == null) {
            return null;
        }
        try {
            String str = value.toString().trim();
            return Long.parseLong(str);
        } catch (NumberFormatException e) {
            log.warn("[금현물 Service] Long 파싱 실패: {}", value);
            return null;
        }
    }

    /**
     * 타임스탬프 파싱 (yyyyMMddHHmmss -> ISO 8601)
     */
    private String parseTimestamp(Object value) {
        if (value == null) {
            return LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        }
        try {
            String str = value.toString().trim();
            // yyyyMMddHHmmss 형식 파싱
            LocalDateTime dateTime = LocalDateTime.parse(str, DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            return dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (Exception e) {
            log.warn("[금현물 Service] 타임스탬프 파싱 실패: {}", value);
            return LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        }
    }

    /**
     * 날짜 파싱 (yyyyMMdd -> ISO 8601 date)
     */
    private String parseDate(Object value) {
        if (value == null) {
            return LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        }
        try {
            String str = value.toString().trim();
            // yyyyMMdd 형식 파싱
            if (str.length() == 8) {
                return str.substring(0, 4) + "-" + str.substring(4, 6) + "-" + str.substring(6, 8);
            }
            return str;
        } catch (Exception e) {
            log.warn("[금현물 Service] 날짜 파싱 실패: {}", value);
            return LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        }
    }
}

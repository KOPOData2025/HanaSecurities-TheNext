package com.hanati.domain.stock.service;

import com.hanati.common.config.TokenConfig;
import com.hanati.common.service.TokenService;
import com.hanati.domain.stock.dto.*;
import com.hanati.domain.stock.entity.StockChartData;
import com.hanati.domain.stock.entity.StockInvestOpinion;
import com.hanati.domain.stock.entity.StockOverview;
import com.hanati.domain.stock.repository.StockChartDataRepository;
import com.hanati.domain.stock.repository.StockInvestOpinionRepository;
import com.hanati.domain.stock.repository.StockOverviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class StockService {

    private final TokenConfig tokenConfig;
    private final TokenService tokenService;
    private final RestTemplate restTemplate;

    // DB 우선 조회 패턴을 위한 의존성
    private final StockOverviewRepository stockOverviewRepository;
    private final StockInvestOpinionRepository stockInvestOpinionRepository;
    private final StockChartDataRepository stockChartDataRepository;
    private final StockDataSyncService stockDataSyncService;
    private final com.hanati.domain.stock.repository.StockRepository stockRepository;

    /**
     * 주식 당일 분봉 조회
     * @param stockCode 종목 코드
     * @param inputTime 입력시간 (HHMMSS)
     * @return 당일 분봉 데이터
     */
    public IntradayChartResponse getIntradayChart(String stockCode, String inputTime) {
        log.info("주식 당일 분봉 조회 시작 - 종목코드: {}, 입력시간: {}", stockCode, inputTime);

        try {
            // 헤더 설정
            HttpHeaders headers = createIntradayChartHeaders();

            // URL 구성
            String url = UriComponentsBuilder.fromUriString(tokenConfig.getBaseUrl())
                    .path("/uapi/domestic-stock/v1/quotations/inquire-time-itemchartprice")
                    .queryParam("FID_COND_MRKT_DIV_CODE", "J")  // J:KRX
                    .queryParam("FID_INPUT_ISCD", stockCode)
                    .queryParam("FID_INPUT_HOUR_1", inputTime)
                    .queryParam("FID_PW_DATA_INCU_YN", "Y")  // 과거 데이터 포함 여부
                    .queryParam("FID_ETC_CLS_CODE", "")  // 기타 구분 코드
                    .build()
                    .toUriString();

            HttpEntity<Void> httpRequest = new HttpEntity<>(headers);

            // API 호출
            ResponseEntity<KisIntradayChartApiResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    httpRequest,
                    KisIntradayChartApiResponse.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                KisIntradayChartApiResponse apiResponse = response.getBody();

                if (!"0".equals(apiResponse.getRtCd())) {
                    log.error("당일 분봉 조회 실패: {}", apiResponse.getMsg1());
                    throw new RuntimeException("당일 분봉 조회 실패: " + apiResponse.getMsg1());
                }

                // 응답 데이터 변환
                return convertToIntradayChartResponse(apiResponse, stockCode);
            }

        } catch (RestClientException e) {
            log.error("당일 분봉 조회 실패: {}", e.getMessage());
            throw new RuntimeException("당일 분봉 조회 실패", e);
        }

        throw new RuntimeException("당일 분봉 조회 실패");
    }

    /**
     * 국내주식 기간별 시세 조회 (일/주/월/년) - DB 캐시 우선
     * @param stockCode 종목 코드
     * @param startDate 조회 시작일자 (YYYYMMDD)
     * @param endDate 조회 종료일자 (YYYYMMDD)
     * @param periodCode 기간분류코드 (D:일봉, W:주봉, M:월봉, Y:년봉)
     * @return 기간별 시세 데이터
     */
    public PeriodChartResponse getPeriodChart(String stockCode, String startDate, String endDate, String periodCode) {
        log.info("[차트 데이터 조회] 종목코드: {}, 기간: {} ({} ~ {})", stockCode, periodCode, startDate, endDate);

        try {
            // 1. DB 조회 - 가장 최근 데이터 확인
            Optional<StockChartData> latestDataOpt = stockChartDataRepository
                    .findTopByStockCodeAndPeriodTypeOrderByTradeDateDesc(stockCode, periodCode);

            // 2. DB에 데이터 있으면
            if (latestDataOpt.isPresent()) {
                StockChartData latestData = latestDataOpt.get();
                String latestTradeDate = latestData.getTradeDate();
                log.info("[DB 조회 성공] 종목코드: {}, 기간: {}, 최신 거래일: {}", stockCode, periodCode, latestTradeDate);

                // 요청 기간의 데이터 조회
                List<StockChartData> dbData = stockChartDataRepository
                        .findByStockCodeAndPeriodTypeAndTradeDateBetweenOrderByTradeDateDesc(
                                stockCode, periodCode, startDate, endDate);

                // 3. 최신 데이터 여부 확인
                if (isChartDataRecent(latestTradeDate)) {
                    log.info("[캐시 적중] 종목코드: {}, 기간: {} - 최신 데이터 사용", stockCode, periodCode);
                    return convertDbToChartResponse(dbData, stockCode, periodCode, true);
                } else {
                    // 오래된 데이터 → 백그라운드 업데이트 트리거
                    log.info("[비동기 업데이트 트리거] 종목코드: {}, 기간: {} - 최신 거래일: {}",
                            stockCode, periodCode, latestTradeDate);
                    stockDataSyncService.syncChartDataAsync(stockCode, periodCode);
                    return convertDbToChartResponse(dbData, stockCode, periodCode, true);
                }
            }

            // 4. DB에 없으면 동기 API 호출 + 저장 + 반환
            log.info("[초회 조회] DB에 없음, API 동기 호출 - 종목코드: {}, 기간: {}", stockCode, periodCode);
            return fetchAndSaveChartData(stockCode, startDate, endDate, periodCode);

        } catch (Exception e) {
            log.error("[차트 데이터 조회 실패] 종목코드: {}, 기간: {}, 에러: {}", stockCode, periodCode, e.getMessage(), e);
            throw new RuntimeException("차트 데이터 조회 실패: " + e.getMessage(), e);
        }
    }

    /**
     * 국내주식 기간별 시세 직접 조회 (DB 캐싱 없음, 성능 비교용)
     * - DB 조회 안 함
     * - 매번 한투 API 직접 호출
     * - DB 저장 안 함
     * - 바로 클라이언트에 응답
     *
     * @param stockCode 종목 코드
     * @param startDate 조회 시작일자 (YYYYMMDD)
     * @param endDate 조회 종료일자 (YYYYMMDD)
     * @param periodCode 기간분류코드 (D:일봉, W:주봉, M:월봉, Y:년봉)
     * @return 기간별 시세 데이터
     */
    public PeriodChartResponse getPeriodChartDirect(String stockCode, String startDate, String endDate, String periodCode) {
        log.info("[차트 데이터 직접 조회 - No Cache] 종목코드: {}, 기간: {} ({} ~ {})", stockCode, periodCode, startDate, endDate);

        try {
            // 헤더 설정
            HttpHeaders headers = createPeriodChartHeaders();

            // URL 구성
            String url = UriComponentsBuilder.fromUriString(tokenConfig.getBaseUrl())
                    .path("/uapi/domestic-stock/v1/quotations/inquire-daily-itemchartprice")
                    .queryParam("FID_COND_MRKT_DIV_CODE", "J")  // J:KRX
                    .queryParam("FID_INPUT_ISCD", stockCode)
                    .queryParam("FID_INPUT_DATE_1", startDate)
                    .queryParam("FID_INPUT_DATE_2", endDate)
                    .queryParam("FID_PERIOD_DIV_CODE", periodCode)
                    .queryParam("FID_ORG_ADJ_PRC", "0")  // 0: 수정주가
                    .build()
                    .toUriString();

            HttpEntity<Void> httpRequest = new HttpEntity<>(headers);

            // API 호출
            log.info("[API 직접 호출 시작] 종목코드: {}, 기간: {}", stockCode, periodCode);
            ResponseEntity<KisPeriodChartApiResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    httpRequest,
                    KisPeriodChartApiResponse.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                KisPeriodChartApiResponse apiResponse = response.getBody();

                if (!"0".equals(apiResponse.getRtCd())) {
                    log.error("[API 호출 실패] {}", apiResponse.getMsg1());
                    throw new RuntimeException("API 호출 실패: " + apiResponse.getMsg1());
                }

                log.info("[API 직접 호출 성공 - No Cache] 종목코드: {}, 기간: {}, 데이터 건수: {}",
                        stockCode, periodCode, apiResponse.getOutput2() != null ? apiResponse.getOutput2().size() : 0);

                // DB 저장 없이 바로 응답 데이터 변환하여 반환
                return convertToPeriodChartResponseDirect(apiResponse, stockCode, periodCode);
            }

            throw new RuntimeException("API 응답 없음");

        } catch (RestClientException e) {
            log.error("[API 직접 호출 실패] {}", e.getMessage());
            throw new RuntimeException("API 호출 실패", e);
        }
    }

    /**
     * 차트 데이터가 최신인지 판단
     * 가장 최근 거래일이 어제 이후면 최신으로 간주 (영업일 고려)
     */
    private boolean isChartDataRecent(String latestTradeDate) {
        try {
            LocalDate latest = LocalDate.parse(latestTradeDate, DateTimeFormatter.BASIC_ISO_DATE);
            LocalDate yesterday = LocalDate.now().minusDays(1);
            return !latest.isBefore(yesterday);  // 어제 또는 그 이후
        } catch (Exception e) {
            log.warn("[날짜 파싱 실패] {}", latestTradeDate);
            return false;
        }
    }

    /**
     * DB에 없을 때: 동기 API 호출 → DB 저장 → 응답 반환
     */
    private PeriodChartResponse fetchAndSaveChartData(String stockCode, String startDate, String endDate, String periodCode) {
        try {
            // 헤더 설정
            HttpHeaders headers = createPeriodChartHeaders();

            // URL 구성
            String url = UriComponentsBuilder.fromUriString(tokenConfig.getBaseUrl())
                    .path("/uapi/domestic-stock/v1/quotations/inquire-daily-itemchartprice")
                    .queryParam("FID_COND_MRKT_DIV_CODE", "J")  // J:KRX
                    .queryParam("FID_INPUT_ISCD", stockCode)
                    .queryParam("FID_INPUT_DATE_1", startDate)
                    .queryParam("FID_INPUT_DATE_2", endDate)
                    .queryParam("FID_PERIOD_DIV_CODE", periodCode)
                    .queryParam("FID_ORG_ADJ_PRC", "0")  // 0: 수정주가
                    .build()
                    .toUriString();

            HttpEntity<Void> httpRequest = new HttpEntity<>(headers);

            // API 호출
            ResponseEntity<KisPeriodChartApiResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    httpRequest,
                    KisPeriodChartApiResponse.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                KisPeriodChartApiResponse apiResponse = response.getBody();

                if (!"0".equals(apiResponse.getRtCd())) {
                    log.error("[API 호출 실패] {}", apiResponse.getMsg1());
                    throw new RuntimeException("API 호출 실패: " + apiResponse.getMsg1());
                }

                // DB 저장
                saveChartDataToDb(stockCode, periodCode, apiResponse);

                // 응답 데이터 변환
                return convertToPeriodChartResponse(apiResponse, stockCode, periodCode);
            }

            throw new RuntimeException("API 응답 없음");

        } catch (RestClientException e) {
            log.error("[API 호출 실패] {}", e.getMessage());
            throw new RuntimeException("API 호출 실패", e);
        }
    }

    /**
     * API 응답 데이터를 DB에 저장 (UPSERT)
     */
    @Transactional
    private void saveChartDataToDb(String stockCode, String periodType, KisPeriodChartApiResponse apiResponse) {
        try {
            if (apiResponse.getOutput2() == null || apiResponse.getOutput2().isEmpty()) {
                log.warn("[DB 저장 스킵] 차트 데이터 없음 - 종목코드: {}, 기간: {}", stockCode, periodType);
                return;
            }

            int savedCount = 0;
            for (KisPeriodChartApiResponse.PeriodData data : apiResponse.getOutput2()) {
                // 기존 데이터 조회 또는 새 엔티티 생성
                com.hanati.domain.stock.entity.StockChartDataId id =
                    new com.hanati.domain.stock.entity.StockChartDataId(stockCode, periodType, data.getStckBsopDate());

                StockChartData chartData = stockChartDataRepository.findById(id)
                        .orElse(StockChartData.builder()
                                .stockCode(stockCode)
                                .periodType(periodType)
                                .tradeDate(data.getStckBsopDate())
                                .build());

                // setter로 필드 업데이트
                chartData.setOpenPrice(data.getStckOprc());
                chartData.setHighPrice(data.getStckHgpr());
                chartData.setLowPrice(data.getStckLwpr());
                chartData.setClosePrice(data.getStckClpr());
                chartData.setVolume(data.getAcmlVol());
                chartData.setTradingValue(data.getAcmlTrPbmn());
                chartData.setChangeSign(data.getPrdyVrssSign());
                chartData.setChangePrice(data.getPrdyVrss());

                stockChartDataRepository.save(chartData);
                savedCount++;
            }

            log.info("[DB 저장 완료] 종목코드: {}, 기간: {}, 저장 건수: {}", stockCode, periodType, savedCount);

        } catch (Exception e) {
            log.error("[DB 저장 실패] 종목코드: {}, 기간: {}, 에러: {}", stockCode, periodType, e.getMessage(), e);
        }
    }

    /**
     * DB 데이터를 응답 형식으로 변환
     */
    private PeriodChartResponse convertDbToChartResponse(List<StockChartData> dbData, String stockCode,
                                                          String periodType, boolean isCache) {
        List<PeriodChartResponse.PeriodItem> periodItems = new ArrayList<>();

        for (StockChartData data : dbData) {
            PeriodChartResponse.PeriodItem item = PeriodChartResponse.PeriodItem.builder()
                    .date(data.getTradeDate())
                    .close(data.getClosePrice())
                    .open(data.getOpenPrice())
                    .high(data.getHighPrice())
                    .low(data.getLowPrice())
                    .volume(data.getVolume())
                    .tradingValue(data.getTradingValue())
                    .changeSign(data.getChangeSign())
                    .changePrice(data.getChangePrice())
                    .build();
            periodItems.add(item);
        }

        // 최신 데이터에서 현재가 정보 추출 (없으면 빈 값)
        String currentPrice = "";
        String changeSign = "";
        String changePrice = "";
        String changeRate = "";
        String volume = "";
        String tradingValue = "";

        if (!dbData.isEmpty()) {
            StockChartData latest = dbData.get(0);
            currentPrice = latest.getClosePrice();
            changeSign = latest.getChangeSign();
            changePrice = latest.getChangePrice();
            volume = latest.getVolume();
            tradingValue = latest.getTradingValue();
        }

        String message = isCache ? " (DB 캐시)" : "";

        return PeriodChartResponse.builder()
                .stockCode(stockCode)
                .stockName("")  // DB에는 종목명 없음
                .currentPrice(currentPrice)
                .changeSign(changeSign)
                .changePrice(changePrice)
                .changeRate(changeRate)
                .volume(volume)
                .tradingValue(tradingValue)
                .totalShares("")  // DB에는 상장주수 없음
                .periodType(periodType)
                .chartData(periodItems)
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + message)
                .build();
    }

    /**
     * 당일 분봉 API 헤더 생성
     */
    private HttpHeaders createIntradayChartHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("authorization", "Bearer " + tokenService.getRestApiAccessToken());
        headers.set("appkey", tokenConfig.getAppKey());
        headers.set("appsecret", tokenConfig.getAppSecret());
        headers.set("tr_id", "FHKST03010200");  // 주식당일분봉조회 거래ID
        headers.set("custtype", "P");  // 개인

        return headers;
    }

    /**
     * 기간별 시세 API 헤더 생성
     */
    private HttpHeaders createPeriodChartHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("authorization", "Bearer " + tokenService.getRestApiAccessToken());
        headers.set("appkey", tokenConfig.getAppKey());
        headers.set("appsecret", tokenConfig.getAppSecret());
        headers.set("tr_id", "FHKST03010100");  // 국내주식기간별시세 거래ID
        headers.set("custtype", "P");  // 개인

        return headers;
    }

    /**
     * 당일 분봉 API 응답을 클라이언트 응답으로 변환
     */
    private IntradayChartResponse convertToIntradayChartResponse(KisIntradayChartApiResponse apiResponse, String stockCode) {
        List<IntradayChartResponse.ChartItem> chartItems = new ArrayList<>();

        if (apiResponse.getOutput2() != null && !apiResponse.getOutput2().isEmpty()) {
            for (KisIntradayChartApiResponse.ChartData data : apiResponse.getOutput2()) {
                IntradayChartResponse.ChartItem item = IntradayChartResponse.ChartItem.builder()
                        .date(data.getStckBsopDate())
                        .time(data.getStckCntgHour())
                        .close(data.getStckPrpr())
                        .open(data.getStckOprc())
                        .high(data.getStckHgpr())
                        .low(data.getStckLwpr())
                        .volume(data.getCntgVol())
                        .tradingValue(data.getAcmlTrPbmn())
                        .build();
                chartItems.add(item);
            }
            log.info("당일 분봉 {}개 조회 완료", chartItems.size());
        }

        KisIntradayChartApiResponse.Output1 output1 = apiResponse.getOutput1();

        return IntradayChartResponse.builder()
                .stockCode(stockCode)
                .stockName(output1.getHtsKorIsnm())
                .currentPrice(output1.getStckPrpr())
                .changeSign(output1.getPrdyVrssSign())
                .changePrice(output1.getPrdyVrss())
                .changeRate(output1.getPrdyCtrt())
                .volume(output1.getAcmlVol())
                .tradingValue(output1.getAcmlTrPbmn())
                .totalShares("0")  // 분봉 API에는 상장 주수 정보 없음
                .chartData(chartItems)
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();
    }

    /**
     * 기간별 시세 API 응답을 클라이언트 응답으로 변환
     */
    private PeriodChartResponse convertToPeriodChartResponse(KisPeriodChartApiResponse apiResponse, String stockCode, String periodCode) {
        List<PeriodChartResponse.PeriodItem> periodItems = new ArrayList<>();

        if (apiResponse.getOutput2() != null && !apiResponse.getOutput2().isEmpty()) {
            for (KisPeriodChartApiResponse.PeriodData data : apiResponse.getOutput2()) {
                PeriodChartResponse.PeriodItem item = PeriodChartResponse.PeriodItem.builder()
                        .date(data.getStckBsopDate())
                        .close(data.getStckClpr())
                        .open(data.getStckOprc())
                        .high(data.getStckHgpr())
                        .low(data.getStckLwpr())
                        .volume(data.getAcmlVol())
                        .tradingValue(data.getAcmlTrPbmn())
                        .changeSign(data.getPrdyVrssSign())
                        .changePrice(data.getPrdyVrss())
                        .build();
                periodItems.add(item);
            }
            log.info("기간별 시세 {}개 조회 완료", periodItems.size());
        }

        KisPeriodChartApiResponse.Output1 output1 = apiResponse.getOutput1();

        return PeriodChartResponse.builder()
                .stockCode(stockCode)
                .stockName(output1.getHtsKorIsnm())
                .currentPrice(output1.getStckPrpr())
                .changeSign(output1.getPrdyVrssSign())
                .changePrice(output1.getPrdyVrss())
                .changeRate(output1.getPrdyCtrt())
                .volume(output1.getAcmlVol())
                .tradingValue(output1.getAcmlTrPbmn())
                .totalShares(output1.getLstnStcn())  // 상장 주수
                .periodType(periodCode)
                .chartData(periodItems)
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();
    }

    /**
     * 기간별 시세 API 응답을 클라이언트 응답으로 변환 (Direct API, No Cache)
     */
    private PeriodChartResponse convertToPeriodChartResponseDirect(KisPeriodChartApiResponse apiResponse, String stockCode, String periodCode) {
        List<PeriodChartResponse.PeriodItem> periodItems = new ArrayList<>();

        if (apiResponse.getOutput2() != null && !apiResponse.getOutput2().isEmpty()) {
            for (KisPeriodChartApiResponse.PeriodData data : apiResponse.getOutput2()) {
                PeriodChartResponse.PeriodItem item = PeriodChartResponse.PeriodItem.builder()
                        .date(data.getStckBsopDate())
                        .close(data.getStckClpr())
                        .open(data.getStckOprc())
                        .high(data.getStckHgpr())
                        .low(data.getStckLwpr())
                        .volume(data.getAcmlVol())
                        .tradingValue(data.getAcmlTrPbmn())
                        .changeSign(data.getPrdyVrssSign())
                        .changePrice(data.getPrdyVrss())
                        .build();
                periodItems.add(item);
            }
            log.info("기간별 시세 (Direct) {}개 조회 완료", periodItems.size());
        }

        KisPeriodChartApiResponse.Output1 output1 = apiResponse.getOutput1();

        return PeriodChartResponse.builder()
                .stockCode(stockCode)
                .stockName(output1.getHtsKorIsnm())
                .currentPrice(output1.getStckPrpr())
                .changeSign(output1.getPrdyVrssSign())
                .changePrice(output1.getPrdyVrss())
                .changeRate(output1.getPrdyCtrt())
                .volume(output1.getAcmlVol())
                .tradingValue(output1.getAcmlTrPbmn())
                .totalShares(output1.getLstnStcn())  // 상장 주수
                .periodType(periodCode)
                .chartData(periodItems)
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + " (Direct API - No Cache)")
                .build();
    }

    /**
     * 국내 주식 현금 매수
     * @param request 주문 요청 정보
     * @return 주문 응답
     */
    public StockOrderResponse buyStock(StockOrderRequest request) {
        log.info("국내 주식 현금 매수 시작 - 종목코드: {}, 수량: {}, 단가: {}",
                request.getPdno(), request.getOrdQty(), request.getOrdUnpr());

        return executeOrder(request, "TTTC0012U"); // 매수 TR_ID
    }

    /**
     * 국내 주식 현금 매도
     * @param request 주문 요청 정보
     * @return 주문 응답
     */
    public StockOrderResponse sellStock(StockOrderRequest request) {
        log.info("국내 주식 현금 매도 시작 - 종목코드: {}, 수량: {}, 단가: {}",
                request.getPdno(), request.getOrdQty(), request.getOrdUnpr());

        return executeOrder(request, "TTTC0011U"); // 매도 TR_ID
    }

    /**
     * 주식 주문 실행
     */
    private StockOrderResponse executeOrder(StockOrderRequest request, String trId) {
        try {
            // 헤더 설정
            HttpHeaders headers = createOrderHeaders(trId);

            // 요청 바디 생성
            KisStockOrderApiRequest apiRequest = KisStockOrderApiRequest.builder()
                    .cano(tokenConfig.getAccountNumber())
                    .acntPrdtCd(tokenConfig.getAccountProductCode())
                    .pdno(request.getPdno())
                    .ordDvsn(request.getOrdDvsn())
                    .ordQty(request.getOrdQty())
                    .ordUnpr(request.getOrdUnpr())
                    .build();

            // URL 구성
            String url = tokenConfig.getBaseUrl() + "/uapi/domestic-stock/v1/trading/order-cash";

            HttpEntity<KisStockOrderApiRequest> httpRequest = new HttpEntity<>(apiRequest, headers);

            // API 호출
            ResponseEntity<KisStockOrderApiResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    httpRequest,
                    KisStockOrderApiResponse.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                KisStockOrderApiResponse apiResponse = response.getBody();

                if (!"0".equals(apiResponse.getRtCd())) {
                    log.error("주식 주문 실패: {}", apiResponse.getMsg1());
                    return StockOrderResponse.builder()
                            .success(false)
                            .message(apiResponse.getMsg1())
                            .build();
                }

                // 응답 데이터 변환
                KisStockOrderApiResponse.Output output = apiResponse.getOutput();
                return StockOrderResponse.builder()
                        .success(true)
                        .orderNumber(output.getOdno())
                        .orderTime(output.getOrdTmd())
                        .exchangeCode(output.getKrxFwdgOrdOrgno())
                        .message(apiResponse.getMsg1())
                        .build();
            }

        } catch (RestClientException e) {
            log.error("주식 주문 실패: {}", e.getMessage());
            return StockOrderResponse.builder()
                    .success(false)
                    .message("주식 주문 실패: " + e.getMessage())
                    .build();
        }

        return StockOrderResponse.builder()
                .success(false)
                .message("주식 주문 실패")
                .build();
    }

    /**
     * 주식 기본 정보 조회 (NXT 지원 여부 확인용)
     * @param stockCode 종목코드
     * @return 주식 기본 정보
     */
    public StockInfoResponse getStockInfo(String stockCode) {
        log.info("주식 기본 정보 조회 시작 - 종목코드: {}", stockCode);

        try {
            // 헤더 설정
            HttpHeaders headers = createStockInfoHeaders();

            // URL 구성
            String url = UriComponentsBuilder.fromUriString(tokenConfig.getBaseUrl())
                    .path("/uapi/domestic-stock/v1/quotations/search-stock-info")
                    .queryParam("PRDT_TYPE_CD", "300")  // 300: 주식
                    .queryParam("PDNO", stockCode)
                    .build()
                    .toUriString();

            HttpEntity<Void> httpRequest = new HttpEntity<>(headers);

            // API 호출
            ResponseEntity<KisStockInfoApiResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    httpRequest,
                    KisStockInfoApiResponse.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                KisStockInfoApiResponse apiResponse = response.getBody();

                if (!"0".equals(apiResponse.getRtCd())) {
                    log.error("주식 기본 정보 조회 실패: {}", apiResponse.getMsg1());
                    throw new RuntimeException("주식 기본 정보 조회 실패: " + apiResponse.getMsg1());
                }

                // 응답 데이터 변환
                return convertToStockInfoResponse(apiResponse);
            }

        } catch (RestClientException e) {
            log.error("주식 기본 정보 조회 실패: {}", e.getMessage());
            throw new RuntimeException("주식 기본 정보 조회 실패", e);
        }

        throw new RuntimeException("주식 기본 정보 조회 실패");
    }

    /**
     * 주식 기본 정보 API 헤더 생성
     */
    private HttpHeaders createStockInfoHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("authorization", "Bearer " + tokenService.getRestApiAccessToken());
        headers.set("appkey", tokenConfig.getAppKey());
        headers.set("appsecret", tokenConfig.getAppSecret());
        headers.set("tr_id", "CTPF1002R");  // 주식기본조회
        headers.set("custtype", "P");  // 개인

        return headers;
    }

    /**
     * 주식 기본 정보 API 응답을 클라이언트 응답으로 변환
     */
    private StockInfoResponse convertToStockInfoResponse(KisStockInfoApiResponse apiResponse) {
        KisStockInfoApiResponse.Output output = apiResponse.getOutput();

        boolean nxtSupported = "Y".equals(output.getCpttTradTrPsblYn());
        boolean nxtTradeStopped = "Y".equals(output.getNxtTrStopYn());

        log.info("주식 정보 조회 완료 - 종목: {}, NXT지원: {}, NXT거래정지: {}",
                output.getPrdtName(), nxtSupported, nxtTradeStopped);

        return StockInfoResponse.builder()
                .stockCode(output.getPdno())
                .stockName(output.getPrdtName())
                .nxtSupported(nxtSupported)
                .nxtTradeStopped(nxtTradeStopped)
                .currentPrice(output.getStckPrpr())
                .changeSign(output.getPrdyVrssSign())
                .changePrice(output.getPrdyVrss())
                .changeRate(output.getPrdyCtrt())
                .build();
    }

    /**
     * 주식 잔고 조회
     * @return 주식 잔고 정보
     */
    public StockBalanceResponse getStockBalance() {
        log.info("주식 잔고 조회 시작 - 계좌번호: {}", tokenConfig.getAccountNumber());

        try {
            // 헤더 설정
            HttpHeaders headers = createBalanceHeaders();

            // URL 구성 (모든 파라미터 고정값 사용)
            String url = UriComponentsBuilder.fromUriString(tokenConfig.getBaseUrl())
                    .path("/uapi/domestic-stock/v1/trading/inquire-balance")
                    .queryParam("CANO", tokenConfig.getAccountNumber())
                    .queryParam("ACNT_PRDT_CD", tokenConfig.getAccountProductCode())
                    .queryParam("AFHR_FLPR_YN", "N")  // 고정: 기본값
                    .queryParam("OFL_YN", "")  // 오프라인여부 (공란)
                    .queryParam("INQR_DVSN", "02")  // 고정: 종목별
                    .queryParam("UNPR_DVSN", "01")  // 고정: 기본값
                    .queryParam("FUND_STTL_ICLD_YN", "Y")  // 고정: 포함
                    .queryParam("FNCG_AMT_AUTO_RDPT_YN", "N")  // 고정: 기본값
                    .queryParam("PRCS_DVSN", "00")  // 고정: 전일매매포함
                    .queryParam("CTX_AREA_FK100", "")
                    .queryParam("CTX_AREA_NK100", "")
                    .build()
                    .toUriString();

            HttpEntity<Void> httpRequest = new HttpEntity<>(headers);

            // API 호출
            ResponseEntity<KisStockBalanceApiResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    httpRequest,
                    KisStockBalanceApiResponse.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                KisStockBalanceApiResponse apiResponse = response.getBody();

                if (!"0".equals(apiResponse.getRtCd())) {
                    log.error("주식 잔고 조회 실패: {}", apiResponse.getMsg1());
                    throw new RuntimeException("주식 잔고 조회 실패: " + apiResponse.getMsg1());
                }

                // 응답 데이터 변환
                return convertToStockBalanceResponse(apiResponse);
            }

        } catch (RestClientException e) {
            log.error("주식 잔고 조회 실패: {}", e.getMessage());
            throw new RuntimeException("주식 잔고 조회 실패", e);
        }

        throw new RuntimeException("주식 잔고 조회 실패");
    }

    /**
     * 주식 잔고 조회 API 헤더 생성
     */
    private HttpHeaders createBalanceHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("authorization", "Bearer " + tokenService.getRestApiAccessToken());
        headers.set("appkey", tokenConfig.getAppKey());
        headers.set("appsecret", tokenConfig.getAppSecret());
        headers.set("tr_id", "TTTC8434R");  // 실전: TTTC8434R, 모의: VTTC8434R
        headers.set("custtype", "P");  // 개인

        return headers;
    }

    /**
     * 주식 잔고 API 응답을 클라이언트 응답으로 변환
     */
    private StockBalanceResponse convertToStockBalanceResponse(KisStockBalanceApiResponse apiResponse) {
        List<StockBalanceResponse.StockHolding> holdings = new ArrayList<>();

        // 보유 주식 목록 변환
        if (apiResponse.getOutput1() != null) {
            for (KisStockBalanceApiResponse.StockBalanceItem item : apiResponse.getOutput1()) {
                holdings.add(StockBalanceResponse.StockHolding.builder()
                        .stockCode(item.getPdno())
                        .stockName(item.getPrdtName())
                        .tradeType(item.getTradDvsnName())
                        .holdingQty(item.getHldgQty())
                        .orderableQty(item.getOrdPsblQty())
                        .avgPurchasePrice(item.getPchsAvgPric())
                        .purchaseAmount(item.getPchsAmt())
                        .currentPrice(item.getPrpr())
                        .evaluationAmount(item.getEvluAmt())
                        .profitLossAmount(item.getEvluPflsAmt())
                        .profitLossRate(item.getEvluPflsRt())
                        .priceChange(item.getBfdyCprsIcdc())
                        .changeRate(item.getFlttRt())
                        .build());
            }
        }

        // 계좌 요약 정보 변환
        StockBalanceResponse.AccountSummary summary = null;
        if (apiResponse.getOutput2() != null && !apiResponse.getOutput2().isEmpty()) {
            KisStockBalanceApiResponse.StockBalanceSummary summaryData = apiResponse.getOutput2().get(0);
            summary = StockBalanceResponse.AccountSummary.builder()
                    .depositAmount(summaryData.getPrvsRcdlExccAmt())  // D+2 예수금
                    .totalEvaluationAmount(summaryData.getTotEvluAmt())
                    .netAssetAmount(summaryData.getNassAmt())
                    .totalPurchaseAmount(summaryData.getPchsAmtSmtlAmt())
                    .totalCurrentAmount(summaryData.getEvluAmtSmtlAmt())
                    .totalProfitLoss(summaryData.getEvluPflsSmtlAmt())
                    .assetChangeAmount(summaryData.getAsstIcdcAmt())
                    .build();
        }

        log.info("주식 잔고 조회 완료 - 보유종목수: {}", holdings.size());

        return StockBalanceResponse.builder()
                .success(true)
                .message(apiResponse.getMsg1())
                .holdings(holdings)
                .summary(summary)
                .build();
    }

    /**
     * 장내채권 잔고 조회
     * @param inqrCndt 조회조건 (00: 전체, 01: 상품번호단위)
     * @return 장내채권 잔고 정보
     */
    public BondBalanceResponse getBondBalance(String inqrCndt) {
        log.info("장내채권 잔고 조회 시작 - 계좌번호: {}, 조회조건: {}", tokenConfig.getAccountNumber(), inqrCndt);

        try {
            // 헤더 설정
            HttpHeaders headers = createBondBalanceHeaders();

            // URL 구성
            String url = UriComponentsBuilder.fromUriString(tokenConfig.getBaseUrl())
                    .path("/uapi/domestic-bond/v1/trading/inquire-balance")
                    .queryParam("CANO", tokenConfig.getAccountNumber())
                    .queryParam("ACNT_PRDT_CD", tokenConfig.getAccountProductCode())
                    .queryParam("INQR_CNDT", inqrCndt)
                    .queryParam("PDNO", "")  // 공백
                    .queryParam("BUY_DT", "")  // 공백
                    .queryParam("CTX_AREA_FK200", "")
                    .queryParam("CTX_AREA_NK200", "")
                    .build()
                    .toUriString();

            HttpEntity<Void> httpRequest = new HttpEntity<>(headers);

            // API 호출
            ResponseEntity<KisBondBalanceApiResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    httpRequest,
                    KisBondBalanceApiResponse.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                KisBondBalanceApiResponse apiResponse = response.getBody();

                if (!"0".equals(apiResponse.getRtCd())) {
                    log.error("장내채권 잔고 조회 실패: {}", apiResponse.getMsg1());
                    throw new RuntimeException("장내채권 잔고 조회 실패: " + apiResponse.getMsg1());
                }

                // 응답 데이터 변환
                return convertToBondBalanceResponse(apiResponse);
            }

        } catch (RestClientException e) {
            log.error("장내채권 잔고 조회 실패: {}", e.getMessage());
            throw new RuntimeException("장내채권 잔고 조회 실패", e);
        }

        throw new RuntimeException("장내채권 잔고 조회 실패");
    }

    /**
     * 장내채권 잔고 조회 API 헤더 생성
     */
    private HttpHeaders createBondBalanceHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("authorization", "Bearer " + tokenService.getRestApiAccessToken());
        headers.set("appkey", tokenConfig.getAppKey());
        headers.set("appsecret", tokenConfig.getAppSecret());
        headers.set("tr_id", "CTSC8407R");  // 장내채권 잔고조회
        headers.set("custtype", "P");  // 개인

        return headers;
    }

    /**
     * 장내채권 잔고 API 응답을 클라이언트 응답으로 변환
     */
    private BondBalanceResponse convertToBondBalanceResponse(KisBondBalanceApiResponse apiResponse) {
        List<BondBalanceResponse.BondHolding> bonds = new ArrayList<>();

        // 보유 채권 목록 변환
        if (apiResponse.getOutput() != null) {
            for (KisBondBalanceApiResponse.BondBalanceItem item : apiResponse.getOutput()) {
                bonds.add(BondBalanceResponse.BondHolding.builder()
                        .bondCode(item.getPdno())
                        .bondName(item.getPrdtName())
                        .buyDate(item.getBuyDt())
                        .buySequence(item.getBuySqno())
                        .balanceQty(item.getCblcQty())
                        .comprehensiveTaxQty(item.getAgrxQty())
                        .separateTaxQty(item.getSprxQty())
                        .maturityDate(item.getExdt())
                        .buyReturnRate(item.getBuyErngRt())
                        .buyUnitPrice(item.getBuyUnpr())
                        .buyAmount(item.getBuyAmt())
                        .orderableQty(item.getOrdPsblQty())
                        .build());
            }
        }

        log.info("장내채권 잔고 조회 완료 - 보유채권수: {}", bonds.size());

        return BondBalanceResponse.builder()
                .success(true)
                .message(apiResponse.getMsg1())
                .bonds(bonds)
                .build();
    }

    /**
     * 종목투자의견 조회 (DB 우선 조회 + 백그라운드 동기화)
     * @param stockCode 종목 코드
     * @return 투자의견 데이터
     */
    public InvestOpinionResponse getInvestOpinion(String stockCode) {
        log.info("[DB 우선 조회] 투자의견 시작 - 종목코드: {}", stockCode);

        try {
            // 1. DB 조회 시도 (최근 발표일 기준 내림차순)
            List<StockInvestOpinion> dbData = stockInvestOpinionRepository
                    .findByStockCodeOrderByStckBsopDateDesc(stockCode);

            // 2. DB에 데이터가 있으면
            if (!dbData.isEmpty()) {
                StockInvestOpinion latestRecord = dbData.get(0);
                log.info("[DB 조회 성공] 종목코드: {}, 데이터 건수: {}, 마지막 동기화: {}",
                        stockCode, dbData.size(), latestRecord.getLastSyncedDate());

                // 3. 오늘 동기화 안 됐으면 백그라운드 업데이트 트리거
                if (latestRecord.needsSync()) {
                    log.info("[비동기 업데이트 트리거] 종목코드: {} - 마지막 동기화: {}",
                            stockCode, latestRecord.getLastSyncedDate());
                    stockDataSyncService.syncInvestOpinionAsync(stockCode);
                } else {
                    log.info("[캐시 적중] 종목코드: {} - 오늘 이미 동기화됨", stockCode);
                }

                // 4. DB 데이터 즉시 반환 (프론트엔드 필수 5개 필드)
                return convertDbToInvestOpinionResponse(dbData);
            }

            // 5. DB에 없으면 동기 API 호출 + 저장 + 반환
            log.info("[초회 조회] DB에 없음, API 동기 호출 - 종목코드: {}", stockCode);
            return fetchAndSaveInvestOpinion(stockCode);

        } catch (Exception e) {
            log.error("[투자의견 조회 실패] 종목코드: {}, 에러: {}", stockCode, e.getMessage(), e);
            throw new RuntimeException("종목투자의견 조회 실패", e);
        }
    }

    /**
     * DB에 없을 때: 동기 API 호출 → DB 저장 → 응답 반환
     */
    private InvestOpinionResponse fetchAndSaveInvestOpinion(String stockCode) {
        try {
            // 현재 날짜로부터 3개월 전 날짜 계산
            LocalDate endDate = LocalDate.now();
            LocalDate startDate = endDate.minusMonths(3);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            String startDateStr = startDate.format(formatter);
            String endDateStr = endDate.format(formatter);

            // 헤더 설정
            HttpHeaders headers = createInvestOpinionHeaders();

            // URL 구성
            String url = UriComponentsBuilder.fromUriString(tokenConfig.getBaseUrl())
                    .path("/uapi/domestic-stock/v1/quotations/invest-opinion")
                    .queryParam("FID_COND_MRKT_DIV_CODE", "J")
                    .queryParam("FID_COND_SCR_DIV_CODE", "16633")
                    .queryParam("FID_INPUT_ISCD", stockCode)
                    .queryParam("FID_INPUT_DATE_1", startDateStr)
                    .queryParam("FID_INPUT_DATE_2", endDateStr)
                    .build()
                    .toUriString();

            HttpEntity<Void> httpRequest = new HttpEntity<>(headers);

            // API 호출
            ResponseEntity<KisInvestOpinionApiResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    httpRequest,
                    KisInvestOpinionApiResponse.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                KisInvestOpinionApiResponse apiResponse = response.getBody();

                if (!"0".equals(apiResponse.getRtCd())) {
                    log.error("종목투자의견 조회 실패: {}", apiResponse.getMsg1());
                    throw new RuntimeException("종목투자의견 조회 실패: " + apiResponse.getMsg1());
                }

                // DB 저장 (프론트엔드 필수 5개 필드만)
                List<StockInvestOpinion> opinions = saveInvestOpinionToDb(stockCode, apiResponse.getOutput());

                // DB 데이터를 응답으로 변환
                return convertDbToInvestOpinionResponse(opinions);
            }

        } catch (RestClientException e) {
            log.error("투자의견 API 호출 실패: {}", e.getMessage());
            throw new RuntimeException("종목투자의견 조회 실패", e);
        }

        throw new RuntimeException("종목투자의견 조회 실패");
    }

    /**
     * API 응답 데이터를 DB에 저장 (프론트엔드 필수 5개 필드만)
     */
    private List<StockInvestOpinion> saveInvestOpinionToDb(
            String stockCode,
            List<KisInvestOpinionApiResponse.InvestOpinionItem> items
    ) {
        try {
            List<StockInvestOpinion> opinions = new ArrayList<>();

            for (KisInvestOpinionApiResponse.InvestOpinionItem item : items) {
                StockInvestOpinion opinion = StockInvestOpinion.builder()
                        .stockCode(stockCode)
                        .stckBsopDate(item.getStckBsopDate())      // 발표일
                        .invtOpnn(item.getInvtOpnn())              // 현재 의견
                        .rgbfInvtOpnn(item.getRgbfInvtOpnn())      // 직전 의견
                        .htsGoalPrc(item.getHtsGoalPrc())          // 목표가
                        .mbcrName(item.getMbcrName())              // 증권사
                        .build();

                opinion.markSynced(); // 오늘 날짜로 동기화 표시
                opinions.add(opinion);
            }

            stockInvestOpinionRepository.saveAll(opinions);
            log.info("[DB 저장 완료] 종목코드: {}, {} 건", stockCode, opinions.size());

            return opinions;

        } catch (Exception e) {
            log.error("[DB 저장 실패] 종목코드: {}, 에러: {}", stockCode, e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    /**
     * DB 엔티티 리스트 → 클라이언트 응답 변환 (프론트엔드 필수 5개 필드)
     */
    private InvestOpinionResponse convertDbToInvestOpinionResponse(List<StockInvestOpinion> dbData) {
        List<InvestOpinionResponse.InvestOpinionItem> items = new ArrayList<>();

        for (StockInvestOpinion opinion : dbData) {
            InvestOpinionResponse.InvestOpinionItem item = InvestOpinionResponse.InvestOpinionItem.builder()
                    .businessDate(opinion.getStckBsopDate())       // 발표일
                    .opinion(opinion.getInvtOpnn())                // 현재 의견
                    .previousOpinion(opinion.getRgbfInvtOpnn())    // 직전 의견
                    .targetPrice(opinion.getHtsGoalPrc())          // 목표가
                    .brokerage(opinion.getMbcrName())              // 증권사
                    .build();

            items.add(item);
        }

        return InvestOpinionResponse.builder()
                .success(true)
                .message("투자의견 조회 성공 (DB 캐시)")
                .opinions(items)
                .build();
    }

    /**
     * 종목투자의견 API 헤더 생성
     */
    private HttpHeaders createInvestOpinionHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("authorization", "Bearer " + tokenService.getRestApiAccessToken());
        headers.set("appkey", tokenConfig.getAppKey());
        headers.set("appsecret", tokenConfig.getAppSecret());
        headers.set("tr_id", "FHKST663300C0");  // 실전 TR_ID
        headers.set("custtype", "P");  // 개인

        return headers;
    }

    /**
     * 매수가능조회
     * @param stockCode 종목코드
     * @return 매수가능 정보
     */
    public BuyableAmountResponse getBuyableAmount(String stockCode) {
        log.info("매수가능조회 시작 - 종목코드: {}", stockCode);

        try {
            // 헤더 설정
            HttpHeaders headers = createBuyableAmountHeaders();

            // URL 구성
            String url = UriComponentsBuilder.fromUriString(tokenConfig.getBaseUrl())
                    .path("/uapi/domestic-stock/v1/trading/inquire-psbl-order")
                    .queryParam("CANO", tokenConfig.getAccountNumber())
                    .queryParam("ACNT_PRDT_CD", tokenConfig.getAccountProductCode())
                    .queryParam("PDNO", stockCode)
                    .queryParam("ORD_UNPR", "")  // 빈 문자열
                    .queryParam("ORD_DVSN", "01")  // 시장가
                    .queryParam("CMA_EVLU_AMT_ICLD_YN", "Y")
                    .queryParam("OVRS_ICLD_YN", "Y")
                    .build()
                    .toUriString();

            HttpEntity<Void> httpRequest = new HttpEntity<>(headers);

            // API 호출
            ResponseEntity<KisBuyableAmountApiResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    httpRequest,
                    KisBuyableAmountApiResponse.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                KisBuyableAmountApiResponse apiResponse = response.getBody();

                if (!"0".equals(apiResponse.getRtCd())) {
                    log.error("매수가능조회 실패: {}", apiResponse.getMsg1());
                    return BuyableAmountResponse.builder()
                            .success(false)
                            .message(apiResponse.getMsg1())
                            .build();
                }

                // 응답 데이터 변환
                return convertToBuyableAmountResponse(apiResponse);
            }

        } catch (RestClientException e) {
            log.error("매수가능조회 실패: {}", e.getMessage());
            return BuyableAmountResponse.builder()
                    .success(false)
                    .message("매수가능조회 실패: " + e.getMessage())
                    .build();
        }

        return BuyableAmountResponse.builder()
                .success(false)
                .message("매수가능조회 실패")
                .build();
    }

    /**
     * 매수가능조회 API 헤더 생성
     */
    private HttpHeaders createBuyableAmountHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("authorization", "Bearer " + tokenService.getRestApiAccessToken());
        headers.set("appkey", tokenConfig.getAppKey());
        headers.set("appsecret", tokenConfig.getAppSecret());
        headers.set("tr_id", "TTTC8908R");  // 실전: TTTC8908R, 모의: VTTC8908R
        headers.set("custtype", "P");  // 개인

        return headers;
    }

    /**
     * 매수가능조회 API 응답을 클라이언트 응답으로 변환
     */
    private BuyableAmountResponse convertToBuyableAmountResponse(KisBuyableAmountApiResponse apiResponse) {
        KisBuyableAmountApiResponse.BuyableAmountOutput output = apiResponse.getOutput();

        BuyableAmountResponse.BuyableAmountData data = BuyableAmountResponse.BuyableAmountData.builder()
                .orderableCash(output.getOrdPsblCash())
                .noCreditBuyAmount(output.getNrcvbBuyAmt())
                .noCreditBuyQuantity(output.getNrcvbBuyQty())
                .maxBuyAmount(output.getMaxBuyAmt())
                .maxBuyQuantity(output.getMaxBuyQty())
                .cmaEvaluationAmount(output.getCmaEvluAmt())
                .overseasReuseAmount(output.getOvrsReUseAmtWcrc())
                .orderableForeignAmount(output.getOrdPsblFrcrAmtWcrc())
                .build();

        log.info("매수가능조회 완료 - 현금최대: {}주, 신용최대: {}주",
                output.getNrcvbBuyQty(), output.getMaxBuyQty());

        return BuyableAmountResponse.builder()
                .success(true)
                .message(apiResponse.getMsg1())
                .data(data)
                .build();
    }

    /**
     * 매도가능수량조회
     * @param stockCode 종목코드
     * @return 매도가능 정보
     */
    public SellableQuantityResponse getSellableQuantity(String stockCode) {
        log.info("매도가능수량조회 시작 - 종목코드: {}", stockCode);

        try {
            // 헤더 설정
            HttpHeaders headers = createSellableQuantityHeaders();

            // URL 구성
            String url = UriComponentsBuilder.fromUriString(tokenConfig.getBaseUrl())
                    .path("/uapi/domestic-stock/v1/trading/inquire-psbl-sell")
                    .queryParam("CANO", tokenConfig.getAccountNumber())
                    .queryParam("ACNT_PRDT_CD", tokenConfig.getAccountProductCode())
                    .queryParam("PDNO", stockCode)
                    .build()
                    .toUriString();

            HttpEntity<Void> httpRequest = new HttpEntity<>(headers);

            // API 호출
            ResponseEntity<KisSellableQuantityApiResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    httpRequest,
                    KisSellableQuantityApiResponse.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                KisSellableQuantityApiResponse apiResponse = response.getBody();

                if (!"0".equals(apiResponse.getRtCd())) {
                    log.error("매도가능수량조회 실패: {}", apiResponse.getMsg1());
                    return SellableQuantityResponse.builder()
                            .success(false)
                            .message(apiResponse.getMsg1())
                            .build();
                }

                // 응답 데이터 변환
                return convertToSellableQuantityResponse(apiResponse);
            }

        } catch (RestClientException e) {
            log.error("매도가능수량조회 실패: {}", e.getMessage());
            return SellableQuantityResponse.builder()
                    .success(false)
                    .message("매도가능수량조회 실패: " + e.getMessage())
                    .build();
        }

        return SellableQuantityResponse.builder()
                .success(false)
                .message("매도가능수량조회 실패")
                .build();
    }

    /**
     * 매도가능수량조회 API 헤더 생성
     */
    private HttpHeaders createSellableQuantityHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("authorization", "Bearer " + tokenService.getRestApiAccessToken());
        headers.set("appkey", tokenConfig.getAppKey());
        headers.set("appsecret", tokenConfig.getAppSecret());
        headers.set("tr_id", "TTTC8408R");  // 실전 TR_ID
        headers.set("custtype", "P");  // 개인

        return headers;
    }

    /**
     * 매도가능수량조회 API 응답을 클라이언트 응답으로 변환
     */
    private SellableQuantityResponse convertToSellableQuantityResponse(KisSellableQuantityApiResponse apiResponse) {
        KisSellableQuantityApiResponse.SellableQuantityOutput output = apiResponse.getOutput();

        SellableQuantityResponse.SellableQuantityData data = SellableQuantityResponse.SellableQuantityData.builder()
                .stockCode(output.getPdno())
                .stockName(output.getPrdtName())
                .buyQuantity(output.getBuyQty())
                .sellQuantity(output.getSllQty())
                .balanceQuantity(output.getCblcQty())
                .nonSavingQuantity(output.getNsvgQty())
                .orderableQuantity(output.getOrdPsblQty())
                .purchaseAveragePrice(output.getPchsAvgPric())
                .purchaseAmount(output.getPchsAmt())
                .currentPrice(output.getNowPric())
                .evaluationAmount(output.getEvluAmt())
                .profitLossAmount(output.getEvluPflsAmt())
                .profitLossRate(output.getEvluPflsRt())
                .build();

        log.info("매도가능수량조회 완료 - 주문가능수량: {}주", output.getOrdPsblQty());

        return SellableQuantityResponse.builder()
                .success(true)
                .message(apiResponse.getMsg1())
                .data(data)
                .build();
    }

    /**
     * 주식 주문 API 헤더 생성
     */
    private HttpHeaders createOrderHeaders(String trId) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("content-type", "application/json; charset=utf-8");
        headers.set("authorization", "Bearer " + tokenService.getRestApiAccessToken());
        headers.set("appkey", tokenConfig.getAppKey());
        headers.set("appsecret", tokenConfig.getAppSecret());
        headers.set("tr_id", trId);
        headers.set("custtype", "P");  // 개인

        return headers;
    }

    /**
     * [국내주식] 주식 기본 정보 조회 (DB 우선 조회 + 백그라운드 동기화)
     * @param productTypeCode 상품유형코드 (300: 주식, ETF, ETN, ELW)
     * @param stockCode 상품번호 (종목코드 6자리)
     * @return 주식 기본 정보
     */
    public StockBasicInfoResponse getStockBasicInfo(String productTypeCode, String stockCode) {
        log.info("[DB 우선 조회] 종목 개요 정보 시작 - 종목코드: {}", stockCode);

        try {
            // 1. DB 조회 시도
            Optional<StockOverview> overviewOpt = stockOverviewRepository.findByStockCode(stockCode);

            // 2. DB에 데이터가 있으면
            if (overviewOpt.isPresent()) {
                StockOverview overview = overviewOpt.get();
                log.info("[DB 조회 성공] 종목코드: {}, 마지막 동기화: {}", stockCode, overview.getLastSyncedDate());

                // 3. 오늘 동기화 안 됐으면 백그라운드 업데이트 트리거
                if (overview.needsSync()) {
                    log.info("[비동기 업데이트 트리거] 종목코드: {} - 마지막 동기화: {}",
                            stockCode, overview.getLastSyncedDate());
                    stockDataSyncService.syncStockOverviewAsync(stockCode, productTypeCode);
                } else {
                    log.info("[캐시 적중] 종목코드: {} - 오늘 이미 동기화됨", stockCode);
                }

                // 4. DB 데이터 즉시 반환 (프론트엔드 필수 14개 필드만 포함)
                return convertDbToStockBasicInfoResponse(overview);
            }

            // 5. DB에 없으면 동기 API 호출 + 저장 + 반환
            log.info("[초회 조회] DB에 없음, API 동기 호출 - 종목코드: {}", stockCode);
            return fetchAndSaveStockBasicInfo(productTypeCode, stockCode);

        } catch (Exception e) {
            log.error("[주식 기본 정보 조회 실패] 종목코드: {}, 에러: {}", stockCode, e.getMessage(), e);
            return StockBasicInfoResponse.builder()
                    .success(false)
                    .message("주식 기본 정보 조회 실패: " + e.getMessage())
                    .build();
        }
    }

    /**
     * DB에 없을 때: 동기 API 호출 → DB 저장 → 응답 반환
     */
    private StockBasicInfoResponse fetchAndSaveStockBasicInfo(String productTypeCode, String stockCode) {
        try {
            // 헤더 설정
            HttpHeaders headers = createStockBasicInfoHeaders();

            // URL 구성
            String url = UriComponentsBuilder.fromUriString(tokenConfig.getBaseUrl())
                    .path("/uapi/domestic-stock/v1/quotations/search-stock-info")
                    .queryParam("PRDT_TYPE_CD", productTypeCode)
                    .queryParam("PDNO", stockCode)
                    .build()
                    .toUriString();

            HttpEntity<Void> httpRequest = new HttpEntity<>(headers);

            // API 호출
            ResponseEntity<KisStockBasicInfoApiResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    httpRequest,
                    KisStockBasicInfoApiResponse.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                KisStockBasicInfoApiResponse apiResponse = response.getBody();

                if (!"0".equals(apiResponse.getRtCd())) {
                    log.error("주식 기본 정보 조회 실패: {}", apiResponse.getMsg1());
                    return StockBasicInfoResponse.builder()
                            .success(false)
                            .message(apiResponse.getMsg1())
                            .build();
                }

                // DB 저장 (프론트엔드 필수 14개 필드만)
                saveStockOverviewToDb(stockCode, apiResponse.getOutput());

                // 응답 데이터 변환 (전체 필드 반환)
                return convertToStockBasicInfoResponse(apiResponse);
            }

        } catch (RestClientException e) {
            log.error("주식 기본 정보 API 호출 실패: {}", e.getMessage());
            return StockBasicInfoResponse.builder()
                    .success(false)
                    .message("주식 기본 정보 조회 실패: " + e.getMessage())
                    .build();
        }

        return StockBasicInfoResponse.builder()
                .success(false)
                .message("주식 기본 정보 조회 실패")
                .build();
    }

    /**
     * API 응답 데이터를 DB에 저장 (프론트엔드 필수 14개 필드만)
     */
    @Transactional
    private void saveStockOverviewToDb(String stockCode, KisStockBasicInfoApiResponse.StockBasicInfoOutput output) {
        try {
            // 기존 데이터가 있으면 업데이트, 없으면 INSERT
            StockOverview overview = stockOverviewRepository.findById(stockCode)
                    .orElse(StockOverview.builder()
                            .stockCode(stockCode)
                            .build());

            // 필드 업데이트
            overview.setMketIdCd(output.getMketIdCd());
            overview.setSctyGrpIdCd(output.getSctyGrpIdCd());
            overview.setExcgDvsnCd(output.getExcgDvsnCd());
            overview.setSetlMmdd(output.getSetlMmdd());
            overview.setLstgStqt(output.getLstgStqt());
            overview.setLstgCptlAmt(output.getLstgCptlAmt());
            overview.setCpta(output.getCpta());
            overview.setPapr(output.getPapr());
            overview.setIssuPric(output.getIssuPric());
            overview.setKospi200ItemYn(output.getKospi200ItemYn());
            overview.setSctsMketLstgDt(output.getSctsMketLstgDt());
            overview.setStckKindCd(output.getStckKindCd());
            overview.setStdIdstClsfCd(output.getStdIdstClsfCd());
            overview.setNxtTrStopYn(output.getNxtTrStopYn());
            overview.markSynced(); // 오늘 날짜로 동기화 표시

            stockOverviewRepository.save(overview);

            log.info("[DB 저장 완료] 종목코드: {}", stockCode);

        } catch (Exception e) {
            log.error("[DB 저장 실패] 종목코드: {}, 에러: {}", stockCode, e.getMessage(), e);
        }
    }

    /**
     * DB 엔티티 → 클라이언트 응답 변환 (프론트엔드 필수 14개 필드만 포함)
     */
    private StockBasicInfoResponse convertDbToStockBasicInfoResponse(StockOverview overview) {
        StockBasicInfoResponse.StockBasicInfoData data = StockBasicInfoResponse.StockBasicInfoData.builder()
                // 프론트엔드 필수 14개 필드
                .mketIdCd(overview.getMketIdCd())
                .sctyGrpIdCd(overview.getSctyGrpIdCd())
                .excgDvsnCd(overview.getExcgDvsnCd())
                .setlMmdd(overview.getSetlMmdd())
                .lstgStqt(overview.getLstgStqt())
                .lstgCptlAmt(overview.getLstgCptlAmt())
                .cpta(overview.getCpta())
                .papr(overview.getPapr())
                .issuPric(overview.getIssuPric())
                .kospi200ItemYn(overview.getKospi200ItemYn())
                .sctsMketLstgDt(overview.getSctsMketLstgDt())
                .stckKindCd(overview.getStckKindCd())
                .stdIdstClsfCd(overview.getStdIdstClsfCd())
                .nxtTrStopYn(overview.getNxtTrStopYn())
                .build();

        return StockBasicInfoResponse.builder()
                .success(true)
                .message("주식 기본 정보 조회 성공 (DB 캐시)")
                .data(data)
                .build();
    }

    /**
     * 주식 기본 정보 조회 API 헤더 생성
     */
    private HttpHeaders createStockBasicInfoHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("authorization", "Bearer " + tokenService.getRestApiAccessToken());
        headers.set("appkey", tokenConfig.getAppKey());
        headers.set("appsecret", tokenConfig.getAppSecret());
        headers.set("tr_id", "CTPF1002R");  // 주식기본조회 TR_ID
        headers.set("custtype", "P");  // 개인

        return headers;
    }

    /**
     * KIS API 응답을 클라이언트 응답으로 변환 (주식 기본 정보)
     */
    private StockBasicInfoResponse convertToStockBasicInfoResponse(KisStockBasicInfoApiResponse apiResponse) {
        KisStockBasicInfoApiResponse.StockBasicInfoOutput output = apiResponse.getOutput();

        StockBasicInfoResponse.StockBasicInfoData data = StockBasicInfoResponse.StockBasicInfoData.builder()
                .pdno(output.getPdno())
                .prdtTypeCd(output.getPrdtTypeCd())
                .prdtName(output.getPrdtName())
                .prdtAbrvName(output.getPrdtAbrvName())
                .prdtEngName(output.getPrdtEngName())
                .stdPdno(output.getStdPdno())
                .mketIdCd(output.getMketIdCd())
                .sctyGrpIdCd(output.getSctyGrpIdCd())
                .excgDvsnCd(output.getExcgDvsnCd())
                .lstgStqt(output.getLstgStqt())
                .lstgCptlAmt(output.getLstgCptlAmt())
                .cpta(output.getCpta())
                .papr(output.getPapr())
                .issuPric(output.getIssuPric())
                .kospi200ItemYn(output.getKospi200ItemYn())
                .sctsMketLstgDt(output.getSctsMketLstgDt())
                .sctsMketLstgAbolDt(output.getSctsMketLstgAbolDt())
                .kosdaqMketLstgDt(output.getKosdaqMketLstgDt())
                .kosdaqMketLstgAbolDt(output.getKosdaqMketLstgAbolDt())
                .frbdMketLstgDt(output.getFrbdMketLstgDt())
                .frbdMketLstgAbolDt(output.getFrbdMketLstgAbolDt())
                .reitsKindCd(output.getReitsKindCd())
                .etfDvsnCd(output.getEtfDvsnCd())
                .oilfFundYn(output.getOilfFundYn())
                .idxBztpLclsCd(output.getIdxBztpLclsCd())
                .idxBztpMclsCd(output.getIdxBztpMclsCd())
                .idxBztpSclsCd(output.getIdxBztpSclsCd())
                .stckKindCd(output.getStckKindCd())
                .mfndOpngDt(output.getMfndOpngDt())
                .mfndEndDt(output.getMfndEndDt())
                .dpsiErlmCnclDt(output.getDpsiErlmCnclDt())
                .etfCuQty(output.getEtfCuQty())
                .prdtName120(output.getPrdtName120())
                .prdtEngName120(output.getPrdtEngName120())
                .prdtEngAbrvName(output.getPrdtEngAbrvName())
                .dpsiAptmErlmYn(output.getDpsiAptmErlmYn())
                .etfTxtnTypeCd(output.getEtfTxtnTypeCd())
                .etfTypeCd(output.getEtfTypeCd())
                .lstgAbolDt(output.getLstgAbolDt())
                .nwstOdstDvsnCd(output.getNwstOdstDvsnCd())
                .sbstPric(output.getSbstPric())
                .thcoSbstPric(output.getThcoSbstPric())
                .thcoSbstPricChngDt(output.getThcoSbstPricChngDt())
                .trStopYn(output.getTrStopYn())
                .admnItemYn(output.getAdmnItemYn())
                .thdtClpr(output.getThdtClpr())
                .bfdyClpr(output.getBfdyClpr())
                .clprChngDt(output.getClprChngDt())
                .stdIdstClsfCd(output.getStdIdstClsfCd())
                .stdIdstClsfCdName(output.getStdIdstClsfCdName())
                .idxBztpLclsCdName(output.getIdxBztpLclsCdName())
                .idxBztpMclsCdName(output.getIdxBztpMclsCdName())
                .idxBztpSclsCdName(output.getIdxBztpSclsCdName())
                .ocrNo(output.getOcrNo())
                .crfdItemYn(output.getCrfdItemYn())
                .elecSctyYn(output.getElecSctyYn())
                .issuIsttCd(output.getIssuIsttCd())
                .etfChasErngRtDbnb(output.getEtfChasErngRtDbnb())
                .etfEtnIvstHeedItemYn(output.getEtfEtnIvstHeedItemYn())
                .stlnIntRtDvsnCd(output.getStlnIntRtDvsnCd())
                .frnrPsnlLmtRt(output.getFrnrPsnlLmtRt())
                .lstgRqsrIssuIsttCd(output.getLstgRqsrIssuIsttCd())
                .lstgRqsrItemCd(output.getLstgRqsrItemCd())
                .trstIsttIssuIsttCd(output.getTrstIsttIssuIsttCd())
                .cpttTradTrPsblYn(output.getCpttTradTrPsblYn())
                .nxtTrStopYn(output.getNxtTrStopYn())
                .setlMmdd(output.getSetlMmdd())
                .build();

        return StockBasicInfoResponse.builder()
                .success(true)
                .message("주식 기본 정보 조회 성공")
                .data(data)
                .build();
    }

    /**
     * 주식 종목 검색
     * @param keyword 검색 키워드 (종목명 또는 종목코드)
     * @return 검색 결과
     */
    @Transactional(readOnly = true)
    public StockSearchResponse searchStocks(String keyword) {
        log.info("[주식 검색] 키워드: {}", keyword);

        try {
            List<com.hanati.domain.stock.entity.Stock> stocks = stockRepository.searchByKeyword(keyword);

            List<StockSearchResponse.StockSearchItem> items = stocks.stream()
                .map(stock -> StockSearchResponse.StockSearchItem.builder()
                    .stockCode(stock.getStockCode())
                    .stockName(stock.getStockName())
                    .marketType(stock.getMarketType())
                    .build())
                .collect(java.util.stream.Collectors.toList());

            return StockSearchResponse.builder()
                .success(true)
                .message("검색 성공")
                .stocks(items)
                .build();

        } catch (Exception e) {
            log.error("[주식 검색 실패] 키워드: {}, 에러: {}", keyword, e.getMessage(), e);
            return StockSearchResponse.builder()
                .success(false)
                .message("검색 실패: " + e.getMessage())
                .stocks(List.of())
                .build();
        }
    }
}
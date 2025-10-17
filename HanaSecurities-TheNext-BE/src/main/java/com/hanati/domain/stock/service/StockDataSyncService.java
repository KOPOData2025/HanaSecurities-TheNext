package com.hanati.domain.stock.service;

import com.hanati.common.config.TokenConfig;
import com.hanati.common.service.TokenService;
import com.hanati.domain.stock.dto.*;
import com.hanati.domain.stock.entity.StockChartData;
import com.hanati.domain.stock.entity.StockFinancialInfo;
import com.hanati.domain.stock.entity.StockInvestOpinion;
import com.hanati.domain.stock.entity.StockOverview;
import com.hanati.domain.stock.repository.StockChartDataRepository;
import com.hanati.domain.stock.repository.StockFinancialInfoRepository;
import com.hanati.domain.stock.repository.StockInvestOpinionRepository;
import com.hanati.domain.stock.repository.StockOverviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;

/**
 * 주식 데이터 동기화 전담 서비스
 * - 한투 OpenAPI 호출 → DB 저장
 * - 비동기 백그라운드 업데이트 처리
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class StockDataSyncService {

    private final TokenConfig tokenConfig;
    private final TokenService tokenService;
    private final RestTemplate restTemplate;

    // Repositories
    private final StockOverviewRepository stockOverviewRepository;
    private final StockFinancialInfoRepository stockFinancialInfoRepository;
    private final StockInvestOpinionRepository stockInvestOpinionRepository;
    private final StockChartDataRepository stockChartDataRepository;

    /**
     * 종목 기본정보 비동기 동기화
     * @param stockCode 종목코드
     * @param productTypeCode 상품유형코드 (300: 주식)
     */
    @Async("apiSyncExecutor")
    @Transactional
    public void syncStockOverviewAsync(String stockCode, String productTypeCode) {
        log.info("[비동기 동기화] 종목 개요 정보 시작 - 종목코드: {}", stockCode);

        try {
            // 한투 API 호출
            KisStockBasicInfoApiResponse apiResponse = callStockBasicInfoApi(stockCode, productTypeCode);

            if (apiResponse == null || !"0".equals(apiResponse.getRtCd())) {
                log.error("[비동기 동기화 실패] 종목 개요 정보 - 종목코드: {}", stockCode);
                return;
            }

            // API 응답 → Entity 변환 (프론트엔드 필수 필드만)
            StockOverview overview = convertToStockOverview(stockCode, apiResponse.getOutput());
            overview.markSynced(); // 오늘 날짜로 동기화 표시

            // DB 저장 (Upsert)
            stockOverviewRepository.save(overview);

            log.info("[비동기 동기화 완료] 종목 개요 정보 - 종목코드: {}", stockCode);

        } catch (Exception e) {
            log.error("[비동기 동기화 예외] 종목 개요 정보 - 종목코드: {}, 에러: {}", stockCode, e.getMessage(), e);
        }
    }

    /**
     * 재무정보 비동기 동기화
     * @param stockCode 종목코드
     * @param divisionCode 분류구분 (0: 년, 1: 분기)
     */
    @Async("apiSyncExecutor")
    @Transactional
    public void syncFinancialInfoAsync(String stockCode, String divisionCode) {
        log.info("[비동기 동기화] 재무정보 시작 - 종목코드: {}, 분류구분: {}", stockCode, divisionCode);

        try {
            // 한투 API 호출 (재무비율, 손익계산서, 대차대조표 통합)
            List<StockFinancialInfo> financialInfoList = fetchAndMergeFinancialData(stockCode, divisionCode);

            if (financialInfoList.isEmpty()) {
                log.warn("[비동기 동기화] 재무정보 없음 - 종목코드: {}", stockCode);
                return;
            }

            // 기존 데이터 삭제 (같은 종목코드 + 분류구분)
            List<StockFinancialInfo> existingData = stockFinancialInfoRepository
                    .findByStockCodeAndDivisionCodeOrderByStacYymmDesc(stockCode, divisionCode);
            if (!existingData.isEmpty()) {
                stockFinancialInfoRepository.deleteAll(existingData);
            }

            // 동기화 날짜 표시
            financialInfoList.forEach(info -> info.markSynced());

            // DB 저장
            stockFinancialInfoRepository.saveAll(financialInfoList);

            log.info("[비동기 동기화 완료] 재무정보 {} 건 저장 - 종목코드: {}", financialInfoList.size(), stockCode);

        } catch (Exception e) {
            log.error("[비동기 동기화 예외] 재무정보 - 종목코드: {}, 에러: {}", stockCode, e.getMessage(), e);
        }
    }

    /**
     * 투자의견 비동기 동기화
     * @param stockCode 종목코드
     */
    @Async("apiSyncExecutor")
    @Transactional
    public void syncInvestOpinionAsync(String stockCode) {
        log.info("[비동기 동기화] 투자의견 시작 - 종목코드: {}", stockCode);

        try {
            // 한투 API 호출
            KisInvestOpinionApiResponse apiResponse = callInvestOpinionApi(stockCode);

            if (apiResponse == null || !"0".equals(apiResponse.getRtCd())) {
                log.error("[비동기 동기화 실패] 투자의견 - 종목코드: {}", stockCode);
                return;
            }

            // API 응답 → Entity 변환 (프론트엔드 필수 필드만)
            List<StockInvestOpinion> opinions = convertToInvestOpinions(stockCode, apiResponse.getOutput());

            if (opinions.isEmpty()) {
                log.warn("[비동기 동기화] 투자의견 없음 - 종목코드: {}", stockCode);
                return;
            }

            // 기존 데이터 삭제 (같은 종목코드의 최근 3개월 데이터)
            List<StockInvestOpinion> existingData = stockInvestOpinionRepository
                    .findByStockCodeOrderByStckBsopDateDesc(stockCode);
            if (!existingData.isEmpty()) {
                stockInvestOpinionRepository.deleteAll(existingData);
            }

            // 동기화 날짜 표시
            opinions.forEach(opinion -> opinion.markSynced());

            // DB 저장
            stockInvestOpinionRepository.saveAll(opinions);

            log.info("[비동기 동기화 완료] 투자의견 {} 건 저장 - 종목코드: {}", opinions.size(), stockCode);

        } catch (Exception e) {
            log.error("[비동기 동기화 예외] 투자의견 - 종목코드: {}, 에러: {}", stockCode, e.getMessage(), e);
        }
    }

    /**
     * 차트 데이터 비동기 동기화 (일/주/월/년봉)
     * @param stockCode 종목코드
     * @param periodType 기간구분 (D/W/M/Y)
     */
    @Async("apiSyncExecutor")
    @Transactional
    public void syncChartDataAsync(String stockCode, String periodType) {
        log.info("[비동기 동기화] 차트 데이터 시작 - 종목코드: {}, 기간: {}", stockCode, periodType);

        try {
            // 최근 100개 데이터 요청을 위한 날짜 계산
            String endDate = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.BASIC_ISO_DATE);
            String startDate = calculateStartDate(periodType);

            // 한투 API 호출
            KisPeriodChartApiResponse apiResponse = callPeriodChartApi(stockCode, startDate, endDate, periodType);

            if (apiResponse == null || !"0".equals(apiResponse.getRtCd())) {
                log.error("[비동기 동기화 실패] 차트 데이터 - 종목코드: {}, 기간: {}", stockCode, periodType);
                return;
            }

            if (apiResponse.getOutput2() == null || apiResponse.getOutput2().isEmpty()) {
                log.warn("[비동기 동기화] 차트 데이터 없음 - 종목코드: {}, 기간: {}", stockCode, periodType);
                return;
            }

            // API 응답 → Entity 변환 및 저장 (UPSERT 패턴)
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

            log.info("[비동기 동기화 완료] 차트 데이터 {} 건 저장 - 종목코드: {}, 기간: {}", savedCount, stockCode, periodType);

        } catch (Exception e) {
            log.error("[비동기 동기화 예외] 차트 데이터 - 종목코드: {}, 기간: {}, 에러: {}", stockCode, periodType, e.getMessage(), e);
        }
    }

    /**
     * 기간 타입별 시작일자 계산 (최근 데이터 요청용)
     */
    private String calculateStartDate(String periodType) {
        java.time.LocalDate now = java.time.LocalDate.now();
        java.time.LocalDate startDate;

        switch (periodType) {
            case "D": // 일봉: 최근 1년
                startDate = now.minusYears(1);
                break;
            case "W": // 주봉: 최근 2년
                startDate = now.minusYears(2);
                break;
            case "M": // 월봉: 최근 5년
                startDate = now.minusYears(5);
                break;
            case "Y": // 년봉: 최근 10년
                startDate = now.minusYears(10);
                break;
            default:
                startDate = now.minusYears(1);
        }

        return startDate.format(java.time.format.DateTimeFormatter.BASIC_ISO_DATE);
    }

    // ============================================================================
    // Private Helper Methods - API 호출
    // ============================================================================

    /**
     * 한투 주식기본정보 API 호출
     */
    private KisStockBasicInfoApiResponse callStockBasicInfoApi(String stockCode, String productTypeCode) {
        try {
            HttpHeaders headers = createStockBasicInfoHeaders();

            String url = UriComponentsBuilder.fromUriString(tokenConfig.getBaseUrl())
                    .path("/uapi/domestic-stock/v1/quotations/search-stock-info")
                    .queryParam("PRDT_TYPE_CD", productTypeCode)
                    .queryParam("PDNO", stockCode)
                    .build()
                    .toUriString();

            HttpEntity<Void> request = new HttpEntity<>(headers);

            ResponseEntity<KisStockBasicInfoApiResponse> response = restTemplate.exchange(
                    url, HttpMethod.GET, request, KisStockBasicInfoApiResponse.class
            );

            return response.getBody();

        } catch (Exception e) {
            log.error("주식기본정보 API 호출 실패 - 종목코드: {}", stockCode, e);
            return null;
        }
    }

    /**
     * 한투 투자의견 API 호출
     */
    private KisInvestOpinionApiResponse callInvestOpinionApi(String stockCode) {
        try {
            HttpHeaders headers = createInvestOpinionHeaders();

            String url = UriComponentsBuilder.fromUriString(tokenConfig.getBaseUrl())
                    .path("/uapi/domestic-stock/v1/quotations/invest-opinion")
                    .queryParam("FID_COND_MRKT_DIV_CODE", "J")
                    .queryParam("FID_INPUT_ISCD", stockCode)
                    .build()
                    .toUriString();

            HttpEntity<Void> request = new HttpEntity<>(headers);

            ResponseEntity<KisInvestOpinionApiResponse> response = restTemplate.exchange(
                    url, HttpMethod.GET, request, KisInvestOpinionApiResponse.class
            );

            return response.getBody();

        } catch (Exception e) {
            log.error("투자의견 API 호출 실패 - 종목코드: {}", stockCode, e);
            return null;
        }
    }

    /**
     * 한투 기간별 시세 API 호출
     */
    private KisPeriodChartApiResponse callPeriodChartApi(String stockCode, String startDate, String endDate, String periodCode) {
        try {
            HttpHeaders headers = createPeriodChartHeaders();

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

            HttpEntity<Void> request = new HttpEntity<>(headers);

            ResponseEntity<KisPeriodChartApiResponse> response = restTemplate.exchange(
                    url, HttpMethod.GET, request, KisPeriodChartApiResponse.class
            );

            return response.getBody();

        } catch (Exception e) {
            log.error("기간별 시세 API 호출 실패 - 종목코드: {}, 기간: {}", stockCode, periodCode, e);
            return null;
        }
    }

    /**
     * 재무정보 3개 API 호출 및 병합
     */
    private List<StockFinancialInfo> fetchAndMergeFinancialData(String stockCode, String divisionCode) {
        try {
            // 3개 API 호출
            KisFinancialRatioApiResponse ratioResponse = callFinancialRatioApi(stockCode, divisionCode);
            KisIncomeStatementApiResponse incomeResponse = callIncomeStatementApi(stockCode, divisionCode);
            KisBalanceSheetApiResponse balanceResponse = callBalanceSheetApi(stockCode, divisionCode);

            // 데이터 병합
            Map<String, StockFinancialInfo> periodMap = new LinkedHashMap<>();

            // 재무비율 데이터 병합
            if (ratioResponse != null && ratioResponse.getOutput() != null) {
                for (KisFinancialRatioApiResponse.FinancialRatioData data : ratioResponse.getOutput()) {
                    String period = data.getStacYymm();
                    StockFinancialInfo info = periodMap.computeIfAbsent(period, k -> StockFinancialInfo.builder()
                            .stockCode(stockCode)
                            .stacYymm(period)
                            .divisionCode(divisionCode)
                            .build());

                    info.setGrs(data.getGrs());
                    info.setBsopPrfiInrt(data.getBsopPrfiInrt());
                    info.setNtinInrt(data.getNtinInrt());
                    info.setRoeVal(data.getRoeVal());
                    info.setEps(data.getEps());
                    info.setSps(data.getSps());
                    info.setBps(data.getBps());
                    info.setRsrvRate(data.getRsrvRate());
                    info.setLbltRate(data.getLbltRate());
                }
            }

            // 손익계산서 데이터 병합
            if (incomeResponse != null && incomeResponse.getOutput() != null) {
                for (KisIncomeStatementApiResponse.IncomeStatementData data : incomeResponse.getOutput()) {
                    String period = data.getStacYymm();
                    StockFinancialInfo info = periodMap.computeIfAbsent(period, k -> StockFinancialInfo.builder()
                            .stockCode(stockCode)
                            .stacYymm(period)
                            .divisionCode(divisionCode)
                            .build());

                    info.setSaleAccount(data.getSaleAccount());
                    info.setSaleCost(data.getSaleCost());
                    info.setSaleTotlPrfi(data.getSaleTotlPrfi());
                    info.setBsopPrti(data.getBsopPrti());
                    info.setOpPrfi(data.getOpPrfi());
                    info.setSpecPrfi(filterNotProvided(data.getSpecPrfi()));
                    info.setSpecLoss(filterNotProvided(data.getSpecLoss()));
                    info.setThtrNtin(data.getThtrNtin());
                }
            }

            // 대차대조표 데이터 병합
            if (balanceResponse != null && balanceResponse.getOutput() != null) {
                for (KisBalanceSheetApiResponse.BalanceSheetData data : balanceResponse.getOutput()) {
                    String period = data.getStacYymm();
                    StockFinancialInfo info = periodMap.computeIfAbsent(period, k -> StockFinancialInfo.builder()
                            .stockCode(stockCode)
                            .stacYymm(period)
                            .divisionCode(divisionCode)
                            .build());

                    info.setCras(data.getCras());
                    info.setFxas(data.getFxas());
                    info.setTotalAset(data.getTotalAset());
                    info.setFlowLblt(data.getFlowLblt());
                    info.setFixLblt(data.getFixLblt());
                    info.setTotalLblt(data.getTotalLblt());
                    info.setCpfn(data.getCpfn());
                    info.setTotalCptl(data.getTotalCptl());
                }
            }

            return new ArrayList<>(periodMap.values());

        } catch (Exception e) {
            log.error("재무정보 API 호출 및 병합 실패 - 종목코드: {}", stockCode, e);
            return new ArrayList<>();
        }
    }

    /**
     * 재무비율 API 호출
     */
    private KisFinancialRatioApiResponse callFinancialRatioApi(String stockCode, String divisionCode) {
        try {
            HttpHeaders headers = createFinancialHeaders("FHKST66430300");

            String url = UriComponentsBuilder.fromUriString(tokenConfig.getBaseUrl())
                    .path("/uapi/domestic-stock/v1/finance/financial-ratio")
                    .queryParam("FID_DIV_CLS_CODE", divisionCode)
                    .queryParam("fid_cond_mrkt_div_code", "J")
                    .queryParam("fid_input_iscd", stockCode)
                    .build()
                    .toUriString();

            HttpEntity<Void> request = new HttpEntity<>(headers);

            ResponseEntity<KisFinancialRatioApiResponse> response = restTemplate.exchange(
                    url, HttpMethod.GET, request, KisFinancialRatioApiResponse.class
            );

            return response.getBody();

        } catch (Exception e) {
            log.error("재무비율 API 호출 실패 - 종목코드: {}", stockCode, e);
            return null;
        }
    }

    /**
     * 손익계산서 API 호출
     */
    private KisIncomeStatementApiResponse callIncomeStatementApi(String stockCode, String divisionCode) {
        try {
            HttpHeaders headers = createFinancialHeaders("FHKST66430200");

            String url = UriComponentsBuilder.fromUriString(tokenConfig.getBaseUrl())
                    .path("/uapi/domestic-stock/v1/finance/income-statement")
                    .queryParam("FID_DIV_CLS_CODE", divisionCode)
                    .queryParam("fid_cond_mrkt_div_code", "J")
                    .queryParam("fid_input_iscd", stockCode)
                    .build()
                    .toUriString();

            HttpEntity<Void> request = new HttpEntity<>(headers);

            ResponseEntity<KisIncomeStatementApiResponse> response = restTemplate.exchange(
                    url, HttpMethod.GET, request, KisIncomeStatementApiResponse.class
            );

            return response.getBody();

        } catch (Exception e) {
            log.error("손익계산서 API 호출 실패 - 종목코드: {}", stockCode, e);
            return null;
        }
    }

    /**
     * 대차대조표 API 호출
     */
    private KisBalanceSheetApiResponse callBalanceSheetApi(String stockCode, String divisionCode) {
        try {
            HttpHeaders headers = createFinancialHeaders("FHKST66430100");

            String url = UriComponentsBuilder.fromUriString(tokenConfig.getBaseUrl())
                    .path("/uapi/domestic-stock/v1/finance/balance-sheet")
                    .queryParam("FID_DIV_CLS_CODE", divisionCode)
                    .queryParam("fid_cond_mrkt_div_code", "J")
                    .queryParam("fid_input_iscd", stockCode)
                    .build()
                    .toUriString();

            HttpEntity<Void> request = new HttpEntity<>(headers);

            ResponseEntity<KisBalanceSheetApiResponse> response = restTemplate.exchange(
                    url, HttpMethod.GET, request, KisBalanceSheetApiResponse.class
            );

            return response.getBody();

        } catch (Exception e) {
            log.error("대차대조표 API 호출 실패 - 종목코드: {}", stockCode, e);
            return null;
        }
    }

    /**
     * 미제공 데이터 필터링 (99.99 → null)
     */
    private String filterNotProvided(String value) {
        if ("99.99".equals(value)) {
            return null;
        }
        return value;
    }

    // ============================================================================
    // Private Helper Methods - Entity 변환
    // ============================================================================

    /**
     * API 응답 → StockOverview Entity 변환 (프론트엔드 필수 필드만)
     */
    private StockOverview convertToStockOverview(String stockCode,
                                                   KisStockBasicInfoApiResponse.StockBasicInfoOutput output) {
        return StockOverview.builder()
                .stockCode(stockCode)
                .mketIdCd(output.getMketIdCd())
                .sctyGrpIdCd(output.getSctyGrpIdCd())
                .excgDvsnCd(output.getExcgDvsnCd())
                .setlMmdd(output.getSetlMmdd())
                .lstgStqt(output.getLstgStqt())
                .lstgCptlAmt(output.getLstgCptlAmt())
                .cpta(output.getCpta())
                .papr(output.getPapr())
                .issuPric(output.getIssuPric())
                .kospi200ItemYn(output.getKospi200ItemYn())
                .sctsMketLstgDt(output.getSctsMketLstgDt())
                .stckKindCd(output.getStckKindCd())
                .stdIdstClsfCd(output.getStdIdstClsfCd())
                .nxtTrStopYn(output.getNxtTrStopYn())
                .build();
    }

    /**
     * API 응답 → StockInvestOpinion Entity List 변환 (프론트엔드 필수 필드만)
     */
    private List<StockInvestOpinion> convertToInvestOpinions(String stockCode,
                                                              List<KisInvestOpinionApiResponse.InvestOpinionItem> items) {
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

            opinions.add(opinion);
        }

        return opinions;
    }

    // ============================================================================
    // Private Helper Methods - HTTP 헤더 생성
    // ============================================================================

    private HttpHeaders createStockBasicInfoHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("authorization", "Bearer " + tokenService.getRestApiToken().getAccessToken());
        headers.set("appkey", tokenConfig.getAppKey());
        headers.set("appsecret", tokenConfig.getAppSecret());
        headers.set("tr_id", "CTPF1002R");
        return headers;
    }

    private HttpHeaders createInvestOpinionHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("authorization", "Bearer " + tokenService.getRestApiToken().getAccessToken());
        headers.set("appkey", tokenConfig.getAppKey());
        headers.set("appsecret", tokenConfig.getAppSecret());
        headers.set("tr_id", "FHKST66430600");
        return headers;
    }

    private HttpHeaders createFinancialHeaders(String trId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("authorization", "Bearer " + tokenService.getRestApiToken().getAccessToken());
        headers.set("appkey", tokenConfig.getAppKey());
        headers.set("appsecret", tokenConfig.getAppSecret());
        headers.set("tr_id", trId);
        headers.set("custtype", "P");
        return headers;
    }

    private HttpHeaders createPeriodChartHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("authorization", "Bearer " + tokenService.getRestApiToken().getAccessToken());
        headers.set("appkey", tokenConfig.getAppKey());
        headers.set("appsecret", tokenConfig.getAppSecret());
        headers.set("tr_id", "FHKST03010100");  // 국내주식기간별시세 거래ID
        headers.set("custtype", "P");  // 개인
        return headers;
    }
}

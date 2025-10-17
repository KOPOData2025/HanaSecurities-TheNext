package com.hanati.domain.stock.service;

import com.hanati.common.config.TokenConfig;
import com.hanati.common.service.TokenService;
import com.hanati.domain.stock.dto.*;
import com.hanati.domain.stock.entity.StockFinancialInfo;
import com.hanati.domain.stock.repository.StockFinancialInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;

/**
 * [국내주식] 재무정보 조회 서비스 (DB 우선 조회 + 백그라운드 동기화)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FinancialInfoService {

    private final TokenConfig tokenConfig;
    private final TokenService tokenService;
    private final RestTemplate restTemplate;

    // DB 우선 조회 패턴을 위한 의존성
    private final StockFinancialInfoRepository stockFinancialInfoRepository;
    private final StockDataSyncService stockDataSyncService;

    /**
     * 통합 재무정보 조회 (DB 우선 조회 + 백그라운드 동기화)
     * @param stockCode 종목코드
     * @param divisionCode 분류 구분 코드 (0: 년, 1: 분기)
     * @return 통합 재무정보
     */
    public FinancialInfoResponse getFinancialInfo(String stockCode, String divisionCode) {
        log.info("[DB 우선 조회] 재무정보 시작 - 종목코드: {}, 분류구분: {}", stockCode, divisionCode);

        try {
            // 1. DB 조회 시도 (종목코드 + 분류구분으로 기간별 데이터 조회)
            List<StockFinancialInfo> dbData = stockFinancialInfoRepository
                    .findByStockCodeAndDivisionCodeOrderByStacYymmDesc(stockCode, divisionCode);

            // 2. DB에 데이터가 있으면
            if (!dbData.isEmpty()) {
                StockFinancialInfo latestRecord = dbData.get(0);
                log.info("[DB 조회 성공] 종목코드: {}, 데이터 건수: {}, 마지막 동기화: {}",
                        stockCode, dbData.size(), latestRecord.getLastSyncedDate());

                // 3. 오늘 동기화 안 됐으면 백그라운드 업데이트 트리거
                if (latestRecord.needsSync()) {
                    log.info("[비동기 업데이트 트리거] 종목코드: {} - 마지막 동기화: {}",
                            stockCode, latestRecord.getLastSyncedDate());
                    stockDataSyncService.syncFinancialInfoAsync(stockCode, divisionCode);
                } else {
                    log.info("[캐시 적중] 종목코드: {} - 오늘 이미 동기화됨", stockCode);
                }

                // 4. DB 데이터 즉시 반환
                return convertDbToFinancialInfoResponse(dbData);
            }

            // 5. DB에 없으면 동기 API 호출 + 저장 + 반환
            log.info("[초회 조회] DB에 없음, API 동기 호출 - 종목코드: {}", stockCode);
            return fetchAndSaveFinancialInfo(stockCode, divisionCode);

        } catch (Exception e) {
            log.error("[재무정보 조회 실패] 종목코드: {}, 에러: {}", stockCode, e.getMessage(), e);
            return FinancialInfoResponse.builder()
                    .success(false)
                    .message("재무정보 조회 실패: " + e.getMessage())
                    .build();
        }
    }

    /**
     * DB에 없을 때: 동기 API 호출 → DB 저장 → 응답 반환
     */
    private FinancialInfoResponse fetchAndSaveFinancialInfo(String stockCode, String divisionCode) {
        try {
            // 3개의 API 병렬 호출
            KisFinancialRatioApiResponse ratioResponse = getFinancialRatio(stockCode, divisionCode);
            KisIncomeStatementApiResponse incomeResponse = getIncomeStatement(stockCode, divisionCode);
            KisBalanceSheetApiResponse balanceResponse = getBalanceSheet(stockCode, divisionCode);

            // 데이터 통합 및 DB 저장
            List<StockFinancialInfo> financialInfoList = mergeAndSaveFinancialData(
                    stockCode, divisionCode, ratioResponse, incomeResponse, balanceResponse
            );

            // DB 데이터를 응답으로 변환
            return convertDbToFinancialInfoResponse(financialInfoList);

        } catch (Exception e) {
            log.error("재무정보 API 호출 실패: {}", e.getMessage());
            return FinancialInfoResponse.builder()
                    .success(false)
                    .message("재무정보 조회 실패: " + e.getMessage())
                    .build();
        }
    }

    /**
     * DB 엔티티 리스트 → 클라이언트 응답 변환
     */
    private FinancialInfoResponse convertDbToFinancialInfoResponse(List<StockFinancialInfo> dbData) {
        List<FinancialInfoResponse.FinancialPeriodInfo> periods = new ArrayList<>();

        for (StockFinancialInfo info : dbData) {
            FinancialInfoResponse.FinancialPeriodInfo periodInfo = FinancialInfoResponse.FinancialPeriodInfo.builder()
                    .period(info.getStacYymm())
                    // 재무비율
                    .salesGrowthRate(info.getGrs())
                    .operatingProfitGrowthRate(info.getBsopPrfiInrt())
                    .netIncomeGrowthRate(info.getNtinInrt())
                    .roe(info.getRoeVal())
                    .eps(info.getEps())
                    .sps(info.getSps())
                    .bps(info.getBps())
                    .reserveRatio(info.getRsrvRate())
                    .debtRatio(info.getLbltRate())
                    // 손익계산서
                    .sales(info.getSaleAccount())
                    .salesCost(info.getSaleCost())
                    .grossProfit(info.getSaleTotlPrfi())
                    .operatingProfit(info.getBsopPrti())
                    .ordinaryProfit(info.getOpPrfi())
                    .extraordinaryGain(info.getSpecPrfi())
                    .extraordinaryLoss(info.getSpecLoss())
                    .netIncome(info.getThtrNtin())
                    // 대차대조표
                    .currentAssets(info.getCras())
                    .fixedAssets(info.getFxas())
                    .totalAssets(info.getTotalAset())
                    .currentLiabilities(info.getFlowLblt())
                    .fixedLiabilities(info.getFixLblt())
                    .totalLiabilities(info.getTotalLblt())
                    .capital(info.getCpfn())
                    .totalEquity(info.getTotalCptl())
                    .build();

            periods.add(periodInfo);
        }

        FinancialInfoResponse.FinancialData data = FinancialInfoResponse.FinancialData.builder()
                .periods(periods)
                .build();

        return FinancialInfoResponse.builder()
                .success(true)
                .message("재무정보 조회 성공 (DB 캐시)")
                .data(data)
                .build();
    }

    /**
     * 3개 API 응답 통합 및 DB 저장
     */
    private List<StockFinancialInfo> mergeAndSaveFinancialData(
            String stockCode,
            String divisionCode,
            KisFinancialRatioApiResponse ratioResponse,
            KisIncomeStatementApiResponse incomeResponse,
            KisBalanceSheetApiResponse balanceResponse
    ) {
        Map<String, StockFinancialInfo> periodMap = new LinkedHashMap<>();

        // 재무비율 데이터 병합
        if (ratioResponse.getOutput() != null) {
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
        if (incomeResponse.getOutput() != null) {
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
        if (balanceResponse.getOutput() != null) {
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

        List<StockFinancialInfo> financialInfoList = new ArrayList<>(periodMap.values());

        // 동기화 날짜 표시 및 DB 저장
        financialInfoList.forEach(info -> info.markSynced());
        stockFinancialInfoRepository.saveAll(financialInfoList);

        log.info("[DB 저장 완료] 종목코드: {}, {} 건", stockCode, financialInfoList.size());

        return financialInfoList;
    }

    /**
     * 재무비율 조회
     */
    private KisFinancialRatioApiResponse getFinancialRatio(String stockCode, String divisionCode) {
        log.info("재무비율 조회 - 종목코드: {}", stockCode);

        HttpHeaders headers = createFinancialHeaders("FHKST66430300");

        String url = UriComponentsBuilder.fromUriString(tokenConfig.getBaseUrl())
                .path("/uapi/domestic-stock/v1/finance/financial-ratio")
                .queryParam("FID_DIV_CLS_CODE", divisionCode)
                .queryParam("fid_cond_mrkt_div_code", "J")
                .queryParam("fid_input_iscd", stockCode)
                .build()
                .toUriString();

        HttpEntity<Void> httpRequest = new HttpEntity<>(headers);

        ResponseEntity<KisFinancialRatioApiResponse> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                httpRequest,
                KisFinancialRatioApiResponse.class
        );

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            return response.getBody();
        }

        throw new RuntimeException("재무비율 조회 실패");
    }

    /**
     * 손익계산서 조회
     */
    private KisIncomeStatementApiResponse getIncomeStatement(String stockCode, String divisionCode) {
        log.info("손익계산서 조회 - 종목코드: {}", stockCode);

        HttpHeaders headers = createFinancialHeaders("FHKST66430200");

        String url = UriComponentsBuilder.fromUriString(tokenConfig.getBaseUrl())
                .path("/uapi/domestic-stock/v1/finance/income-statement")
                .queryParam("FID_DIV_CLS_CODE", divisionCode)
                .queryParam("fid_cond_mrkt_div_code", "J")
                .queryParam("fid_input_iscd", stockCode)
                .build()
                .toUriString();

        HttpEntity<Void> httpRequest = new HttpEntity<>(headers);

        ResponseEntity<KisIncomeStatementApiResponse> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                httpRequest,
                KisIncomeStatementApiResponse.class
        );

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            return response.getBody();
        }

        throw new RuntimeException("손익계산서 조회 실패");
    }

    /**
     * 대차대조표 조회
     */
    private KisBalanceSheetApiResponse getBalanceSheet(String stockCode, String divisionCode) {
        log.info("대차대조표 조회 - 종목코드: {}", stockCode);

        HttpHeaders headers = createFinancialHeaders("FHKST66430100");

        String url = UriComponentsBuilder.fromUriString(tokenConfig.getBaseUrl())
                .path("/uapi/domestic-stock/v1/finance/balance-sheet")
                .queryParam("FID_DIV_CLS_CODE", divisionCode)
                .queryParam("fid_cond_mrkt_div_code", "J")
                .queryParam("fid_input_iscd", stockCode)
                .build()
                .toUriString();

        HttpEntity<Void> httpRequest = new HttpEntity<>(headers);

        ResponseEntity<KisBalanceSheetApiResponse> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                httpRequest,
                KisBalanceSheetApiResponse.class
        );

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            return response.getBody();
        }

        throw new RuntimeException("대차대조표 조회 실패");
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

    /**
     * 재무정보 API용 공통 헤더 생성
     */
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
}

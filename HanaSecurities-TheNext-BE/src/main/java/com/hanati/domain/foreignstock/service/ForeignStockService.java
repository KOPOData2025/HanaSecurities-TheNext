package com.hanati.domain.foreignstock.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hanati.common.config.TokenConfig;
import com.hanati.common.service.TokenService;
import com.hanati.domain.foreignstock.dto.ForeignCurrentPriceResponse;
import com.hanati.domain.foreignstock.dto.ForeignIntradayChartResponse;
import com.hanati.domain.foreignstock.dto.ForeignPeriodChartResponse;
import com.hanati.domain.foreignstock.dto.ForeignBuyableAmountResponse;
import com.hanati.domain.foreignstock.dto.ForeignSellableQuantityResponse;
import com.hanati.domain.foreignstock.dto.ForeignStockBalanceResponse;
import com.hanati.domain.foreignstock.dto.ForeignStockBasicInfoResponse;
import com.hanati.domain.foreignstock.dto.ForeignStockOrderRequest;
import com.hanati.domain.foreignstock.dto.ForeignStockOrderResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ForeignStockService {

    private final TokenConfig tokenConfig;
    private final TokenService tokenService;
    private final RestTemplate restTemplate;

    /**
     * 해외주식 API 공통 헤더 생성
     * @param trId 거래ID
     * @return HttpHeaders
     */
    protected HttpHeaders createHeaders(String trId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("authorization", "Bearer " + tokenService.getRestApiAccessToken());
        headers.set("appkey", tokenConfig.getAppKey());
        headers.set("appsecret", tokenConfig.getAppSecret());
        headers.set("tr_id", trId);
        headers.set("custtype", "P");  // 개인 고객

        log.debug("해외주식 API 헤더 생성 완료 - TR_ID: {}", trId);
        return headers;
    }

    /**
     * 해외주식 POST 요청용 헤더 생성
     * @param trId 거래ID
     * @return HttpHeaders
     */
    protected HttpHeaders createPostHeaders(String trId) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("content-type", "application/json; charset=utf-8");
        headers.set("authorization", "Bearer " + tokenService.getRestApiAccessToken());
        headers.set("appkey", tokenConfig.getAppKey());
        headers.set("appsecret", tokenConfig.getAppSecret());
        headers.set("tr_id", trId);
        headers.set("custtype", "P");  // 개인 고객

        log.debug("해외주식 POST API 헤더 생성 완료 - TR_ID: {}", trId);
        return headers;
    }

    /**
     * 거래소코드 유효성 검증
     * @param exchangeCode 거래소코드
     * @return 유효 여부
     */
    protected boolean isValidExchangeCode(String exchangeCode) {
        return exchangeCode != null &&
               (exchangeCode.equals("NASD") ||  // 나스닥
                exchangeCode.equals("NYSE") ||  // 뉴욕
                exchangeCode.equals("NAS") ||   // 나스닥 (일부 API)
                exchangeCode.equals("NYS") ||   // 뉴욕 (일부 API)
                exchangeCode.equals("AMEX") ||  // 아멕스
                exchangeCode.equals("AMS") ||   // 아멕스 (일부 API)
                exchangeCode.equals("SEHK") ||  // 홍콩
                exchangeCode.equals("HKS") ||   // 홍콩 (일부 API)
                exchangeCode.equals("TKSE") ||  // 도쿄
                exchangeCode.equals("TSE"));    // 도쿄 (일부 API)
    }

    /**
     * 에러 응답 로깅
     * @param operation 작업명
     * @param errorMessage 에러 메시지
     */
    protected void logError(String operation, String errorMessage) {
        log.error("[{}] 실패: {}", operation, errorMessage);
    }

    /**
     * 에러 응답 로깅 (상세)
     * @param operation 작업명
     * @param errorMessage 에러 메시지
     * @param exception 예외
     */
    protected void logError(String operation, String errorMessage, Exception exception) {
        log.error("[{}] 실패: {}", operation, errorMessage, exception);
    }

    /**
     * 해외주식 현재가 상세 조회
     * @param exchangeCode 거래소코드 (HKS, NYS, NAS, AMS, TSE, etc.)
     * @param stockCode 종목코드
     * @return 해외주식 현재가 상세 정보
     */
    public ForeignCurrentPriceResponse getCurrentPrice(String exchangeCode, String stockCode) {
        log.info("[해외주식 현재가 조회] 거래소: {}, 종목코드: {}", exchangeCode, stockCode);

        try {
            // 거래소코드 유효성 검증
            if (!isValidExchangeCode(exchangeCode)) {
                logError("해외주식 현재가 조회", "유효하지 않은 거래소코드: " + exchangeCode);
                throw new RuntimeException("유효하지 않은 거래소코드: " + exchangeCode);
            }

            // 헤더 설정
            HttpHeaders headers = createHeaders("HHDFS76200200");

            // URL 구성
            String url = UriComponentsBuilder.fromUriString(tokenConfig.getBaseUrl())
                    .path("/uapi/overseas-price/v1/quotations/price-detail")
                    .queryParam("AUTH", "")  // 사용자권한정보
                    .queryParam("EXCD", exchangeCode)
                    .queryParam("SYMB", stockCode)
                    .build()
                    .toUriString();

            HttpEntity<Void> httpRequest = new HttpEntity<>(headers);

            // API 호출
            ResponseEntity<KisForeignCurrentPriceApiResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    httpRequest,
                    KisForeignCurrentPriceApiResponse.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                KisForeignCurrentPriceApiResponse apiResponse = response.getBody();

                if (!"0".equals(apiResponse.getRtCd())) {
                    logError("해외주식 현재가 조회", apiResponse.getMsg1());
                    throw new RuntimeException("해외주식 현재가 조회 실패: " + apiResponse.getMsg1());
                }

                log.info("[해외주식 현재가 조회 성공] 거래소: {}, 종목: {}, 현재가: {}",
                        exchangeCode, stockCode, apiResponse.getOutput().getLast());

                // 응답 데이터 변환
                return convertToCurrentPriceResponse(apiResponse.getOutput());
            }

            throw new RuntimeException("API 응답 없음");

        } catch (RestClientException e) {
            logError("해외주식 현재가 조회", e.getMessage(), e);
            throw new RuntimeException("해외주식 현재가 조회 실패", e);
        }
    }

    /**
     * KIS API 응답을 클라이언트 응답으로 변환 (현재가 상세)
     */
    private ForeignCurrentPriceResponse convertToCurrentPriceResponse(
            KisForeignCurrentPriceApiResponse.Output output) {
        return ForeignCurrentPriceResponse.builder()
                .rsym(output.getRsym())
                .pvol(output.getPvol())
                .open(output.getOpen())
                .high(output.getHigh())
                .low(output.getLow())
                .last(output.getLast())
                .base(output.getBase())
                .tomv(output.getTomv())
                .pamt(output.getPamt())
                .uplp(output.getUplp())
                .dnlp(output.getDnlp())
                .h52p(output.getH52p())
                .h52d(output.getH52d())
                .l52p(output.getL52p())
                .l52d(output.getL52d())
                .perx(output.getPerx())
                .pbrx(output.getPbrx())
                .epsx(output.getEpsx())
                .bpsx(output.getBpsx())
                .shar(output.getShar())
                .mcap(output.getMcap())
                .curr(output.getCurr())
                .zdiv(output.getZdiv())
                .vnit(output.getVnit())
                .tXprc(output.getTXprc())
                .tXdif(output.getTXdif())
                .tXrat(output.getTXrat())
                .pXprc(output.getPXprc())
                .pXdif(output.getPXdif())
                .pXrat(output.getPXrat())
                .tRate(output.getTRate())
                .pRate(output.getPRate())
                .tXsgn(output.getTXsgn())
                .pXsng(output.getPXsng())
                .eOrdyn(output.getEOrdyn())
                .eHogau(output.getEHogau())
                .eIcod(output.getEIcod())
                .eParp(output.getEParp())
                .tvol(output.getTvol())
                .tamt(output.getTamt())
                .etypNm(output.getEtypNm())
                .build();
    }

    /**
     * 해외주식 분봉 조회
     * @param exchangeCode 거래소코드 (NYS, NAS, HKS, TSE, etc.)
     * @param stockCode 종목코드
     * @param minuteGap 분갭 (1: 1분봉, 5: 5분봉, etc.)
     * @return 해외주식 분봉 차트 데이터
     */
    public ForeignIntradayChartResponse getIntradayChart(String exchangeCode, String stockCode, String minuteGap) {
        log.info("[해외주식 분봉 조회] 거래소: {}, 종목: {}, 분갭: {}", exchangeCode, stockCode, minuteGap);

        try {
            // 거래소코드 유효성 검증
            if (!isValidExchangeCode(exchangeCode)) {
                logError("해외주식 분봉 조회", "유효하지 않은 거래소코드: " + exchangeCode);
                throw new RuntimeException("유효하지 않은 거래소코드: " + exchangeCode);
            }

            // 헤더 설정
            HttpHeaders headers = createHeaders("HHDFS76950200");

            // URL 구성
            String url = UriComponentsBuilder.fromUriString(tokenConfig.getBaseUrl())
                    .path("/uapi/overseas-price/v1/quotations/inquire-time-itemchartprice")
                    .queryParam("AUTH", "")  // 사용자권한정보 (공백)
                    .queryParam("EXCD", exchangeCode)
                    .queryParam("SYMB", stockCode)
                    .queryParam("NMIN", minuteGap)  // 분갭 (1: 1분봉, 5: 5분봉, etc.)
                    .queryParam("PINC", "1")  // 전일포함 (1: 전일포함)
                    .queryParam("NEXT", "")  // 다음조회 (처음: 공백)
                    .queryParam("NREC", "120")  // 요청갯수 (최대 120)
                    .queryParam("FILL", "")  // 미체결채움 (공백)
                    .queryParam("KEYB", "")  // NEXT KEY BUFF (처음: 공백)
                    .build()
                    .toUriString();

            HttpEntity<Void> httpRequest = new HttpEntity<>(headers);

            // API 호출
            ResponseEntity<KisForeignIntradayChartApiResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    httpRequest,
                    KisForeignIntradayChartApiResponse.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                KisForeignIntradayChartApiResponse apiResponse = response.getBody();

                if (!"0".equals(apiResponse.getRtCd())) {
                    logError("해외주식 분봉 조회", apiResponse.getMsg1());
                    throw new RuntimeException("해외주식 분봉 조회 실패: " + apiResponse.getMsg1());
                }

                log.info("[해외주식 분봉 조회 성공] 거래소: {}, 종목: {}, 데이터 건수: {}",
                        exchangeCode, stockCode, apiResponse.getOutput2() != null ? apiResponse.getOutput2().size() : 0);

                // 응답 데이터 변환
                return convertToIntradayChartResponse(apiResponse);
            }

            throw new RuntimeException("API 응답 없음");

        } catch (RestClientException e) {
            logError("해외주식 분봉 조회", e.getMessage(), e);
            throw new RuntimeException("해외주식 분봉 조회 실패", e);
        }
    }

    /**
     * KIS API 응답을 클라이언트 응답으로 변환 (분봉)
     */
    private ForeignIntradayChartResponse convertToIntradayChartResponse(
            KisForeignIntradayChartApiResponse apiResponse) {

        java.util.List<ForeignIntradayChartResponse.ChartData> chartDataList = new java.util.ArrayList<>();

        if (apiResponse.getOutput2() != null) {
            for (KisForeignIntradayChartApiResponse.ChartItem item : apiResponse.getOutput2()) {
                ForeignIntradayChartResponse.ChartData chartData = ForeignIntradayChartResponse.ChartData.builder()
                        .kymd(item.getKymd())
                        .khms(item.getKhms())
                        .open(item.getOpen())
                        .high(item.getHigh())
                        .low(item.getLow())
                        .last(item.getLast())
                        .evol(item.getEvol())
                        .eamt(item.getEamt())
                        .build();
                chartDataList.add(chartData);
            }
        }

        KisForeignIntradayChartApiResponse.Output1 output1 = apiResponse.getOutput1();

        return ForeignIntradayChartResponse.builder()
                .rsym(output1.getRsym())
                .next(output1.getNext())
                .more(output1.getMore())
                .chartData(chartDataList)
                .build();
    }

    /**
     * 해외주식 기간별 시세 조회 (일/주/월)
     * @param exchangeCode 거래소코드 (NYS, NAS, HKS, TSE, etc.)
     * @param stockCode 종목코드
     * @param periodCode 기간분류코드 (D: 일봉, W: 주봉, M: 월봉)
     * @return 해외주식 기간별 차트 데이터
     */
    public ForeignPeriodChartResponse getPeriodChart(String exchangeCode, String stockCode, String periodCode) {
        log.info("[해외주식 기간별 시세 조회] 거래소: {}, 종목: {}, 기간: {}", exchangeCode, stockCode, periodCode);

        try {
            // 거래소코드 유효성 검증
            if (!isValidExchangeCode(exchangeCode)) {
                logError("해외주식 기간별 시세 조회", "유효하지 않은 거래소코드: " + exchangeCode);
                throw new RuntimeException("유효하지 않은 거래소코드: " + exchangeCode);
            }

            // 기간코드 유효성 검증
            if (!isValidPeriodCode(periodCode)) {
                logError("해외주식 기간별 시세 조회", "유효하지 않은 기간코드: " + periodCode);
                throw new RuntimeException("유효하지 않은 기간코드: " + periodCode + " (D/W/M만 가능)");
            }

            // 기간코드 변환: D/W/M -> 0/1/2 (API 요구 형식)
            String gubnCode = convertPeriodCodeToGubn(periodCode);

            // 헤더 설정
            HttpHeaders headers = createHeaders("HHDFS76240000");

            // URL 구성
            String url = UriComponentsBuilder.fromUriString(tokenConfig.getBaseUrl())
                    .path("/uapi/overseas-price/v1/quotations/dailyprice")
                    .queryParam("AUTH", "")  // 사용자권한정보 (공백)
                    .queryParam("EXCD", exchangeCode)
                    .queryParam("SYMB", stockCode)
                    .queryParam("GUBN", gubnCode)  // 0: 일, 1: 주, 2: 월
                    .queryParam("BYMD", "")  // 조회시작일자 (공백: 최근 100건)
                    .queryParam("MODP", "0")  // 수정주가 반영여부 (0: 미반영)
                    .build()
                    .toUriString();

            HttpEntity<Void> httpRequest = new HttpEntity<>(headers);

            // API 호출
            ResponseEntity<KisForeignPeriodChartApiResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    httpRequest,
                    KisForeignPeriodChartApiResponse.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                KisForeignPeriodChartApiResponse apiResponse = response.getBody();

                if (!"0".equals(apiResponse.getRtCd())) {
                    logError("해외주식 기간별 시세 조회", apiResponse.getMsg1());
                    throw new RuntimeException("해외주식 기간별 시세 조회 실패: " + apiResponse.getMsg1());
                }

                log.info("[해외주식 기간별 시세 조회 성공] 거래소: {}, 종목: {}, 데이터 건수: {}",
                        exchangeCode, stockCode, apiResponse.getOutput2() != null ? apiResponse.getOutput2().size() : 0);

                // 응답 데이터 변환
                return convertToPeriodChartResponse(apiResponse);
            }

            throw new RuntimeException("API 응답 없음");

        } catch (RestClientException e) {
            logError("해외주식 기간별 시세 조회", e.getMessage(), e);
            throw new RuntimeException("해외주식 기간별 시세 조회 실패", e);
        }
    }

    /**
     * 기간코드 유효성 검증
     * @param periodCode 기간코드
     * @return 유효 여부
     */
    private boolean isValidPeriodCode(String periodCode) {
        return periodCode != null &&
               (periodCode.equals("D") ||  // 일봉
                periodCode.equals("W") ||  // 주봉
                periodCode.equals("M"));    // 월봉 (년봉은 API 미지원)
    }

    /**
     * 기간코드를 API GUBN 파라미터로 변환
     * @param periodCode 기간코드 (D/W/M)
     * @return GUBN 코드 (0/1/2)
     */
    private String convertPeriodCodeToGubn(String periodCode) {
        switch (periodCode) {
            case "D":
                return "0";  // 일봉
            case "W":
                return "1";  // 주봉
            case "M":
                return "2";  // 월봉
            default:
                return "0";  // 기본값: 일봉
        }
    }

    /**
     * KIS API 응답을 클라이언트 응답으로 변환 (기간별)
     */
    private ForeignPeriodChartResponse convertToPeriodChartResponse(
            KisForeignPeriodChartApiResponse apiResponse) {

        java.util.List<ForeignPeriodChartResponse.ChartData> chartDataList = new java.util.ArrayList<>();

        if (apiResponse.getOutput2() != null) {
            for (KisForeignPeriodChartApiResponse.ChartItem item : apiResponse.getOutput2()) {
                ForeignPeriodChartResponse.ChartData chartData = ForeignPeriodChartResponse.ChartData.builder()
                        .xymd(item.getXymd())
                        .open(item.getOpen())
                        .high(item.getHigh())
                        .low(item.getLow())
                        .clos(item.getClos())
                        .tvol(item.getTvol())
                        .tamt(item.getTamt())
                        .build();
                chartDataList.add(chartData);
            }
        }

        return ForeignPeriodChartResponse.builder()
                .rsym(apiResponse.getOutput1().getRsym())
                .chartData(chartDataList)
                .build();
    }

    /**
     * 해외주식 매수 주문
     * @param request 주문 요청 정보
     * @return 주문 응답
     */
    public ForeignStockOrderResponse buyStock(ForeignStockOrderRequest request) {
        log.info("[해외주식 매수 주문] 거래소: {}, 종목: {}, 수량: {}, 단가: {}",
                request.getOvrsExcgCd(), request.getPdno(), request.getOrdQty(), request.getOvrsOrdUnpr());

        return executeForeignOrder(request, "TTTT1002U", "매수"); // 미국 매수 TR_ID (실전)
    }

    /**
     * 해외주식 매도 주문
     * @param request 주문 요청 정보
     * @return 주문 응답
     */
    public ForeignStockOrderResponse sellStock(ForeignStockOrderRequest request) {
        log.info("[해외주식 매도 주문] 거래소: {}, 종목: {}, 수량: {}, 단가: {}",
                request.getOvrsExcgCd(), request.getPdno(), request.getOrdQty(), request.getOvrsOrdUnpr());

        return executeForeignOrder(request, "TTTT1006U", "매도"); // 미국 매도 TR_ID (실전)
    }

    /**
     * 해외주식 주문 실행
     */
    private ForeignStockOrderResponse executeForeignOrder(ForeignStockOrderRequest request, String trId, String orderType) {
        try {
            // 거래소코드 유효성 검증
            if (!isValidExchangeCode(request.getOvrsExcgCd())) {
                logError("해외주식 주문", "유효하지 않은 거래소코드: " + request.getOvrsExcgCd());
                return ForeignStockOrderResponse.builder()
                        .success(false)
                        .message("유효하지 않은 거래소코드: " + request.getOvrsExcgCd())
                        .build();
            }

            // 헤더 설정
            HttpHeaders headers = createPostHeaders(trId);

            // 요청 바디 생성
            KisForeignStockOrderApiRequest apiRequest = KisForeignStockOrderApiRequest.builder()
                    .cano(tokenConfig.getAccountNumber())
                    .acntPrdtCd(tokenConfig.getAccountProductCode())
                    .ovrsExcgCd(request.getOvrsExcgCd())
                    .pdno(request.getPdno())
                    .ordQty(request.getOrdQty())
                    .ovrsOrdUnpr(request.getOvrsOrdUnpr())
                    .sllType(request.getSllType() != null ? request.getSllType() : "")
                    .ordSvrDvsnCd(request.getOrdSvrDvsnCd())
                    .ordDvsn(request.getOrdDvsn())
                    .build();

            // URL 구성
            String url = tokenConfig.getBaseUrl() + "/uapi/overseas-stock/v1/trading/order";

            HttpEntity<KisForeignStockOrderApiRequest> httpRequest = new HttpEntity<>(apiRequest, headers);

            // API 호출
            ResponseEntity<KisForeignStockOrderApiResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    httpRequest,
                    KisForeignStockOrderApiResponse.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                KisForeignStockOrderApiResponse apiResponse = response.getBody();

                if (!"0".equals(apiResponse.getRtCd())) {
                    logError("해외주식 " + orderType + " 주문", apiResponse.getMsg1());
                    return ForeignStockOrderResponse.builder()
                            .success(false)
                            .message(apiResponse.getMsg1())
                            .build();
                }

                // 응답 데이터 변환
                KisForeignStockOrderApiResponse.Output output = apiResponse.getOutput();

                log.info("[해외주식 {} 주문 성공] 주문번호: {}, 주문시각: {}",
                        orderType, output.getOdno(), output.getOrdTmd());

                return ForeignStockOrderResponse.builder()
                        .success(true)
                        .orderNumber(output.getOdno())
                        .orderTime(output.getOrdTmd())
                        .organizationNumber(output.getKrxFwdgOrdOrgno())
                        .message(apiResponse.getMsg1())
                        .build();
            }

        } catch (RestClientException e) {
            logError("해외주식 " + orderType + " 주문", e.getMessage(), e);
            return ForeignStockOrderResponse.builder()
                    .success(false)
                    .message("해외주식 " + orderType + " 주문 실패: " + e.getMessage())
                    .build();
        }

        return ForeignStockOrderResponse.builder()
                .success(false)
                .message("해외주식 " + orderType + " 주문 실패")
                .build();
    }

    /**
     * 한국투자증권 해외주식 주문 API 요청 클래스
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @lombok.Builder
    static class KisForeignStockOrderApiRequest {
        @JsonProperty("CANO")
        private String cano;

        @JsonProperty("ACNT_PRDT_CD")
        private String acntPrdtCd;

        @JsonProperty("OVRS_EXCG_CD")
        private String ovrsExcgCd;

        @JsonProperty("PDNO")
        private String pdno;

        @JsonProperty("ORD_QTY")
        private String ordQty;

        @JsonProperty("OVRS_ORD_UNPR")
        private String ovrsOrdUnpr;

        @JsonProperty("SLL_TYPE")
        private String sllType;

        @JsonProperty("ORD_SVR_DVSN_CD")
        private String ordSvrDvsnCd;

        @JsonProperty("ORD_DVSN")
        private String ordDvsn;
    }

    /**
     * 한국투자증권 해외주식 주문 API 응답 클래스
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class KisForeignStockOrderApiResponse {
        @JsonProperty("rt_cd")
        private String rtCd;

        @JsonProperty("msg_cd")
        private String msgCd;

        @JsonProperty("msg1")
        private String msg1;

        @JsonProperty("output")
        private Output output;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        static class Output {
            @JsonProperty("KRX_FWDG_ORD_ORGNO")
            private String krxFwdgOrdOrgno;

            @JsonProperty("ODNO")
            private String odno;

            @JsonProperty("ORD_TMD")
            private String ordTmd;
        }
    }

    /**
     * 해외주식 잔고 조회
     * @param exchangeCode 거래소코드 (NASD, NYSE, AMEX, SEHK, TKSE, etc.)
     * @param currencyCode 거래통화코드 (USD, HKD, CNY, JPY, VND)
     * @return 해외주식 잔고 정보
     */
    public ForeignStockBalanceResponse getBalance(String exchangeCode, String currencyCode) {
        log.info("[해외주식 잔고 조회] 거래소: {}, 통화: {}", exchangeCode, currencyCode);

        try {
            // 거래소코드 유효성 검증
            if (!isValidExchangeCode(exchangeCode)) {
                logError("해외주식 잔고 조회", "유효하지 않은 거래소코드: " + exchangeCode);
                throw new RuntimeException("유효하지 않은 거래소코드: " + exchangeCode);
            }

            // 헤더 설정
            HttpHeaders headers = createHeaders("TTTS3012R");

            // URL 구성
            String url = UriComponentsBuilder.fromUriString(tokenConfig.getBaseUrl())
                    .path("/uapi/overseas-stock/v1/trading/inquire-balance")
                    .queryParam("CANO", tokenConfig.getAccountNumber())
                    .queryParam("ACNT_PRDT_CD", tokenConfig.getAccountProductCode())
                    .queryParam("OVRS_EXCG_CD", exchangeCode)
                    .queryParam("TR_CRCY_CD", currencyCode)
                    .queryParam("CTX_AREA_FK200", "")  // 최초 조회
                    .queryParam("CTX_AREA_NK200", "")  // 최초 조회
                    .build()
                    .toUriString();

            HttpEntity<Void> httpRequest = new HttpEntity<>(headers);

            // API 호출
            ResponseEntity<KisForeignStockBalanceApiResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    httpRequest,
                    KisForeignStockBalanceApiResponse.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                KisForeignStockBalanceApiResponse apiResponse = response.getBody();

                if (!"0".equals(apiResponse.getRtCd())) {
                    logError("해외주식 잔고 조회", apiResponse.getMsg1());
                    throw new RuntimeException("해외주식 잔고 조회 실패: " + apiResponse.getMsg1());
                }

                log.info("[해외주식 잔고 조회 성공] 거래소: {}, 보유종목수: {}",
                        exchangeCode, apiResponse.getOutput1() != null ? apiResponse.getOutput1().size() : 0);

                // 응답 데이터 변환
                return convertToBalanceResponse(apiResponse);
            }

            throw new RuntimeException("API 응답 없음");

        } catch (RestClientException e) {
            logError("해외주식 잔고 조회", e.getMessage(), e);
            throw new RuntimeException("해외주식 잔고 조회 실패", e);
        }
    }

    /**
     * KIS API 응답을 클라이언트 응답으로 변환 (잔고)
     */
    private ForeignStockBalanceResponse convertToBalanceResponse(
            KisForeignStockBalanceApiResponse apiResponse) {

        java.util.List<ForeignStockBalanceResponse.HoldingData> holdings = new java.util.ArrayList<>();

        if (apiResponse.getOutput1() != null) {
            for (KisForeignStockBalanceApiResponse.BalanceItem item : apiResponse.getOutput1()) {
                // 빈 데이터는 제외 (종목코드가 없는 경우)
                if (item.getOvrsPdno() == null || item.getOvrsPdno().trim().isEmpty()) {
                    continue;
                }

                ForeignStockBalanceResponse.HoldingData holding = ForeignStockBalanceResponse.HoldingData.builder()
                        .ovrsPdno(item.getOvrsPdno())
                        .ovrsItemName(item.getOvrsItemName())
                        .ovrsCblcQty(item.getOvrsCblcQty())
                        .pchsAvgPric(item.getPchsAvgPric())
                        .nowPric2(item.getNowPric2())
                        .frcrEvluPflsAmt(item.getFrcrEvluPflsAmt())
                        .evluPflsRt(item.getEvluPflsRt())
                        .frcrPchsAmt1(item.getFrcrPchsAmt1())
                        .ovrsStckEvluAmt(item.getOvrsStckEvluAmt())
                        .ordPsblQty(item.getOrdPsblQty())
                        .trCrcyCd(item.getTrCrcyCd())
                        .ovrsExcgCd(item.getOvrsExcgCd())
                        .build();
                holdings.add(holding);
            }
        }

        KisForeignStockBalanceApiResponse.BalanceSummary summary = apiResponse.getOutput2();

        return ForeignStockBalanceResponse.builder()
                .holdings(holdings)
                .totEvluPflsAmt(summary != null ? summary.getTotEvluPflsAmt() : "0")
                .totPftrt(summary != null ? summary.getTotPftrt() : "0")
                .build();
    }

    /**
     * 해외주식 매수가능금액 조회
     * @param exchangeCode 거래소코드 (NASD, NYSE, AMEX, SEHK, TKSE, etc.)
     * @param stockCode 종목코드
     * @param currencyCode 거래통화코드 (USD, HKD, CNY, JPY, VND)
     * @return 매수가능금액 정보
     */
    public ForeignBuyableAmountResponse getBuyableAmount(String exchangeCode, String stockCode, String currencyCode) {
        log.info("[해외주식 매수가능금액 조회] 거래소: {}, 종목: {}, 통화: {}", exchangeCode, stockCode, currencyCode);

        try {
            // Note: 한국투자증권 API에서 해외주식 매수가능금액 전용 API가 없는 경우
            // 잔고 API의 summary 정보나 별도 계산이 필요할 수 있습니다.
            // 현재는 기본 구조만 제공합니다.

            // 거래소코드 유효성 검증
            if (!isValidExchangeCode(exchangeCode)) {
                logError("해외주식 매수가능금액 조회", "유효하지 않은 거래소코드: " + exchangeCode);
                throw new RuntimeException("유효하지 않은 거래소코드: " + exchangeCode);
            }

            // TODO: 실제 API 연동 필요
            // 현재는 기본 응답 구조만 반환
            log.warn("[해외주식 매수가능금액 조회] 실제 API 미구현 - 기본 구조만 반환");

            return ForeignBuyableAmountResponse.builder()
                    .buyableAmount("0")
                    .foreignBuyableAmount("0")
                    .currencyCode(currencyCode)
                    .exchangeRate("1300.00")  // 기본 환율 (실제로는 실시간 환율 조회 필요)
                    .build();

        } catch (Exception e) {
            logError("해외주식 매수가능금액 조회", e.getMessage(), e);
            throw new RuntimeException("해외주식 매수가능금액 조회 실패", e);
        }
    }

    /**
     * 해외주식 매도가능수량 조회
     * @param exchangeCode 거래소코드 (NASD, NYSE, AMEX, SEHK, TKSE, etc.)
     * @param stockCode 종목코드
     * @param currencyCode 거래통화코드 (USD, HKD, CNY, JPY, VND)
     * @return 매도가능수량 정보
     */
    public ForeignSellableQuantityResponse getSellableQuantity(String exchangeCode, String stockCode, String currencyCode) {
        log.info("[해외주식 매도가능수량 조회] 거래소: {}, 종목: {}, 통화: {}", exchangeCode, stockCode, currencyCode);

        try {
            // 거래소코드 유효성 검증
            if (!isValidExchangeCode(exchangeCode)) {
                logError("해외주식 매도가능수량 조회", "유효하지 않은 거래소코드: " + exchangeCode);
                throw new RuntimeException("유효하지 않은 거래소코드: " + exchangeCode);
            }

            // 잔고 API를 통해 해당 종목의 매도가능수량 조회
            ForeignStockBalanceResponse balanceResponse = getBalance(exchangeCode, currencyCode);

            if (balanceResponse.getHoldings() != null) {
                for (ForeignStockBalanceResponse.HoldingData holding : balanceResponse.getHoldings()) {
                    if (stockCode.equals(holding.getOvrsPdno())) {
                        log.info("[해외주식 매도가능수량 조회 성공] 종목: {}, 매도가능수량: {}",
                                stockCode, holding.getOrdPsblQty());

                        return ForeignSellableQuantityResponse.builder()
                                .sellableQuantity(holding.getOrdPsblQty())
                                .holdingQuantity(holding.getOvrsCblcQty())
                                .stockCode(holding.getOvrsPdno())
                                .stockName(holding.getOvrsItemName())
                                .build();
                    }
                }
            }

            // 보유하지 않은 종목인 경우
            log.info("[해외주식 매도가능수량 조회] 종목: {} - 보유 없음", stockCode);
            return ForeignSellableQuantityResponse.builder()
                    .sellableQuantity("0")
                    .holdingQuantity("0")
                    .stockCode(stockCode)
                    .stockName("")
                    .build();

        } catch (Exception e) {
            logError("해외주식 매도가능수량 조회", e.getMessage(), e);
            throw new RuntimeException("해외주식 매도가능수량 조회 실패", e);
        }
    }

    /**
     * 해외주식 상품기본정보 조회
     * @param productTypeCode 상품유형코드 (512: 나스닥, 513: 뉴욕, 515: 일본, 501: 홍콩 등)
     * @param stockCode 종목코드
     * @return 해외주식 기본정보
     */
    public ForeignStockBasicInfoResponse getBasicInfo(String productTypeCode, String stockCode) {
        log.info("[해외주식 기본정보 조회] 상품유형: {}, 종목: {}", productTypeCode, stockCode);

        try {
            // 헤더 설정
            HttpHeaders headers = createHeaders("CTPF1702R");

            // URL 구성
            String url = UriComponentsBuilder.fromUriString(tokenConfig.getBaseUrl())
                    .path("/uapi/overseas-price/v1/quotations/search-info")
                    .queryParam("PRDT_TYPE_CD", productTypeCode)
                    .queryParam("PDNO", stockCode)
                    .build()
                    .toUriString();

            HttpEntity<Void> httpRequest = new HttpEntity<>(headers);

            // API 호출
            ResponseEntity<KisForeignStockBasicInfoApiResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    httpRequest,
                    KisForeignStockBasicInfoApiResponse.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                KisForeignStockBasicInfoApiResponse apiResponse = response.getBody();

                if (!"0".equals(apiResponse.getRtCd())) {
                    logError("해외주식 기본정보 조회", apiResponse.getMsg1());
                    throw new RuntimeException("해외주식 기본정보 조회 실패: " + apiResponse.getMsg1());
                }

                log.info("[해외주식 기본정보 조회 성공] 종목: {}, 종목명: {}",
                        stockCode, apiResponse.getOutput().getPrdtName());

                // 응답 데이터 변환
                return convertToBasicInfoResponse(apiResponse.getOutput(), stockCode);
            }

            throw new RuntimeException("API 응답 없음");

        } catch (RestClientException e) {
            logError("해외주식 기본정보 조회", e.getMessage(), e);
            throw new RuntimeException("해외주식 기본정보 조회 실패", e);
        }
    }

    /**
     * KIS API 응답을 클라이언트 응답으로 변환 (기본정보)
     */
    private ForeignStockBasicInfoResponse convertToBasicInfoResponse(
            KisForeignStockBasicInfoApiResponse.Output output, String stockCode) {

        return ForeignStockBasicInfoResponse.builder()
                .stockCode(stockCode)
                .stockName(output.getPrdtName())
                .shar(output.getLstgStckNum())
                .mcap("")  // 시가총액은 별도 계산 필요
                .sector(output.getPrdtClsfName())
                .parValue(output.getOvrsPapr())
                .currency(output.getTrCrcyCd())
                .exchangeCode(output.getOvrsExcgCd())
                .build();
    }

    /**
     * 해외주식 검색
     * @param keyword 검색 키워드 (종목코드 또는 종목명)
     * @param exchangeCode 거래소코드 (선택사항, null이면 전체 검색)
     * @return 검색된 주식 목록
     */
    public com.hanati.domain.foreignstock.dto.ForeignStockSearchResponse searchStocks(String keyword, String exchangeCode) {
        log.info("[해외주식 검색] 키워드: {}, 거래소: {}", keyword, exchangeCode);

        try {
            if (keyword == null || keyword.trim().isEmpty()) {
                logError("해외주식 검색", "검색 키워드가 비어있습니다");
                return com.hanati.domain.foreignstock.dto.ForeignStockSearchResponse.builder()
                        .stocks(new java.util.ArrayList<>())
                        .build();
            }

            // 주요 해외 주식 목록 (실무에서는 DB나 캐시에서 조회)
            java.util.List<StockTemplate> stockTemplates = getStockTemplates();

            // 키워드와 거래소로 필터링
            String searchKeyword = keyword.trim().toUpperCase();
            java.util.List<com.hanati.domain.foreignstock.dto.ForeignStockSearchResponse.StockInfo> results =
                    new java.util.ArrayList<>();

            for (StockTemplate template : stockTemplates) {
                // 거래소 필터링
                if (exchangeCode != null && !exchangeCode.trim().isEmpty()
                        && !template.getExchangeCode().equals(exchangeCode)) {
                    continue;
                }

                // 키워드 매칭 (종목코드 또는 종목명)
                boolean matchesCode = template.getStockCode().toUpperCase().contains(searchKeyword);
                boolean matchesName = template.getStockName().toUpperCase().contains(searchKeyword);

                if (matchesCode || matchesName) {
                    com.hanati.domain.foreignstock.dto.ForeignStockSearchResponse.StockInfo stockInfo =
                            com.hanati.domain.foreignstock.dto.ForeignStockSearchResponse.StockInfo.builder()
                            .stockCode(template.getStockCode())
                            .stockName(template.getStockName())
                            .exchangeCode(template.getExchangeCode())
                            .currency(template.getCurrency())
                            .currentPrice("")  // 실시간 가격은 별도 조회 필요
                            .build();
                    results.add(stockInfo);
                }

                // 최대 20개 결과 제한
                if (results.size() >= 20) {
                    break;
                }
            }

            log.info("[해외주식 검색 성공] 키워드: {}, 결과: {}건", keyword, results.size());

            return com.hanati.domain.foreignstock.dto.ForeignStockSearchResponse.builder()
                    .stocks(results)
                    .build();

        } catch (Exception e) {
            logError("해외주식 검색", e.getMessage(), e);
            return com.hanati.domain.foreignstock.dto.ForeignStockSearchResponse.builder()
                    .stocks(new java.util.ArrayList<>())
                    .build();
        }
    }

    /**
     * 주요 해외 주식 템플릿 목록 반환
     * (실무에서는 DB나 Redis 캐시에서 조회)
     */
    private java.util.List<StockTemplate> getStockTemplates() {
        java.util.List<StockTemplate> templates = new java.util.ArrayList<>();

        // 미국 나스닥 주요 종목
        templates.add(new StockTemplate("AAPL", "Apple Inc.", "NAS", "USD"));
        templates.add(new StockTemplate("MSFT", "Microsoft Corporation", "NAS", "USD"));
        templates.add(new StockTemplate("GOOGL", "Alphabet Inc. Class A", "NAS", "USD"));
        templates.add(new StockTemplate("AMZN", "Amazon.com Inc.", "NAS", "USD"));
        templates.add(new StockTemplate("NVDA", "NVIDIA Corporation", "NAS", "USD"));
        templates.add(new StockTemplate("META", "Meta Platforms Inc.", "NAS", "USD"));
        templates.add(new StockTemplate("TSLA", "Tesla Inc.", "NAS", "USD"));
        templates.add(new StockTemplate("NFLX", "Netflix Inc.", "NAS", "USD"));
        templates.add(new StockTemplate("AMD", "Advanced Micro Devices Inc.", "NAS", "USD"));
        templates.add(new StockTemplate("INTC", "Intel Corporation", "NAS", "USD"));

        // 미국 뉴욕 주요 종목
        templates.add(new StockTemplate("JPM", "JPMorgan Chase & Co.", "NYS", "USD"));
        templates.add(new StockTemplate("BAC", "Bank of America Corporation", "NYS", "USD"));
        templates.add(new StockTemplate("WMT", "Walmart Inc.", "NYS", "USD"));
        templates.add(new StockTemplate("V", "Visa Inc.", "NYS", "USD"));
        templates.add(new StockTemplate("JNJ", "Johnson & Johnson", "NYS", "USD"));
        templates.add(new StockTemplate("PG", "Procter & Gamble Co.", "NYS", "USD"));
        templates.add(new StockTemplate("XOM", "Exxon Mobil Corporation", "NYS", "USD"));
        templates.add(new StockTemplate("CVX", "Chevron Corporation", "NYS", "USD"));
        templates.add(new StockTemplate("KO", "Coca-Cola Company", "NYS", "USD"));
        templates.add(new StockTemplate("DIS", "Walt Disney Company", "NYS", "USD"));

        // 홍콩 주요 종목
        templates.add(new StockTemplate("00700", "Tencent Holdings Ltd.", "HKS", "HKD"));
        templates.add(new StockTemplate("09988", "Alibaba Group Holding Ltd.", "HKS", "HKD"));
        templates.add(new StockTemplate("00941", "China Mobile Ltd.", "HKS", "HKD"));

        // 일본 주요 종목
        templates.add(new StockTemplate("7203", "Toyota Motor Corporation", "TSE", "JPY"));
        templates.add(new StockTemplate("6758", "Sony Group Corporation", "TSE", "JPY"));
        templates.add(new StockTemplate("9984", "SoftBank Group Corp.", "TSE", "JPY"));

        return templates;
    }

    /**
     * 주식 템플릿 클래스
     */
    @Data
    @AllArgsConstructor
    static class StockTemplate {
        private String stockCode;
        private String stockName;
        private String exchangeCode;
        private String currency;
    }

    /**
     * 한국투자증권 해외주식 기본정보 API 응답 클래스
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class KisForeignStockBasicInfoApiResponse {
        @JsonProperty("rt_cd")
        private String rtCd;

        @JsonProperty("msg_cd")
        private String msgCd;

        @JsonProperty("msg1")
        private String msg1;

        @JsonProperty("output")
        private Output output;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        static class Output {
            @JsonProperty("std_pdno")
            private String stdPdno;

            @JsonProperty("prdt_eng_name")
            private String prdtEngName;

            @JsonProperty("natn_cd")
            private String natnCd;

            @JsonProperty("natn_name")
            private String natnName;

            @JsonProperty("tr_mket_cd")
            private String trMketCd;

            @JsonProperty("tr_mket_name")
            private String trMketName;

            @JsonProperty("ovrs_excg_cd")
            private String ovrsExcgCd;

            @JsonProperty("ovrs_excg_name")
            private String ovrsExcgName;

            @JsonProperty("tr_crcy_cd")
            private String trCrcyCd;

            @JsonProperty("ovrs_papr")
            private String ovrsPapr;

            @JsonProperty("crcy_name")
            private String crcyName;

            @JsonProperty("ovrs_stck_dvsn_cd")
            private String ovrsStckDvsnCd;

            @JsonProperty("prdt_clsf_cd")
            private String prdtClsfCd;

            @JsonProperty("prdt_clsf_name")
            private String prdtClsfName;

            @JsonProperty("lstg_stck_num")
            private String lstgStckNum;

            @JsonProperty("lstg_dt")
            private String lstgDt;

            @JsonProperty("prdt_name")
            private String prdtName;
        }
    }

    /**
     * 한국투자증권 해외주식 잔고 API 응답 클래스
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class KisForeignStockBalanceApiResponse {
        @JsonProperty("rt_cd")
        private String rtCd;

        @JsonProperty("msg_cd")
        private String msgCd;

        @JsonProperty("msg1")
        private String msg1;

        @JsonProperty("output1")
        private java.util.List<BalanceItem> output1;

        @JsonProperty("output2")
        private BalanceSummary output2;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        static class BalanceItem {
            @JsonProperty("cano")
            private String cano;

            @JsonProperty("acnt_prdt_cd")
            private String acntPrdtCd;

            @JsonProperty("prdt_type_cd")
            private String prdtTypeCd;

            @JsonProperty("ovrs_pdno")
            private String ovrsPdno;

            @JsonProperty("ovrs_item_name")
            private String ovrsItemName;

            @JsonProperty("frcr_evlu_pfls_amt")
            private String frcrEvluPflsAmt;

            @JsonProperty("evlu_pfls_rt")
            private String evluPflsRt;

            @JsonProperty("pchs_avg_pric")
            private String pchsAvgPric;

            @JsonProperty("ovrs_cblc_qty")
            private String ovrsCblcQty;

            @JsonProperty("ord_psbl_qty")
            private String ordPsblQty;

            @JsonProperty("frcr_pchs_amt1")
            private String frcrPchsAmt1;

            @JsonProperty("ovrs_stck_evlu_amt")
            private String ovrsStckEvluAmt;

            @JsonProperty("now_pric2")
            private String nowPric2;

            @JsonProperty("tr_crcy_cd")
            private String trCrcyCd;

            @JsonProperty("ovrs_excg_cd")
            private String ovrsExcgCd;

            @JsonProperty("loan_type_cd")
            private String loanTypeCd;

            @JsonProperty("loan_dt")
            private String loanDt;

            @JsonProperty("expd_dt")
            private String expdDt;
        }

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        static class BalanceSummary {
            @JsonProperty("frcr_pchs_amt1")
            private String frcrPchsAmt1;

            @JsonProperty("ovrs_rlzt_pfls_amt")
            private String ovrsRlztPflsAmt;

            @JsonProperty("ovrs_tot_pfls")
            private String ovrsTotPfls;

            @JsonProperty("rlzt_erng_rt")
            private String rlztErngRt;

            @JsonProperty("tot_evlu_pfls_amt")
            private String totEvluPflsAmt;

            @JsonProperty("tot_pftrt")
            private String totPftrt;

            @JsonProperty("frcr_buy_amt_smtl1")
            private String frcrBuyAmtSmtl1;

            @JsonProperty("ovrs_rlzt_pfls_amt2")
            private String ovrsRlztPflsAmt2;

            @JsonProperty("frcr_buy_amt_smtl2")
            private String frcrBuyAmtSmtl2;
        }
    }

    /**
     * 한국투자증권 해외주식 현재가 API 응답 클래스
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class KisForeignCurrentPriceApiResponse {
        @JsonProperty("rt_cd")
        private String rtCd;

        @JsonProperty("msg_cd")
        private String msgCd;

        @JsonProperty("msg1")
        private String msg1;

        @JsonProperty("output")
        private Output output;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        static class Output {
            @JsonProperty("rsym")
            private String rsym;

            @JsonProperty("pvol")
            private String pvol;

            @JsonProperty("open")
            private String open;

            @JsonProperty("high")
            private String high;

            @JsonProperty("low")
            private String low;

            @JsonProperty("last")
            private String last;

            @JsonProperty("base")
            private String base;

            @JsonProperty("tomv")
            private String tomv;

            @JsonProperty("pamt")
            private String pamt;

            @JsonProperty("uplp")
            private String uplp;

            @JsonProperty("dnlp")
            private String dnlp;

            @JsonProperty("h52p")
            private String h52p;

            @JsonProperty("h52d")
            private String h52d;

            @JsonProperty("l52p")
            private String l52p;

            @JsonProperty("l52d")
            private String l52d;

            @JsonProperty("perx")
            private String perx;

            @JsonProperty("pbrx")
            private String pbrx;

            @JsonProperty("epsx")
            private String epsx;

            @JsonProperty("bpsx")
            private String bpsx;

            @JsonProperty("shar")
            private String shar;

            @JsonProperty("mcap")
            private String mcap;

            @JsonProperty("curr")
            private String curr;

            @JsonProperty("zdiv")
            private String zdiv;

            @JsonProperty("vnit")
            private String vnit;

            @JsonProperty("t_xprc")
            private String tXprc;

            @JsonProperty("t_xdif")
            private String tXdif;

            @JsonProperty("t_xrat")
            private String tXrat;

            @JsonProperty("p_xprc")
            private String pXprc;

            @JsonProperty("p_xdif")
            private String pXdif;

            @JsonProperty("p_xrat")
            private String pXrat;

            @JsonProperty("t_rate")
            private String tRate;

            @JsonProperty("p_rate")
            private String pRate;

            @JsonProperty("t_xsgn")
            private String tXsgn;

            @JsonProperty("p_xsng")
            private String pXsng;

            @JsonProperty("e_ordyn")
            private String eOrdyn;

            @JsonProperty("e_hogau")
            private String eHogau;

            @JsonProperty("e_icod")
            private String eIcod;

            @JsonProperty("e_parp")
            private String eParp;

            @JsonProperty("tvol")
            private String tvol;

            @JsonProperty("tamt")
            private String tamt;

            @JsonProperty("etyp_nm")
            private String etypNm;
        }
    }

    /**
     * 한국투자증권 해외주식 기간별 시세 API 응답 클래스
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class KisForeignPeriodChartApiResponse {
        @JsonProperty("rt_cd")
        private String rtCd;

        @JsonProperty("msg_cd")
        private String msgCd;

        @JsonProperty("msg1")
        private String msg1;

        @JsonProperty("output1")
        private Output1 output1;

        @JsonProperty("output2")
        private java.util.List<ChartItem> output2;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        static class Output1 {
            @JsonProperty("rsym")
            private String rsym;

            @JsonProperty("zdiv")
            private String zdiv;

            @JsonProperty("nrec")
            private String nrec;
        }

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        static class ChartItem {
            @JsonProperty("xymd")
            private String xymd;

            @JsonProperty("open")
            private String open;

            @JsonProperty("high")
            private String high;

            @JsonProperty("low")
            private String low;

            @JsonProperty("clos")
            private String clos;

            @JsonProperty("tvol")
            private String tvol;

            @JsonProperty("tamt")
            private String tamt;
        }
    }

    /**
     * 한국투자증권 해외주식 분봉 API 응답 클래스
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class KisForeignIntradayChartApiResponse {
        @JsonProperty("rt_cd")
        private String rtCd;

        @JsonProperty("msg_cd")
        private String msgCd;

        @JsonProperty("msg1")
        private String msg1;

        @JsonProperty("output1")
        private Output1 output1;

        @JsonProperty("output2")
        private java.util.List<ChartItem> output2;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        static class Output1 {
            @JsonProperty("rsym")
            private String rsym;

            @JsonProperty("zdiv")
            private String zdiv;

            @JsonProperty("stim")
            private String stim;

            @JsonProperty("etim")
            private String etim;

            @JsonProperty("sktm")
            private String sktm;

            @JsonProperty("ektm")
            private String ektm;

            @JsonProperty("next")
            private String next;

            @JsonProperty("more")
            private String more;

            @JsonProperty("nrec")
            private String nrec;
        }

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        static class ChartItem {
            @JsonProperty("tymd")
            private String tymd;

            @JsonProperty("xymd")
            private String xymd;

            @JsonProperty("xhms")
            private String xhms;

            @JsonProperty("kymd")
            private String kymd;

            @JsonProperty("khms")
            private String khms;

            @JsonProperty("open")
            private String open;

            @JsonProperty("high")
            private String high;

            @JsonProperty("low")
            private String low;

            @JsonProperty("last")
            private String last;

            @JsonProperty("evol")
            private String evol;

            @JsonProperty("eamt")
            private String eamt;
        }
    }
}

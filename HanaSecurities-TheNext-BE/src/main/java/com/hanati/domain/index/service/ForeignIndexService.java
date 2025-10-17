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

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ForeignIndexService {

    private final TokenConfig tokenConfig;
    private final TokenService tokenService;
    private final RestTemplate restTemplate;

    // 해외 지수 코드 매핑 (4개 지수)
    private static final Map<String, IndexInfo> FOREIGN_INDEX_MAP = new HashMap<>() {{
        // 미국 지수 (1개)
        put("SPX", new IndexInfo("N", "SPX", "S&P500", "US"));

        // 중국 지수 (1개)
        put("SHANG", new IndexInfo("N", "SHANG", "상해종합", "CN"));

        // 유럽 지수 (1개)
        put("SX5E", new IndexInfo("N", "SX5E", "유로STOXX50", "EU"));

        // 홍콩 지수 (1개)
        put("HSCE", new IndexInfo("N", "HSCE", "홍콩H지수", "HK"));
    }};

    /**
     * 해외 지수 분봉 데이터 조회
     * @param indexType 지수 타입 (SPX, SHANG, SX5E, HSCE)
     * @return 해외 지수 분봉 데이터
     */
    public ForeignIndexTimePriceResponse getForeignIndexTimePrice(String indexType) {
        String upperIndexType = indexType.toUpperCase();
        IndexInfo indexInfo = FOREIGN_INDEX_MAP.get(upperIndexType);

        if (indexInfo == null) {
            throw new IllegalArgumentException("Invalid foreign index type: " + indexType);
        }

        log.info("해외 지수 {} 분봉 데이터 조회 시작", indexType);

        try {
            // 먼저 장중 데이터 조회 시도 (FID_HOUR_CLS_CODE = 0)
            log.debug("해외 지수 {} 장중 데이터 조회 시도 (FID_HOUR_CLS_CODE=0)", indexType);
            KisForeignIndexTimeApiResponse apiResponse = fetchIndexData(indexInfo, "0");

            // 응답 확인 및 재시도 로직
            if (apiResponse != null && "0".equals(apiResponse.getRtCd())) {
                // output2가 비어있으면 장외 시간으로 판단
                if (apiResponse.getOutput2() == null || apiResponse.getOutput2().isEmpty()) {
                    log.info("해외 지수 {} 장중 데이터 없음, 장외 데이터 조회 시도 (FID_HOUR_CLS_CODE=1)", indexType);

                    // 장외 데이터 조회 시도
                    KisForeignIndexTimeApiResponse closedResponse = fetchIndexData(indexInfo, "1");

                    if (closedResponse != null && "0".equals(closedResponse.getRtCd())) {
                        // 장외 데이터도 비어있다면 원래 응답 사용
                        if (closedResponse.getOutput2() != null && !closedResponse.getOutput2().isEmpty()) {
                            log.info("해외 지수 {} 장외 데이터 {}건 조회 성공", indexType, closedResponse.getOutput2().size());
                            apiResponse = closedResponse;
                        } else {
                            log.warn("해외 지수 {} 장외 데이터도 없음", indexType);
                        }
                    }
                } else {
                    log.info("해외 지수 {} 장중 데이터 {}건 조회 성공", indexType, apiResponse.getOutput2().size());
                }
            }

            // 최종 응답 처리
            if (apiResponse != null && "0".equals(apiResponse.getRtCd())) {
                // 응답 데이터 변환
                return convertToForeignIndexTimePriceResponse(apiResponse, indexInfo, upperIndexType);
            } else {
                String errorMsg = apiResponse != null ? apiResponse.getMsg1() : "Unknown error";
                log.error("해외 지수 {} 분봉 조회 실패: {}", indexType, errorMsg);
                throw new RuntimeException("해외 지수 분봉 조회 실패: " + errorMsg);
            }

        } catch (RestClientException e) {
            log.error("해외 지수 {} 분봉 조회 실패: {}", indexType, e.getMessage());
            throw new RuntimeException("해외 지수 " + indexType + " 분봉 조회 실패", e);
        }
    }

    /**
     * 지수 데이터 조회 (장중/장마감 구분)
     * @param indexInfo 지수 정보
     * @param hourClsCode 시간 구분 코드 (0: 장중, 1: 장마감)
     * @return KIS API 응답
     */
    private KisForeignIndexTimeApiResponse fetchIndexData(IndexInfo indexInfo, String hourClsCode) {
        try {
            // 헤더 설정
            HttpHeaders headers = createHeaders("FHKST03030200");  // 해외지수분봉조회 거래ID

            // URL 구성 - UriComponentsBuilder 사용
            String url = UriComponentsBuilder.fromUriString(tokenConfig.getBaseUrl())
                    .path("/uapi/overseas-price/v1/quotations/inquire-time-indexchartprice")
                    .queryParam("FID_COND_MRKT_DIV_CODE", indexInfo.marketDivCode)
                    .queryParam("FID_INPUT_ISCD", indexInfo.symbolCode)
                    .queryParam("FID_HOUR_CLS_CODE", "")
                    .queryParam("FID_PW_DATA_INCU_YN", "Y")
                    .build(true)
                    .toUriString();

            log.debug("API 요청 URL: {}", url);
            log.debug("Index Info - Market Div Code: {}, Symbol Code: {}, Hour Cls Code: {}",
                    indexInfo.marketDivCode, indexInfo.symbolCode, hourClsCode);

            HttpEntity<Void> request = new HttpEntity<>(headers);

            // API 호출
            ResponseEntity<KisForeignIndexTimeApiResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    request,
                    KisForeignIndexTimeApiResponse.class
            );

            log.debug("Response Status: {}", response.getStatusCode());

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                KisForeignIndexTimeApiResponse body = response.getBody();
                log.debug("API 응답 - rt_cd: {}, msg_cd: {}, msg1: {}, output2 size: {}",
                    body.getRtCd(),
                    body.getMsgCd(),
                    body.getMsg1(),
                    body.getOutput2() != null ? body.getOutput2().size() : "null");

                // output1 정보도 로그
                if (body.getOutput1() != null) {
                    log.debug("output1 - 지수명: {}, 현재가: {}",
                        body.getOutput1().getHtsKorIsnm(),
                        body.getOutput1().getOvrsNmixPrpr());
                }

                return body;
            }

            log.warn("응답 상태 이상 - Status: {}", response.getStatusCode());
            return null;
        } catch (RestClientException e) {
            log.error("지수 데이터 조회 실패 (symbolCode={}, hourClsCode={}): {}",
                    indexInfo.symbolCode, hourClsCode, e.getMessage());
            throw e;
        }
    }

    /**
     * API 요청 헤더 생성
     */
    private HttpHeaders createHeaders(String trId) {
        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.set("authorization", "Bearer " + tokenService.getRestApiAccessToken());
        headers.set("appkey", tokenConfig.getAppKey());
        headers.set("appsecret", tokenConfig.getAppSecret());
        headers.set("tr_id", trId);
        headers.set("custtype", "P");  // 고객타입 (개인)

        return headers;
    }

    /**
     * KIS API 응답을 ForeignIndexTimePriceResponse로 변환
     */
    private ForeignIndexTimePriceResponse convertToForeignIndexTimePriceResponse(
            KisForeignIndexTimeApiResponse apiResponse, IndexInfo indexInfo, String indexType) {

        List<ForeignIndexTimePriceResponse.TimePrice> timePrices = new ArrayList<>();

        if (apiResponse.getOutput2() != null && !apiResponse.getOutput2().isEmpty()) {
            // output1에서 전일 대비 정보 가져오기
            KisForeignIndexTimeApiResponse.Output1 output1 = apiResponse.getOutput1();
            String changePrice = output1 != null ? output1.getOvrsNmixPrdyVrss() : "0";
            String changeRate = output1 != null ? output1.getPrdyCtrt() : "0";
            String changeSign = output1 != null ? output1.getPrdyVrssSign() : "3";

            // 최신 데이터가 먼저 오는 경우 역순으로 정렬
            List<KisForeignIndexTimeApiResponse.TimeData> dataList = apiResponse.getOutput2();
            Collections.reverse(dataList);  // 시간 순서대로 정렬

            for (KisForeignIndexTimeApiResponse.TimeData timeData : dataList) {
                ForeignIndexTimePriceResponse.TimePrice timePrice = ForeignIndexTimePriceResponse.TimePrice.builder()
                        .time(timeData.getStckCntgHour())
                        .price(timeData.getOptnPrpr())
                        .changePrice(changePrice)  // output1의 전일 대비 사용
                        .changeRate(changeRate)    // output1의 전일 대비율 사용
                        .changeSign(changeSign)    // output1의 부호 사용
                        .volume(timeData.getCntgVol())
                        .tradingValue("0")  // 거래대금 정보 없음
                        .build();
                timePrices.add(timePrice);
            }
            log.info("해외 지수 {} 분봉 데이터 {}건 변환 완료", indexType, timePrices.size());
        } else {
            log.warn("해외 지수 {} 분봉 데이터가 없습니다", indexType);
        }

        // output1에서 지수명 가져오기
        String indexName = indexInfo.koreanName;
        if (apiResponse.getOutput1() != null && apiResponse.getOutput1().getHtsKorIsnm() != null) {
            indexName = apiResponse.getOutput1().getHtsKorIsnm();
        }

        return ForeignIndexTimePriceResponse.builder()
                .indexSymbol(indexType)
                .indexName(indexName)
                .countryCode(indexInfo.countryCode)
                .timePrices(timePrices)
                .build();
    }

    /**
     * 지수 정보를 담는 내부 클래스
     */
    private static class IndexInfo {
        String marketDivCode;  // 시장 구분 코드 (P: 미국, W: 기타)
        String symbolCode;     // 심볼 코드
        String koreanName;     // 한글명
        String countryCode;    // 국가 코드

        IndexInfo(String marketDivCode, String symbolCode, String koreanName, String countryCode) {
            this.marketDivCode = marketDivCode;
            this.symbolCode = symbolCode;
            this.koreanName = koreanName;
            this.countryCode = countryCode;
        }
    }
}
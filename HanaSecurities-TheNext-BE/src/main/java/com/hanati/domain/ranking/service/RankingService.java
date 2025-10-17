package com.hanati.domain.ranking.service;

import com.hanati.common.config.TokenConfig;
import com.hanati.common.service.TokenService;
import com.hanati.domain.ranking.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RankingService {

    private final TokenConfig tokenConfig;
    private final TokenService tokenService;
    private final RestTemplate restTemplate;

    // 랭킹 타입 상수
    public static final String RANKING_VOLUME = "VOLUME";
    public static final String RANKING_TRADING_VALUE = "TRADING_VALUE";
    public static final String RANKING_RISE = "RISE";
    public static final String RANKING_FALL = "FALL";

    /**
     * 통합 랭킹 조회
     * @param rankingType 랭킹 타입 (VOLUME, TRADING_VALUE, RISE, FALL)
     * @param marketCode 시장 코드 (J:KRX, NX:NXT)
     * @return 랭킹 응답
     */
    public StockRankingResponse getStockRanking(String rankingType, String marketCode) {
        log.info("주식 랭킹 조회 시작 - 타입: {}, 시장: {}", rankingType, marketCode);

        switch (rankingType.toUpperCase()) {
            case RANKING_VOLUME:
                return getVolumeRanking(marketCode, "0");  // 평균거래량
            case RANKING_TRADING_VALUE:
                return getVolumeRanking(marketCode, "3");  // 거래금액순
            case RANKING_RISE:
                return getFluctuationRanking(marketCode, "0");  // 상승률순
            case RANKING_FALL:
                return getFluctuationRanking(marketCode, "1");  // 하락률순
            default:
                throw new IllegalArgumentException("Invalid ranking type: " + rankingType);
        }
    }

    /**
     * 거래량/거래대금 순위 조회
     */
    private StockRankingResponse getVolumeRanking(String marketCode, String belongCode) {
        try {

            // 헤더 설정
            HttpHeaders headers = createVolumeRankHeaders();

            // URL 구성
            String url = UriComponentsBuilder.fromUriString(tokenConfig.getBaseUrl())
                    .path("/uapi/domestic-stock/v1/quotations/volume-rank")
                    .queryParam("FID_COND_MRKT_DIV_CODE", marketCode)
                    .queryParam("FID_COND_SCR_DIV_CODE", "20171")
                    .queryParam("FID_INPUT_ISCD", "0000")  // 전체
                    .queryParam("FID_DIV_CLS_CODE", "0")  // 전체
                    .queryParam("FID_BLNG_CLS_CODE", belongCode)  // 0:평균거래량, 3:거래금액순
                    .queryParam("FID_TRGT_CLS_CODE", "111111111")
                    .queryParam("FID_TRGT_EXLS_CLS_CODE", "0000000000")
                    .queryParam("FID_INPUT_PRICE_1", "")
                    .queryParam("FID_INPUT_PRICE_2", "")
                    .queryParam("FID_VOL_CNT", "")
                    .queryParam("FID_INPUT_DATE_1", "")
                    .build()
                    .toUriString();

            HttpEntity<Void> httpRequest = new HttpEntity<>(headers);

            // API 호출
            ResponseEntity<KisVolumeRankApiResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    httpRequest,
                    KisVolumeRankApiResponse.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                KisVolumeRankApiResponse apiResponse = response.getBody();

                if (!"0".equals(apiResponse.getRtCd())) {
                    log.error("거래량 순위 조회 실패: {}", apiResponse.getMsg1());
                    throw new RuntimeException("거래량 순위 조회 실패: " + apiResponse.getMsg1());
                }

                // 응답 데이터 변환
                String rankingType = "0".equals(belongCode) ? RANKING_VOLUME : RANKING_TRADING_VALUE;
                return convertVolumeToStockRanking(apiResponse, rankingType);
            }

        } catch (RestClientException e) {
            log.error("거래량/거래대금 순위 조회 실패: {}", e.getMessage());
            throw new RuntimeException("거래량/거래대금 순위 조회 실패", e);
        }

        throw new RuntimeException("거래량/거래대금 순위 조회 실패");
    }

    /**
     * 등락률 순위 조회
     */
    private StockRankingResponse getFluctuationRanking(String marketCode, String sortCode) {
        try {
            // 헤더 설정
            HttpHeaders headers = createFluctuationRankHeaders();

            // URL 구성
            String url = UriComponentsBuilder.fromUriString(tokenConfig.getBaseUrl())
                    .path("/uapi/domestic-stock/v1/ranking/fluctuation")
                    .queryParam("fid_rsfl_rate2", "")
                    .queryParam("fid_cond_mrkt_div_code", marketCode)
                    .queryParam("fid_cond_scr_div_code", "20170")
                    .queryParam("fid_input_iscd", "0000")  // 전체
                    .queryParam("fid_rank_sort_cls_code", sortCode)  // 0:상승률순, 1:하락률순
                    .queryParam("fid_input_cnt_1", "0")  // 전체
                    .queryParam("fid_prc_cls_code", "1")  // 전체
                    .queryParam("fid_input_price_1", "")
                    .queryParam("fid_input_price_2", "")
                    .queryParam("fid_vol_cnt", "")
                    .queryParam("fid_trgt_cls_code", "0")
                    .queryParam("fid_trgt_exls_cls_code", "0")
                    .queryParam("fid_div_cls_code", "0")
                    .queryParam("fid_rsfl_rate1", "")
                    .build()
                    .toUriString();

            HttpEntity<Void> httpRequest = new HttpEntity<>(headers);

            // API 호출
            ResponseEntity<KisFluctuationRankApiResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    httpRequest,
                    KisFluctuationRankApiResponse.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                KisFluctuationRankApiResponse apiResponse = response.getBody();

                if (!"0".equals(apiResponse.getRtCd())) {
                    log.error("등락률 순위 조회 실패: {}", apiResponse.getMsg1());
                    throw new RuntimeException("등락률 순위 조회 실패: " + apiResponse.getMsg1());
                }

                // 응답 데이터 변환
                String rankingType = "0".equals(sortCode) ? RANKING_RISE : RANKING_FALL;
                return convertFluctuationToStockRanking(apiResponse, rankingType);
            }

        } catch (RestClientException e) {
            log.error("등락률 순위 조회 실패: {}", e.getMessage());
            throw new RuntimeException("등락률 순위 조회 실패", e);
        }

        throw new RuntimeException("등락률 순위 조회 실패");
    }


    /**
     * 거래량 순위 API 헤더 생성
     */
    private HttpHeaders createVolumeRankHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("authorization", "Bearer " + tokenService.getRestApiAccessToken());
        headers.set("appkey", tokenConfig.getAppKey());
        headers.set("appsecret", tokenConfig.getAppSecret());
        headers.set("tr_id", "FHPST01710000");  // 거래량순위 조회 거래ID
        headers.set("custtype", "P");  // 개인

        return headers;
    }

    /**
     * 등락률 순위 API 헤더 생성
     */
    private HttpHeaders createFluctuationRankHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("authorization", "Bearer " + tokenService.getRestApiAccessToken());
        headers.set("appkey", tokenConfig.getAppKey());
        headers.set("appsecret", tokenConfig.getAppSecret());
        headers.set("tr_id", "FHPST01700000");  // 등락률순위 조회 거래ID
        headers.set("custtype", "P");  // 개인

        return headers;
    }

    /**
     * 거래량/거래대금 API 응답을 통합 랭킹 응답으로 변환
     */
    private StockRankingResponse convertVolumeToStockRanking(KisVolumeRankApiResponse apiResponse, String rankingType) {
        List<StockRankingResponse.StockRankItem> stocks = new ArrayList<>();

        if (apiResponse.getOutput() != null && !apiResponse.getOutput().isEmpty()) {
            for (KisVolumeRankApiResponse.VolumeRankData data : apiResponse.getOutput()) {
                StockRankingResponse.StockRankItem stock = StockRankingResponse.StockRankItem.builder()
                        .rank(data.getDataRank())
                        .stockName(data.getHtsKorIsnm())
                        .stockCode(data.getMkscShrnIscd())
                        .currentPrice(data.getStckPrpr())
                        .changeSign(data.getPrdyVrssSign())
                        .changePrice(data.getPrdyVrss())
                        .changeRate(data.getPrdyCtrt())
                        .volume(data.getAcmlVol())
                        .tradingValue(data.getAcmlTrPbmn())
                        .averageVolume(data.getAvrgVol())
                        .volumeIncreaseRate(data.getVolInrt())
                        .volumeTurnoverRate(data.getVolTnrt())
                        .averageTradingValue(data.getAvrgTrPbmn())
                        .tradingValueTurnoverRate(data.getTrPbmnTnrt())
                        .build();
                stocks.add(stock);
            }
            log.info("{} 랭킹 {}개 종목 조회 완료", rankingType, stocks.size());
        }

        return StockRankingResponse.builder()
                .rankingType(rankingType)
                .stocks(stocks)
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();
    }

    /**
     * 등락률 API 응답을 통합 랭킹 응답으로 변환
     */
    private StockRankingResponse convertFluctuationToStockRanking(KisFluctuationRankApiResponse apiResponse, String rankingType) {
        List<StockRankingResponse.StockRankItem> stocks = new ArrayList<>();

        if (apiResponse.getOutput() != null && !apiResponse.getOutput().isEmpty()) {
            for (KisFluctuationRankApiResponse.FluctuationRankData data : apiResponse.getOutput()) {
                StockRankingResponse.StockRankItem stock = StockRankingResponse.StockRankItem.builder()
                        .rank(data.getDataRank())
                        .stockName(data.getHtsKorIsnm())
                        .stockCode(data.getStckShrnIscd())
                        .currentPrice(data.getStckPrpr())
                        .changeSign(data.getPrdyVrssSign())
                        .changePrice(data.getPrdyVrss())
                        .changeRate(data.getPrdyCtrt())
                        .volume(data.getAcmlVol())
                        .highPrice(data.getStckHgpr())
                        .lowPrice(data.getStckLwpr())
                        .openPriceChange(data.getOprcVrssPrpr())
                        .openPriceChangeRate(data.getOprcVrssPrprRate())
                        .highPriceRatio(data.getHgprVrssPrprRate())
                        .lowPriceRatio(data.getLwprVrssPrprRate())
                        .build();
                stocks.add(stock);
            }
            log.info("{} 랭킹 {}개 종목 조회 완료", rankingType, stocks.size());
        }

        return StockRankingResponse.builder()
                .rankingType(rankingType)
                .stocks(stocks)
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();
    }
}
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
public class ForeignRankingService {

    private final TokenConfig tokenConfig;
    private final TokenService tokenService;
    private final RestTemplate restTemplate;

    // 랭킹 타입 상수
    public static final String RANKING_VOLUME = "VOLUME";
    public static final String RANKING_TRADING_VALUE = "TRADING_VALUE";
    public static final String RANKING_RISE = "RISE";
    public static final String RANKING_FALL = "FALL";

    /**
     * 해외 주식 통합 랭킹 조회
     * @param rankingType 랭킹 타입 (VOLUME, TRADING_VALUE, RISE, FALL)
     * @param exchangeCode 거래소 코드 (NYS, NAS, AMS, HKS, SHS, SZS, HSX, HNX, TSE)
     * @return 랭킹 응답
     */
    public ForeignStockRankingResponse getForeignStockRanking(String rankingType, String exchangeCode) {
        log.info("해외 주식 랭킹 조회 시작 - 타입: {}, 거래소: {}", rankingType, exchangeCode);

        switch (rankingType.toUpperCase()) {
            case RANKING_VOLUME:
                return getVolumeRanking(exchangeCode);
            case RANKING_TRADING_VALUE:
                return getTradingValueRanking(exchangeCode);
            case RANKING_RISE:
                return getUpDownRanking(exchangeCode, "1");  // 상승율
            case RANKING_FALL:
                return getUpDownRanking(exchangeCode, "0");  // 하락율
            default:
                throw new IllegalArgumentException("Invalid ranking type: " + rankingType);
        }
    }

    /**
     * 해외 주식 거래량 순위 조회
     */
    private ForeignStockRankingResponse getVolumeRanking(String exchangeCode) {
        try {
            // 먼저 당일(NDAY=0) 데이터 조회 시도
            KisForeignVolumeRankApiResponse apiResponse = fetchVolumeRanking(exchangeCode, "0");

            // 당일 데이터가 없으면 전일(NDAY=1) 데이터 조회
            if (apiResponse.getOutput2() == null || apiResponse.getOutput2().isEmpty()) {
                log.info("해외 거래량 당일 데이터 없음. 전일 데이터 조회 시도 - 거래소: {}", exchangeCode);
                apiResponse = fetchVolumeRanking(exchangeCode, "1");
            }

            return convertVolumeToForeignRanking(apiResponse, exchangeCode);

        } catch (RestClientException e) {
            log.error("해외 거래량 순위 조회 실패: {}", e.getMessage());
            throw new RuntimeException("해외 거래량 순위 조회 실패", e);
        }
    }

    /**
     * 해외 주식 거래량 API 호출
     */
    private KisForeignVolumeRankApiResponse fetchVolumeRanking(String exchangeCode, String nday) {
        // 헤더 설정
        HttpHeaders headers = createHeaders("HHDFS76310010");

        // URL 구성
        String url = UriComponentsBuilder.fromUriString(tokenConfig.getBaseUrl())
                .path("/uapi/overseas-stock/v1/ranking/trade-vol")
                .queryParam("KEYB", "")
                .queryParam("AUTH", "")
                .queryParam("EXCD", exchangeCode)
                .queryParam("NDAY", nday)  // 0:당일, 1:전일
                .queryParam("PRC1", "")
                .queryParam("PRC2", "")
                .queryParam("VOL_RANG", "0")  // 전체
                .build()
                .toUriString();

        HttpEntity<Void> httpRequest = new HttpEntity<>(headers);

        // API 호출
        ResponseEntity<KisForeignVolumeRankApiResponse> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                httpRequest,
                KisForeignVolumeRankApiResponse.class
        );

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            KisForeignVolumeRankApiResponse apiResponse = response.getBody();

            if (!"0".equals(apiResponse.getRtCd())) {
                log.error("해외 거래량 순위 조회 실패: {}", apiResponse.getMsg1());
                throw new RuntimeException("해외 거래량 순위 조회 실패: " + apiResponse.getMsg1());
            }

            return apiResponse;
        }

        throw new RuntimeException("해외 거래량 순위 조회 실패");
    }

    /**
     * 해외 주식 거래대금 순위 조회
     */
    private ForeignStockRankingResponse getTradingValueRanking(String exchangeCode) {
        try {
            // 먼저 당일(NDAY=0) 데이터 조회 시도
            KisForeignTradingValueRankApiResponse apiResponse = fetchTradingValueRanking(exchangeCode, "0");

            // 당일 데이터가 없으면 전일(NDAY=1) 데이터 조회
            if (apiResponse.getOutput2() == null || apiResponse.getOutput2().isEmpty()) {
                log.info("해외 거래대금 당일 데이터 없음. 전일 데이터 조회 시도 - 거래소: {}", exchangeCode);
                apiResponse = fetchTradingValueRanking(exchangeCode, "1");
            }

            return convertTradingValueToForeignRanking(apiResponse, exchangeCode);

        } catch (RestClientException e) {
            log.error("해외 거래대금 순위 조회 실패: {}", e.getMessage());
            throw new RuntimeException("해외 거래대금 순위 조회 실패", e);
        }
    }

    /**
     * 해외 주식 거래대금 API 호출
     */
    private KisForeignTradingValueRankApiResponse fetchTradingValueRanking(String exchangeCode, String nday) {
        // 헤더 설정
        HttpHeaders headers = createHeaders("HHDFS76320010");

        // URL 구성
        String url = UriComponentsBuilder.fromUriString(tokenConfig.getBaseUrl())
                .path("/uapi/overseas-stock/v1/ranking/trade-pbmn")
                .queryParam("KEYB", "")
                .queryParam("AUTH", "")
                .queryParam("EXCD", exchangeCode)
                .queryParam("NDAY", nday)  // 0:당일, 1:전일
                .queryParam("VOL_RANG", "0")  // 전체
                .queryParam("PRC1", "")
                .queryParam("PRC2", "")
                .build()
                .toUriString();

        HttpEntity<Void> httpRequest = new HttpEntity<>(headers);

        // API 호출
        ResponseEntity<KisForeignTradingValueRankApiResponse> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                httpRequest,
                KisForeignTradingValueRankApiResponse.class
        );

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            KisForeignTradingValueRankApiResponse apiResponse = response.getBody();

            if (!"0".equals(apiResponse.getRtCd())) {
                log.error("해외 거래대금 순위 조회 실패: {}", apiResponse.getMsg1());
                throw new RuntimeException("해외 거래대금 순위 조회 실패: " + apiResponse.getMsg1());
            }

            return apiResponse;
        }

        throw new RuntimeException("해외 거래대금 순위 조회 실패");
    }

    /**
     * 해외 주식 상승율/하락율 순위 조회
     */
    private ForeignStockRankingResponse getUpDownRanking(String exchangeCode, String gubn) {
        try {
            // 먼저 당일(NDAY=0) 데이터 조회 시도
            KisForeignUpDownRankApiResponse apiResponse = fetchUpDownRanking(exchangeCode, gubn, "0");

            // 당일 데이터가 없으면 전일(NDAY=1) 데이터 조회
            if (apiResponse.getOutput2() == null || apiResponse.getOutput2().isEmpty()) {
                log.info("해외 {} 당일 데이터 없음. 전일 데이터 조회 시도 - 거래소: {}",
                        "1".equals(gubn) ? "상승율" : "하락율", exchangeCode);
                apiResponse = fetchUpDownRanking(exchangeCode, gubn, "1");
            }

            String rankingType = "1".equals(gubn) ? RANKING_RISE : RANKING_FALL;
            return convertUpDownToForeignRanking(apiResponse, exchangeCode, rankingType);

        } catch (RestClientException e) {
            log.error("해외 상승율/하락율 순위 조회 RestClient 에러: ", e);
            throw new RuntimeException("해외 상승율/하락율 순위 조회 실패", e);
        } catch (Exception e) {
            log.error("해외 상승율/하락율 순위 조회 예외 발생: ", e);
            throw new RuntimeException("해외 상승율/하락율 순위 조회 실패", e);
        }
    }

    /**
     * 해외 주식 상승율/하락율 API 호출
     */
    private KisForeignUpDownRankApiResponse fetchUpDownRanking(String exchangeCode, String gubn, String nday) {
        // 헤더 설정
        HttpHeaders headers = createHeaders("HHDFS76290000");

        // URL 구성
        String url = UriComponentsBuilder.fromUriString(tokenConfig.getBaseUrl())
                .path("/uapi/overseas-stock/v1/ranking/updown-rate")
                .queryParam("KEYB", "")
                .queryParam("AUTH", "")
                .queryParam("EXCD", exchangeCode)
                .queryParam("GUBN", gubn)  // 0:하락율, 1:상승율
                .queryParam("NDAY", nday)  // 0:당일, 1:전일
                .queryParam("VOL_RANG", "0")  // 전체
                .build()
                .toUriString();

        log.debug("해외 상승율/하락율 API 호출 URL: {}, NDAY: {}", url, nday);

        HttpEntity<Void> httpRequest = new HttpEntity<>(headers);

        // API 호출
        ResponseEntity<KisForeignUpDownRankApiResponse> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                httpRequest,
                KisForeignUpDownRankApiResponse.class
        );

        log.debug("API 응답 상태코드: {}", response.getStatusCode());

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            KisForeignUpDownRankApiResponse apiResponse = response.getBody();

            log.debug("API 응답 rt_cd: {}, msg1: {}", apiResponse.getRtCd(), apiResponse.getMsg1());
            log.debug("output2 size: {}", apiResponse.getOutput2() != null ? apiResponse.getOutput2().size() : "null");

            if (!"0".equals(apiResponse.getRtCd())) {
                log.error("해외 상승율/하락율 순위 조회 실패: {}", apiResponse.getMsg1());
                throw new RuntimeException("해외 상승율/하락율 순위 조회 실패: " + apiResponse.getMsg1());
            }

            return apiResponse;
        }

        throw new RuntimeException("해외 상승율/하락율 순위 조회 실패");
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
        headers.set("tr_id", trId);
        headers.set("custtype", "P");  // 개인

        return headers;
    }

    /**
     * 거래량 API 응답을 해외 랭킹 응답으로 변환
     */
    private ForeignStockRankingResponse convertVolumeToForeignRanking(
            KisForeignVolumeRankApiResponse apiResponse, String exchangeCode) {

        List<ForeignStockRankingResponse.ForeignStockRankItem> stocks = new ArrayList<>();

        if (apiResponse.getOutput2() != null && !apiResponse.getOutput2().isEmpty()) {
            for (KisForeignVolumeRankApiResponse.VolumeRankData data : apiResponse.getOutput2()) {
                ForeignStockRankingResponse.ForeignStockRankItem stock =
                        ForeignStockRankingResponse.ForeignStockRankItem.builder()
                                .rank(data.getRank())
                                .rsym(data.getRsym())
                                .stockCode(data.getSymb())
                                .stockName(data.getName())
                                .englishName(data.getEname())
                                .currentPrice(data.getLast())
                                .changeSign(data.getSign())
                                .changePrice(data.getDiff())
                                .changeRate(data.getRate())
                                .volume(data.getTvol())
                                .tradingValue(data.getTamt())
                                .askPrice(data.getPask())
                                .bidPrice(data.getPbid())
                                .averageVolume(data.getATvol())
                                .tradeable(data.getEOrdyn())
                                .build();
                stocks.add(stock);
            }
            log.info("해외 거래량 랭킹 {}개 종목 조회 완료", stocks.size());
        }

        return ForeignStockRankingResponse.builder()
                .rankingType(RANKING_VOLUME)
                .exchangeCode(exchangeCode)
                .stocks(stocks)
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();
    }

    /**
     * 거래대금 API 응답을 해외 랭킹 응답으로 변환
     */
    private ForeignStockRankingResponse convertTradingValueToForeignRanking(
            KisForeignTradingValueRankApiResponse apiResponse, String exchangeCode) {

        List<ForeignStockRankingResponse.ForeignStockRankItem> stocks = new ArrayList<>();

        if (apiResponse.getOutput2() != null && !apiResponse.getOutput2().isEmpty()) {
            for (KisForeignTradingValueRankApiResponse.TradingValueRankData data : apiResponse.getOutput2()) {
                ForeignStockRankingResponse.ForeignStockRankItem stock =
                        ForeignStockRankingResponse.ForeignStockRankItem.builder()
                                .rank(data.getRank())
                                .rsym(data.getRsym())
                                .stockCode(data.getSymb())
                                .stockName(data.getName())
                                .englishName(data.getEname())
                                .currentPrice(data.getLast())
                                .changeSign(data.getSign())
                                .changePrice(data.getDiff())
                                .changeRate(data.getRate())
                                .volume(data.getTvol())
                                .tradingValue(data.getTamt())
                                .askPrice(data.getPask())
                                .bidPrice(data.getPbid())
                                .averageTradingValue(data.getATamt())
                                .tradeable(data.getEOrdyn())
                                .build();
                stocks.add(stock);
            }
            log.info("해외 거래대금 랭킹 {}개 종목 조회 완료", stocks.size());
        }

        return ForeignStockRankingResponse.builder()
                .rankingType(RANKING_TRADING_VALUE)
                .exchangeCode(exchangeCode)
                .stocks(stocks)
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();
    }

    /**
     * 상승율/하락율 API 응답을 해외 랭킹 응답으로 변환
     */
    private ForeignStockRankingResponse convertUpDownToForeignRanking(
            KisForeignUpDownRankApiResponse apiResponse, String exchangeCode, String rankingType) {

        List<ForeignStockRankingResponse.ForeignStockRankItem> stocks = new ArrayList<>();

        if (apiResponse.getOutput2() != null && !apiResponse.getOutput2().isEmpty()) {
            for (KisForeignUpDownRankApiResponse.UpDownRankData data : apiResponse.getOutput2()) {
                ForeignStockRankingResponse.ForeignStockRankItem stock =
                        ForeignStockRankingResponse.ForeignStockRankItem.builder()
                                .rank(data.getRank())
                                .rsym(data.getRsym())
                                .stockCode(data.getSymb())
                                .stockName(data.getName())
                                .englishName(data.getEname())
                                .currentPrice(data.getLast())
                                .changeSign(data.getSign())
                                .changePrice(data.getDiff())
                                .changeRate(data.getRate())
                                .volume(data.getTvol())
                                .askPrice(data.getPask())
                                .bidPrice(data.getPbid())
                                .basePrice(data.getNBase())
                                .baseDiff(data.getNDiff())
                                .baseRate(data.getNRate())
                                .tradeable(data.getEOrdyn())
                                .build();
                stocks.add(stock);
            }
            log.info("해외 {} 랭킹 {}개 종목 조회 완료", rankingType, stocks.size());
        }

        return ForeignStockRankingResponse.builder()
                .rankingType(rankingType)
                .exchangeCode(exchangeCode)
                .stocks(stocks)
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();
    }
}
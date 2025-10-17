import type {
  ForeignStockBasicInfoResponse,
  ForeignStockSearchResponse,
  ForeignCurrentPriceResponse,
  ForeignIntradayChartResponse,
  ForeignPeriodChartResponse
} from '../types/foreignStock.types';

const BASE_URL = '/api/foreign-stock';

export const foreignStockApi = {
  /**
   * 해외 주식 기본 정보 조회
   * @param productTypeCode 상품유형코드 (512: 해외주식)
   * @param stockCode 종목코드
   */
  getBasicInfo: async (productTypeCode: string, stockCode: string): Promise<ForeignStockBasicInfoResponse> => {
    const response = await fetch(
      `${BASE_URL}/basic-info?productTypeCode=${productTypeCode}&stockCode=${stockCode}`
    );
    if (!response.ok) {
      throw new Error('Failed to fetch foreign stock basic info');
    }
    return response.json();
  },

  /**
   * 해외 주식 종목 검색
   * @param keyword 검색 키워드 (종목명 또는 종목코드)
   * @param exchangeCode 거래소코드 (선택, NAS/NYS/HKS/TSE)
   */
  searchStocks: async (keyword: string, exchangeCode?: string): Promise<ForeignStockSearchResponse> => {
    const url = exchangeCode
      ? `${BASE_URL}/search?keyword=${encodeURIComponent(keyword)}&exchangeCode=${exchangeCode}`
      : `${BASE_URL}/search?keyword=${encodeURIComponent(keyword)}`;

    const response = await fetch(url);
    if (!response.ok) {
      throw new Error('Failed to search foreign stocks');
    }
    return response.json();
  },

  /**
   * 해외 주식 현재가 조회
   * @param exchangeCode 거래소코드 (NAS/NYS/HKS/TSE)
   * @param stockCode 종목코드
   */
  getCurrentPrice: async (exchangeCode: string, stockCode: string): Promise<ForeignCurrentPriceResponse> => {
    const response = await fetch(
      `${BASE_URL}/current-price?exchangeCode=${exchangeCode}&stockCode=${stockCode}`
    );
    if (!response.ok) {
      throw new Error('Failed to fetch foreign stock current price');
    }
    return response.json();
  },

  /**
   * 해외 주식 분봉 조회
   * @param exchangeCode 거래소코드 (NAS/NYS/HKS/TSE)
   * @param stockCode 종목코드
   * @param minuteGap 분봉 간격 (1, 5, 15, 30, 60)
   */
  getIntradayChart: async (
    exchangeCode: string,
    stockCode: string,
    minuteGap: number = 5
  ): Promise<ForeignIntradayChartResponse> => {
    const url = `${BASE_URL}/intraday?exchangeCode=${exchangeCode}&stockCode=${stockCode}&minuteGap=${minuteGap}`;

    const response = await fetch(url);
    if (!response.ok) {
      throw new Error('Failed to fetch foreign stock intraday chart');
    }
    return response.json();
  },

  /**
   * 해외 주식 기간별 차트 조회
   * @param exchangeCode 거래소코드 (NAS/NYS/HKS/TSE)
   * @param stockCode 종목코드
   * @param periodCode 기간 구분 코드 (D: 일봉, W: 주봉, M: 월봉, Y: 년봉)
   */
  getPeriodChart: async (
    exchangeCode: string,
    stockCode: string,
    periodCode: 'D' | 'W' | 'M' | 'Y'
  ): Promise<ForeignPeriodChartResponse> => {
    const response = await fetch(
      `${BASE_URL}/period?exchangeCode=${exchangeCode}&stockCode=${stockCode}&periodCode=${periodCode}`
    );
    if (!response.ok) {
      throw new Error('Failed to fetch foreign stock period chart');
    }
    return response.json();
  },

  /**
   * 현재 날짜 기준으로 최근 차트 데이터 조회 (백엔드에서 자동으로 최근 100건 반환)
   * @param exchangeCode 거래소코드
   * @param stockCode 종목코드
   * @param days 사용안함 (백엔드에서 자동으로 최근 100건 반환)
   * @param periodCode 기간분류코드 (D: 일봉, W: 주봉, M: 월봉, Y: 년봉)
   */
  getRecentPeriodChart: async (
    exchangeCode: string,
    stockCode: string,
    days: number = 30,
    periodCode: 'D' | 'W' | 'M' | 'Y' = 'D'
  ): Promise<ForeignPeriodChartResponse> => {
    return foreignStockApi.getPeriodChart(
      exchangeCode,
      stockCode,
      periodCode
    );
  }
};

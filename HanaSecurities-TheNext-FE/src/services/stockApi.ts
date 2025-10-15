import type {
  IntradayChartResponse,
  PeriodChartResponse,
  PeriodCode,
  StockBalanceResponse,
  BondBalanceResponse,
  InvestOpinionResponse,
  BuyableAmountResponse,
  SellableQuantityResponse,
  StockBasicInfoResponse,
  FinancialInfoResponse,
  StockSearchResponse
} from '../types/stock.types';

const BASE_URL = '/api';

export const stockApi = {
  /**
   * 주식 당일 분봉 조회
   * @param stockCode 종목 코드 (ex: 005930)
   * @param inputTime 입력시간 (HHMMSS 형식, ex: 100000)
   */
  getIntradayChart: async (stockCode: string, inputTime: string): Promise<IntradayChartResponse> => {
    const response = await fetch(
      `${BASE_URL}/stock/intraday?stockCode=${stockCode}&inputTime=${inputTime}`
    );
    if (!response.ok) {
      throw new Error(`Failed to fetch intraday chart data for ${stockCode}`);
    }
    return response.json();
  },

  /**
   * 국내주식 기간별 시세 조회 (일/주/월/년)
   * @param stockCode 종목 코드 (ex: 005930)
   * @param startDate 조회 시작일자 (YYYYMMDD 형식)
   * @param endDate 조회 종료일자 (YYYYMMDD 형식)
   * @param periodCode 기간분류코드 (D:일봉, W:주봉, M:월봉, Y:년봉)
   */
  getPeriodChart: async (
    stockCode: string,
    startDate: string,
    endDate: string,
    periodCode: PeriodCode
  ): Promise<PeriodChartResponse> => {
    const response = await fetch(
      `${BASE_URL}/stock/period?stockCode=${stockCode}&startDate=${startDate}&endDate=${endDate}&periodCode=${periodCode}`
    );
    if (!response.ok) {
      throw new Error(`Failed to fetch period chart data for ${stockCode}`);
    }
    return response.json();
  },

  /**
   * 현재 시간 기준으로 당일 분봉 조회
   * @param stockCode 종목 코드
   */
  getCurrentIntradayChart: async (stockCode: string): Promise<IntradayChartResponse> => {
    const now = new Date();
    const inputTime = now.toTimeString().slice(0, 8).replace(/:/g, '');
    return stockApi.getIntradayChart(stockCode, inputTime);
  },

  /**
   * 현재 날짜 기준으로 과거 N일간의 일봉 데이터 조회
   * @param stockCode 종목 코드
   * @param days 과거 N일 (기본: 30일)
   * @param periodCode 기간분류코드 (기본: D)
   */
  getRecentPeriodChart: async (
    stockCode: string,
    days: number = 30,
    periodCode: PeriodCode = 'D'
  ): Promise<PeriodChartResponse> => {
    const endDate = new Date();
    const startDate = new Date();

    
    const daysToSubtract = periodCode === 'D' ? days :
                          periodCode === 'W' ? days * 7 :
                          periodCode === 'M' ? days * 30 :
                          days * 365;

    startDate.setDate(startDate.getDate() - daysToSubtract);

    const formatDate = (date: Date): string => {
      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, '0');
      const day = String(date.getDate()).padStart(2, '0');
      return `${year}${month}${day}`;
    };

    return stockApi.getPeriodChart(
      stockCode,
      formatDate(startDate),
      formatDate(endDate),
      periodCode
    );
  },

  /**
   * 주식 잔고 조회
   */
  getStockBalance: async (): Promise<StockBalanceResponse> => {
    const response = await fetch(`${BASE_URL}/stock/balance`);
    if (!response.ok) {
      throw new Error('Failed to fetch stock balance');
    }
    return response.json();
  },

  /**
   * 장내채권 잔고 조회
   * @param inqrCndt 조회조건 (00: 전체, 01: 상품번호단위)
   */
  getBondBalance: async (inqrCndt: string = '01'): Promise<BondBalanceResponse> => {
    const response = await fetch(`${BASE_URL}/stock/bond/balance?inqrCndt=${inqrCndt}`);
    if (!response.ok) {
      throw new Error('Failed to fetch bond balance');
    }
    return response.json();
  },

  /**
   * 종목투자의견 조회 (최근 3개월)
   * @param stockCode 종목 코드
   */
  getInvestOpinion: async (stockCode: string): Promise<InvestOpinionResponse> => {
    const response = await fetch(`${BASE_URL}/stock/invest-opinion?stockCode=${stockCode}`);
    if (!response.ok) {
      throw new Error('Failed to fetch investment opinion');
    }
    return response.json();
  },

  /**
   * 매수가능조회
   * @param stockCode 종목 코드
   */
  getBuyableAmount: async (stockCode: string): Promise<BuyableAmountResponse> => {
    const response = await fetch(`${BASE_URL}/stock/buyable-amount?stockCode=${stockCode}`);
    if (!response.ok) {
      throw new Error('Failed to fetch buyable amount');
    }
    return response.json();
  },

  /**
   * 매도가능수량조회
   * @param stockCode 종목 코드
   */
  getSellableQuantity: async (stockCode: string): Promise<SellableQuantityResponse> => {
    const response = await fetch(`${BASE_URL}/stock/sellable-quantity?stockCode=${stockCode}`);
    if (!response.ok) {
      throw new Error('Failed to fetch sellable quantity');
    }
    return response.json();
  },

  /**
   * 주식 기본 정보 조회
   * @param productTypeCode 상품유형코드 (300: 주식, ETF, ETN, ELW)
   * @param stockCode 종목 코드
   */
  getStockBasicInfo: async (productTypeCode: string, stockCode: string): Promise<StockBasicInfoResponse> => {
    const response = await fetch(
      `${BASE_URL}/stock/basic-info?productTypeCode=${productTypeCode}&stockCode=${stockCode}`
    );
    if (!response.ok) {
      throw new Error('Failed to fetch stock basic info');
    }
    return response.json();
  },

  /**
   * 통합 재무정보 조회 (재무비율 + 손익계산서 + 대차대조표)
   * @param stockCode 종목 코드
   * @param divisionCode 분류 구분 코드 (0: 년, 1: 분기)
   */
  getFinancialInfo: async (stockCode: string, divisionCode: string = '0'): Promise<FinancialInfoResponse> => {
    const response = await fetch(
      `${BASE_URL}/stock/financial-info?stockCode=${stockCode}&divisionCode=${divisionCode}`
    );
    if (!response.ok) {
      throw new Error('Failed to fetch financial info');
    }
    return response.json();
  },

  /**
   * 주식 종목 검색
   * @param keyword 검색 키워드 (종목명 또는 종목코드)
   */
  searchStocks: async (keyword: string): Promise<StockSearchResponse> => {
    const response = await fetch(
      `${BASE_URL}/stock/search?keyword=${encodeURIComponent(keyword)}`
    );
    if (!response.ok) {
      throw new Error('Failed to search stocks');
    }
    return response.json();
  }
};
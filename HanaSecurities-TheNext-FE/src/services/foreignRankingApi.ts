const BASE_URL = '/api';

/** 해외 거래소 코드 타입 */
export type ForeignExchangeCode = 'NYS' | 'NAS' | 'HKS' | 'SHS' | 'TSE';

/** 해외 주식 랭킹 타입 */
export type ForeignRankingType = 'VOLUME' | 'TRADING_VALUE' | 'RISE' | 'FALL';

/**
 * 해외 주식 랭킹 아이템
 */
export interface ForeignStockRankItem {
  /** 순위 */
  rank: string;
  /** 실시간 심볼 */
  rsym: string;
  /** 종목 코드 */
  stockCode: string;
  /** 종목명 */
  stockName: string;
  /** 영문명 */
  englishName: string;
  /** 현재가 */
  currentPrice: string;
  /** 등락 부호 */
  changeSign: string;
  /** 등락가 */
  changePrice: string;
  /** 등락률 */
  changeRate: string;
  /** 거래량 */
  volume: string;
  /** 거래대금 */
  tradingValue: string;
  /** 매도호가 */
  askPrice: string;
  /** 매수호가 */
  bidPrice: string;
  /** 거래 가능 여부 */
  tradeable: string;
  /** 기준가 */
  basePrice?: string;
  /** 기준가 대비 */
  baseDiff?: string;
  /** 기준가 대비율 */
  baseRate?: string;
  /** 평균 거래량 */
  averageVolume?: string;
  /** 평균 거래대금 */
  averageTradingValue?: string;
}

/**
 * 해외 주식 랭킹 응답
 */
export interface ForeignStockRankingResponse {
  /** 랭킹 타입 */
  rankingType: string;
  /** 거래소 코드 */
  exchangeCode: string;
  /** 종목 목록 */
  stocks: ForeignStockRankItem[];
  /** 타임스탬프 */
  timestamp: string;
}

export const foreignRankingApi = {
  /**
   * 해외 주식 랭킹 조회
   * @param type 랭킹 타입 (거래량, 거래대금, 상승, 하락)
   * @param exchange 거래소 코드 (NYS, NAS, HKS, SHS, TSE)
   * @returns 해외 주식 랭킹 데이터
   */
  getForeignRanking: async (
    type: ForeignRankingType,
    exchange: ForeignExchangeCode
  ): Promise<ForeignStockRankingResponse> => {
    const response = await fetch(
      `${BASE_URL}/foreign-ranking?type=${type}&exchange=${exchange}`
    );
    if (!response.ok) {
      throw new Error(`Failed to fetch foreign ranking data for ${exchange}`);
    }
    return response.json();
  }
};
/**
 * 주식 랭킹 항목
 */
export interface StockRankingItem {
  /** 순위 */
  rank: string;
  /** 종목명 */
  stockName: string;
  /** 종목 코드 */
  stockCode: string;
  /** 현재가 */
  currentPrice: string;
  /** 등락 부호 */
  changeSign: string;
  /** 전일대비 가격 */
  changePrice: string;
  /** 등락률 */
  changeRate: string;
  /** 거래량 */
  volume: string;
  /** 거래대금 */
  tradingValue: string;
  /** 평균 거래량 */
  averageVolume: string;
  /** 거래량 증가율 */
  volumeIncreaseRate: string;
  /** 거래량 회전율 */
  volumeTurnoverRate: string;
  /** 평균 거래대금 */
  averageTradingValue: string;
  /** 거래대금 회전율 */
  tradingValueTurnoverRate: string;
  /** 고가 (선택) */
  highPrice: string | null;
  /** 저가 (선택) */
  lowPrice: string | null;
  /** 시가 대비 변동 (선택) */
  openPriceChange: string | null;
  /** 시가 대비 변동률 (선택) */
  openPriceChangeRate: string | null;
  /** 고가 비율 (선택) */
  highPriceRatio: string | null;
  /** 저가 비율 (선택) */
  lowPriceRatio: string | null;
}

/**
 * 랭킹 응답 데이터
 */
export interface RankingResponse {
  /** 랭킹 타입 */
  rankingType: string;
  /** 주식 목록 */
  stocks: StockRankingItem[];
  /** 타임스탬프 */
  timestamp: string;
}

/** 랭킹 타입 (거래량/거래대금/상승/하락) */
export type RankingType = 'VOLUME' | 'TRADING_VALUE' | 'RISE' | 'FALL';
/** 시장 타입 (J: 코스피, NX: 코스닥+코넥스) */
export type MarketType = 'J' | 'NX';

/**
 * 지수 가격 응답 데이터
 */
export interface IndexPriceResponse {
  /** 지수 코드 */
  indexCode: string;
  /** 지수명 */
  indexName: string;
  /** 현재가 */
  currentPrice: string;
  /** 전일대비 가격 */
  changePrice: string;
  /** 등락률 */
  changeRate: string;
  /** 등락 부호 */
  changeSign: string;
  /** 시가 */
  openPrice: string;
  /** 고가 */
  highPrice: string;
  /** 저가 */
  lowPrice: string;
  /** 거래량 */
  volume: string;
  /** 거래대금 */
  tradingValue: string;
  /** 시가총액 (선택) */
  marketCap: string | null;
  /** 타임스탬프 */
  timestamp: string;
}

/**
 * 시간별 가격 정보
 */
export interface TimePrice {
  /** 시각 (선택) */
  time: string | null;
  /** 가격 */
  price: string;
  /** 전일대비 가격 */
  changePrice: string;
  /** 등락률 */
  changeRate: string;
  /** 등락 부호 */
  changeSign: string;
  /** 거래량 */
  volume: string;
  /** 거래대금 */
  tradingValue: string;
}

/**
 * 지수 시간별 가격 응답 데이터
 */
export interface IndexTimePriceResponse {
  /** 지수 코드 */
  indexCode: string;
  /** 지수명 */
  indexName: string;
  /** 시간별 가격 목록 */
  timePrices: TimePrice[];
}

/** 지수 타입 (코스피/코스닥/코스피200) */
export type IndexType = 'kospi' | 'kosdaq' | 'kospi200';

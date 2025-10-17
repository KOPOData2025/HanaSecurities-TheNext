/**
 * 해외 주식 관련 타입 정의
 */

/**
 * 거래소 코드 타입 (4개 거래소만 지원)
 */
export type ForeignExchangeCode = 'NAS' | 'NYS' | 'HKS' | 'TSE';

/**
 * 통화 코드 타입
 */
export type CurrencyCode = 'USD' | 'HKD' | 'JPY';

/**
 * 해외 주식 기본 정보 응답
 */
export interface ForeignStockBasicInfoResponse {
  /** 종목코드 */
  stockCode: string;
  /** 종목명 */
  stockName: string;
  /** 상장 주식수 */
  shar: string;
  /** 시가총액 */
  mcap: string;
  /** 섹터 */
  sector: string;
  /** 액면가 */
  parValue: string;
  /** 통화 */
  currency: string;
  /** 거래소코드 */
  exchangeCode: string;
}

/**
 * 해외 주식 검색 결과 항목
 */
export interface ForeignStockSearchItem {
  /** 종목코드 */
  stockCode: string;
  /** 종목명 */
  stockName: string;
  /** 거래소코드 */
  exchangeCode: string;
  /** 통화 */
  currency: string;
  /** 현재가 (선택) */
  currentPrice?: string;
}

/**
 * 해외 주식 검색 응답
 */
export interface ForeignStockSearchResponse {
  /** 검색된 종목 목록 */
  stocks: ForeignStockSearchItem[];
}

/**
 * 해외 주식 현재가 응답
 */
export interface ForeignCurrentPriceResponse {
  /** 실시간 조회 종목코드 */
  rsym: string;
  /** 시가 */
  open: string;
  /** 고가 */
  high: string;
  /** 저가 */
  low: string;
  /** 현재가 */
  last: string;
  /** 전일 종가 */
  base: string;
  /** 전일대비 거래량 */
  pvol: string;
  /** 거래량 */
  tvol: string;
  /** 거래대금 */
  tamt: string;
  /** 52주 최고가 */
  h52p: string;
  /** 52주 최저가 */
  l52p: string;
  /** PER */
  perx: string;
  /** PBR */
  pbrx: string;
  /** EPS */
  epsx: string;
  /** BPS */
  bpsx: string;
  /** 시가총액 */
  tomv: string;
  /** 통화 */
  curr: string;
  /** 거래량 단위 */
  vnit: string;
  /** 호가 단위 */
  eHogau: string;
  /** 거래소 코드 */
  eIcod: string;
}

/**
 * 해외 주식 분봉 차트 데이터
 */
export interface ForeignIntradayChartData {
  /** 한국날짜 */
  kymd: string;
  /** 한국시간 */
  khms: string;
  /** 시가 */
  open: string;
  /** 고가 */
  high: string;
  /** 저가 */
  low: string;
  /** 현재가 */
  last: string;
  /** 거래량 */
  evol: string;
  /** 거래대금 */
  eamt: string;
}

/**
 * 해외 주식 분봉 차트 응답
 */
export interface ForeignIntradayChartResponse {
  /** 실시간 조회 종목코드 */
  rsym: string;
  /** 연속조회 키 */
  next: string;
  /** 연속조회 여부 */
  more: string;
  /** 차트 데이터 */
  chartData: ForeignIntradayChartData[];
}

/**
 * 해외 주식 기간별 차트 데이터
 */
export interface ForeignPeriodChartData {
  /** 날짜 */
  xymd: string;
  /** 시가 */
  open: string;
  /** 고가 */
  high: string;
  /** 저가 */
  low: string;
  /** 종가 */
  clos: string;
  /** 거래량 */
  tvol: string;
  /** 거래대금 */
  tamt: string;
}

/**
 * 해외 주식 기간별 차트 응답
 */
export interface ForeignPeriodChartResponse {
  /** 실시간 조회 종목코드 */
  rsym: string;
  /** 차트 데이터 */
  chartData: ForeignPeriodChartData[];
}

/**
 * 실시간 해외 주식 호가 데이터
 */
export interface ForeignQuoteData {
  /** 거래소코드 */
  exchangeCode: string;
  /** 종목코드 */
  stockCode: string;
  /** 현재가 */
  last: string;
  /** 전일대비 */
  diff: string;
  /** 등락률 */
  rate: string;
  /** 시가 */
  open: string;
  /** 고가 */
  high: string;
  /** 저가 */
  low: string;
  /** 매수호가 */
  bidp: string;
  /** 매도호가 */
  askp: string;
  /** 매수잔량 */
  bidv: string;
  /** 매도잔량 */
  askv: string;
  /** 거래량 */
  tvol: string;
  /** 거래대금 */
  tamt: string;
  /** 체결시간 */
  tday: string;
  /** 통화 */
  currency: string;
  /** 타임스탬프 */
  timestamp: string;
}

/**
 * WebSocket 메시지 타입
 */
export interface ForeignQuoteMessage {
  /** 메시지 타입 (subscribe, unsubscribe, quote) */
  type: 'subscribe' | 'unsubscribe' | 'quote';
  /** 거래소코드 (subscribe/unsubscribe 시) */
  exchangeCode?: string;
  /** 종목코드 (subscribe/unsubscribe 시) */
  stockCode?: string;
  /** 호가 데이터 (quote 시) */
  data?: ForeignQuoteData;
}

/**
 * WebSocket 연결 정보
 */
export interface ForeignQuoteWebSocketInfo {
  /** WebSocket URL */
  url: string;
  /** 프로토콜 */
  protocol: string;
}

/**
 * 해외 관심종목 항목
 */
export interface ForeignWatchlistItem {
  /** 종목코드 */
  stockCode: string;
  /** 종목명 */
  stockName: string;
  /** 거래소코드 */
  exchangeCode: string;
  /** 현재가 */
  currentPrice: string;
  /** 등락률 */
  changeRate: string;
  /** 통화 */
  currency: string;
}

/**
 * 해외 관심종목 목록 응답
 */
export interface ForeignWatchlistResponse {
  /** 관심종목 목록 */
  watchlist: ForeignWatchlistItem[];
}

/**
 * 해외 관심종목 추가 요청
 */
export interface ForeignWatchlistAddRequest {
  /** 사용자 ID */
  userId: number;
  /** 거래소코드 */
  exchangeCode: string;
  /** 종목코드 */
  stockCode: string;
}

/**
 * 해외 관심종목 추가/삭제 응답
 */
export interface ForeignWatchlistActionResponse {
  /** 성공 여부 */
  success: boolean;
  /** 메시지 */
  message: string;
}

/**
 * 주식 정보
 */
export interface Stock {
  /** 종목 코드 */
  code: string;
  /** 종목명 */
  name: string;
  /** 현재가 */
  currentPrice: number;
  /** 전일 대비 가격 */
  change: number;
  /** 등락률 */
  changeRate: number;
  /** 거래량 */
  volume: number;
  /** 고가 */
  high: number;
  /** 저가 */
  low: number;
  /** 차트 데이터 */
  chartData: ChartDataPoint[];
}

/**
 * 차트 데이터 포인트
 */
export interface ChartDataPoint {
  /** 시간 */
  time: string;
  /** 가격 */
  price: number;
}

/**
 * 포트폴리오 정보
 */
export interface Portfolio {
  /** 주식 정보 */
  stock: Stock;
  /** 보유 수량 */
  quantity: number;
  /** 평균 매수 가격 */
  averagePrice: number;
  /** 총 평가 금액 */
  totalValue: number;
  /** 손익 금액 */
  profit: number;
  /** 손익률 */
  profitRate: number;
}

/**
 * 뉴스 기사
 */
export interface NewsArticle {
  /** 기사 ID */
  id: string;
  /** 기사 제목 */
  title: string;
  /** 출처 */
  source: string;
  /** 작성 시간 */
  time: string;
  /** 이미지 URL */
  image: string;
  /** 카테고리 */
  category?: string;
}

/**
 * 시장 지수 정보
 */
export interface MarketIndex {
  /** 지수명 */
  name: string;
  /** 지수 값 */
  value: number;
  /** 전일 대비 변화 */
  change: number;
  /** 등락률 */
  changeRate: number;
}
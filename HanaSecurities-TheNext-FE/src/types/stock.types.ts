/**
 * 일중 차트 응답 데이터
 */
export interface IntradayChartResponse {
  /** 종목 코드 */
  stockCode: string;
  /** 종목명 */
  stockName: string;
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
  /** 총 발행 주식수 */
  totalShares: string;
  /** 차트 데이터 배열 */
  chartData: IntradayChartItem[];
  /** 타임스탬프 */
  timestamp: string;
}

/**
 * 일중 차트 개별 항목
 */
export interface IntradayChartItem {
  /** 날짜 */
  date: string;
  /** 시각 */
  time: string;
  /** 종가 */
  close: string;
  /** 시가 */
  open: string;
  /** 고가 */
  high: string;
  /** 저가 */
  low: string;
  /** 거래량 */
  volume: string;
  /** 거래대금 */
  tradingValue: string;
}

/**
 * 기간별 차트 응답 데이터
 */
export interface PeriodChartResponse {
  /** 종목 코드 */
  stockCode: string;
  /** 종목명 */
  stockName: string;
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
  /** 총 발행 주식수 */
  totalShares: string;
  /** 기간 타입 (D: 일, W: 주, M: 월, Y: 년) */
  periodType: 'D' | 'W' | 'M' | 'Y';
  /** 차트 데이터 배열 */
  chartData: PeriodChartItem[];
  /** 타임스탬프 */
  timestamp: string;
}

/**
 * 기간별 차트 개별 항목
 */
export interface PeriodChartItem {
  /** 날짜 */
  date: string;
  /** 종가 */
  close: string;
  /** 시가 */
  open: string;
  /** 고가 */
  high: string;
  /** 저가 */
  low: string;
  /** 거래량 */
  volume: string;
  /** 거래대금 */
  tradingValue: string;
  /** 등락 부호 */
  changeSign: string;
  /** 전일대비 가격 */
  changePrice: string;
}

/**
 * 차트 데이터
 */
export interface ChartData {
  /** 종목 코드 */
  stockCode: string;
  /** 종목명 */
  stockName: string;
  /** 현재가 */
  currentPrice: number;
  /** 전일대비 가격 */
  priceChange: number;
  /** 등락률 (%) */
  changePercent: string;
  /** 총 발행 주식수 (선택) */
  totalShares?: number;
  /** 지수 목록 (선택) */
  indices?: string[];
  /** 캔들 데이터 배열 */
  candleData: CandleData[];
  /** 거래량 데이터 배열 */
  volumeData: VolumeData[];
}

/**
 * 캔들스틱 차트 데이터
 */
export interface CandleData {
  /** 날짜 */
  date: string;
  /** 시각 (선택) */
  time?: string;
  /** 시가 */
  open: number;
  /** 고가 */
  high: number;
  /** 저가 */
  low: number;
  /** 종가 */
  close: number;
  /** 거래량 */
  volume: number;
  /** 전일대비 가격 (선택) */
  change?: number;
  /** 등락률 (선택) */
  changeRate?: number;
  /** 등락 부호 (선택) */
  changeSign?: string;
  /** 5일 이동평균선 (선택) */
  ma5?: number;
  /** 20일 이동평균선 (선택) */
  ma20?: number;
  /** 60일 이동평균선 (선택) */
  ma60?: number;
  /** 120일 이동평균선 (선택) */
  ma120?: number;
}

/**
 * 거래량 데이터
 */
export interface VolumeData {
  /** 날짜 */
  date: string;
  /** 거래량 */
  volume: number;
  /** 전일대비 가격 */
  priceChange: number;
  /** 색상 (선택) */
  color?: string;
}

/** 기간 타입 (한글) */
export type PeriodType = '분' | '일' | '주' | '월' | '년';
/** 기간 코드 (영문) */
export type PeriodCode = 'MIN' | 'D' | 'W' | 'M' | 'Y';

/**
 * 주식 잔고 조회 응답
 */
export interface StockBalanceResponse {
  /** 성공 여부 */
  success: boolean;
  /** 응답 메시지 */
  message: string;
  /** 보유 종목 목록 */
  holdings: StockHolding[];
  /** 계좌 요약 정보 */
  summary: AccountSummary;
}

/**
 * 보유 주식 정보
 */
export interface StockHolding {
  /** 종목 코드 */
  stockCode: string;
  /** 종목명 */
  stockName: string;
  /** 보유 수량 */
  holdingQty: string;
  /** 매입 금액 */
  buyAmount: string;
  /** 현재가 */
  currentPrice: string;
  /** 평가 금액 */
  evaluationAmount: string;
  /** 평가 손익 금액 */
  profitLossAmount: string;
  /** 평가 손익률 */
  profitLossRate: string;
  /** 대출일 */
  loanDate: string;
}

/**
 * 계좌 요약 정보
 */
export interface AccountSummary {
  /** 예수금 */
  depositAmount: string;
  /** 총 평가 금액 */
  totalEvaluationAmount: string;
  /** 순자산 금액 */
  netAssetAmount: string;
  /** 출금 가능 금액 */
  withdrawableAmount: string;
  /** 총 평가 손익 */
  totalProfitLoss: string;
  /** 총 평가 손익률 */
  totalProfitLossRate: string;
  /** 예상 수수료 */
  estimatedFee: string;
  /** 총 매입 금액 */
  totalBuyAmount: string;
}

/**
 * 채권 잔고 조회 응답
 */
export interface BondBalanceResponse {
  /** 성공 여부 */
  success: boolean;
  /** 응답 메시지 */
  message: string;
  /** 보유 채권 목록 */
  bonds: BondHolding[];
}

/**
 * 보유 채권 정보
 */
export interface BondHolding {
  /** 채권 코드 */
  bondCode: string;
  /** 채권명 */
  bondName: string;
  /** 매수일 */
  buyDate: string;
  /** 매수 순번 */
  buySequence: string;
  /** 잔고 수량 */
  balanceQty: string;
  /** 종합과세 수량 */
  comprehensiveTaxQty: string;
  /** 분리과세 수량 */
  separateTaxQty: string;
  /** 만기일 */
  maturityDate: string;
  /** 매입 수익률 */
  buyReturnRate: string;
  /** 매입 단가 */
  buyUnitPrice: string;
  /** 매입 금액 */
  buyAmount: string;
  /** 주문 가능 수량 */
  orderableQty: string;
}

/**
 * 투자 의견 응답
 */
export interface InvestOpinionResponse {
  /** 성공 여부 */
  success: boolean;
  /** 응답 메시지 */
  message: string;
  /** 투자 의견 목록 */
  opinions: InvestOpinionItem[];
}

/**
 * 투자 의견 항목
 */
export interface InvestOpinionItem {
  /** 영업일자 */
  businessDate: string;
  /** 투자 의견 */
  opinion: string;
  /** 투자 의견 코드 */
  opinionCode: string;
  /** 이전 투자 의견 */
  previousOpinion: string;
  /** 이전 투자 의견 코드 */
  previousOpinionCode: string;
  /** 증권사명 */
  brokerage: string;
  /** 목표 주가 */
  targetPrice: string;
  /** 전일 종가 */
  previousClose: string;
  /** 목표가와 전일 종가 차이 */
  priceGap: string;
  /** 목표가와 전일 종가 차이율 */
  gapRate: string;
  /** 선물 가격 차이 */
  futuresGap: string;
  /** 선물 가격 차이율 */
  futuresGapRate: string;
}

/**
 * 매수 가능 금액 응답
 */
export interface BuyableAmountResponse {
  /** 성공 여부 */
  success: boolean;
  /** 응답 메시지 */
  message: string;
  /** 매수 가능 금액 데이터 */
  data: BuyableAmountData;
}

/**
 * 매수 가능 금액 데이터
 */
export interface BuyableAmountData {
  /** 주문 가능 현금 */
  orderableCash: string;
  /** 신용 없이 매수 가능 금액 */
  noCreditBuyAmount: string;
  /** 신용 없이 매수 가능 수량 */
  noCreditBuyQuantity: string;
  /** 최대 매수 금액 */
  maxBuyAmount: string;
  /** 최대 매수 수량 */
  maxBuyQuantity: string;
  /** CMA 평가 금액 */
  cmaEvaluationAmount: string;
  /** 해외 주식 재사용 금액 */
  overseasReuseAmount: string;
  /** 주문 가능 외화 금액 */
  orderableForeignAmount: string;
}

/**
 * 매도 가능 수량 응답
 */
export interface SellableQuantityResponse {
  /** 성공 여부 */
  success: boolean;
  /** 응답 메시지 */
  message: string;
  /** 매도 가능 수량 데이터 */
  data: SellableQuantityData;
}

/**
 * 매도 가능 수량 데이터
 */
export interface SellableQuantityData {
  /** 종목 코드 */
  stockCode: string;
  /** 종목명 */
  stockName: string;
  /** 매수 수량 */
  buyQuantity: string;
  /** 매도 수량 */
  sellQuantity: string;
  /** 잔고 수량 */
  balanceQuantity: string;
  /** 비저축 수량 */
  nonSavingQuantity: string;
  /** 주문 가능 수량 */
  orderableQuantity: string;
  /** 매입 평균 가격 */
  purchaseAveragePrice: string;
  /** 매입 금액 */
  purchaseAmount: string;
  /** 현재가 */
  currentPrice: string;
  /** 평가 금액 */
  evaluationAmount: string;
  /** 평가 손익 금액 */
  profitLossAmount: string;
  /** 평가 손익률 */
  profitLossRate: string;
}

/**
 * 주식 기본 정보 응답
 */
export interface StockBasicInfoResponse {
  /** 성공 여부 */
  success: boolean;
  /** 응답 메시지 */
  message: string;
  /** 주식 기본 정보 데이터 */
  data: StockBasicInfoData;
}

/**
 * 주식 기본 정보 데이터
 */
export interface StockBasicInfoData {
  /** 상품번호 */
  pdno: string;
  /** 상품유형코드 */
  prdtTypeCd: string;
  /** 상품명 */
  prdtName: string;
  /** 상품약어명 */
  prdtAbrvName: string;
  /** 상품영문명 */
  prdtEngName: string;
  /** 표준상품번호 */
  stdPdno: string;
  /** 시장ID코드 */
  mketIdCd: string;
  /** 유가증권그룹ID코드 */
  sctyGrpIdCd: string;
  /** 거래소구분코드 */
  excgDvsnCd: string;
  /** 상장주식수 */
  lstgStqt: string;
  /** 상장자본금액 */
  lstgCptlAmt: string;
  /** 자본금 */
  cpta: string;
  /** 액면가 */
  papr: string;
  /** 발행가격 */
  issuPric: string;
  /** 코스피200종목여부 */
  kospi200ItemYn: string;
  /** 유가증권시장상장일자 */
  sctsMketLstgDt: string;
  /** 유가증권시장상장폐지일자 */
  sctsMketLstgAbolDt: string;
  /** 코스닥시장상장일자 */
  kosdaqMketLstgDt: string;
  /** 코스닥시장상장폐지일자 */
  kosdaqMketLstgAbolDt: string;
  /** 프리보드시장상장일자 */
  frbdMketLstgDt: string;
  /** 프리보드시장상장폐지일자 */
  frbdMketLstgAbolDt: string;
  /** 리츠종류코드 */
  reitsKindCd: string;
  /** ETF구분코드 */
  etfDvsnCd: string;
  /** 유전펀드여부 */
  oilfFundYn: string;
  /** 지수업종대분류코드 */
  idxBztpLclsCd: string;
  /** 지수업종중분류코드 */
  idxBztpMclsCd: string;
  /** 지수업종소분류코드 */
  idxBztpSclsCd: string;
  /** 주식종류코드 */
  stckKindCd: string;
  /** 뮤추얼펀드개시일자 */
  mfndOpngDt: string;
  /** 뮤추얼펀드종료일자 */
  mfndEndDt: string;
  /** 예탁결제조기종료일자 */
  dpsiErlmCnclDt: string;
  /** ETF구성수량 */
  etfCuQty: string;
  /** 상품명120 */
  prdtName120: string;
  /** 상품영문명120 */
  prdtEngName120: string;
  /** 상품영문약어명 */
  prdtEngAbrvName: string;
  /** 예탁적임결제조기종료여부 */
  dpsiAptmErlmYn: string;
  /** ETF과세유형코드 */
  etfTxtnTypeCd: string;
  /** ETF유형코드 */
  etfTypeCd: string;
  /** 상장폐지일자 */
  lstgAbolDt: string;
  /** 신주구주구분코드 */
  nwstOdstDvsnCd: string;
  /** 대용가격 */
  sbstPric: string;
  /** 당사대용가격 */
  thcoSbstPric: string;
  /** 당사대용가격변경일자 */
  thcoSbstPricChngDt: string;
  /** 거래정지여부 */
  trStopYn: string;
  /** 관리종목여부 */
  admnItemYn: string;
  /** 당일종가 */
  thdtClpr: string;
  /** 전일종가 */
  bfdyClpr: string;
  /** 종가변경일자 */
  clprChngDt: string;
  /** 표준산업분류코드 */
  stdIdstClsfCd: string;
  /** 표준산업분류코드명 */
  stdIdstClsfCdName: string;
  /** 지수업종대분류코드명 */
  idxBztpLclsCdName: string;
  /** 지수업종중분류코드명 */
  idxBztpMclsCdName: string;
  /** 지수업종소분류코드명 */
  idxBztpSclsCdName: string;
  /** OCR번호 */
  ocrNo: string;
  /** 신인증권종목여부 */
  crfdItemYn: string;
  /** 전자유가증권여부 */
  elecSctyYn: string;
  /** 발행기관코드 */
  issuIsttCd: string;
  /** ETF추적오차율배수 */
  etfChasErngRtDbnb: string;
  /** ETF/ETN투자유의종목여부 */
  etfEtnIvstHeedItemYn: string;
  /** 결제이자율구분코드 */
  stlnIntRtDvsnCd: string;
  /** 외국인개인한도율 */
  frnrPsnlLmtRt: string;
  /** 상장신청자발행기관코드 */
  lstgRqsrIssuIsttCd: string;
  /** 상장신청자종목코드 */
  lstgRqsrItemCd: string;
  /** 신탁기관발행기관코드 */
  trstIsttIssuIsttCd: string;
  /** 담보거래거래소가능여부 */
  cpttTradTrPsblYn: string;
  /** 익일거래정지여부 */
  nxtTrStopYn: string;
  /** 결제월일 */
  setlMmdd: string;
}

/**
 * 재무 정보 응답
 */
export interface FinancialInfoResponse {
  /** 성공 여부 */
  success: boolean;
  /** 응답 메시지 */
  message: string;
  /** 재무 데이터 */
  data: FinancialData;
}

/**
 * 재무 데이터
 */
export interface FinancialData {
  /** 기간별 재무 정보 목록 */
  periods: FinancialPeriodInfo[];
}

/**
 * 기간별 재무 정보
 */
export interface FinancialPeriodInfo {
  /** 회계 기간 */
  period: string;

  /** 매출 성장률 (%) */
  salesGrowthRate: string;
  /** 영업이익 성장률 (%) */
  operatingProfitGrowthRate: string;
  /** 당기순이익 성장률 (%) */
  netIncomeGrowthRate: string;
  /** 자기자본이익률 (%) */
  roe: string;
  /** 주당순이익 */
  eps: string;
  /** 주당매출액 */
  sps: string;
  /** 주당순자산가치 */
  bps: string;
  /** 유보율 (%) */
  reserveRatio: string;
  /** 부채비율 (%) */
  debtRatio: string;

  /** 매출액 */
  sales: string;
  /** 매출원가 */
  salesCost: string;
  /** 매출총이익 */
  grossProfit: string;
  /** 영업이익 */
  operatingProfit: string;
  /** 경상이익 */
  ordinaryProfit: string;
  /** 특별이익 (선택) */
  extraordinaryGain: string | null;
  /** 특별손실 (선택) */
  extraordinaryLoss: string | null;
  /** 당기순이익 */
  netIncome: string;

  /** 유동자산 */
  currentAssets: string;
  /** 고정자산 */
  fixedAssets: string;
  /** 자산총계 */
  totalAssets: string;
  /** 유동부채 */
  currentLiabilities: string;
  /** 고정부채 */
  fixedLiabilities: string;
  /** 부채총계 */
  totalLiabilities: string;
  /** 자본금 */
  capital: string;
  /** 자본총계 */
  totalEquity: string;
}

/**
 * 주식 검색 응답
 */
export interface StockSearchResponse {
  /** 성공 여부 */
  success: boolean;
  /** 응답 메시지 */
  message: string;
  /** 검색된 주식 목록 */
  stocks: StockSearchItem[];
}

/**
 * 주식 검색 항목
 */
export interface StockSearchItem {
  /** 종목 코드 */
  stockCode: string;
  /** 종목명 */
  stockName: string;
  /** 시장 구분 */
  marketType: string;
}

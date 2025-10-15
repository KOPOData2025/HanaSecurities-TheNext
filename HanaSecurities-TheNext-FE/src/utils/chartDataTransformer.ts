import type {
  IntradayChartResponse,
  PeriodChartResponse,
  ChartData,
  CandleData,
  VolumeData
} from '../types/stock.types';

/**
 * 분봉 API 응답을 ChartData 형식으로 변환
 * @param response 분봉 차트 API 응답 데이터
 * @returns 변환된 차트 데이터 (캔들 데이터 및 거래량 데이터 포함)
 */
export const transformIntradayToChartData = (response: IntradayChartResponse): ChartData => {
  
  const candleData: CandleData[] = response.chartData.reverse().map(item => ({
    date: `${item.date.slice(0, 4)}-${item.date.slice(4, 6)}-${item.date.slice(6, 8)}`,
    time: `${item.time.slice(0, 2)}:${item.time.slice(2, 4)}:${item.time.slice(4, 6)}`,
    open: parseFloat(item.open),
    high: parseFloat(item.high),
    low: parseFloat(item.low),
    close: parseFloat(item.close),
    volume: parseFloat(item.volume),
  }));

  const volumeData: VolumeData[] = response.chartData.map(item => {
    const open = parseFloat(item.open);
    const close = parseFloat(item.close);
    const priceChange = close - open;

    return {
      date: `${item.date.slice(0, 4)}-${item.date.slice(4, 6)}-${item.date.slice(6, 8)} ${item.time.slice(0, 2)}:${item.time.slice(2, 4)}`,
      volume: parseFloat(item.volume),
      priceChange,
      color: priceChange > 0 ? '#E84041' : priceChange < 0 ? '#1070E0' : '#888888'
    };
  });

  return {
    stockCode: response.stockCode,
    stockName: response.stockName,
    currentPrice: parseFloat(response.currentPrice),
    priceChange: parseFloat(response.changePrice),
    changePercent: response.changeRate,
    totalShares: parseFloat(response.totalShares || '0'),
    indices: ['KOSPI200', 'KRX+NXT'],
    candleData,
    volumeData
  };
};

/**
 * 기간별 시세 API 응답을 ChartData 형식으로 변환
 * @param response 기간별 차트 API 응답 데이터
 * @returns 변환된 차트 데이터 (캔들 데이터 및 거래량 데이터 포함)
 */
export const transformPeriodToChartData = (response: PeriodChartResponse): ChartData => {
  
  const candleData: CandleData[] = response.chartData.reverse().map(item => ({
    date: `${item.date.slice(0, 4)}-${item.date.slice(4, 6)}-${item.date.slice(6, 8)}`,
    open: parseFloat(item.open),
    high: parseFloat(item.high),
    low: parseFloat(item.low),
    close: parseFloat(item.close),
    volume: parseFloat(item.volume),
    change: parseFloat(item.changePrice),
    changeSign: item.changeSign
  }));

  const volumeData: VolumeData[] = response.chartData.map(item => {
    const priceChange = parseFloat(item.changePrice);

    return {
      date: `${item.date.slice(0, 4)}-${item.date.slice(4, 6)}-${item.date.slice(6, 8)}`,
      volume: parseFloat(item.volume),
      priceChange,
      color: item.changeSign === '2' ? '#E84041' :
             item.changeSign === '5' ? '#1070E0' :
             '#888888'
    };
  });

  return {
    stockCode: response.stockCode,
    stockName: response.stockName,
    currentPrice: parseFloat(response.currentPrice),
    priceChange: parseFloat(response.changePrice),
    changePercent: response.changeRate,
    totalShares: parseFloat(response.totalShares || '0'),
    indices: ['KOSPI200', 'KRX+NXT'],
    candleData,
    volumeData
  };
};

/**
 * 기간 타입을 API 기간 코드로 변환
 * @param period 기간 타입 ('분', '일', '주', '월', '년')
 * @returns API 기간 코드 ('MIN', 'D', 'W', 'M', 'Y')
 */
export const periodToCode = (period: string): 'MIN' | 'D' | 'W' | 'M' | 'Y' => {
  const periodMap: Record<string, 'MIN' | 'D' | 'W' | 'M' | 'Y'> = {
    '분': 'MIN',
    '일': 'D',
    '주': 'W',
    '월': 'M',
    '년': 'Y'
  };
  return periodMap[period] || 'D';
};

/**
 * 분봉 데이터의 현재 시간 계산
 * @returns 현재 시각을 HHMMSS 형식으로 반환
 */
export const getCurrentInputTime = (): string => {
  const now = new Date();
  const hours = String(now.getHours()).padStart(2, '0');
  const minutes = String(now.getMinutes()).padStart(2, '0');
  const seconds = String(now.getSeconds()).padStart(2, '0');
  return `${hours}${minutes}${seconds}`;
};

/**
 * 과거 날짜 계산
 * @param daysAgo 과거 일수 (예: 7일 전이면 7 입력)
 * @returns YYYYMMDD 형식의 과거 날짜 문자열
 */
export const getPastDate = (daysAgo: number): string => {
  const date = new Date();
  date.setDate(date.getDate() - daysAgo);

  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');

  return `${year}${month}${day}`;
};

/**
 * 오늘 날짜 계산
 * @returns YYYYMMDD 형식의 오늘 날짜 문자열
 */
export const getTodayDate = (): string => {
  const date = new Date();

  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');

  return `${year}${month}${day}`;
};
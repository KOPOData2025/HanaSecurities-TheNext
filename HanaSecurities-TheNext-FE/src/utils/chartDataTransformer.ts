import type {
  IntradayChartResponse,
  PeriodChartResponse,
  ChartData,
  CandleData,
  VolumeData
} from '../types/stock.types';
import type {
  ForeignIntradayChartResponse,
  ForeignPeriodChartResponse
} from '../types/foreignStock.types';
import type {
  GoldCurrentPriceResponse,
  GoldChartResponse
} from '../services/goldApi';

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

/**
 * 해외주식 분봉 API 응답을 ChartData 형식으로 변환
 * @param response 해외주식 분봉 차트 API 응답 데이터
 * @returns 변환된 차트 데이터 (캔들 데이터 및 거래량 데이터 포함)
 */
export const transformForeignIntradayToChartData = (response: ForeignIntradayChartResponse): ChartData => {
  // API는 최근 데이터를 먼저 보내므로, reverse 전에 현재가 저장
  const latestData = response.chartData.length > 0 ? response.chartData[0] : null;
  const currentPrice = latestData ? parseFloat(latestData.last) : 0;

  // 캔들스틱 차트 데이터 변환 (reverse로 시간 순서대로 정렬)
  const candleData: CandleData[] = response.chartData.reverse().map(item => ({
    date: `${item.kymd.slice(0, 4)}-${item.kymd.slice(4, 6)}-${item.kymd.slice(6, 8)}`,
    time: `${item.khms.slice(0, 2)}:${item.khms.slice(2, 4)}:${item.khms.slice(4, 6)}`,
    open: parseFloat(item.open),
    high: parseFloat(item.high),
    low: parseFloat(item.low),
    close: parseFloat(item.last),  // 해외는 'last'를 사용
    volume: parseFloat(item.evol),
  }));

  // 거래량 차트 데이터 변환
  const volumeData: VolumeData[] = response.chartData.map(item => {
    const open = parseFloat(item.open);
    const close = parseFloat(item.last);
    const priceChange = close - open;

    return {
      date: `${item.kymd.slice(0, 4)}-${item.kymd.slice(4, 6)}-${item.kymd.slice(6, 8)} ${item.khms.slice(0, 2)}:${item.khms.slice(2, 4)}`,
      volume: parseFloat(item.evol),
      priceChange,
      color: priceChange > 0 ? '#E84041' : priceChange < 0 ? '#1070E0' : '#888888'
    };
  });

  return {
    stockCode: response.rsym || '',
    stockName: response.rsym || '',  // 해외주식은 종목명이 없으므로 코드 사용
    currentPrice: currentPrice,  // reverse 전 첫 번째(최근) 데이터의 종가 사용
    priceChange: 0,  // 해외주식 분봉 응답에는 전일대비 정보 없음
    changePercent: '0',
    totalShares: 0,
    indices: [],
    candleData,
    volumeData
  };
};

/**
 * 해외주식 기간별 시세 API 응답을 ChartData 형식으로 변환
 * @param response 해외주식 기간별 차트 API 응답 데이터
 * @returns 변환된 차트 데이터 (캔들 데이터 및 거래량 데이터 포함)
 */
export const transformForeignPeriodToChartData = (response: ForeignPeriodChartResponse): ChartData => {
  // 캔들스틱 차트 데이터 변환
  const candleData: CandleData[] = response.chartData.reverse().map(item => ({
    date: `${item.xymd.slice(0, 4)}-${item.xymd.slice(4, 6)}-${item.xymd.slice(6, 8)}`,
    open: parseFloat(item.open),
    high: parseFloat(item.high),
    low: parseFloat(item.low),
    close: parseFloat(item.clos),  // 해외는 'clos'를 사용
    volume: parseFloat(item.tvol),
  }));

  // 거래량 차트 데이터 변환
  const volumeData: VolumeData[] = response.chartData.map((item, index) => {
    // 전일대비 계산 (이전 항목과 비교)
    const prevClose = index > 0 ? parseFloat(response.chartData[index - 1].clos) : parseFloat(item.open);
    const currentClose = parseFloat(item.clos);
    const priceChange = currentClose - prevClose;

    return {
      date: `${item.xymd.slice(0, 4)}-${item.xymd.slice(4, 6)}-${item.xymd.slice(6, 8)}`,
      volume: parseFloat(item.tvol),
      priceChange,
      color: priceChange > 0 ? '#E84041' : priceChange < 0 ? '#1070E0' : '#888888'
    };
  });

  return {
    stockCode: response.rsym || '',
    stockName: response.rsym || '',  // 해외주식은 종목명이 없으므로 코드 사용
    currentPrice: candleData.length > 0 ? candleData[candleData.length - 1].close : 0,
    priceChange: 0,  // 계산 필요시 첫날과 마지막날 비교
    changePercent: '0',
    totalShares: 0,
    indices: [],
    candleData,
    volumeData
  };
};
/**
 * 금현물 현재가 + 차트 데이터를 ChartData 형식으로 변환
 * @param goldPrice 금현물 현재가 데이터
 * @param goldChart 금현물 차트 데이터 (선택적)
 * @returns 변환된 차트 데이터
 */
export const transformGoldToChartData = (
  goldPrice: GoldCurrentPriceResponse,
  goldChart?: GoldChartResponse
): ChartData => {
  if (!goldChart || !goldChart.data || goldChart.data.length === 0) {
    // 차트 데이터가 없으면 빈 배열 반환
    const productName = goldPrice.productCode === 'M04020000' ? '금 99.99% 1Kg' : '미니금 99.99% 100g';
    const effectiveCurrentPrice = goldPrice.currentPrice ?? goldPrice.previousClose;

    return {
      stockCode: goldPrice.productCode,
      stockName: productName,
      currentPrice: effectiveCurrentPrice,
      priceChange: goldPrice.changeAmount,
      changePercent: goldPrice.changeRate.toString(),
      totalShares: goldPrice.volume || 0,
      indices: [],
      candleData: [],
      volumeData: []
    };
  }

  // timestamp 파싱 함수: ISO 8601 또는 YYYYMMDDHHMMSS 형식 지원
  const parseTimestamp = (timestamp: string): { date: string; time?: string } => {
    if (timestamp.includes('T')) {
      // ISO 8601 형식: "2025-10-16T16:50:50.228301"
      const parts = timestamp.split('T');
      const date = parts[0]; // "2025-10-16"
      const time = parts[1]?.slice(0, 8); // "16:50:50"
      return { date, time };
    } else if (timestamp.length === 14) {
      // YYYYMMDDHHMMSS 형식: "20251013000000"
      const year = timestamp.slice(0, 4);
      const month = timestamp.slice(4, 6);
      const day = timestamp.slice(6, 8);
      const hour = timestamp.slice(8, 10);
      const minute = timestamp.slice(10, 12);
      const second = timestamp.slice(12, 14);

      const date = `${year}-${month}-${day}`;
      const time = (hour !== '00' || minute !== '00' || second !== '00')
        ? `${hour}:${minute}:${second}`
        : undefined;

      return { date, time };
    } else if (timestamp.length === 8) {
      // YYYYMMDD 형식: "20251013"
      const year = timestamp.slice(0, 4);
      const month = timestamp.slice(4, 6);
      const day = timestamp.slice(6, 8);
      return { date: `${year}-${month}-${day}` };
    } else {
      // 기본값: 그대로 사용
      return { date: timestamp };
    }
  };

  // API 데이터는 최신→과거 순서이므로 reverse하여 과거→최신 순서로 변환
  // 음수 가격을 절댓값으로 변환하고, close가 null인 경우 다음 영업일의 open 가격으로 채우기
  // 데이터 개수 제한: 분봉 25개, 일봉 50개, 주봉 13개, 월봉 3개
  const maxDataCount: Record<string, number> = {
    '분봉': 25,
    '일봉': 50,
    '주봉': 13,
    '월봉': 3
  };
  const limit = maxDataCount[goldChart.interval] || 50;

  const dataWithClose = [...goldChart.data]
    .reverse()
    .slice(-limit) // 최근 데이터만 선택
    .map(candle => ({
      ...candle,
      open: Math.abs(candle.open),
      high: Math.abs(candle.high),
      low: Math.abs(candle.low),
      close: candle.close !== null ? Math.abs(candle.close) : null,
      volume: Math.abs(candle.volume)
    }));

  // close가 null인 경우 다음 영업일의 open 가격으로 채우기 (역순으로 처리)
  for (let i = dataWithClose.length - 1; i >= 0; i--) {
    if (dataWithClose[i].close === null) {
      // 다음 영업일(i+1)의 open 가격 찾기
      let nextOpen = null;
      for (let j = i + 1; j < dataWithClose.length; j++) {
        if (dataWithClose[j].open !== null && dataWithClose[j].open !== undefined) {
          nextOpen = dataWithClose[j].open;
          break;
        }
      }
      // 다음 영업일의 open이 없으면 현재 open 사용
      dataWithClose[i].close = nextOpen ?? dataWithClose[i].open;
    }
  }

  // 캔들스틱 차트 데이터 변환
  const candleData: CandleData[] = dataWithClose.map(candle => {
    const { date, time } = parseTimestamp(candle.timestamp);

    // 분봉은 시간 포함, 일/주/월봉은 날짜만
    const formattedDate = time ? `${date} ${time}` : date;

    return {
      date: formattedDate,
      time: time,
      open: candle.open,
      high: candle.high,
      low: candle.low,
      close: candle.close ?? candle.open, // 여전히 null이면 open 사용
      volume: candle.volume
    };
  });

  // 거래량 차트 데이터 변환
  const volumeData: VolumeData[] = dataWithClose.map((candle, index) => {
    const { date, time } = parseTimestamp(candle.timestamp);
    const formattedDate = time ? `${date} ${time}` : date;

    const effectiveClose = candle.close ?? candle.open;

    // 전일대비 계산
    const prevClose = index > 0
      ? (dataWithClose[index - 1].close ?? dataWithClose[index - 1].open)
      : candle.open;
    const priceChange = effectiveClose - prevClose;

    return {
      date: formattedDate,
      volume: candle.volume,
      priceChange,
      color: priceChange > 0 ? '#E84041' : priceChange < 0 ? '#1070E0' : '#888888'
    };
  });

  // 상품명 결정
  const productName = goldPrice.productCode === 'M04020000' ? '금 99.99% 1Kg' : '미니금 99.99% 100g';

  // currentPrice가 null이면 previousClose 사용
  const effectiveCurrentPrice = goldPrice.currentPrice ?? goldPrice.previousClose;

  return {
    stockCode: goldPrice.productCode,
    stockName: productName,
    currentPrice: effectiveCurrentPrice,
    priceChange: goldPrice.changeAmount,
    changePercent: goldPrice.changeRate.toString(),
    totalShares: goldPrice.volume || 0,  // 금현물은 거래량을 totalShares에 저장
    indices: [],
    candleData,
    volumeData
  };
};

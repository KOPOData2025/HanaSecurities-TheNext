export interface StockDetailData {
  code: string;
  name: string;
  market: string;
  currentPrice: number;
  change: number;
  changePercent: number;
  volume: number;
  high: number;
  low: number;
  open: number;
  close: number;
}

export interface ChartDataPoint {
  time: string;
  date: string;
  open: number;
  high: number;
  low: number;
  close: number;
  volume: number;
  ma5?: number;
  ma20?: number;
  ma60?: number;
  ma120?: number;
}

export interface VolumeDataPoint {
  date: string;
  volume: number;
  isPositive: boolean;
}

export const getStockDetailData = (code: string): StockDetailData => {
  const stockDetails: Record<string, StockDetailData> = {
    '030530': {
      code: '030530',
      name: '원익홀딩스',
      market: '코스닥',
      currentPrice: 11560,
      change: 2220,
      changePercent: 23.77,
      volume: 29468384,
      high: 11760,
      low: 9340,
      open: 9340,
      close: 11560
    },
    '005930': {
      code: '005930',
      name: '삼성전자',
      market: '코스피',
      currentPrice: 55700,
      change: 300,
      changePercent: 0.54,
      volume: 15234567,
      high: 56000,
      low: 55200,
      open: 55400,
      close: 55700
    },
    '086790': {
      code: '086790',
      name: '하나금융지주',
      market: '코스피',
      currentPrice: 62400,
      change: 1200,
      changePercent: 1.96,
      volume: 2345678,
      high: 62800,
      low: 61200,
      open: 61200,
      close: 62400
    }
  };

  return stockDetails[code] || stockDetails['030530'];
};

export const getChartData = (code: string, period: string): ChartDataPoint[] => {
  
  const basePrice = code === '030530' ? 6000 : code === '005930' ? 55000 : 61000;
  const data: ChartDataPoint[] = [];
  
  const dates = [
    '07.15', '07.16', '07.17', '07.18', '07.19', '07.22', '07.23', '07.24', '07.25', '07.26',
    '07.29', '07.30', '07.31', '08.01', '08.02', '08.05', '08.06', '08.07', '08.08', '08.09',
    '08.12', '08.13', '08.14', '08.16', '08.19', '08.20', '08.21', '08.22', '08.23', '08.26',
    '08.27', '08.28', '08.29', '08.30', '09.02', '09.03', '09.04', '09.05', '09.06', '09.09'
  ];

  let prevClose = basePrice;
  
  dates.forEach((date, index) => {
    
    const volatility = 0.02; 
    const trend = index > 30 ? 0.05 : 0; 
    const spike = index > 35 ? 0.5 : 0; 
    
    
    const openChange = (Math.random() - 0.5) * volatility;
    const open = prevClose * (1 + openChange);
    
    
    const closeChange = (Math.random() - 0.3) * volatility + trend + spike;
    const close = open * (1 + closeChange);
    
    
    const high = Math.max(open, close) * (1 + Math.random() * volatility * 0.5);
    const low = Math.min(open, close) * (1 - Math.random() * volatility * 0.5);
    
    
    const ma5Price = close * (0.98 + Math.random() * 0.04);
    const ma20Price = close * (0.95 + Math.random() * 0.04);
    const ma60Price = close * (0.92 + Math.random() * 0.04);
    const ma120Price = close * (0.88 + Math.random() * 0.04);
    
    data.push({
      time: date,
      date: date,
      open: Math.round(open),
      high: Math.round(high),
      low: Math.round(low),
      close: Math.round(close),
      volume: Math.floor(Math.random() * 1000000) + 500000,
      ma5: Math.round(ma5Price),
      ma20: Math.round(ma20Price),
      ma60: Math.round(ma60Price),
      ma120: Math.round(ma120Price)
    });
    
    prevClose = close;
  });

  return data;
};

export const getVolumeData = (code: string, period: string): VolumeDataPoint[] => {
  const data: VolumeDataPoint[] = [];
  
  const dates = [
    '07.15', '07.22', '07.29', '08.01', '08.08', '08.15', '08.21', '08.28', '09.02', '09.09'
  ];

  dates.forEach((date, index) => {
    const isSpike = index === 5 || index === 6 || index === 9; 
    const baseVolume = isSpike ? 30000 : 5000;
    
    data.push({
      date: date,
      volume: baseVolume + Math.random() * 5000,
      isPositive: Math.random() > 0.5
    });
  });

  return data;
};
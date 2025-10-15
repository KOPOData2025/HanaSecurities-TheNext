import priceData from '../../../005930_priceData_day.json';

export interface ChartData {
  stockCode: string;
  stockName: string;
  currentPrice: number;
  priceChange: number;
  changePercent: string;
  totalShares: number;
  indices: string[];
  candleData: CandleData[];
  volumeData: VolumeData[];
}

export interface CandleData {
  date: string;
  open: number;
  high: number;
  low: number;
  close: number;
  volume: number;
  change?: number;
  changeRate?: number;
  changeSign?: string;
  ma5?: number;
  ma20?: number;
  ma60?: number;
  ma120?: number;
}

export interface VolumeData {
  date: string;
  volume: number;
  priceChange: number;
  color?: string;
}


const transformPriceData = (): CandleData[] => {
  return priceData.output.slice(0, 30).reverse().map(item => ({
    date: `${item.stck_bsop_date.slice(0, 4)}-${item.stck_bsop_date.slice(4, 6)}-${item.stck_bsop_date.slice(6, 8)}`,
    open: parseFloat(item.stck_oprc),
    high: parseFloat(item.stck_hgpr),
    low: parseFloat(item.stck_lwpr),
    close: parseFloat(item.stck_clpr),
    volume: parseInt(item.acml_vol),
    change: parseFloat(item.prdy_vrss),
    changeRate: parseFloat(item.prdy_ctrt),
    changeSign: item.prdy_vrss_sign 
  }));
};

const generateCandleData = (): CandleData[] => {
  return transformPriceData();
};

const generateVolumeData = (): VolumeData[] => {
  const candleData = transformPriceData();
  return candleData.map(candle => ({
    date: candle.date,
    volume: candle.volume,
    priceChange: candle.change || 0,
    color: candle.changeSign === '2' ? '#E84041' : candle.changeSign === '5' ? '#1070E0' : '#888888'
  }));
};

const mockChartData: ChartData = {
  stockCode: '005930',
  stockName: '삼성전자',
  currentPrice: 79200,
  priceChange: 1000,
  changePercent: '1.28',
  totalShares: 5969783,
  indices: ['KOSPI200', 'KOSPI'],
  candleData: generateCandleData(),
  volumeData: generateVolumeData(),
};

export const getChartData = (stockCode: string): ChartData => {
  
  return {
    ...mockChartData,
    stockCode,
  };
};
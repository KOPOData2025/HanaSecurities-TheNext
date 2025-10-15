export interface OrderLevel {
  price: number;
  quantity: number;
  changePercent: number;
  totalQuantity?: number;
  broker?: string;
  time?: string;
  orderCount?: number;
}

export interface OrderBookData {
  code: string;
  name: string;
  currentPrice: number;
  priceChange: number;
  changePercent: number;
  marketCap: number;
  marketCapPercent: number;
  bidOrders: OrderLevel[];
  askOrders: OrderLevel[];
  totalBidQuantity: number;
  totalAskQuantity: number;
  totalBidAmount: number;
  totalAskAmount: number;
  chartData: { time: string; value: number }[];
  brokerInfo: {
    yesterdayBuy: number;
    yesterdayBuyVolume: number;
    weekAvg: number;
    weekAvgVolume: number;
    monthAvg: number;
    monthAvgVolume: number;
    topBuyer: string;
    topBuyerVolume: number;
    topBuyerPercent: number;
    topSeller: string;
    topSellerVolume: number;
    topSellerPercent: number;
  };
}

export const getOrderBookData = (code: string): OrderBookData => {
  
  return {
    code: '086790',
    name: '하나금융지주',
    currentPrice: 90800,
    priceChange: 2100,
    changePercent: 2.37,
    marketCap: 942038,
    marketCapPercent: 85.82,
    bidOrders: [
      { price: 90800, quantity: 3652, changePercent: 2.37, totalQuantity: 10 },
      { price: 90700, quantity: 2683, changePercent: 2.25 },
      { price: 90600, quantity: 2577, changePercent: 2.14 },
      { price: 90500, quantity: 2573, changePercent: 2.03 },
      { price: 90400, quantity: 1990, changePercent: 1.92 }
    ],
    askOrders: [
      { price: 90900, quantity: 2758, changePercent: 2.48 },
      { price: 91000, quantity: 2116, changePercent: 2.59 },
      { price: 91100, quantity: 1236, changePercent: 2.71 },
      { price: 91200, quantity: 2780, changePercent: 2.82 },
      { price: 91300, quantity: 2756, changePercent: 2.93 }
    ],
    totalBidQuantity: 26542,
    totalAskQuantity: 20454,
    totalBidAmount: 6088,
    totalAskAmount: 14275,
    chartData: [
      { time: '09:00', value: 88500 },
      { time: '09:30', value: 89000 },
      { time: '10:00', value: 89500 },
      { time: '10:30', value: 90000 },
      { time: '11:00', value: 90300 },
      { time: '11:30', value: 90500 },
      { time: '12:00', value: 90400 },
      { time: '12:30', value: 90600 },
      { time: '13:00', value: 90700 },
      { time: '13:30', value: 90800 },
      { time: '14:00', value: 90750 },
      { time: '14:30', value: 90800 },
      { time: '15:00', value: 90800 }
    ],
    brokerInfo: {
      yesterdayBuy: 1097685,
      yesterdayBuyVolume: 88700,
      weekAvg: 97300,
      weekAvgVolume: 51500,
      monthAvg: 87700,
      monthAvgVolume: 83590,
      topBuyer: '삼환기',
      topBuyerVolume: 115300,
      topBuyerPercent: 0.90,
      topSeller: '하환기',
      topSellerVolume: 62100,
      topSellerPercent: 3.61
    }
  };
};
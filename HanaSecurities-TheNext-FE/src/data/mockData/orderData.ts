export interface OrderBookItem {
  price: number;
  quantity: number;
}

export interface OrderData {
  stockCode: string;
  stockName: string;
  marketInfo: string;
  currentPrice: number;
  priceChange: number;
  changePercent: number;
  totalShares: number;
  account: {
    id: string;
    name: string;
    type: string;
  };
  orderBook: {
    sell: OrderBookItem[];
    buy: OrderBookItem[];
  };
  availableBalance: number;
  maxOrderableAmount: number;
  minOrderableAmount: number;
  upperLimit: number;
  lowerLimit: number;
}

export const getOrderData = (): OrderData => {
  return {
    stockCode: '086790',
    stockName: '하나금융지주',
    marketInfo: 'KOSPI200 KRX+NXT',
    currentPrice: 90850,
    priceChange: 2150,
    changePercent: 2.42,
    totalShares: 942764,
    account: {
      id: '40123419-010',
      name: '종합매매',
      type: '이상현'
    },
    orderBook: {
      sell: [
        { price: 91700, quantity: 1987 },
        { price: 91600, quantity: 1348 },
        { price: 91500, quantity: 2331 },
        { price: 91400, quantity: 5383 },
        { price: 91300, quantity: 2756 },
        { price: 91200, quantity: 2780 },
        { price: 91100, quantity: 1236 },
        { price: 91000, quantity: 2116 },
        { price: 90900, quantity: 2758 }
      ],
      buy: [
        { price: 90800, quantity: 3652 },
        { price: 90700, quantity: 2683 },
        { price: 90600, quantity: 2577 },
        { price: 90500, quantity: 2573 },
        { price: 90400, quantity: 1990 },
        { price: 90300, quantity: 431 },
        { price: 90200, quantity: 1433 },
        { price: 90100, quantity: 1902 },
        { price: 90000, quantity: 2672 }
      ]
    },
    availableBalance: 66863,
    maxOrderableAmount: 0,
    minOrderableAmount: 0,
    upperLimit: 115300,
    lowerLimit: 62100
  };
};
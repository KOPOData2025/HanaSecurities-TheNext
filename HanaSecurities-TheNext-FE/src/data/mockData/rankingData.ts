export interface RankingItem {
  rank: number;
  name: string;
  code: string;
  currentPrice: string;
  change: string;
  changePercent: string;
  isPositive: boolean;
}

export const risingStocks: RankingItem[] = [
  {
    rank: 1,
    name: '하나금융티아이',
    code: '086790',
    currentPrice: '55,700원',
    change: '1,300원',
    changePercent: '+2.39%',
    isPositive: true
  },
  {
    rank: 2,
    name: '하나금융지주',
    code: '086790',
    currentPrice: '62,400원',
    change: '1,200원',
    changePercent: '+1.96%',
    isPositive: true
  },
  {
    rank: 3,
    name: '하나증권',
    code: '086790',
    currentPrice: '46,250원',
    change: '850원',
    changePercent: '+1.87%',
    isPositive: true
  },
  {
    rank: 4,
    name: '하나은행',
    code: '086790',
    currentPrice: '31,200원',
    change: '550원',
    changePercent: '+1.79%',
    isPositive: true
  },
  {
    rank: 5,
    name: '하나펀드서비스',
    code: '086790',
    currentPrice: '367,000원',
    change: '5,500원',
    changePercent: '+1.52%',
    isPositive: true
  }
];

export const fallingStocks: RankingItem[] = [
  {
    rank: 1,
    name: '삼성바이오로직스',
    code: '005930',
    currentPrice: '755,000원',
    change: '-15,000원',
    changePercent: '-1.95%',
    isPositive: false
  },
  {
    rank: 2,
    name: '하나금융지주',
    code: '086790',
    currentPrice: '61,200원',
    change: '-800원',
    changePercent: '-1.29%',
    isPositive: false
  },
  {
    rank: 3,
    name: '삼성전기',
    code: '005930',
    currentPrice: '156,400원',
    change: '-1,900원',
    changePercent: '-1.20%',
    isPositive: false
  },
  {
    rank: 4,
    name: '삼성화재',
    code: '005930',
    currentPrice: '287,500원',
    change: '-3,000원',
    changePercent: '-1.03%',
    isPositive: false
  },
  {
    rank: 5,
    name: '하나금융지주우',
    code: '086790',
    currentPrice: '30,650원',
    change: '-250원',
    changePercent: '-0.81%',
    isPositive: false
  }
];

export const popularStocks: RankingItem[] = [
  {
    rank: 1,
    name: '삼성전자',
    code: '005930',
    currentPrice: '55,700원',
    change: '300원',
    changePercent: '+0.54%',
    isPositive: true
  },
  {
    rank: 2,
    name: '하나금융지주',
    code: '086790',
    currentPrice: '62,000원',
    change: '0원',
    changePercent: '0.00%',
    isPositive: true
  },
  {
    rank: 3,
    name: '삼성SDI',
    code: '005930',
    currentPrice: '365,500원',
    change: '-1,500원',
    changePercent: '-0.41%',
    isPositive: false
  },
  {
    rank: 4,
    name: '삼성전자우',
    code: '005930',
    currentPrice: '46,100원',
    change: '200원',
    changePercent: '+0.44%',
    isPositive: true
  },
  {
    rank: 5,
    name: '하나금융지주우',
    code: '086790',
    currentPrice: '31,000원',
    change: '100원',
    changePercent: '+0.32%',
    isPositive: true
  }
];

export const volumeStocks: RankingItem[] = [
  {
    rank: 1,
    name: '삼성전자',
    code: '005930',
    currentPrice: '55,700원',
    change: '300원',
    changePercent: '+0.54%',
    isPositive: true
  },
  {
    rank: 2,
    name: '하나금융지주',
    code: '086790',
    currentPrice: '62,000원',
    change: '-200원',
    changePercent: '-0.32%',
    isPositive: false
  },
  {
    rank: 3,
    name: '삼성바이오로직스',
    code: '005930',
    currentPrice: '758,000원',
    change: '3,000원',
    changePercent: '+0.40%',
    isPositive: true
  },
  {
    rank: 4,
    name: '삼성SDI',
    code: '005930',
    currentPrice: '365,500원',
    change: '-1,500원',
    changePercent: '-0.41%',
    isPositive: false
  },
  {
    rank: 5,
    name: '삼성전자우',
    code: '005930',
    currentPrice: '46,100원',
    change: '200원',
    changePercent: '+0.44%',
    isPositive: true
  }
];

export const tradingAmountStocks: RankingItem[] = [
  {
    rank: 1,
    name: '삼성전자',
    code: '005930',
    currentPrice: '55,700원',
    change: '300원',
    changePercent: '+0.54%',
    isPositive: true
  },
  {
    rank: 2,
    name: '삼성바이오로직스',
    code: '005930',
    currentPrice: '758,000원',
    change: '3,000원',
    changePercent: '+0.40%',
    isPositive: true
  },
  {
    rank: 3,
    name: '하나금융지주',
    code: '086790',
    currentPrice: '62,000원',
    change: '-200원',
    changePercent: '-0.32%',
    isPositive: false
  },
  {
    rank: 4,
    name: '삼성SDI',
    code: '005930',
    currentPrice: '365,500원',
    change: '-1,500원',
    changePercent: '-0.41%',
    isPositive: false
  },
  {
    rank: 5,
    name: '삼성전기',
    code: '005930',
    currentPrice: '157,300원',
    change: '900원',
    changePercent: '+0.58%',
    isPositive: true
  }
];

export const getRankingData = (tab: string): RankingItem[] => {
  switch (tab) {
    case '상승':
      return risingStocks;
    case '하락':
      return fallingStocks;
    case '인기':
      return popularStocks;
    case '거래량':
      return volumeStocks;
    case '거래대금':
      return tradingAmountStocks;
    default:
      return risingStocks;
  }
};
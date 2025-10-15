export interface InternationalIndex {
  name: string;
  value: string;
  change: string;
  changePercent: string;
  isPositive: boolean;
}

export const internationalIndices: InternationalIndex[] = [
  {
    name: 'S&P 500',
    value: '5,973.10',
    change: '44.82',
    changePercent: '+0.76%',
    isPositive: true
  },
  {
    name: '나스닥',
    value: '19,735.12',
    change: '157.69',
    changePercent: '+0.81%',
    isPositive: true
  },
  {
    name: '다우존스',
    value: '43,870.35',
    change: '188.94',
    changePercent: '+0.43%',
    isPositive: true
  },
  {
    name: '닛케이 225',
    value: '39,500.37',
    change: '-312.42',
    changePercent: '-0.78%',
    isPositive: false
  },
  {
    name: '상해종합',
    value: '3,391.74',
    change: '-28.65',
    changePercent: '-0.84%',
    isPositive: false
  },
  {
    name: '항셍',
    value: '19,846.88',
    change: '-153.78',
    changePercent: '-0.77%',
    isPositive: false
  }
];

export interface InternationalStock {
  rank: number;
  name: string;
  code: string;
  ticker: string;
  currentPrice: string;
  change: string;
  changePercent: string;
  isPositive: boolean;
}


export const usRisingStocks: InternationalStock[] = [
  {
    rank: 1,
    name: '엔비디아',
    code: 'NVDA',
    ticker: 'NVDA',
    currentPrice: '$149.43',
    change: '+8.24',
    changePercent: '+5.84%',
    isPositive: true
  },
  {
    rank: 2,
    name: '테슬라',
    code: 'TSLA',
    ticker: 'TSLA',
    currentPrice: '$413.64',
    change: '+15.32',
    changePercent: '+3.85%',
    isPositive: true
  },
  {
    rank: 3,
    name: '애플',
    code: 'AAPL',
    ticker: 'AAPL',
    currentPrice: '$227.79',
    change: '+5.12',
    changePercent: '+2.30%',
    isPositive: true
  },
  {
    rank: 4,
    name: '마이크로소프트',
    code: 'MSFT',
    ticker: 'MSFT',
    currentPrice: '$423.46',
    change: '+7.89',
    changePercent: '+1.90%',
    isPositive: true
  },
  {
    rank: 5,
    name: '아마존',
    code: 'AMZN',
    ticker: 'AMZN',
    currentPrice: '$219.27',
    change: '+3.45',
    changePercent: '+1.60%',
    isPositive: true
  }
];

export const usFallingStocks: InternationalStock[] = [
  {
    rank: 1,
    name: '인텔',
    code: 'INTC',
    ticker: 'INTC',
    currentPrice: '$22.18',
    change: '-1.05',
    changePercent: '-4.52%',
    isPositive: false
  },
  {
    rank: 2,
    name: '디즈니',
    code: 'DIS',
    ticker: 'DIS',
    currentPrice: '$112.42',
    change: '-3.21',
    changePercent: '-2.78%',
    isPositive: false
  },
  {
    rank: 3,
    name: '보잉',
    code: 'BA',
    ticker: 'BA',
    currentPrice: '$174.83',
    change: '-4.12',
    changePercent: '-2.30%',
    isPositive: false
  },
  {
    rank: 4,
    name: '페이팔',
    code: 'PYPL',
    ticker: 'PYPL',
    currentPrice: '$87.44',
    change: '-1.89',
    changePercent: '-2.12%',
    isPositive: false
  },
  {
    rank: 5,
    name: '넷플릭스',
    code: 'NFLX',
    ticker: 'NFLX',
    currentPrice: '$831.78',
    change: '-15.23',
    changePercent: '-1.80%',
    isPositive: false
  }
];


export const hkRisingStocks: InternationalStock[] = [
  {
    rank: 1,
    name: '텐센트',
    code: '0700.HK',
    ticker: '0700.HK',
    currentPrice: 'HK$408.60',
    change: '+12.40',
    changePercent: '+3.13%',
    isPositive: true
  },
  {
    rank: 2,
    name: '알리바바',
    code: '9988.HK',
    ticker: '9988.HK',
    currentPrice: 'HK$86.35',
    change: '+2.15',
    changePercent: '+2.55%',
    isPositive: true
  },
  {
    rank: 3,
    name: '샤오미',
    code: '1810.HK',
    ticker: '1810.HK',
    currentPrice: 'HK$26.85',
    change: '+0.60',
    changePercent: '+2.29%',
    isPositive: true
  },
  {
    rank: 4,
    name: 'BYD',
    code: '1211.HK',
    ticker: '1211.HK',
    currentPrice: 'HK$268.40',
    change: '+4.80',
    changePercent: '+1.82%',
    isPositive: true
  },
  {
    rank: 5,
    name: '메이투안',
    code: '3690.HK',
    ticker: '3690.HK',
    currentPrice: 'HK$177.90',
    change: '+2.70',
    changePercent: '+1.54%',
    isPositive: true
  }
];


export const cnRisingStocks: InternationalStock[] = [
  {
    rank: 1,
    name: '구이저우 마오타이',
    code: '600519.SS',
    ticker: '600519.SS',
    currentPrice: '¥1,598.00',
    change: '+45.32',
    changePercent: '+2.92%',
    isPositive: true
  },
  {
    rank: 2,
    name: 'CATL',
    code: '300750.SZ',
    ticker: '300750.SZ',
    currentPrice: '¥268.45',
    change: '+6.78',
    changePercent: '+2.59%',
    isPositive: true
  },
  {
    rank: 3,
    name: '중국평안보험',
    code: '601318.SS',
    ticker: '601318.SS',
    currentPrice: '¥53.67',
    change: '+1.12',
    changePercent: '+2.13%',
    isPositive: true
  },
  {
    rank: 4,
    name: '우링예',
    code: '000858.SZ',
    ticker: '000858.SZ',
    currentPrice: '¥27.89',
    change: '+0.52',
    changePercent: '+1.90%',
    isPositive: true
  },
  {
    rank: 5,
    name: '중국건설은행',
    code: '601939.SS',
    ticker: '601939.SS',
    currentPrice: '¥8.32',
    change: '+0.13',
    changePercent: '+1.59%',
    isPositive: true
  }
];


export const jpRisingStocks: InternationalStock[] = [
  {
    rank: 1,
    name: '소니',
    code: '6758.T',
    ticker: '6758.T',
    currentPrice: '¥3,208',
    change: '+125',
    changePercent: '+4.05%',
    isPositive: true
  },
  {
    rank: 2,
    name: '닌텐도',
    code: '7974.T',
    ticker: '7974.T',
    currentPrice: '¥7,854',
    change: '+234',
    changePercent: '+3.07%',
    isPositive: true
  },
  {
    rank: 3,
    name: '소프트뱅크',
    code: '9984.T',
    ticker: '9984.T',
    currentPrice: '¥9,127',
    change: '+198',
    changePercent: '+2.22%',
    isPositive: true
  },
  {
    rank: 4,
    name: '토요타',
    code: '7203.T',
    ticker: '7203.T',
    currentPrice: '¥2,894',
    change: '+52',
    changePercent: '+1.83%',
    isPositive: true
  },
  {
    rank: 5,
    name: '미쓰비시UFJ',
    code: '8306.T',
    ticker: '8306.T',
    currentPrice: '¥1,832',
    change: '+28',
    changePercent: '+1.55%',
    isPositive: true
  }
];


export const usPopularStocks = [...usRisingStocks];
export const hkPopularStocks = [...hkRisingStocks];
export const cnPopularStocks = [...cnRisingStocks];
export const jpPopularStocks = [...jpRisingStocks];


export const usVolumeStocks = [...usRisingStocks];
export const usTradingStocks = [...usRisingStocks];
export const hkVolumeStocks = [...hkRisingStocks];
export const hkTradingStocks = [...hkRisingStocks];
export const cnVolumeStocks = [...cnRisingStocks];
export const cnTradingStocks = [...cnRisingStocks];
export const jpVolumeStocks = [...jpRisingStocks];
export const jpTradingStocks = [...jpRisingStocks];

export const getInternationalRankingData = (country: string, tab: string): any[] => {
  const dataMap: { [key: string]: { [key: string]: InternationalStock[] } } = {
    '미국': {
      '상승': usRisingStocks,
      '하락': usFallingStocks,
      '인기': usPopularStocks,
      '거래량': usVolumeStocks,
      '거래대금': usTradingStocks
    },
    '홍콩': {
      '상승': hkRisingStocks,
      '하락': hkRisingStocks.map(stock => ({ ...stock, isPositive: false, changePercent: stock.changePercent.replace('+', '-') })),
      '인기': hkPopularStocks,
      '거래량': hkVolumeStocks,
      '거래대금': hkTradingStocks
    },
    '중국': {
      '상승': cnRisingStocks,
      '하락': cnRisingStocks.map(stock => ({ ...stock, isPositive: false, changePercent: stock.changePercent.replace('+', '-') })),
      '인기': cnPopularStocks,
      '거래량': cnVolumeStocks,
      '거래대금': cnTradingStocks
    },
    '일본': {
      '상승': jpRisingStocks,
      '하락': jpRisingStocks.map(stock => ({ ...stock, isPositive: false, changePercent: stock.changePercent.replace('+', '-') })),
      '인기': jpPopularStocks,
      '거래량': jpVolumeStocks,
      '거래대금': jpTradingStocks
    }
  };

  
  const data = dataMap[country]?.[tab] || usRisingStocks;
  return data.map(item => ({
    rank: item.rank,
    name: item.name,
    code: item.code || item.ticker, 
    currentPrice: item.currentPrice,
    change: item.change,
    changePercent: item.changePercent,
    isPositive: item.isPositive
  }));
};
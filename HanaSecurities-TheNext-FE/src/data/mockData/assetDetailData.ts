export interface Holding {
  code: string;
  name: string;
  amount: number;
  changeAmount: number;
  changePercent: number;
}

export interface AssetDetailData {
  title: string;
  totalAmount: number;
  changeAmount: number;
  changePercent: number;
  updateDate: string;
  updateTime: string;
  holdings: Holding[];
}

const bondsDetailData: AssetDetailData = {
  title: '채권',
  totalAmount: 1171824,
  changeAmount: 1423,
  changePercent: 0.12,
  updateDate: '09.12(금)',
  updateTime: '10:45',
  holdings: [
    {
      code: '43192142-01 위탁계좌',
      name: '국고01500-5003(20-2)',
      amount: 1552,
      changeAmount: -7,
      changePercent: -0.45
    },
    {
      code: '43192142-01 위탁계좌',
      name: '한국수출입금융2502바-할인-273',
      amount: 499899,
      changeAmount: 610,
      changePercent: 0.12
    },
    {
      code: '43192142-01 위탁계좌',
      name: '하나캐피탈385-2',
      amount: 670373,
      changeAmount: 820,
      changePercent: 0.12
    }
  ]
};

const fundsDetailData: AssetDetailData = {
  title: '펀드',
  totalAmount: 200001,
  changeAmount: 240,
  changePercent: 0.12,
  updateDate: '09.12(금)',
  updateTime: '10:45',
  holdings: [
    {
      code: '43192142-01 위탁계좌',
      name: '한국투자글로벌리더스펀드',
      amount: 120000,
      changeAmount: 144,
      changePercent: 0.12
    },
    {
      code: '43192142-01 위탁계좌',
      name: '미래에셋글로벌그레이트컨슈머펀드',
      amount: 80001,
      changeAmount: 96,
      changePercent: 0.12
    }
  ]
};

const depositsDetailData: AssetDetailData = {
  title: '예수금',
  totalAmount: 502049,
  changeAmount: 602,
  changePercent: 0.12,
  updateDate: '09.12(금)',
  updateTime: '10:45',
  holdings: [
    {
      code: '43192142-01 위탁계좌',
      name: '위탁계좌 예수금',
      amount: 302049,
      changeAmount: 362,
      changePercent: 0.12
    },
    {
      code: '43192142-02 ISA계좌',
      name: 'ISA계좌 예수금',
      amount: 200000,
      changeAmount: 240,
      changePercent: 0.12
    }
  ]
};

const foreignDepositsDetailData: AssetDetailData = {
  title: '외화예수금',
  totalAmount: 138995,
  changeAmount: 167,
  changePercent: 0.12,
  updateDate: '09.12(금)',
  updateTime: '10:45',
  holdings: [
    {
      code: '43192142-01 위탁계좌',
      name: 'USD 예수금',
      amount: 100000,
      changeAmount: 120,
      changePercent: 0.12
    },
    {
      code: '43192142-01 위탁계좌',
      name: 'EUR 예수금',
      amount: 38995,
      changeAmount: 47,
      changePercent: 0.12
    }
  ]
};

const stockDetailData: AssetDetailData = {
  title: '국내주식',
  totalAmount: 3725,
  changeAmount: 4,
  changePercent: 0.11,
  updateDate: '09.12(금)',
  updateTime: '10:45',
  holdings: [
    {
      code: '43192142-01 위탁계좌',
      name: '삼성전자',
      amount: 2000,
      changeAmount: 2,
      changePercent: 0.10
    },
    {
      code: '43192142-01 위탁계좌',
      name: 'SK하이닉스',
      amount: 1725,
      changeAmount: 2,
      changePercent: 0.12
    }
  ]
};

const foreignBondsDetailData: AssetDetailData = {
  title: '해외채권',
  totalAmount: 850000,
  changeAmount: 1020,
  changePercent: 0.12,
  updateDate: '09.12(금)',
  updateTime: '10:45',
  holdings: [
    {
      code: '43192142-01 위탁계좌',
      name: 'US Treasury 10Y',
      amount: 500000,
      changeAmount: 600,
      changePercent: 0.12
    },
    {
      code: '43192142-01 위탁계좌',
      name: 'German Bund 5Y',
      amount: 350000,
      changeAmount: 420,
      changePercent: 0.12
    }
  ]
};

const foreignStocksDetailData: AssetDetailData = {
  title: '해외주식',
  totalAmount: 1250000,
  changeAmount: 15000,
  changePercent: 1.21,
  updateDate: '09.12(금)',
  updateTime: '10:45',
  holdings: [
    {
      code: '43192142-01 위탁계좌',
      name: 'Apple Inc.',
      amount: 750000,
      changeAmount: 9000,
      changePercent: 1.21
    },
    {
      code: '43192142-01 위탁계좌',
      name: 'Microsoft Corp.',
      amount: 500000,
      changeAmount: 6000,
      changePercent: 1.21
    }
  ]
};

export const getAssetTypeOptions = (currentType: string) => {
  
  const allOptions = {
    bonds: [
      { id: 'bonds', label: '채권' },
      { id: 'funds', label: '펀드' },
      { id: 'foreign-bonds', label: '해외채권' }
    ],
    funds: [
      { id: 'bonds', label: '채권' },
      { id: 'funds', label: '펀드' },
      { id: 'foreign-bonds', label: '해외채권' }
    ],
    'foreign-bonds': [
      { id: 'bonds', label: '채권' },
      { id: 'funds', label: '펀드' },
      { id: 'foreign-bonds', label: '해외채권' }
    ],
    deposits: [
      { id: 'deposits', label: '예수금' },
      { id: 'foreign-deposits', label: '외화예수금' }
    ],
    'foreign-deposits': [
      { id: 'deposits', label: '예수금' },
      { id: 'foreign-deposits', label: '외화예수금' }
    ],
    stocks: [
      { id: 'stocks', label: '국내주식' },
      { id: 'foreign-stocks', label: '해외주식' }
    ],
    'foreign-stocks': [
      { id: 'stocks', label: '국내주식' },
      { id: 'foreign-stocks', label: '해외주식' }
    ]
  };

  
  const options = allOptions[currentType] || allOptions.bonds;

  
  return options.map(option => ({
    ...option,
    isSelected: option.id === currentType
  }));
};

export const getAssetDetailData = (type: string): AssetDetailData => {
  switch (type) {
    case 'bonds':
      return bondsDetailData;
    case 'foreign-bonds':
      return foreignBondsDetailData;
    case 'funds':
      return fundsDetailData;
    case 'deposits':
      return depositsDetailData;
    case 'foreign-deposits':
      return foreignDepositsDetailData;
    case 'stocks':
      return stockDetailData;
    case 'foreign-stocks':
      return foreignStocksDetailData;
    default:
      return bondsDetailData;
  }
};
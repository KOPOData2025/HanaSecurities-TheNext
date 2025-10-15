export interface AssetData {
  totalAmount: number;
  changeAmount: number;
  changePercent: number;
  stock: {
    domestic: {
      amount: number;
      percent: number;
    };
  };
  financialProducts: {
    securities: {
      amount: number;
      percent: number;
    };
    funds: {
      amount: number;
      percent: number;
    };
  };
  cash: {
    deposits: {
      amount: number;
      percent: number;
    };
    foreignCurrency: {
      amount: number;
      percent: number;
    };
  };
}

export const assetData: AssetData = {
  totalAmount: 2016594,
  changeAmount: 1523,
  changePercent: 0.11,
  stock: {
    domestic: {
      amount: 3725,
      percent: 100
    }
  },
  financialProducts: {
    securities: {
      amount: 1171824,
      percent: 85
    },
    funds: {
      amount: 200001,
      percent: 15
    }
  },
  cash: {
    deposits: {
      amount: 502049,
      percent: 78
    },
    foreignCurrency: {
      amount: 138995,
      percent: 22
    }
  }
};

export const assetChartData = {
  stockPercent: 0, 
  financialPercent: 68, 
  cashPercent: 32 
};

export const assetTabs = ['전체', '주식', '금융상품', '현금성'];
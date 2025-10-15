export interface ETPItem {
  rank: number;
  name: string;
  code: string;
  price: string;
  change: string;
  changePercent: string;
  isPositive: boolean;
  type: 'ETF' | 'ETN';
  iconText?: {
    line1: string;
    line2: string;
  };
}

export const getETFRankingData = (): ETPItem[] => {
  return [
    {
      rank: 1,
      name: "KODEX 200",
      code: "069500",
      price: "45,345",
      change: "+176",
      changePercent: "+0.39%",
      isPositive: true,
      type: 'ETF',
      iconText: {
        line1: "코스피",
        line2: "200"
      }
    },
    {
      rank: 2,
      name: "KODEX 미국S&P500",
      code: "379800",
      price: "72,350",
      change: "+450",
      changePercent: "+0.63%",
      isPositive: true,
      type: 'ETF',
      iconText: {
        line1: "S&P",
        line2: "500"
      }
    },
    {
      rank: 3,
      name: "KODEX 코스닥150레버리지",
      code: "233740",
      price: "15,785",
      change: "+285",
      changePercent: "+1.84%",
      isPositive: true,
      type: 'ETF',
      iconText: {
        line1: "2X",
        line2: ""
      }
    },
    {
      rank: 4,
      name: "TIGER 차이나전기차",
      code: "371460",
      price: "13,975",
      change: "+346",
      changePercent: "+2.54%",
      isPositive: true,
      type: 'ETF',
      iconText: {
        line1: "중국",
        line2: "EV"
      }
    },
    {
      rank: 5,
      name: "ARIRANG 200",
      code: "152100",
      price: "45,280",
      change: "+165",
      changePercent: "+0.37%",
      isPositive: true,
      type: 'ETF',
      iconText: {
        line1: "코스피",
        line2: "200"
      }
    },
    {
      rank: 6,
      name: "KODEX 은행",
      code: "091170",
      price: "6,420",
      change: "-80",
      changePercent: "-1.23%",
      isPositive: false,
      type: 'ETF',
      iconText: {
        line1: "은행",
        line2: ""
      }
    },
    {
      rank: 7,
      name: "KODEX 미국S&P500",
      code: "379800",
      price: "72,350",
      change: "+450",
      changePercent: "+0.63%",
      isPositive: true,
      type: 'ETF',
      iconText: {
        line1: "S&P",
        line2: "500"
      }
    },
    {
      rank: 8,
      name: "KODEX 삼성그룹",
      code: "102780",
      price: "10,845",
      change: "+95",
      changePercent: "+0.88%",
      isPositive: true,
      type: 'ETF',
      iconText: {
        line1: "삼성",
        line2: ""
      }
    },
    {
      rank: 9,
      name: "TIGER 반도체",
      code: "091230",
      price: "17,520",
      change: "-320",
      changePercent: "-1.79%",
      isPositive: false,
      type: 'ETF',
      iconText: {
        line1: "반도체",
        line2: ""
      }
    },
    {
      rank: 10,
      name: "KODEX 코스닥150레버리지",
      code: "233740",
      price: "15,785",
      change: "+285",
      changePercent: "+1.84%",
      isPositive: true,
      type: 'ETF',
      iconText: {
        line1: "2X",
        line2: ""
      }
    }
  ];
};

export const getETNRankingData = (): ETPItem[] => {
  return [
    {
      rank: 1,
      name: "신한 인버스 2X WTI원유",
      code: "500032",
      price: "1,845",
      change: "+125",
      changePercent: "+7.27%",
      isPositive: true,
      type: 'ETN',
      iconText: {
        line1: "WTI",
        line2: "-2X"
      }
    },
    {
      rank: 2,
      name: "삼성 레버리지 WTI원유",
      code: "530031",
      price: "3,975",
      change: "-46",
      changePercent: "-1.14%",
      isPositive: false,
      type: 'ETN',
      iconText: {
        line1: "WTI",
        line2: "2X"
      }
    },
    {
      rank: 3,
      name: "미래에셋 인버스 2X 니켈",
      code: "520016",
      price: "2,420",
      change: "+80",
      changePercent: "+3.42%",
      isPositive: true,
      type: 'ETN',
      iconText: {
        line1: "니켈",
        line2: "-2X"
      }
    },
    {
      rank: 4,
      name: "KB 인버스 천연가스",
      code: "590029",
      price: "5,545",
      change: "+205",
      changePercent: "+3.84%",
      isPositive: true,
      type: 'ETN',
      iconText: {
        line1: "가스",
        line2: "-1X"
      }
    },
    {
      rank: 5,
      name: "신한 레버리지 구리",
      code: "500034",
      price: "4,785",
      change: "-155",
      changePercent: "-3.14%",
      isPositive: false,
      type: 'ETN',
      iconText: {
        line1: "구리",
        line2: "2X"
      }
    },
    {
      rank: 6,
      name: "삼성 인버스 2X 금",
      code: "530033",
      price: "1,320",
      change: "+45",
      changePercent: "+3.53%",
      isPositive: true,
      type: 'ETN'
    },
    {
      rank: 7,
      name: "미래에셋 레버리지 은",
      code: "520017",
      price: "3,850",
      change: "+120",
      changePercent: "+3.22%",
      isPositive: true,
      type: 'ETN'
    },
    {
      rank: 8,
      name: "KB 레버리지 WTI원유",
      code: "590027",
      price: "2,965",
      change: "-85",
      changePercent: "-2.79%",
      isPositive: false,
      type: 'ETN'
    },
    {
      rank: 9,
      name: "신한 인버스 팔라듐",
      code: "500035",
      price: "6,720",
      change: "+280",
      changePercent: "+4.35%",
      isPositive: true,
      type: 'ETN'
    },
    {
      rank: 10,
      name: "삼성 레버리지 천연가스",
      code: "530032",
      price: "1,985",
      change: "-65",
      changePercent: "-3.17%",
      isPositive: false,
      type: 'ETN'
    }
  ];
};
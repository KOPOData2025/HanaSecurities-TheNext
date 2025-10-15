export interface WatchlistItem {
  name: string;
  code: string;
  currentPrice: string;
  change: string;
  changePercent: string;
  isPositive: boolean;
  hasAlert?: boolean;
}

export const watchlistData: WatchlistItem[] = [
  {
    name: "1Q 미국메디컬AI",
    code: "1Q",
    currentPrice: "11,205",
    change: "535",
    changePercent: "5.01%",
    isPositive: true,
    hasAlert: true
  },
  {
    name: "삼성전자",
    code: "005930",
    currentPrice: "71,200",
    change: "0",
    changePercent: "0.00%",
    isPositive: false
  },
  {
    name: "1Q 중단기회사채(합성)",
    code: "1QBOND",
    currentPrice: "50,440",
    change: "320",
    changePercent: "-0.63%",
    isPositive: false
  },
  {
    name: "엔에스씨",
    code: "NSCI",
    currentPrice: "12,490",
    change: "2,880",
    changePercent: "29.97%",
    isPositive: true,
    hasAlert: true
  },
  {
    name: "LG화학",
    code: "051910",
    currentPrice: "435,500",
    change: "0",
    changePercent: "0.00%",
    isPositive: false
  },
  {
    name: "1Q 미니엠캣엣터보",
    code: "1QMINI",
    currentPrice: "52,660",
    change: "1,250",
    changePercent: "-2.32%",
    isPositive: false
  },
  {
    name: "1Q 차이나H(H)",
    code: "1QCH",
    currentPrice: "17,170",
    change: "250",
    changePercent: "1.48%",
    isPositive: true,
    hasAlert: true
  },
  {
    name: "KODEX 미국S&P500",
    code: "KODEXSP",
    currentPrice: "20,860",
    change: "95",
    changePercent: "0.46%",
    isPositive: true
  },
  {
    name: "PLUS 미국사이버이커머스",
    code: "PLUSCYBER",
    currentPrice: "9,275",
    change: "70",
    changePercent: "0.76%",
    isPositive: true
  },
  {
    name: "KB금융",
    code: "105560",
    currentPrice: "82,100",
    change: "0",
    changePercent: "0.00%",
    isPositive: false
  },
  {
    name: "TIGER 엔비디아",
    code: "TIGERNVDA",
    currentPrice: "9,785",
    change: "65",
    changePercent: "0.67%",
    isPositive: true
  },
  {
    name: "PLUS 글로벌원자력",
    code: "PLUSNUCLEAR",
    currentPrice: "13,525",
    change: "25",
    changePercent: "0.18%",
    isPositive: true
  },
  {
    name: "SOL 말레이시아",
    code: "SOLMALAY",
    currentPrice: "11,435",
    change: "40",
    changePercent: "0.35%",
    isPositive: true
  },

  {
    name: "셀트리온",
    code: "068270",
    currentPrice: "186,700",
    change: "0",
    changePercent: "0.00%",
    isPositive: false
  },
  {
    name: "KODEX 미국AI산업",
    code: "KODEXAI",
    currentPrice: "15,225",
    change: "270",
    changePercent: "1.74%",
    isPositive: true
  },
  {
    name: "코오롱모빌리티그룹",
    code: "KMOBILITY",
    currentPrice: "5,890",
    change: "1,335",
    changePercent: "29.31%",
    isPositive: true,
    hasAlert: true
  },
  {
    name: "하나 S&P 인버스",
    code: "HANAINV",
    currentPrice: "15,010",
    change: "685",
    changePercent: "4.78%",
    isPositive: true,
    hasAlert: true
  },
  {
    name: "에스엘랭증권15-2",
    code: "SL15-2",
    currentPrice: "10,035.00",
    change: "14.00",
    changePercent: "0.14%",
    isPositive: true
  },
  {
    name: "iSHARES GOLD",
    code: "ISHGOLD",
    currentPrice: "68,530.0",
    change: "0,110.0",
    changePercent: "0.16%",
    isPositive: true
  },
  {
    name: "한국전력1276",
    code: "KEPCO1276",
    currentPrice: "10,240.00",
    change: "5.10",
    changePercent: "0.05%",
    isPositive: true
  },
  {
    name: "하나 Solactive 2차전지",
    code: "HANABATT",
    currentPrice: "119,780",
    change: "3,540",
    changePercent: "-2.87%",
    isPositive: false
  },
  {
    name: "하나 CD금리투자채권(합성)",
    code: "HANACD",
    currentPrice: "104,800",
    change: "450",
    changePercent: "-0.43%",
    isPositive: false
  },
  {
    name: "하나 레버리지 골드",
    code: "HANAGOLD",
    currentPrice: "19,610",
    change: "195",
    changePercent: "1.00%",
    isPositive: true,
    hasAlert: true
  },
  {
    name: "하나 레버리지 반도체",
    code: "HANASEMI",
    currentPrice: "32,750",
    change: "1,900",
    changePercent: "6.16%",
    isPositive: true,
    hasAlert: true
  },
  {
    name: "1Q 단기금융채권(합성)",
    code: "1QSHORT",
    currentPrice: "108,285",
    change: "10",
    changePercent: "0.01%",
    isPositive: true
  },
  {
    name: "RISE 비코서포트",
    code: "RISEBC",
    currentPrice: "12,957",
    change: "57",
    changePercent: "0.44%",
    isPositive: true
  },
  {
    name: "비크서 액서버어 A",
    code: "BCXA",
    currentPrice: "45,001.46",
    change: "7,007.41",
    changePercent: "0.95%",
    isPositive: true,
    hasAlert: true
  },
  {
    name: "TIMEFOLIO 글로벌메타버스",
    code: "TIMEMETA",
    currentPrice: "18,530",
    change: "200",
    changePercent: "1.09%",
    isPositive: true,
    hasAlert: true
  },
  {
    name: "PLUS K방산",
    code: "PLUSKDEF",
    currentPrice: "54,540",
    change: "60",
    changePercent: "0.11%",
    isPositive: true
  },
  {
    name: "하나금융지주",
    code: "086790",
    currentPrice: "88,900",
    change: "1,200",
    changePercent: "1.37%",
    isPositive: true,
    hasAlert: true
  },
  {
    name: "ACE KRX금현물",
    code: "ACEKRX",
    currentPrice: "23,315",
    change: "25",
    changePercent: "0.11%",
    isPositive: true
  },
  {
    name: "WISDOMTREE M",
    code: "WISDOM",
    currentPrice: "37,490.0",
    change: "0,466.2",
    changePercent: "1.26%",
    isPositive: true,
    hasAlert: true
  },
  {
    name: "한국전력1304",
    code: "KEPCO1304",
    currentPrice: "10,266.60",
    change: "0.00",
    changePercent: "0.00%",
    isPositive: false
  },
  {
    name: "국고01125-3909",
    code: "GOVT01125",
    currentPrice: "8,077.00",
    change: "9.00",
    changePercent: "0.11%",
    isPositive: true
  },
  {
    name: "경익들디스",
    code: "KYUNGIK",
    currentPrice: "11,270",
    change: "990",
    changePercent: "8.08%",
    isPositive: true,
    hasAlert: true
  },
  {
    name: "KODEX 27-12 회사채",
    code: "KODEX2712",
    currentPrice: "10,290",
    change: "780",
    changePercent: "-7.05%",
    isPositive: false
  },
  {
    name: "동일스틸텍스",
    code: "DONGIL",
    currentPrice: "3,750",
    change: "105",
    changePercent: "2.72%",
    isPositive: true
  },
  {
    name: "코아스",
    code: "COAS",
    currentPrice: "6,780",
    change: "520",
    changePercent: "7.12%",
    isPositive: true,
    hasAlert: true
  },
  {
    name: "삼성전자",
    code: "005930",
    currentPrice: "71,200",
    change: "0",
    changePercent: "0.00%",
    isPositive: false
  },
  {
    name: "LG화학",
    code: "051910",
    currentPrice: "435,500",
    change: "0",
    changePercent: "0.00%",
    isPositive: false
  },
  {
    name: "SK하이닉스",
    code: "000660",
    currentPrice: "178,900",
    change: "0",
    changePercent: "0.00%",
    isPositive: false
  },
  {
    name: "NAVER",
    code: "035420",
    currentPrice: "213,500",
    change: "0",
    changePercent: "0.00%",
    isPositive: false
  },
  {
    name: "카카오",
    code: "035720",
    currentPrice: "45,850",
    change: "0",
    changePercent: "0.00%",
    isPositive: false
  },
  {
    name: "현대차",
    code: "005380",
    currentPrice: "245,000",
    change: "0",
    changePercent: "0.00%",
    isPositive: false
  },
  {
    name: "기아",
    code: "000270",
    currentPrice: "112,300",
    change: "0",
    changePercent: "0.00%",
    isPositive: false
  },
  {
    name: "POSCO홀딩스",
    code: "005490",
    currentPrice: "398,000",
    change: "0",
    changePercent: "0.00%",
    isPositive: false
  }
];


export const getFilteredWatchlist = (filter: string): WatchlistItem[] => {
  switch (filter) {
    case '전체':
      return watchlistData;
    case '인증':
      return watchlistData.filter(item => ['1Q', 'KODEX', 'TIGER', 'PLUS', 'SOL', 'ACE', 'RISE', 'TIMEFOLIO'].some(prefix => item.code.includes(prefix)));
    case '현재가':
      return [...watchlistData].sort((a, b) => {
        const priceA = parseInt(a.currentPrice.replace(/,/g, '').replace('.', ''));
        const priceB = parseInt(b.currentPrice.replace(/,/g, '').replace('.', ''));
        return priceB - priceA;
      });
    case '대비':
      return [...watchlistData].sort((a, b) => {
        const changeA = parseFloat(a.changePercent.replace('%', ''));
        const changeB = parseFloat(b.changePercent.replace('%', ''));
        return changeB - changeA;
      });
    case '등락률':
      return [...watchlistData].sort((a, b) => {
        const changeA = Math.abs(parseFloat(a.changePercent.replace('%', '')));
        const changeB = Math.abs(parseFloat(b.changePercent.replace('%', '')));
        return changeB - changeA;
      });
    default:
      return watchlistData;
  }
};
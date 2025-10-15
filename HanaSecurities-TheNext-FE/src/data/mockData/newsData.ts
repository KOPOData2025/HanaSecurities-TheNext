export interface NewsItem {
  id: number;
  title: string;
  source: string;
  time: string;
  image: string;
}

export const newsData: NewsItem[] = [
  {
    id: 1,
    title: '금투협회장 "혁신만금 \'신뢰\' 중요...투자자 보호 최우선"',
    source: '뉴시스',
    time: '오전 10:42',
    image: '/stockIcon/086790.png'
  },
  {
    id: 2,
    title: '\'초유의 \'구금사태\' 떨고 있는 기업..."앞으로가 더 걱정"',
    source: '한국경제TV',
    time: '오전 10:38',
    image: '/stockIcon/086790.png'
  },
  {
    id: 3,
    title: '"갱신 다가오는데 왜 전세 연장 대출 2억밖에 안돼요?" [머니뭐니]',
    source: '헤럴드경제',
    time: '오전 10:36',
    image: '/stockIcon/086790.png'
  },
  {
    id: 4,
    title: '이찬진 "금융감독 개편 매우 안타까워...직원 처우 개선 노력할 것"',
    source: '서경',
    time: '오전 10:32',
    image: '/stockIcon/086790.png'
  },
  {
    id: 5,
    title: '방향성 탐색하고 있는 코스피...3210선 강보합세 [시황]',
    source: '데일리안',
    time: '오전 10:06',
    image: '/stockIcon/086790.png'
  },
  {
    id: 6,
    title: '하나금융, 4분기 실적 전망 상향...목표주가 8만원',
    source: '한국경제',
    time: '오전 09:45',
    image: '/stockIcon/086790.png'
  },
  {
    id: 7,
    title: '삼성전자, AI 반도체 수요 증가로 실적 개선 기대',
    source: '매일경제',
    time: '오전 09:30',
    image: '/stockIcon/086790.png'
  },
  {
    id: 8,
    title: '금융위, 가계대출 관리 강화...DSR 규제 단계적 시행',
    source: '연합뉴스',
    time: '오전 09:15',
    image: '/stockIcon/086790.png'
  }
];

export const getLatestNews = (count: number = 5): NewsItem[] => {
  return newsData.slice(0, count);
};

export const getNewsBySource = (source: string): NewsItem[] => {
  return newsData.filter(news => news.source === source);
};

export const getNewsById = (id: number): NewsItem | undefined => {
  return newsData.find(news => news.id === id);
};

export const internationalNews: NewsItem[] = [
  {
    id: 1,
    title: "Fed 금리 동결 시사, 글로벌 증시 상승세",
    source: "Bloomberg",
    time: "30분 전",
    image: "/stockIcon/086790.png",
  },
  {
    id: 2,
    title: "엔비디아, AI 칩 신제품 발표로 주가 급등",
    source: "Reuters",
    time: "1시간 전",
    image: "/stockIcon/086790.png",
  },
  {
    id: 3,
    title: "일본 닛케이, 엔화 약세로 수출주 강세",
    source: "Nikkei",
    time: "2시간 전",
    image: "/stockIcon/086790.png",
  },
  {
    id: 4,
    title: "중국 경기부양책 발표, 항셍지수 반등",
    source: "CNBC",
    time: "3시간 전",
    image: "/stockIcon/086790.png",
  },
  {
    id: 5,
    title: "유럽 ECB, 추가 금리인상 가능성 시사",
    source: "FT",
    time: "4시간 전",
    image: "/stockIcon/086790.png",
  },
];
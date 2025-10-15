export interface BondItem {
  rank: number;
  name: string;
  yield: string;
  maturity: string;
  changePercent: string;
  isPositive: boolean;
  icon?: string;
  emoji?: string;
}

export const getBondRankingData = (): BondItem[] => {
  return [
    {
      rank: 1,
      name: "미국 국채 10년",
      yield: "4.25%",
      maturity: "2034.03",
      changePercent: "+0.12%",
      isPositive: true,
      emoji: "🇺🇸"
    },
    {
      rank: 2,
      name: "일본 국채 10년",
      yield: "0.85%",
      maturity: "2034.06",
      changePercent: "+0.08%",
      isPositive: true,
      emoji: "🇯🇵"
    },
    {
      rank: 3,
      name: "한국 국고채 5년",
      yield: "3.45%",
      maturity: "2029.09",
      changePercent: "+0.05%",
      isPositive: true,
      emoji: "🇰🇷"
    },
    {
      rank: 4,
      name: "홍콩 국채 10년",
      yield: "3.95%",
      maturity: "2034.12",
      changePercent: "-0.02%",
      isPositive: false,
      emoji: "🇭🇰"
    },
    {
      rank: 5,
      name: "중국 국채 10년",
      yield: "2.28%",
      maturity: "2034.07",
      changePercent: "+0.15%",
      isPositive: true,
      emoji: "🇨🇳"
    },
    {
      rank: 6,
      name: "특수채 3년",
      yield: "3.78%",
      maturity: "2027.11",
      changePercent: "+0.10%",
      isPositive: true
    },
    {
      rank: 7,
      name: "회사채 A+ 5년",
      yield: "4.25%",
      maturity: "2029.05",
      changePercent: "-0.05%",
      isPositive: false
    },
    {
      rank: 8,
      name: "국고채권 3년",
      yield: "3.38%",
      maturity: "2027.03",
      changePercent: "+0.07%",
      isPositive: true
    },
    {
      rank: 9,
      name: "금융채 2년",
      yield: "3.55%",
      maturity: "2026.08",
      changePercent: "+0.03%",
      isPositive: true
    },
    {
      rank: 10,
      name: "국고채권 20년",
      yield: "3.65%",
      maturity: "2044.06",
      changePercent: "-0.08%",
      isPositive: false
    }
  ];
};
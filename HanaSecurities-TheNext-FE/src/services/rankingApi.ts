import type { RankingResponse, RankingType, MarketType } from '../types/ranking.types';

const BASE_URL = '/api';

export const rankingApi = {
  /**
   * 실시간 주식 랭킹 조회
   * @param type 랭킹 타입 (거래량, 거래대금, 상승, 하락)
   * @param market 시장 구분 (J: 코스피, Q: 코스닥)
   * @returns 랭킹 데이터
   */
  getRanking: async (type: RankingType, market: MarketType = 'J'): Promise<RankingResponse> => {
    const response = await fetch(`${BASE_URL}/ranking?type=${type}&market=${market}`);
    if (!response.ok) {
      throw new Error(`Failed to fetch ranking data for ${type}`);
    }
    return response.json();
  },

  /**
   * 모든 랭킹 데이터 조회 (거래량, 거래대금, 상승, 하락)
   * @param market 시장 구분 (J: 코스피, Q: 코스닥)
   * @returns 모든 랭킹 데이터
   */
  getAllRankings: async (market: MarketType = 'J'): Promise<{
    volume: RankingResponse;
    tradingValue: RankingResponse;
    rise: RankingResponse;
    fall: RankingResponse;
  }> => {
    const [volume, tradingValue, rise, fall] = await Promise.all([
      rankingApi.getRanking('VOLUME', market),
      rankingApi.getRanking('TRADING_VALUE', market),
      rankingApi.getRanking('RISE', market),
      rankingApi.getRanking('FALL', market)
    ]);

    return { volume, tradingValue, rise, fall };
  }
};
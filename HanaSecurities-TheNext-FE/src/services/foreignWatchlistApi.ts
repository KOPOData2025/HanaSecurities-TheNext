import type {
  ForeignWatchlistResponse,
  ForeignWatchlistAddRequest,
  ForeignWatchlistActionResponse
} from '../types/foreignStock.types';

const BASE_URL = '/api';

export const foreignWatchlistApi = {
  /**
   * 해외 관심종목 목록 조회
   * @param userId 사용자 ID
   */
  getWatchlist: async (userId: number): Promise<ForeignWatchlistResponse> => {
    const response = await fetch(`${BASE_URL}/foreign-watchlist?userId=${userId}`);
    if (!response.ok) {
      throw new Error('Failed to fetch foreign watchlist');
    }
    return response.json();
  },

  /**
   * 해외 관심종목 추가
   * @param request 관심종목 추가 요청 데이터
   */
  addWatchlist: async (request: ForeignWatchlistAddRequest): Promise<ForeignWatchlistActionResponse> => {
    const response = await fetch(`${BASE_URL}/foreign-watchlist`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(request),
    });
    if (!response.ok) {
      throw new Error('Failed to add foreign watchlist item');
    }
    return response.json();
  },

  /**
   * 해외 관심종목 삭제
   * @param userId 사용자 ID
   * @param exchangeCode 거래소코드 (NAS/NYS/HKS/TSE)
   * @param stockCode 종목코드
   */
  removeWatchlist: async (
    userId: number,
    exchangeCode: string,
    stockCode: string
  ): Promise<ForeignWatchlistActionResponse> => {
    const response = await fetch(
      `${BASE_URL}/foreign-watchlist?userId=${userId}&exchangeCode=${exchangeCode}&stockCode=${stockCode}`,
      {
        method: 'DELETE',
      }
    );
    if (!response.ok) {
      throw new Error('Failed to remove foreign watchlist item');
    }
    return response.json();
  },
};

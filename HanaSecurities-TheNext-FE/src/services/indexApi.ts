import type { IndexPriceResponse, IndexTimePriceResponse, IndexType } from '../types/index.types';

const BASE_URL = '/api';

export const indexApi = {
  /**
   * 지수 현재가 조회
   * @param indexType 지수 타입 (kospi, kosdaq, kospi200)
   * @returns 지수 현재가 정보
   */
  getIndexPrice: async (indexType: IndexType): Promise<IndexPriceResponse> => {
    const response = await fetch(`${BASE_URL}/index/price?indexType=${indexType}`);
    if (!response.ok) {
      throw new Error(`Failed to fetch index price for ${indexType}`);
    }
    return response.json();
  },

  /**
   * 지수 시간별 가격 조회
   * @param indexType 지수 타입 (kospi, kosdaq, kospi200)
   * @returns 지수 시간별 가격 정보
   */
  getIndexTimePrice: async (indexType: IndexType): Promise<IndexTimePriceResponse> => {
    const response = await fetch(`${BASE_URL}/index/timeprice?indexType=${indexType}`);
    if (!response.ok) {
      throw new Error(`Failed to fetch time prices for ${indexType}`);
    }
    return response.json();
  },

  /**
   * 모든 지수 현재가 조회 (KOSPI, KOSDAQ, KOSPI200)
   * @returns KOSPI, KOSDAQ, KOSPI200 현재가 정보
   */
  getAllIndexPrices: async (): Promise<{
    kospi: IndexPriceResponse;
    kosdaq: IndexPriceResponse;
    kospi200: IndexPriceResponse;
  }> => {
    const [kospi, kosdaq, kospi200] = await Promise.all([
      indexApi.getIndexPrice('kospi'),
      indexApi.getIndexPrice('kosdaq'),
      indexApi.getIndexPrice('kospi200')
    ]);

    return { kospi, kosdaq, kospi200 };
  },

  /**
   * 모든 지수 시간별 가격 조회 (KOSPI, KOSDAQ, KOSPI200)
   * @returns KOSPI, KOSDAQ, KOSPI200 시간별 가격 정보
   */
  getAllIndexTimePrices: async (): Promise<{
    kospi: IndexTimePriceResponse;
    kosdaq: IndexTimePriceResponse;
    kospi200: IndexTimePriceResponse;
  }> => {
    const [kospi, kosdaq, kospi200] = await Promise.all([
      indexApi.getIndexTimePrice('kospi'),
      indexApi.getIndexTimePrice('kosdaq'),
      indexApi.getIndexTimePrice('kospi200')
    ]);

    return { kospi, kosdaq, kospi200 };
  }
};
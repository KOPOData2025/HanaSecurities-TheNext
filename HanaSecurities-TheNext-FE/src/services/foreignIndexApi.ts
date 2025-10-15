const BASE_URL = '/api';

/**
 * 해외 지수 시간별 가격 데이터
 */
export interface ForeignIndexTimePrice {
  /** 시간 */
  time: string;
  /** 가격 */
  price: string;
  /** 등락가 */
  change_price: string;
  /** 등락률 */
  change_rate: string;
  /** 등락 부호 */
  change_sign: string;
  /** 거래량 */
  volume: string;
  /** 거래대금 */
  trading_value: string;
}

/**
 * 해외 지수 응답 데이터
 */
export interface ForeignIndexResponse {
  /** 지수 심볼 */
  index_symbol: string;
  /** 지수명 */
  index_name: string;
  /** 국가 코드 */
  country_code: string;
  /** 시간별 가격 목록 */
  time_prices: ForeignIndexTimePrice[];
}

/** 지수 타입 (S&P500, 상하이종합, 유로스톡스50, 항셍중국기업) */
export type IndexType = 'SPX' | 'SHANG' | 'SX5E' | 'HSCE';

export const foreignIndexApi = {
  /**
   * 해외 지수 시간별 가격 조회
   * @param indexType 지수 타입
   * @returns 지수 시간별 가격 데이터
   */
  getIndexTimePrice: async (indexType: IndexType): Promise<ForeignIndexResponse> => {
    const response = await fetch(`${BASE_URL}/foreign-index/timeprice?indexType=${indexType}`);
    if (!response.ok) {
      throw new Error(`Failed to fetch ${indexType} data`);
    }
    return response.json();
  },

  /**
   * 모든 해외 지수 조회
   * @returns 모든 지수의 시간별 가격 데이터
   */
  getAllIndices: async (): Promise<{ [key: string]: ForeignIndexResponse }> => {
    const indices: IndexType[] = ['SPX', 'SHANG', 'SX5E', 'HSCE'];
    const results: { [key: string]: ForeignIndexResponse } = {};

    const promises = indices.map(index => foreignIndexApi.getIndexTimePrice(index));
    const responses = await Promise.all(promises);

    indices.forEach((index, i) => {
      results[index] = responses[i];
    });

    return results;
  }
};
import type { NewsResponse, NewsSummaryResponse } from '../types/news.types';

const BASE_URL = '/api';

export const newsApi = {
  /**
   * 뉴스 검색
   * @param query 검색어
   * @param display 검색 결과 출력 건수 (기본값: 30)
   */
  getNews: async (query: string, display: number = 30): Promise<NewsResponse> => {
    const response = await fetch(
      `${BASE_URL}/news?query=${encodeURIComponent(query)}&display=${display}`
    );
    if (!response.ok) {
      throw new Error(`Failed to fetch news for query: ${query}`);
    }
    return response.json();
  },

  /**
   * 뉴스 요약 조회
   * @param link 네이버 뉴스 링크
   */
  getNewsSummary: async (link: string): Promise<NewsSummaryResponse> => {
    const response = await fetch(
      `${BASE_URL}/news/summary?link=${encodeURIComponent(link)}`
    );
    if (!response.ok) {
      throw new Error(`Failed to fetch news summary for link: ${link}`);
    }
    return response.json();
  }
};

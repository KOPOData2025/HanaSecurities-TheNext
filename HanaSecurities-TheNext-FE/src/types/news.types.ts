/**
 * 뉴스 응답 데이터
 */
export interface NewsResponse {
  /** 성공 여부 */
  success: boolean;
  /** 응답 메시지 */
  message: string;
  /** 뉴스 데이터 */
  data: NewsData;
}

/**
 * 뉴스 데이터
 */
export interface NewsData {
  /** 전체 검색 결과 수 */
  total: number;
  /** 현재 응답 결과 수 */
  display: number;
  /** 뉴스 항목 배열 */
  items: NewsItem[];
}

/**
 * 뉴스 항목
 */
export interface NewsItem {
  /** 뉴스 제목 */
  title: string;
  /** 원본 링크 */
  originalLink: string;
  /** 뉴스 링크 */
  link: string;
  /** 뉴스 요약 설명 */
  description: string;
  /** 발행 날짜 */
  pubDate: string;
}

/**
 * 뉴스 요약 응답 데이터
 */
export interface NewsSummaryResponse {
  /** 성공 여부 */
  success: boolean;
  /** 응답 메시지 */
  message: string;
  /** 뉴스 요약 데이터 */
  data: NewsSummaryData;
}

/**
 * 뉴스 요약 데이터
 */
export interface NewsSummaryData {
  /** 뉴스 제목 */
  title: string;
  /** AI 요약 내용 */
  summary: string;
}

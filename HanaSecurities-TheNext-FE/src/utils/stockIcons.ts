/**
 * 종목 아이콘 URL 반환
 * @param code 종목 코드
 * @returns 종목 아이콘 이미지 URL
 */
export const getStockIcon = (code: string): string => {
  
  return `https://images.tossinvest.com/https%3A%2F%2Fstatic.toss.im%2Fpng-icons%2Fsecurities%2Ficn-sec-fill-${code}.png?width=96&height=96`;
};
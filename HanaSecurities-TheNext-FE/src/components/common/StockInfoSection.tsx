import React from 'react';
import './StockInfoSection.css';

interface StockInfoSectionProps {
  stockData: {
    stockCode: string;
    stockName: string;
    currentPrice: number;
    priceChange: number;
    changePercent: string;
    totalShares: number;
    marketType?: string;
    indices?: string[];
  };
  exchangeCode?: string;  // 해외 주식 거래소코드
  isForeignStock?: boolean;  // 해외 주식 여부
  classPrefix?: string;  // 클래스명 접두어 (stock-detail 또는 gold-detail)
}

const StockInfoSection: React.FC<StockInfoSectionProps> = ({ stockData, exchangeCode, isForeignStock = false, classPrefix = 'stock-detail' }) => {
  /**
   * 거래소별 소수점 자릿수 결정
   * - 도쿄(TSE/TKSE): 0자리 (엔화)
   * - 중국(SHS/SZS): 0자리 (위안화)
   * - 베트남(HSX/HNX): 0자리 (동화)
   * - 홍콩(HKS/SEHK): 2자리 (홍콩달러)
   * - 미국(NAS/NYS/AMS): 2자리 (달러)
   */
  const getDecimalPlaces = (exchangeCode?: string): number => {
    if (!exchangeCode) return 0;

    const code = exchangeCode.toUpperCase();
    // 소수점 없는 통화 (정수 표시)
    if (code.match(/^(TSE|TKSE|SHS|SZS|HSX|HNX)$/)) {
      return 0;
    }
    // 소수점 2자리 통화
    return 2;
  };

  const formatPrice = (price: number = 0) => {
    if (isForeignStock) {
      const decimalPlaces = getDecimalPlaces(exchangeCode);
      return (price || 0).toLocaleString('en-US', {
        minimumFractionDigits: decimalPlaces,
        maximumFractionDigits: decimalPlaces
      });
    }
    return (price || 0).toLocaleString('ko-KR');
  };

  // 주식수 포맷 (소수점 없이)
  const formatShares = (shares: number = 0) => {
    return Math.floor(shares || 0).toLocaleString();
  };


  const getPriceStatus = () => {
    if (stockData.priceChange > 0) return 'up';
    if (stockData.priceChange < 0) return 'down';
    return 'neutral';
  };

  const priceStatus = getPriceStatus();

  return (
    <div className={`${classPrefix}-info-section`}>
      <div className={`${classPrefix}-codes`}>
        <span className={`${classPrefix}-code-primary`}>{stockData.stockCode}</span>
        {isForeignStock && exchangeCode ? (
          <span className={`${classPrefix}-code-secondary`}>{exchangeCode}</span>
        ) : (
          <>
            {stockData.indices?.map((index, idx) => (
              <span key={idx} className={`${classPrefix}-code-secondary`}>{index}</span>
            ))}
            <div className={`${classPrefix}-market-time`}>
              <span>종 20</span>
              <span>신 45</span>
            </div>
          </>
        )}
      </div>
      <div className={`${classPrefix}-price-info`}>
        <div className={`${classPrefix}-current-price`}>
          <span className={`${classPrefix}-price-value ${priceStatus}`}>
            {formatPrice(stockData.currentPrice)}
          </span>
          <span className={`${classPrefix}-price-change ${priceStatus}`}>
            <span className={`${classPrefix}-arrow`}>{stockData.priceChange > 0 ? '▲' : stockData.priceChange < 0 ? '▼' : ''}</span>
            <span className={`${classPrefix}-change-amount`}>
              {formatPrice(Math.abs(stockData.priceChange))}
            </span>
            <span className={`${classPrefix}-change-percent`}>{stockData.changePercent}%</span>
          </span>
        </div>
        <div className={`${classPrefix}-total-shares`}>
          {formatShares(stockData.totalShares)}주
        </div>
      </div>
    </div>
  );
};

export default StockInfoSection;
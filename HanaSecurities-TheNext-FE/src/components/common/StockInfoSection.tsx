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
}

const StockInfoSection: React.FC<StockInfoSectionProps> = ({ stockData }) => {
  const formatPrice = (price: number = 0) => {
    return (price || 0).toLocaleString('ko-KR');
  };

  
  const getPriceStatus = () => {
    if (stockData.priceChange > 0) return 'up';
    if (stockData.priceChange < 0) return 'down';
    return 'neutral';
  };

  const priceStatus = getPriceStatus();

  return (
    <div className="stock-info-section">
      <div className="stock-codes">
        <span className="code-primary">{stockData.stockCode}</span>
        {stockData.indices?.map((index, idx) => (
          <span key={idx} className="code-secondary">{index}</span>
        ))}
        <div className="market-time">
          <span>종 20</span>
          <span>신 45</span>
        </div>
      </div>
      <div className="stock-price-info">
        <div className="stock-detail-page-current-price">
          <span className={`stock-detail-page-price-value ${priceStatus}`}>{formatPrice(stockData.currentPrice)}</span>
          <span className={`stock-detail-page-price-change ${priceStatus}`}>
            <span className="stock-detail-page-arrow">{stockData.priceChange > 0 ? '▲' : stockData.priceChange < 0 ? '▼' : ''}</span>
            <span className="stock-detail-page-change-amount">{formatPrice(Math.abs(stockData.priceChange))}</span>
            <span className="stock-detail-page-change-percent">{stockData.changePercent}%</span>
          </span>
        </div>
        <div className="total-shares">
          {formatPrice(stockData.totalShares)}주
        </div>
      </div>
    </div>
  );
};

export default StockInfoSection;
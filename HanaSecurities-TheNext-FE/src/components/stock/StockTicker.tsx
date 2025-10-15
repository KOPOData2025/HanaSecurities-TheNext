import React from 'react';
import type { Stock } from '../../types';

interface StockTickerProps {
  stocks: Stock[];
}

const StockTicker: React.FC<StockTickerProps> = ({ stocks }) => {
  return (
    <div className="stock-ticker">
      <div className="ticker-scroll">
        {stocks.map((stock) => (
          <div key={stock.code} className="ticker-item">
            <img src="/samsung-logo.png" alt={stock.name} className="ticker-logo" />
            <div className="ticker-info">
              <div className="ticker-name">{stock.name}</div>
              <div className="ticker-price">
                {stock.currentPrice.toLocaleString()}
                <span className={`ticker-change ${stock.change < 0 ? 'negative' : 'positive'}`}>
                  {stock.change > 0 ? '+' : ''}{stock.change.toLocaleString()}
                  ({stock.changeRate > 0 ? '+' : ''}{stock.changeRate.toFixed(2)}%)
                </span>
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default StockTicker;
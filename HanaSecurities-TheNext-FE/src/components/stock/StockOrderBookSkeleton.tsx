import React from 'react';
import { SkeletonBox, SkeletonText } from '../common/Skeleton';
import './StockOrderBookSkeleton.css';

const StockOrderBookSkeleton: React.FC = () => {
  return (
    <div className="stock-orderbook-skeleton">
      {/* Header Row */}
      <div className="orderbook-skeleton-header">
        <SkeletonBox width="40px" height="16px" />
        <SkeletonBox width="40px" height="16px" />
        <SkeletonBox width="60px" height="18px" />
        <SkeletonBox width="40px" height="16px" />
        <SkeletonBox width="40px" height="16px" />
      </div>

      {/* Main Grid */}
      <div className="orderbook-skeleton-main-grid">
        {/* Left Section - Sell Orders */}
        <div className="orderbook-skeleton-left">
          <div className="orderbook-skeleton-orders">
            {Array.from({ length: 10 }).map((_, index) => (
              <div key={`sell-${index}`} className="orderbook-skeleton-order-row">
                <SkeletonBox width="80%" height="24px" />
              </div>
            ))}
          </div>

          {/* Transaction History */}
          <div className="orderbook-skeleton-transactions">
            <div className="orderbook-skeleton-transaction-header">
              <SkeletonBox width="60px" height="16px" />
              <SkeletonBox width="50px" height="16px" />
            </div>
            {Array.from({ length: 8 }).map((_, index) => (
              <div key={`trans-${index}`} className="orderbook-skeleton-transaction-row">
                <SkeletonBox width="60px" height="14px" />
                <SkeletonBox width="40px" height="14px" />
              </div>
            ))}
          </div>
        </div>

        {/* Center Section - Prices */}
        <div className="orderbook-skeleton-center">
          {Array.from({ length: 20 }).map((_, index) => (
            <div key={`price-${index}`} className="orderbook-skeleton-price-row">
              <SkeletonBox width="70px" height="20px" />
              <SkeletonBox width="50px" height="16px" />
            </div>
          ))}
        </div>

        {/* Right Section - Market Info & Buy Orders */}
        <div className="orderbook-skeleton-right">
          {/* Market Info */}
          <div className="orderbook-skeleton-market-info">
            {Array.from({ length: 9 }).map((_, index) => (
              <div key={`info-${index}`} className="orderbook-skeleton-info-row">
                <SkeletonBox width="60px" height="14px" />
                <SkeletonBox width="80px" height="14px" />
              </div>
            ))}
            <div className="orderbook-skeleton-button">
              <SkeletonBox width="80px" height="32px" borderRadius="4px" />
            </div>
          </div>

          {/* Buy Orders */}
          <div className="orderbook-skeleton-orders">
            {Array.from({ length: 10 }).map((_, index) => (
              <div key={`buy-${index}`} className="orderbook-skeleton-order-row">
                <SkeletonBox width="80%" height="24px" />
              </div>
            ))}
          </div>
        </div>
      </div>

      {/* Footer Row */}
      <div className="orderbook-skeleton-footer">
        <SkeletonBox width="40px" height="16px" />
        <SkeletonBox width="40px" height="16px" />
        <SkeletonBox width="60px" height="18px" />
        <SkeletonBox width="40px" height="16px" />
        <SkeletonBox width="40px" height="16px" />
      </div>

      {/* Bottom Summary */}
      <div className="orderbook-skeleton-summary">
        <SkeletonBox width="80px" height="20px" />
        <SkeletonBox width="100px" height="20px" />
        <SkeletonBox width="80px" height="20px" />
      </div>
    </div>
  );
};

export default StockOrderBookSkeleton;

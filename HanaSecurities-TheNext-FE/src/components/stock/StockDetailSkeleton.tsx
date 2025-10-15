import React from 'react';
import { SkeletonBox } from '../common/Skeleton';
import StockInfoSkeleton from '../common/StockInfoSkeleton';
import './StockDetailSkeleton.css';

const StockDetailSkeleton: React.FC = () => {
  return (
    <div className="stock-detail-skeleton">
      {/* Header Skeleton */}
      <div className="stock-detail-skeleton-header">
        <SkeletonBox width="24px" height="24px" />
        <SkeletonBox width="120px" height="24px" />
        <div style={{ marginLeft: 'auto', display: 'flex', gap: '12px' }}>
          <SkeletonBox width="24px" height="24px" />
          <SkeletonBox width="24px" height="24px" />
        </div>
      </div>

      {/* Stock Info Skeleton */}
      <StockInfoSkeleton />

      {/* Tab Menu Skeleton */}
      <div className="stock-detail-skeleton-tabs">
        <SkeletonBox width="60px" height="36px" />
        <SkeletonBox width="60px" height="36px" />
        <SkeletonBox width="60px" height="36px" />
        <SkeletonBox width="60px" height="36px" />
      </div>

      {/* Chart Period Buttons Skeleton */}
      <div className="stock-detail-skeleton-period-buttons">
        {['분', '일', '주', '월', '년'].map((period) => (
          <SkeletonBox key={period} width="40px" height="32px" />
        ))}
      </div>

      {/* Chart Area Skeleton */}
      <div className="stock-detail-skeleton-chart">
        <SkeletonBox width="100%" height="300px" borderRadius="8px" />
      </div>

      {/* Time Range Buttons Skeleton */}
      <div className="stock-detail-skeleton-timerange">
        {[1, 2, 3, 4, 5, 6].map((item) => (
          <SkeletonBox key={item} width="50px" height="32px" />
        ))}
      </div>

      {/* Volume Chart Skeleton */}
      <div className="stock-detail-skeleton-volume">
        <SkeletonBox width="100%" height="80px" borderRadius="8px" />
      </div>
    </div>
  );
};

export default StockDetailSkeleton;

import React from 'react';
import { SkeletonBox } from '../common/Skeleton';
import './ChartTabSkeleton.css';

const ChartTabSkeleton: React.FC = () => {
  return (
    <div className="chart-tab-skeleton">
      {/* Period and Time Range Selector */}
      <div className="chart-skeleton-controls">
        <div className="chart-skeleton-period">
          {Array.from({ length: 5 }).map((_, index) => (
            <SkeletonBox key={index} width="40px" height="32px" borderRadius="4px" />
          ))}
        </div>
        <div className="chart-skeleton-actions">
          {Array.from({ length: 3 }).map((_, index) => (
            <SkeletonBox key={index} width="32px" height="32px" borderRadius="4px" />
          ))}
        </div>
      </div>

      {/* Candlestick Chart */}
      <div className="chart-skeleton-candlestick">
        <SkeletonBox width="100%" height="300px" borderRadius="8px" />
      </div>

      {/* Volume Chart */}
      <div className="chart-skeleton-volume">
        <SkeletonBox width="100%" height="120px" borderRadius="8px" />
      </div>
    </div>
  );
};

export default ChartTabSkeleton;

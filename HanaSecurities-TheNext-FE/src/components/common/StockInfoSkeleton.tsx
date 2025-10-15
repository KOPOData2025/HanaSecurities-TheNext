import React from 'react';
import { SkeletonBox, SkeletonText } from './Skeleton';
import './StockInfoSkeleton.css';

const StockInfoSkeleton: React.FC = () => {
  return (
    <div className="stock-info-skeleton">
      <div className="stock-codes-skeleton">
        <SkeletonBox width="80px" height="20px" />
        <SkeletonBox width="60px" height="18px" />
        <SkeletonBox width="60px" height="18px" />
      </div>
      <div className="stock-price-info-skeleton">
        <div className="current-price-skeleton">
          <SkeletonBox width="120px" height="32px" />
          <SkeletonBox width="100px" height="24px" />
        </div>
        <SkeletonBox width="100px" height="18px" />
      </div>
    </div>
  );
};

export default StockInfoSkeleton;

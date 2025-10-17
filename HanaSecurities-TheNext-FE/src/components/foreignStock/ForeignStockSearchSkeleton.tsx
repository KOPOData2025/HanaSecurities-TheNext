import React from 'react';
import { SkeletonBox } from '../common/Skeleton';
import './ForeignStockSearchSkeleton.css';

const ForeignStockSearchSkeleton: React.FC = () => {
  return (
    <div className="foreign-search-skeleton">
      {/* Header */}
      <div className="foreign-search-skeleton-header">
        <SkeletonBox width="24px" height="24px" borderRadius="50%" />
        <SkeletonBox width="120px" height="20px" />
        <div style={{ marginLeft: 'auto' }} />
      </div>

      {/* Search Input */}
      <div className="foreign-search-skeleton-input">
        <SkeletonBox width="100%" height="24px" borderRadius="4px" />
      </div>

      {/* Exchange Filter */}
      <div className="foreign-search-skeleton-filters">
        <SkeletonBox width="60px" height="32px" borderRadius="20px" />
        <SkeletonBox width="70px" height="32px" borderRadius="20px" />
        <SkeletonBox width="60px" height="32px" borderRadius="20px" />
        <SkeletonBox width="60px" height="32px" borderRadius="20px" />
        <SkeletonBox width="60px" height="32px" borderRadius="20px" />
      </div>

      {/* Result List */}
      <div className="foreign-search-skeleton-results">
        {Array.from({ length: 8 }).map((_, index) => (
          <div key={index} className="foreign-search-skeleton-item">
            <div className="foreign-search-skeleton-item-main">
              <SkeletonBox width="140px" height="18px" />
              <SkeletonBox width="80px" height="16px" />
            </div>
            <div className="foreign-search-skeleton-item-sub">
              <SkeletonBox width="40px" height="14px" />
              <SkeletonBox width="40px" height="14px" />
              <SkeletonBox width="60px" height="14px" />
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default ForeignStockSearchSkeleton;

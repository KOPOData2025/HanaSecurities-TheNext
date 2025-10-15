import React from 'react';
import { SkeletonBox } from '../common/Skeleton';
import './FinancialTabSkeleton.css';

const FinancialTabSkeleton: React.FC = () => {
  return (
    <div className="financial-tab-skeleton">
      {/* Tab Controls */}
      <div className="financial-skeleton-tabs">
        {Array.from({ length: 3 }).map((_, index) => (
          <SkeletonBox key={index} width="60px" height="32px" borderRadius="4px" />
        ))}
      </div>

      {/* Table */}
      <div className="financial-skeleton-table">
        {/* Header */}
        <div className="financial-skeleton-header">
          <SkeletonBox width="80px" height="16px" />
          <SkeletonBox width="60px" height="16px" />
          <SkeletonBox width="60px" height="16px" />
          <SkeletonBox width="60px" height="16px" />
        </div>

        {/* Rows */}
        {Array.from({ length: 8 }).map((_, index) => (
          <div key={index} className="financial-skeleton-row">
            <SkeletonBox width="80px" height="16px" />
            <SkeletonBox width="60px" height="16px" />
            <SkeletonBox width="60px" height="16px" />
            <SkeletonBox width="60px" height="16px" />
          </div>
        ))}
      </div>
    </div>
  );
};

export default FinancialTabSkeleton;

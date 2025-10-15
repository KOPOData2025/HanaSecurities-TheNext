import React from 'react';
import { SkeletonBox } from '../common/Skeleton';
import './InvestmentOpinionTabSkeleton.css';

const InvestmentOpinionTabSkeleton: React.FC = () => {
  return (
    <div className="investment-opinion-tab-skeleton">
      {/* Summary Cards */}
      <div className="opinion-skeleton-summary">
        <div className="opinion-skeleton-card">
          <SkeletonBox width="60px" height="16px" />
          <SkeletonBox width="80px" height="24px" />
        </div>
        <div className="opinion-skeleton-card">
          <SkeletonBox width="60px" height="16px" />
          <SkeletonBox width="80px" height="24px" />
        </div>
      </div>

      {/* Opinion List */}
      <div className="opinion-skeleton-list">
        {Array.from({ length: 7 }).map((_, index) => (
          <div key={index} className="opinion-skeleton-item">
            <div className="opinion-skeleton-item-left">
              <SkeletonBox width="80px" height="16px" />
              <SkeletonBox width="60px" height="16px" />
            </div>
            <div className="opinion-skeleton-item-right">
              <SkeletonBox width="50px" height="20px" borderRadius="4px" />
              <SkeletonBox width="80px" height="16px" />
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default InvestmentOpinionTabSkeleton;

import React from 'react';
import { SkeletonBox, SkeletonText } from '../common/Skeleton';
import './IndexCardSkeleton.css';

const IndexCardSkeleton: React.FC = () => {
  return (
    <div className="index-card-skeleton">
      <div className="index-card-skeleton-content">
        <SkeletonText width="60%" height="12px" />
        <div style={{ marginTop: '6px' }}>
          <SkeletonBox width="80%" height="19px" />
        </div>
        <div style={{ marginTop: '6px' }}>
          <SkeletonText width="70%" height="12px" />
        </div>
      </div>
      <div className="index-card-skeleton-chart">
        <SkeletonBox width="100%" height="55px" borderRadius="4px" />
      </div>
    </div>
  );
};

export default IndexCardSkeleton;

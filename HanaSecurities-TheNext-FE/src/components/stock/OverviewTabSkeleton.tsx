import React from 'react';
import { SkeletonBox, SkeletonText } from '../common/Skeleton';
import './OverviewTabSkeleton.css';

const OverviewTabSkeleton: React.FC = () => {
  return (
    <div className="overview-tab-skeleton">
      {/* Section 1 */}
      <div className="overview-skeleton-section">
        <SkeletonBox width="80px" height="18px" borderRadius="4px" />
        <div className="overview-skeleton-rows">
          {Array.from({ length: 4 }).map((_, index) => (
            <div key={index} className="overview-skeleton-row">
              <SkeletonBox width="100px" height="16px" />
              <SkeletonBox width="120px" height="16px" />
            </div>
          ))}
        </div>
      </div>

      {/* Section 2 */}
      <div className="overview-skeleton-section">
        <SkeletonBox width="80px" height="18px" borderRadius="4px" />
        <div className="overview-skeleton-rows">
          {Array.from({ length: 4 }).map((_, index) => (
            <div key={index} className="overview-skeleton-row">
              <SkeletonBox width="100px" height="16px" />
              <SkeletonBox width="120px" height="16px" />
            </div>
          ))}
        </div>
      </div>

      {/* Section 3 */}
      <div className="overview-skeleton-section">
        <SkeletonBox width="80px" height="18px" borderRadius="4px" />
        <div className="overview-skeleton-rows">
          {Array.from({ length: 3 }).map((_, index) => (
            <div key={index} className="overview-skeleton-row">
              <SkeletonBox width="100px" height="16px" />
              <SkeletonBox width="120px" height="16px" />
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};

export default OverviewTabSkeleton;

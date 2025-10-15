import React from 'react';
import IndexCardSkeleton from './IndexCardSkeleton';
import './MarketIndices.css';

interface MarketIndicesSkeletonProps {
  count?: number;
}

const MarketIndicesSkeleton: React.FC<MarketIndicesSkeletonProps> = ({ count = 3 }) => {
  return (
    <div className="domestic-index-container">
      <div className="domestic-index-indices">
        {Array.from({ length: count }).map((_, index) => (
          <IndexCardSkeleton key={index} />
        ))}
      </div>
    </div>
  );
};

export default MarketIndicesSkeleton;

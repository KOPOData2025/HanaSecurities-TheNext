import React from 'react';
import IndexCardSkeleton from './IndexCardSkeleton';
import './MarketIndices.css';

const ForeignIndicesSkeleton: React.FC = () => {
  return (
    <div className="domestic-index-container">
      <div className="domestic-index-indices">
        {Array.from({ length: 4 }).map((_, index) => (
          <IndexCardSkeleton key={index} />
        ))}
      </div>
    </div>
  );
};

export default ForeignIndicesSkeleton;

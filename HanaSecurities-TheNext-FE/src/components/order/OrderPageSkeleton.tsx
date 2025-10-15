import React from 'react';
import { SkeletonBox, SkeletonText } from '../common/Skeleton';
import './OrderPageSkeleton.css';

const OrderPageSkeleton: React.FC = () => {
  return (
    <div className="order-page-skeleton">
      {/* Header Skeleton */}
      <div className="order-skeleton-header">
        <SkeletonBox width="24px" height="24px" />
        <SkeletonBox width="120px" height="20px" />
        <div style={{ display: 'flex', gap: '8px' }}>
          <SkeletonBox width="24px" height="24px" />
          <SkeletonBox width="24px" height="24px" />
        </div>
      </div>

      {/* Stock Info Skeleton */}
      <div className="order-skeleton-stock-info">
        <div className="order-skeleton-codes">
          <SkeletonText width="80px" height="14px" />
          <SkeletonText width="100px" height="12px" />
        </div>
        <div className="order-skeleton-price-info">
          <SkeletonBox width="120px" height="24px" />
          <SkeletonText width="150px" height="16px" />
        </div>
      </div>

      {/* Account Selector Skeleton */}
      <div className="order-skeleton-account">
        <SkeletonBox width="100%" height="48px" borderRadius="8px" />
      </div>

      {/* Tabs Skeleton */}
      <div className="order-skeleton-tabs">
        <SkeletonBox width="60px" height="32px" />
        <SkeletonBox width="60px" height="32px" />
        <SkeletonBox width="80px" height="32px" />
        <SkeletonBox width="80px" height="32px" />
      </div>

      {/* Order Content Skeleton */}
      <div className="order-skeleton-content">
        {/* Left Side - Order Book */}
        <div className="order-skeleton-book">
          <div className="order-skeleton-type-selector">
            <SkeletonBox width="60px" height="36px" />
            <SkeletonBox width="60px" height="36px" />
          </div>

          {/* Upper Limit */}
          <div className="order-skeleton-limit">
            <SkeletonText width="100%" height="32px" />
          </div>

          {/* Sell Orders (10개) */}
          <div className="order-skeleton-orders">
            {[...Array(10)].map((_, index) => (
              <div key={`sell-${index}`} className="order-skeleton-row">
                <SkeletonText width="70px" height="20px" />
                <SkeletonText width="60px" height="20px" />
              </div>
            ))}
          </div>

          {/* Buy Orders (10개) */}
          <div className="order-skeleton-orders">
            {[...Array(10)].map((_, index) => (
              <div key={`buy-${index}`} className="order-skeleton-row">
                <SkeletonText width="70px" height="20px" />
                <SkeletonText width="60px" height="20px" />
              </div>
            ))}
          </div>

          {/* Lower Limit */}
          <div className="order-skeleton-limit">
            <SkeletonText width="100%" height="32px" />
          </div>

          {/* Balance Info */}
          <div className="order-skeleton-balance">
            {[...Array(3)].map((_, index) => (
              <div key={index} className="order-skeleton-balance-row">
                <SkeletonText width="50px" height="14px" />
                <SkeletonText width="70px" height="14px" />
              </div>
            ))}
          </div>
        </div>

        {/* Right Side - Order Form */}
        <div className="order-skeleton-form">
          {/* Form Header */}
          <div className="order-skeleton-form-header">
            <SkeletonBox width="60px" height="24px" />
            <SkeletonBox width="60px" height="24px" />
          </div>

          {/* Market Selector */}
          <div className="order-skeleton-market">
            {[...Array(4)].map((_, index) => (
              <SkeletonBox key={index} width="50px" height="32px" />
            ))}
          </div>

          {/* Price Input */}
          <div className="order-skeleton-input-group">
            <SkeletonText width="60px" height="16px" />
            <SkeletonBox width="100%" height="48px" borderRadius="8px" />
          </div>

          {/* Quantity Input */}
          <div className="order-skeleton-input-group">
            <SkeletonText width="60px" height="16px" />
            <SkeletonBox width="100%" height="48px" borderRadius="8px" />
          </div>

          {/* Amount Display */}
          <div className="order-skeleton-amount">
            <SkeletonBox width="100%" height="60px" borderRadius="8px" />
          </div>

          {/* Action Buttons */}
          <div className="order-skeleton-actions">
            <SkeletonBox width="100%" height="52px" borderRadius="8px" />
            <SkeletonBox width="100%" height="52px" borderRadius="8px" />
          </div>
        </div>
      </div>
    </div>
  );
};

export default OrderPageSkeleton;

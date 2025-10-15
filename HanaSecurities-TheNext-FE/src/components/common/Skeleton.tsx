import React from 'react';
import './Skeleton.css';

interface SkeletonProps {
  width?: string;
  height?: string;
  borderRadius?: string;
  className?: string;
}

export const SkeletonBox: React.FC<SkeletonProps> = ({
  width = '100%',
  height = '20px',
  borderRadius = '4px',
  className = ''
}) => {
  return (
    <div
      className={`skeleton skeleton-box ${className}`}
      style={{ width, height, borderRadius }}
    />
  );
};

export const SkeletonCircle: React.FC<{ size?: string; className?: string }> = ({
  size = '40px',
  className = ''
}) => {
  return (
    <div
      className={`skeleton skeleton-circle ${className}`}
      style={{ width: size, height: size, borderRadius: '50%' }}
    />
  );
};

interface SkeletonTextProps {
  lines?: number;
  width?: string;
  height?: string;
  gap?: string;
  className?: string;
}

export const SkeletonText: React.FC<SkeletonTextProps> = ({
  lines = 1,
  width = '100%',
  height = '16px',
  gap = '8px',
  className = ''
}) => {
  return (
    <div className={`skeleton-text-container ${className}`} style={{ gap }}>
      {Array.from({ length: lines }).map((_, index) => (
        <div
          key={index}
          className="skeleton skeleton-text"
          style={{
            width: index === lines - 1 && lines > 1 ? '80%' : width,
            height
          }}
        />
      ))}
    </div>
  );
};

import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { ArrowLeft, MoreVertical, Menu } from 'lucide-react';
import BottomNavigation from '../navigation/BottomNavigation';
import { getFilteredWatchlist } from '../../data/mockData/watchlistData';
import './WatchlistTab.css';

const WatchlistTab: React.FC = () => {
  const [activeFilter, setActiveFilter] = useState('전체');
  const navigate = useNavigate();

  const filters = ['전체', '인증', '현재가', '대비', '등락률'];
  const watchlistItems = getFilteredWatchlist(activeFilter);

  const getChangeType = (changePercent: string): 'up' | 'down' | 'neutral' => {
    const value = parseFloat(changePercent.replace('%', ''));
    if (value > 0) return 'up';
    if (value < 0) return 'down';
    return 'neutral';
  };

  const handleStockClick = (code: string) => {
    navigate(`/stock/${code}`);
  };

  return (
    <div className="watchlist-page">
      <div className="watchlist-header-top">
        <button className="back-btn" onClick={() => navigate(-1)}>
          <ArrowLeft size={24} />
        </button>
        <span className="page-heading">관심종목</span>
        <button className="more-btn">
          <MoreVertical size={24} />
        </button>
      </div>

      <div className="watchlist-content">
        <div className="group-section">
          <div className="group-header">
            <div className="group-tabs">
              <button className="group-tab active">최근조회</button>
              <button className="group-tab">보유</button>
              <button className="group-tab">타사소유</button>
              <button className="group-tab">기본그룹1</button>
              <button className="group-dropdown-btn">
                <svg width="10" height="6" viewBox="0 0 10 6" fill="none">
                  <path d="M1 1L5 5L9 1" stroke="#666" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round"/>
                </svg>
              </button>
            </div>
          </div>

          <div className="filter-section">
            <div className="filter-tabs">
              {filters.map((filter) => (
                <button
                  key={filter}
                  className={`filter-tab ${activeFilter === filter ? 'active' : ''}`}
                  onClick={() => setActiveFilter(filter)}
                >
                  {filter}
                </button>
              ))}
            </div>
          </div>

          <div className="stats-bar">
            <span className="stats-count">총 {watchlistItems.length}개</span>
            <span className="stats-divider"></span>
            <button className="stats-filter-btn">
              <span>통합</span>
              <svg width="10" height="6" viewBox="0 0 10 6" fill="none">
                <path d="M1 1L5 5L9 1" stroke="#666" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round"/>
              </svg>
            </button>
            <button className="stats-filter-btn">
              <span>전체</span>
              <svg width="10" height="6" viewBox="0 0 10 6" fill="none">
                <path d="M1 1L5 5L9 1" stroke="#666" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round"/>
              </svg>
            </button>
          </div>
        </div>

        <div className="stock-list">
          {watchlistItems.map((item, index) => {
            const changeType = getChangeType(item.changePercent);
            return (
              <div
                key={`${item.code}-${index}`}
                className="stock-item"
                onClick={() => handleStockClick(item.code)}
              >
                <div className="stock-info-group">
                  <span className="stock-name">{item.name}</span>
                  <span className={`stock-price ${changeType}`}>{item.currentPrice}</span>
                </div>
                <div className="stock-change-group">
                  <span className={`change-amount ${changeType}`}>
                    {item.change}
                  </span>
                  <span className={`change-arrow ${changeType}`}>
                    {changeType === 'neutral' ? '−' : (changeType === 'up' ? '▲' : '▼')}
                  </span>
                  <span className={`change-rate ${changeType}`}>
                    {item.changePercent}
                  </span>
                </div>
              </div>
            );
          })}
          
          <button className="edit-button">
            편집/관심종목
          </button>
        </div>
      </div>

      <BottomNavigation />
    </div>
  );
};

export default WatchlistTab;
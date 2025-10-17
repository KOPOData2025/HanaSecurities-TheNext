import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { ArrowLeft, MoreVertical, X } from 'lucide-react';
import { foreignWatchlistApi } from '../../services/foreignWatchlistApi';
import { useAuth } from '../../contexts/AuthContext';
import type { ForeignWatchlistItem } from '../../types/foreignStock.types';
import './ForeignWatchlistTab.css';

const ForeignWatchlistTab: React.FC = () => {
  const navigate = useNavigate();
  const { userId } = useAuth();
  const [watchlistItems, setWatchlistItems] = useState<ForeignWatchlistItem[]>([]);
  const [isLoading, setIsLoading] = useState<boolean>(true);

  // 관심종목 목록 조회
  const fetchWatchlist = async () => {
    if (!userId) return;

    try {
      setIsLoading(true);
      const response = await foreignWatchlistApi.getWatchlist(userId);
      setWatchlistItems(response.watchlist);
    } catch (error) {
      console.error('관심종목 조회 실패:', error);
      setWatchlistItems([]);
    } finally {
      setIsLoading(false);
    }
  };

  // 컴포넌트 마운트 시 목록 조회
  useEffect(() => {
    fetchWatchlist();
  }, [userId]);

  // 등락률에 따른 타입 판별
  const getChangeType = (changeRate: string): 'up' | 'down' | 'neutral' => {
    const value = parseFloat(changeRate.replace('%', ''));
    if (value > 0) return 'up';
    if (value < 0) return 'down';
    return 'neutral';
  };

  // 통화 심볼 가져오기
  const getCurrencySymbol = (currency: string): string => {
    switch (currency) {
      case 'USD':
        return '$';
      case 'JPY':
        return '¥';
      case 'HKD':
        return 'HK$';
      default:
        return '$';
    }
  };

  // 종목 클릭 핸들러
  const handleStockClick = (item: ForeignWatchlistItem) => {
    navigate(`/stock/${item.exchangeCode}/${item.stockCode}`);
  };

  // 관심종목 삭제 핸들러
  const handleRemoveClick = async (
    e: React.MouseEvent,
    item: ForeignWatchlistItem
  ) => {
    e.stopPropagation(); // 부모 요소의 클릭 이벤트 방지

    if (!userId) return;

    try {
      await foreignWatchlistApi.removeWatchlist(
        userId,
        item.exchangeCode,
        item.stockCode
      );
      // 삭제 후 목록 새로고침
      fetchWatchlist();
    } catch (error) {
      console.error('관심종목 삭제 실패:', error);
    }
  };

  // 로딩 스켈레톤
  if (isLoading) {
    return (
      <div className="foreign-watchlist-page">
        <div className="foreign-watchlist-header-top">
          <button className="foreign-back-btn" onClick={() => navigate(-1)}>
            <ArrowLeft size={24} />
          </button>
          <span className="foreign-page-heading">해외 관심종목</span>
          <button className="foreign-more-btn">
            <MoreVertical size={24} />
          </button>
        </div>

        <div className="foreign-watchlist-content">
          <div className="foreign-watchlist-skeleton">
            {[...Array(5)].map((_, index) => (
              <div key={index} className="foreign-skeleton-item">
                <div className="foreign-skeleton-text foreign-skeleton-title"></div>
                <div className="foreign-skeleton-text foreign-skeleton-subtitle"></div>
              </div>
            ))}
          </div>
        </div>
      </div>
    );
  }

  // 빈 상태
  if (watchlistItems.length === 0) {
    return (
      <div className="foreign-watchlist-page">
        <div className="foreign-watchlist-header-top">
          <button className="foreign-back-btn" onClick={() => navigate(-1)}>
            <ArrowLeft size={24} />
          </button>
          <span className="foreign-page-heading">해외 관심종목</span>
          <button className="foreign-more-btn">
            <MoreVertical size={24} />
          </button>
        </div>

        <div className="foreign-watchlist-content">
          <div className="foreign-empty-state">
            <div className="foreign-empty-icon">?</div>
            <p className="foreign-empty-text">관심종목이 없습니다</p>
            <button
              className="foreign-empty-button"
              onClick={() => navigate('/foreign-stock-search')}
            >
              종목 검색하기
            </button>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="foreign-watchlist-page">
      <div className="foreign-watchlist-header-top">
        <button className="foreign-back-btn" onClick={() => navigate(-1)}>
          <ArrowLeft size={24} />
        </button>
        <span className="foreign-page-heading">해외 관심종목</span>
        <button className="foreign-more-btn">
          <MoreVertical size={24} />
        </button>
      </div>

      <div className="foreign-watchlist-content">
        <div className="foreign-stats-bar">
          <span className="foreign-stats-count">총 {watchlistItems.length}개</span>
        </div>

        <div className="foreign-stock-list">
          {watchlistItems.map((item, index) => {
            const changeType = getChangeType(item.changeRate);
            const currencySymbol = getCurrencySymbol(item.currency);

            return (
              <div
                key={`${item.exchangeCode}-${item.stockCode}-${index}`}
                className="foreign-stock-item"
                onClick={() => handleStockClick(item)}
              >
                <div className="foreign-stock-main">
                  <div className="foreign-stock-info-group">
                    <span className="foreign-stock-name">{item.stockName}</span>
                    <div className="foreign-stock-meta">
                      <span className="foreign-stock-code">{item.stockCode}</span>
                      <span className="foreign-stock-exchange-badge">{item.exchangeCode}</span>
                    </div>
                  </div>
                  <button
                    className="foreign-remove-btn"
                    onClick={(e) => handleRemoveClick(e, item)}
                  >
                    <X size={20} />
                  </button>
                </div>
                <div className="foreign-stock-price-group">
                  <span className={`foreign-stock-price ${changeType}`}>
                    {currencySymbol}{parseFloat(item.currentPrice).toLocaleString('en-US', {
                      minimumFractionDigits: 2,
                      maximumFractionDigits: 2
                    })}
                  </span>
                  <div className="foreign-stock-change-group">
                    <span className={`foreign-change-arrow ${changeType}`}>
                      {changeType === 'neutral' ? '−' : (changeType === 'up' ? '▲' : '▼')}
                    </span>
                    <span className={`foreign-change-rate ${changeType}`}>
                      {item.changeRate}
                    </span>
                  </div>
                </div>
              </div>
            );
          })}
        </div>
      </div>
    </div>
  );
};

export default ForeignWatchlistTab;

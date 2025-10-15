import React, { useState, useEffect } from 'react';
import { ArrowLeft, Search, X } from 'lucide-react';
import { useNavigate, useLocation } from 'react-router-dom';
import { stockApi } from '../../services/stockApi';
import type { StockSearchItem } from '../../types/stock.types';
import './SearchResultPage.css';

interface SearchResultPageProps {}

const SearchResultPage: React.FC<SearchResultPageProps> = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const searchQuery = location.state?.searchQuery || '';
  const [searchValue, setSearchValue] = useState(searchQuery);
  const [searchResults, setSearchResults] = useState<StockSearchItem[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  
  const performSearch = async (keyword: string) => {
    if (!keyword.trim()) {
      setSearchResults([]);
      return;
    }

    setIsLoading(true);
    setError(null);

    try {
      const response = await stockApi.searchStocks(keyword.trim());
      if (response.success) {
        setSearchResults(response.stocks);
      } else {
        setError(response.message || '검색 실패');
        setSearchResults([]);
      }
    } catch (err) {
      console.error('검색 오류:', err);
      setError('검색 중 오류가 발생했습니다.');
      setSearchResults([]);
    } finally {
      setIsLoading(false);
    }
  };

  
  useEffect(() => {
    if (searchQuery) {
      performSearch(searchQuery);
    }
  }, [searchQuery]);

  const handleBack = () => {
    
    if (window.history.length > 1) {
      navigate(-1);
    } else {
      navigate('/');
    }
  };

  const handleClearSearch = () => {
    setSearchValue('');
    setSearchResults([]);
  };

  const handleSearch = () => {
    if (searchValue.trim()) {
      
      performSearch(searchValue);
      
      navigate('/search-result', {
        state: { searchQuery: searchValue },
        replace: true
      });
    }
  };

  return (
    <div className="srp-page">
      <div className="srp-header">
        <button className="srp-back-btn" onClick={handleBack}>
          <ArrowLeft size={24} />
        </button>
        <h1 className="srp-title">통합검색</h1>
      </div>

      <div className="srp-search-container">
        <button
          className="srp-search-icon-btn"
          onClick={handleSearch}
          style={{ border: 'none', background: 'none', padding: 0, cursor: 'pointer', display: 'flex', alignItems: 'center' }}
        >
          <Search size={24} className="srp-search-icon" />
        </button>
        <input
          type="text"
          className="srp-search-input"
          placeholder="메뉴, 종목(코드), 상품 검색"
          value={searchValue}
          onChange={(e) => setSearchValue(e.target.value)}
          onKeyDown={(e) => e.key === 'Enter' && handleSearch()}
        />
        {searchValue && (
          <button className="srp-clear-btn" onClick={handleClearSearch}>
            <X size={20} />
          </button>
        )}
      </div>

      <div className="srp-content">
        {isLoading && (
          <div className="srp-loading">검색 중...</div>
        )}

        {error && (
          <div className="srp-error">{error}</div>
        )}

        {!isLoading && !error && searchResults.length === 0 && searchValue && (
          <div className="srp-no-results">
            <h3 className="srp-no-results-title">검색 결과가 없습니다</h3>
            <p className="srp-no-results-description">
              다른 검색어를 입력하거나<br />
              종목코드로 검색해 보세요
            </p>
          </div>
        )}

        {!isLoading && !error && searchResults.length > 0 && (
          <div className="srp-section">
            <div className="srp-section-header">
              <h2 className="srp-section-title">국내주식</h2>
              <span className="srp-section-time">{searchResults.length}개 종목</span>
            </div>
            <div className="srp-stock-list">
              {searchResults.map((stock) => (
                <div
                  key={stock.stockCode}
                  className="srp-stock-item"
                  onClick={() => navigate(`/stock/${stock.stockCode}`, {
                    state: { stockName: stock.stockName }
                  })}
                  style={{ cursor: 'pointer' }}
                >
                  <div className="srp-stock-info">
                    <span className="srp-stock-name">{stock.stockName}</span>
                    <div className="srp-stock-code-row">
                      <span className="srp-stock-code">{stock.marketType} {stock.stockCode}</span>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default SearchResultPage;
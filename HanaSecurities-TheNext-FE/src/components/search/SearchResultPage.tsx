import React, { useState, useEffect } from 'react';
import { ArrowLeft, Search, X } from 'lucide-react';
import { useNavigate, useLocation } from 'react-router-dom';
import { stockApi } from '../../services/stockApi';
import { GOLD_PRODUCTS } from '../../services/goldApi';
import type { StockSearchItem } from '../../types/stock.types';
import './SearchResultPage.css';

interface SearchResultPageProps {}

interface GoldSearchResult {
  type: 'GOLD';
  code: string;
  name: string;
  description: string;
}

const SearchResultPage: React.FC<SearchResultPageProps> = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const searchQuery = location.state?.searchQuery || '';
  const [searchValue, setSearchValue] = useState(searchQuery);
  const [searchResults, setSearchResults] = useState<StockSearchItem[]>([]);
  const [goldResults, setGoldResults] = useState<GoldSearchResult[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // 금현물 검색 결과 데이터
  const GOLD_SEARCH_DATA: GoldSearchResult[] = [
    {
      type: 'GOLD',
      code: GOLD_PRODUCTS.GOLD_1KG,
      name: '금 99.99% 1Kg',
      description: '한국거래소 금현물'
    },
    {
      type: 'GOLD',
      code: GOLD_PRODUCTS.GOLD_100G,
      name: '미니금 99.99% 100g',
      description: '한국거래소 금현물'
    }
  ];

  // 금현물 검색 여부 확인
  const isGoldSearch = (keyword: string): boolean => {
    const lowercaseKeyword = keyword.toLowerCase();
    return lowercaseKeyword.includes('금') ||
           lowercaseKeyword.includes('gold') ||
           lowercaseKeyword.includes('골드');
  };


  const performSearch = async (keyword: string) => {
    if (!keyword.trim()) {
      setSearchResults([]);
      setGoldResults([]);
      return;
    }

    setIsLoading(true);
    setError(null);

    try {
      // 주식 검색
      const response = await stockApi.searchStocks(keyword.trim());
      if (response.success) {
        setSearchResults(response.stocks);
      } else {
        setSearchResults([]);
      }

      // 금현물 검색
      if (isGoldSearch(keyword)) {
        setGoldResults(GOLD_SEARCH_DATA);
      } else {
        setGoldResults([]);
      }
    } catch (err) {
      console.error('검색 오류:', err);
      setError('검색 중 오류가 발생했습니다.');
      setSearchResults([]);
      setGoldResults([]);
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
    setGoldResults([]);
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

        {!isLoading && !error && searchResults.length === 0 && goldResults.length === 0 && searchValue && (
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

        {!isLoading && !error && goldResults.length > 0 && (
          <div className="srp-section">
            <div className="srp-section-header">
              <h2 className="srp-section-title">금현물</h2>
              <span className="srp-section-time">{goldResults.length}개 상품</span>
            </div>
            <div className="srp-stock-list">
              {goldResults.map((gold) => (
                <div
                  key={gold.code}
                  className="srp-stock-item"
                  onClick={() => navigate(`/gold/${gold.code}`)}
                  style={{ cursor: 'pointer' }}
                >
                  <div className="srp-stock-info">
                    <span className="srp-stock-name">{gold.name}</span>
                    <div className="srp-stock-code-row">
                      <span className="srp-stock-code">{gold.description}</span>
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
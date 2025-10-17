import React, { useState, useEffect } from 'react';
import { ArrowLeft, Search } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import { foreignStockApi } from '../../services/foreignStockApi';
import type { ForeignStockSearchItem, ForeignExchangeCode } from '../../types/foreignStock.types';
import './ForeignStockSearch.css';

interface ForeignStockSearchProps {
  isOpen: boolean;
  onClose: () => void;
}

const ForeignStockSearch: React.FC<ForeignStockSearchProps> = ({ isOpen, onClose }) => {
  const [searchValue, setSearchValue] = useState('');
  const [selectedExchange, setSelectedExchange] = useState<ForeignExchangeCode | ''>('');
  const [searchResults, setSearchResults] = useState<ForeignStockSearchItem[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [hasSearched, setHasSearched] = useState(false);
  const navigate = useNavigate();

  // 거래소 필터 옵션
  const exchangeOptions = [
    { code: '', label: '전체' },
    { code: 'NAS', label: '나스닥' },
    { code: 'NYS', label: '뉴욕' },
    { code: 'HKS', label: '홍콩' },
    { code: 'TSE', label: '도쿄' }
  ];

  // 검색 API 호출 (300ms debounce)
  useEffect(() => {
    if (!searchValue.trim()) {
      setSearchResults([]);
      setHasSearched(false);
      return;
    }

    setIsLoading(true);
    const timeoutId = setTimeout(async () => {
      try {
        const response = await foreignStockApi.searchStocks(
          searchValue,
          selectedExchange || undefined
        );
        setSearchResults(response.stocks);
        setHasSearched(true);
      } catch (error) {
        console.error('검색 실패:', error);
        setSearchResults([]);
        setHasSearched(true);
      } finally {
        setIsLoading(false);
      }
    }, 300);

    return () => clearTimeout(timeoutId);
  }, [searchValue, selectedExchange]);

  // 검색 결과 클릭 핸들러
  const handleResultClick = (stock: ForeignStockSearchItem) => {
    navigate(`/stock/${stock.exchangeCode}/${stock.stockCode}`);
    onClose();
  };

  // 거래소 필터 변경 핸들러
  const handleExchangeChange = (code: ForeignExchangeCode | '') => {
    setSelectedExchange(code);
  };

  return (
    <div className={`foreign-stock-search ${isOpen ? 'open' : ''}`}>
      <div className="fs-search-header">
        <button className="fs-back-btn" onClick={onClose}>
          <ArrowLeft size={24} />
        </button>
        <h1 className="fs-title">해외주식 검색</h1>
      </div>

      <div className="fs-search-input-container">
        <Search size={24} className="fs-search-icon" />
        <input
          type="text"
          className="fs-search-input"
          placeholder="종목명 또는 종목코드 검색"
          value={searchValue}
          onChange={(e) => setSearchValue(e.target.value)}
        />
      </div>

      {/* 거래소 필터 */}
      <div className="fs-exchange-filter">
        {exchangeOptions.map((option) => (
          <button
            key={option.code}
            className={`fs-exchange-btn ${selectedExchange === option.code ? 'active' : ''}`}
            onClick={() => handleExchangeChange(option.code as ForeignExchangeCode | '')}
          >
            {option.label}
          </button>
        ))}
      </div>

      <div className="fs-search-content">
        {/* 로딩 스켈레톤 */}
        {isLoading && (
          <div className="fs-skeleton-list">
            {[...Array(5)].map((_, index) => (
              <div key={index} className="fs-skeleton-item">
                <div className="fs-skeleton-text fs-skeleton-title"></div>
                <div className="fs-skeleton-text fs-skeleton-subtitle"></div>
              </div>
            ))}
          </div>
        )}

        {/* 검색 결과 */}
        {!isLoading && searchResults.length > 0 && (
          <div className="fs-result-list">
            {searchResults.map((stock) => (
              <div
                key={`${stock.exchangeCode}-${stock.stockCode}`}
                className="fs-result-item"
                onClick={() => handleResultClick(stock)}
              >
                <div className="fs-result-main">
                  <span className="fs-stock-name">{stock.stockName}</span>
                  <span className="fs-stock-code">{stock.stockCode}</span>
                </div>
                <div className="fs-result-sub">
                  <span className="fs-exchange-badge">{stock.exchangeCode}</span>
                  <span className="fs-currency">{stock.currency}</span>
                  {stock.currentPrice && (
                    <span className="fs-current-price">{stock.currentPrice}</span>
                  )}
                </div>
              </div>
            ))}
          </div>
        )}

        {/* 검색 결과 없음 */}
        {!isLoading && hasSearched && searchResults.length === 0 && (
          <div className="fs-empty-state">
            <div className="fs-empty-icon">?</div>
            <p className="fs-empty-text">검색 결과가 없습니다</p>
          </div>
        )}

        {/* 초기 상태 */}
        {!isLoading && !hasSearched && (
          <div className="fs-empty-state">
            <div className="fs-empty-icon">
              <Search size={24} />
            </div>
            <p className="fs-empty-text">종목명 또는 종목코드를 입력하세요</p>
          </div>
        )}
      </div>
    </div>
  );
};

export default ForeignStockSearch;

import React, { useState, useEffect } from 'react';
import { ArrowLeft, Search, ChevronUp } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import './SearchPage.css';

interface SearchPageProps {
  isOpen: boolean;
  onClose: () => void;
}

const SearchPage: React.FC<SearchPageProps> = ({ isOpen, onClose }) => {
  const [searchValue, setSearchValue] = useState('');
  const navigate = useNavigate();

  useEffect(() => {
    if (searchValue.trim()) {
      
      navigate('/search-result', {
        state: { searchQuery: searchValue }
      });
      onClose();
      setSearchValue(''); 
    }
  }, [searchValue, navigate, onClose]);

  const onlineTradingItems = [
    { rank: 1, name: '삼성전자' },
    { rank: 2, name: '클로봇' },
    { rank: 3, name: 'KODEX 코스닥150레버리지' },
    { rank: 4, name: '노을' },
    { rank: 5, name: 'SK하이닉스' }
  ];

  return (
    <div className={`search-page ${isOpen ? 'open' : ''}`}>
      <div className="search-header">
        <button className="search-back-btn" onClick={onClose}>
          <ArrowLeft size={24} />
        </button>
        <h1 className="search-title">통합검색</h1>
      </div>

      <div className="search-input-container">
        <Search size={24} className="sp-search-icon" />
        <input
          type="text"
          className="search-input"
          placeholder="메뉴, 종목(코드), 상품 검색"
          value={searchValue}
          onChange={(e) => setSearchValue(e.target.value)}
        />
      </div>

      <div className="search-content">
        <div className="search-section">
          <div className="sp-section-header">
            <h2 className="section-title">온라인 조회 종목상위</h2>
            <span className="section-time">오늘 10:40 기준</span>
          </div>
          <div className="trading-list">
            {onlineTradingItems.map((item) => (
              <div key={item.rank} className="trading-item">
                <span className={`trading-rank rank-${item.rank}`}>
                  {item.rank}
                </span>
                <span className="trading-name">{item.name}</span>
              </div>
            ))}
          </div>
          <button className="more-link">
            <span>닫기</span>
            <ChevronUp size={16} className="more-icon" />
          </button>
        </div>
      </div>
    </div>
  );
};

export default SearchPage;
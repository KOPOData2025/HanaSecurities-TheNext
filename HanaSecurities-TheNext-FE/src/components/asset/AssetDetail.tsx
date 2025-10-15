import React, { useState, useEffect } from 'react';
import { ArrowLeft, ChevronUp, ChevronDown, MoreVertical, Check } from 'lucide-react';
import { useNavigate, useParams } from 'react-router-dom';
import { getAssetDetailData, getAssetTypeOptions } from '../../data/mockData/assetDetailData';
import BottomNavigation from '../navigation/BottomNavigation';
import PageHeader from '../common/PageHeader';
import './AssetDetail.css';

const AssetDetail: React.FC = () => {
  const navigate = useNavigate();
  const { type } = useParams<{ type: string }>();
  const [isDropdownOpen, setIsDropdownOpen] = useState(false);
  const currentType = type || 'bonds';
  const [selectedOptions, setSelectedOptions] = useState(getAssetTypeOptions(currentType));

  const assetDetail = getAssetDetailData(currentType);

  
  useEffect(() => {
    setSelectedOptions(getAssetTypeOptions(currentType));
  }, [currentType]);

  const formatAmount = (amount: number): string => {
    return amount.toLocaleString('ko-KR');
  };

  const formatChangeAmount = (amount: number): string => {
    if (amount >= 0) {
      return `+${formatAmount(amount)}`;
    }
    return formatAmount(amount).toString();
  };

  const handleOptionToggle = (optionId: string) => {
    
    if (optionId !== currentType) {
      navigate(`/asset/${optionId}`);
      setIsDropdownOpen(false);
    }
  };

  const handleDropdownClose = () => {
    setIsDropdownOpen(false);
  };

  return (
    <div className="asset-detail-page">
      <PageHeader
        title="상품유형별 잔고"
        leftAction={
          <button className="back-btn" onClick={() => navigate('/asset')}>
            <ArrowLeft size={24} />
          </button>
        }
      />

      <div className="asset-detail-content">
        <div className="asset-type-selector">
          <button
            className={`dropdown-button ${isDropdownOpen ? 'active' : ''}`}
            onClick={() => setIsDropdownOpen(!isDropdownOpen)}
          >
            <span>{assetDetail.title}</span>
            {isDropdownOpen ? <ChevronUp size={20} /> : <ChevronDown size={20} />}
          </button>
        </div>

        {isDropdownOpen && (
          <>
            <div className="modal-overlay" onClick={handleDropdownClose} />
            <div className="dropdown-modal">
              <div className="modal-header">
                <button className="modal-back-btn" onClick={() => navigate('/asset')}>
                  <ArrowLeft size={24} />
                </button>
                <span className="modal-title">상품유형별 잔고</span>
              </div>
              <div className="modal-selector">
                <button
                  className="modal-dropdown-button"
                  onClick={handleDropdownClose}
                >
                  <span>{assetDetail.title}</span>
                  <ChevronUp size={20} />
                </button>
              </div>
              <div className="dropdown-options">
                {selectedOptions.map(option => (
                  <div
                    key={option.id}
                    className={`dropdown-option ${option.isSelected ? 'selected' : ''}`}
                    onClick={() => handleOptionToggle(option.id)}
                  >
                    <span>{option.label}</span>
                    {option.isSelected && <Check size={20} color="#000" />}
                  </div>
                ))}
              </div>
            </div>
          </>
        )}

        <div className="asset-total-section">
          <div className="total-label">
            평가금액
            <span className="info-icon">ⓘ</span>
          </div>
          <div className="total-amount">
            {formatAmount(assetDetail.totalAmount)}원
          </div>
          <div className="total-change">
            <span className={`change-amount ${assetDetail.changeAmount >= 0 ? 'positive' : 'negative'}`}>
              {formatAmount(Math.abs(assetDetail.changeAmount))}원
            </span>
            <span className={`change-percent ${assetDetail.changePercent >= 0 ? 'positive' : 'negative'}`}>
              {assetDetail.changePercent > 0 ? '+' : ''}{assetDetail.changePercent}%
            </span>
          </div>
          <div className="update-time">
            결제기준 {assetDetail.updateDate} {assetDetail.updateTime}
          </div>
        </div>

        <div className="holdings-section">
          <div className="holdings-header">
            <span className="holdings-title">보유현황</span>
            <button className="sort-button">
              종목별
              <MoreVertical size={16} />
            </button>
          </div>

          <div className="holdings-list">
            {assetDetail.holdings.map((holding, index) => (
              <div key={index} className="holding-item">
                <div className="holding-header">
                  <span className="holding-code">{holding.code}</span>
                </div>
                <div className="holding-info">
                  <div className="holding-name-row">
                    <span className="holding-name">{holding.name}</span>
                    <span className="holding-amount">{formatAmount(holding.amount)}원</span>
                  </div>
                  <div className="holding-change-row">
                    <span className={`holding-change ${holding.changeAmount >= 0 ? 'positive' : 'negative'}`}>
                      {formatChangeAmount(holding.changeAmount)}원
                    </span>
                    <span className={`holding-percent ${holding.changePercent >= 0 ? 'positive' : 'negative'}`}>
                      {holding.changePercent >= 0 ? '+' : ''}{holding.changePercent}%
                    </span>
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>

      <BottomNavigation />
    </div>
  );
};

export default AssetDetail;
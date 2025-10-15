import React, { useState, useEffect } from 'react';
import { ArrowLeft, Info, ChevronRight } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import BottomNavigation from '../navigation/BottomNavigation';
import PageHeader from '../common/PageHeader';
import { assetTabs } from '../../data/mockData/assetData';
import { stockApi } from '../../services/stockApi';
import type { StockBalanceResponse, BondBalanceResponse } from '../../types/stock.types';
import './AssetTab.css';

const AssetTab: React.FC = () => {
  const navigate = useNavigate();
  const [activeTab, setActiveTab] = useState('전체');
  const [stockBalance, setStockBalance] = useState<StockBalanceResponse | null>(null);
  const [bondBalance, setBondBalance] = useState<BondBalanceResponse | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  
  useEffect(() => {
    const fetchBalanceData = async () => {
      try {
        setIsLoading(true);
        const [stockData, bondData] = await Promise.all([
          stockApi.getStockBalance(),
          stockApi.getBondBalance('01') 
        ]);
        setStockBalance(stockData);
        setBondBalance(bondData);
      } catch (error) {
        console.error('자산 조회 실패:', error);
      } finally {
        setIsLoading(false);
      }
    };

    fetchBalanceData();
  }, []);

  const formatAmount = (amount: number): string => {
    return amount.toLocaleString('ko-KR');
  };

  
  const stockAmount = stockBalance?.summary?.totalEvaluationAmount
    ? parseFloat(stockBalance.summary.totalEvaluationAmount)
    : 0;

  const bondAmount = bondBalance?.bonds
    ? bondBalance.bonds.reduce((sum, bond) => sum + parseFloat(bond.buyAmount || '0'), 0)
    : 0;

  const depositAmount = stockBalance?.summary?.depositAmount
    ? parseFloat(stockBalance.summary.depositAmount)
    : 0;

  const totalAmount = stockAmount + bondAmount + depositAmount;

  
  const stockPercent = totalAmount > 0 ? (stockAmount / totalAmount) * 100 : 0;
  const financialPercent = totalAmount > 0 ? (bondAmount / totalAmount) * 100 : 0;
  const cashPercent = totalAmount > 0 ? (depositAmount / totalAmount) * 100 : 0;

  
  const totalProfitLoss = stockBalance?.summary?.totalProfitLoss
    ? parseFloat(stockBalance.summary.totalProfitLoss)
    : 0;

  
  const totalProfitLossRate = totalAmount > 0
    ? (totalProfitLoss / totalAmount) * 100
    : 0;

  
  const getAssetStatus = () => {
    if (totalProfitLoss > 0) return 'up';
    if (totalProfitLoss < 0) return 'down';
    return 'neutral';
  };

  const assetStatus = getAssetStatus();

  
  const createDonutPath = () => {
    const radius = 70;
    const innerRadius = 45;
    const centerX = 100;
    const centerY = 100;

    
    let currentAngle = -90;

    const segments = [
      { percent: stockPercent, color: '#6B7AFF' },
      { percent: financialPercent, color: '#99A6FF' },
      { percent: cashPercent, color: '#C5CCFF' }
    ];

    return segments.map((segment, index) => {
      if (segment.percent === 0) return null;

      const angle = (segment.percent / 100) * 360;
      const endAngle = currentAngle + angle;

      const startAngleRad = (currentAngle * Math.PI) / 180;
      const endAngleRad = (endAngle * Math.PI) / 180;

      const x1 = centerX + radius * Math.cos(startAngleRad);
      const y1 = centerY + radius * Math.sin(startAngleRad);
      const x2 = centerX + radius * Math.cos(endAngleRad);
      const y2 = centerY + radius * Math.sin(endAngleRad);

      const innerX1 = centerX + innerRadius * Math.cos(startAngleRad);
      const innerY1 = centerY + innerRadius * Math.sin(startAngleRad);
      const innerX2 = centerX + innerRadius * Math.cos(endAngleRad);
      const innerY2 = centerY + innerRadius * Math.sin(endAngleRad);

      const largeArcFlag = angle > 180 ? 1 : 0;

      const pathData = [
        `M ${x1} ${y1}`,
        `A ${radius} ${radius} 0 ${largeArcFlag} 1 ${x2} ${y2}`,
        `L ${innerX2} ${innerY2}`,
        `A ${innerRadius} ${innerRadius} 0 ${largeArcFlag} 0 ${innerX1} ${innerY1}`,
        'Z'
      ].join(' ');

      currentAngle = endAngle;

      return (
        <path
          key={index}
          d={pathData}
          fill={segment.color}
          stroke="white"
          strokeWidth="2"
        />
      );
    });
  };

  return (
    <div className="asset-page">
      <PageHeader
        title="총자산"
        leftAction={
          <button className="back-btn" onClick={() => navigate(-1)}>
            <ArrowLeft size={24} />
          </button>
        }
      />

      <div className="asset-content">
        <div className="asset-summary">
          <div className="country-selector">
            <span>한국투자 총자산</span>
            <svg width="10" height="6" viewBox="0 0 10 6" fill="none">
              <path d="M1 1L5 5L9 1" stroke="#666" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round"/>
            </svg>
          </div>

          <div className="total-amount">
            <span className="amount">
              {isLoading ? '조회 중...' : `${formatAmount(totalAmount)}원`}
            </span>
          </div>

          <div className="asset-summary-amount-change">
            <span className={`asset-summary-change-arrow ${assetStatus}`}>
              {totalProfitLoss > 0 ? '▲' : totalProfitLoss < 0 ? '▼' : ''}
            </span>
            <span className={`asset-summary-change-amount ${assetStatus}`}>
              {formatAmount(Math.abs(totalProfitLoss))}원
            </span>
            <span className={`asset-summary-change-percent ${assetStatus}`}>
              {totalProfitLoss > 0 ? '+' : ''}{totalProfitLossRate.toFixed(2)}%
            </span>
          </div>
        </div>

        <div className="asset-tabs">
          {assetTabs.map((tab) => (
            <button
              key={tab}
              className={`asset-tab ${activeTab === tab ? 'active' : ''}`}
              onClick={() => setActiveTab(tab)}
            >
              {tab}
            </button>
          ))}
        </div>

        <div className="asset-chart-section">
          <svg width="200" height="200" viewBox="0 0 200 200" className="donut-chart">
            {createDonutPath()}
          </svg>

          <div className="chart-legend">
            <div className="legend-item">
              <span className="legend-dot" style={{ backgroundColor: '#6B7AFF' }}></span>
              <span className="legend-label">주식</span>
              <span className="legend-value">{stockPercent.toFixed(1)}%</span>
            </div>
            <div className="legend-item">
              <span className="legend-dot" style={{ backgroundColor: '#99A6FF' }}></span>
              <span className="legend-label">금융상품</span>
              <span className="legend-value">{financialPercent.toFixed(1)}%</span>
            </div>
            <div className="legend-item">
              <span className="legend-dot" style={{ backgroundColor: '#C5CCFF' }}></span>
              <span className="legend-label">현금성</span>
              <span className="legend-value">{cashPercent.toFixed(1)}%</span>
            </div>
          </div>
        </div>

        <div className="asset-details">
          <div className="asset-section">
            <div className="section-header">
              <span className="section-title">주식</span>
              <span className="section-amount">{formatAmount(stockAmount)}원</span>
            </div>
            <div className="section-items">
              <div className="asset-item" onClick={() => navigate('/asset/stocks')}>
                <span className="item-label">국내주식</span>
                <div className="item-values">
                  <span className="item-amount">{formatAmount(stockAmount)}원</span>
                  <span className="item-percent">{stockPercent.toFixed(1)}%</span>
                  <ChevronRight size={16} color="#999" />
                </div>
              </div>
            </div>
          </div>

          <div className="asset-section">
            <div className="section-header">
              <span className="section-title">금융상품</span>
              <span className="section-amount">{formatAmount(bondAmount)}원</span>
            </div>
            <div className="section-items">
              <div className="asset-item" onClick={() => navigate('/asset/bonds')}>
                <span className="item-label">채권</span>
                <div className="item-values">
                  <span className="item-amount">{formatAmount(bondAmount)}원</span>
                  <span className="item-percent">{financialPercent.toFixed(1)}%</span>
                  <ChevronRight size={16} color="#999" />
                </div>
              </div>
            </div>
          </div>

          <div className="asset-section">
            <div className="section-header">
              <span className="section-title">현금성</span>
              <span className="section-amount">{formatAmount(depositAmount)}원</span>
            </div>
            <div className="section-items">
              <div className="asset-item" onClick={() => navigate('/asset/deposits')}>
                <span className="item-label">예수금</span>
                <div className="item-values">
                  <span className="item-amount">{formatAmount(depositAmount)}원</span>
                  <span className="item-percent">{cashPercent.toFixed(1)}%</span>
                  <ChevronRight size={16} color="#999" />
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <BottomNavigation />
    </div>
  );
};

export default AssetTab;
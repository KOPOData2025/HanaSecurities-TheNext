import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { stockApi } from '../../services/stockApi';
import type { FinancialPeriodInfo } from '../../types/stock.types';
import './FinancialInfo.css';

const FinancialInfo: React.FC = () => {
  const { code } = useParams<{ code: string }>();
  const [periodType, setPeriodType] = useState('연결');
  const [financialData, setFinancialData] = useState<FinancialPeriodInfo[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchFinancialInfo = async () => {
      if (!code) return;

      try {
        setLoading(true);
        const response = await stockApi.getFinancialInfo(code, '0'); 
        if (response.success && response.data) {
          setFinancialData(response.data.periods.slice(0, 5)); 
        }
      } catch (error) {
        console.error('재무정보 조회 실패:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchFinancialInfo();
  }, [code]);

  
  const formatBillion = (value: string | null | undefined): string => {
    if (!value || value === '' || value === '0' || value === '0.00') return '-';
    const num = parseFloat(value);
    if (isNaN(num) || num === 0) return '-';
    return num.toLocaleString(undefined, { maximumFractionDigits: 0 });
  };

  
  const formatNumber = (value: string | null | undefined): string => {
    if (!value || value === '' || value === '0' || value === '0.00') return '-';
    const num = parseFloat(value);
    if (isNaN(num) || num === 0) return '-';
    return num.toLocaleString(undefined, { maximumFractionDigits: 2 });
  };

  
  const formatPeriod = (period: string): string => {
    if (period.length !== 6) return period;
    return `${period.substring(0, 4)}.${period.substring(4, 6)}`;
  };

  const periods = financialData.map(d => formatPeriod(d.period));

  
  const ratioRows = [
    { label: '매출액증가율(%)', values: financialData.map(d => formatNumber(d.salesGrowthRate)) },
    { label: '영업이익증가율(%)', values: financialData.map(d => formatNumber(d.operatingProfitGrowthRate)) },
    { label: '순이익증가율(%)', values: financialData.map(d => formatNumber(d.netIncomeGrowthRate)) },
    { label: 'ROE(%)', values: financialData.map(d => formatNumber(d.roe)) },
    { label: 'EPS(원)', values: financialData.map(d => formatNumber(d.eps)) },
    { label: 'SPS(원)', values: financialData.map(d => formatNumber(d.sps)) },
    { label: 'BPS(원)', values: financialData.map(d => formatNumber(d.bps)) },
    { label: '유보율(%)', values: financialData.map(d => formatNumber(d.reserveRatio)) },
    { label: '부채비율(%)', values: financialData.map(d => formatNumber(d.debtRatio)) },
  ];

  
  const incomeRows = [
    { label: '매출액(억)', values: financialData.map(d => formatBillion(d.sales)) },
    { label: '매출원가(억)', values: financialData.map(d => formatBillion(d.salesCost)) },
    { label: '매출총이익(억)', values: financialData.map(d => formatBillion(d.grossProfit)) },
    { label: '영업이익(억)', values: financialData.map(d => formatBillion(d.operatingProfit)) },
    { label: '경상이익(억)', values: financialData.map(d => formatBillion(d.ordinaryProfit)) },
    { label: '당기순이익(억)', values: financialData.map(d => formatBillion(d.netIncome)) },
  ];

  
  const balanceRows = [
    { label: '유동자산(억)', values: financialData.map(d => formatBillion(d.currentAssets)) },
    { label: '고정자산(억)', values: financialData.map(d => formatBillion(d.fixedAssets)) },
    { label: '자산총계(억)', values: financialData.map(d => formatBillion(d.totalAssets)) },
    { label: '유동부채(억)', values: financialData.map(d => formatBillion(d.currentLiabilities)) },
    { label: '고정부채(억)', values: financialData.map(d => formatBillion(d.fixedLiabilities)) },
    { label: '부채총계(억)', values: financialData.map(d => formatBillion(d.totalLiabilities)) },
    { label: '자본금(억)', values: financialData.map(d => formatBillion(d.capital)) },
    { label: '자본총계(억)', values: financialData.map(d => formatBillion(d.totalEquity)) },
  ];

  if (loading) {
    return (
      <div className="financial-info">
        <div className="period-selector">
          <button className="period-type">결산</button>
          <button className="period-type active">연결</button>
          <button className="period-detail">재무상세</button>
        </div>
        <div className="financial-table-wrapper">
          <div style={{ padding: '20px', textAlign: 'center' }}>데이터 로딩 중...</div>
        </div>
      </div>
    );
  }

  return (
    <div className="financial-info">
      {/* Period Type Selector */}
      <div className="period-selector">
        <button
          className={`period-type ${periodType === '결산' ? 'active' : ''}`}
          onClick={() => setPeriodType('결산')}
        >
          결산
        </button>
        <button
          className={`period-type ${periodType === '연결' ? 'active' : ''}`}
          onClick={() => setPeriodType('연결')}
        >
          연결
        </button>
        <button className="period-detail">재무상세</button>
      </div>

      {/* Financial Table */}
      <div className="financial-table-wrapper">
        <div className="financial-table-container">
          <table className="financial-table">
            <thead>
              <tr>
                <th className="sticky-header sticky-column">기간</th>
                {periods.map((period) => (
                  <th key={period} className="sticky-header">
                    {period}
                  </th>
                ))}
              </tr>
            </thead>
            <tbody>
              {/* 재무비율 섹션 */}
              {ratioRows.map((row, index) => (
                <tr
                  key={`ratio-${index}`}
                  className={index === ratioRows.length - 1 ? 'section-divider' : ''}
                >
                  <td className="sticky-column">{row.label}</td>
                  {row.values.map((value, idx) => (
                    <td key={idx} className="data-cell">
                      {value}
                    </td>
                  ))}
                </tr>
              ))}

              {/* 손익계산서 섹션 */}
              {incomeRows.map((row, index) => (
                <tr
                  key={`income-${index}`}
                  className={index === incomeRows.length - 1 ? 'section-divider' : ''}
                >
                  <td className="sticky-column">{row.label}</td>
                  {row.values.map((value, idx) => (
                    <td key={idx} className="data-cell">
                      {value}
                    </td>
                  ))}
                </tr>
              ))}

              {/* 대차대조표 섹션 */}
              {balanceRows.map((row, index) => (
                <tr key={`balance-${index}`}>
                  <td className="sticky-column">{row.label}</td>
                  {row.values.map((value, idx) => (
                    <td key={idx} className="data-cell">
                      {value}
                    </td>
                  ))}
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};

export default FinancialInfo;

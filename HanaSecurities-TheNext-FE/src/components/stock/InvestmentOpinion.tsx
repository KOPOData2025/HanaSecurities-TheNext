import React from 'react';
import './InvestmentOpinion.css';

interface OpinionItem {
  date: string;
  currentOpinion: string;
  previousOpinion: string;
  targetPrice: number;
  broker: string;
}

interface InvestmentOpinionProps {
  opinions: OpinionItem[];
  averageScore: number;
  targetPrice: number;
  brokersCount?: number;
  latestDate?: string;
}

const InvestmentOpinion: React.FC<InvestmentOpinionProps> = ({
  opinions,
  averageScore,
  targetPrice,
  brokersCount,
  latestDate
}) => {

  
  const getScorePosition = () => {
    const percentage = (averageScore / 5) * 100;
    
    return Math.min(Math.max(percentage, 12), 88);
  };

  return (
    <div className="investment-opinion">
      {/* Score and Target Price Section */}
      <div className="score-target-section">
        <div className="score-display" style={{ left: `${getScorePosition()}%` }}>
          <span className="score-text">{averageScore.toFixed(2)}점</span>
          <div className="score-arrow"></div>
        </div>

        <div className="score-bar-container">
          <div className="score-bar">
            <div className="score-segment-wrapper">
              <div className="score-segment strong-buy">
                <div className="score-fill" style={{ width: averageScore <= 1 ? `${(averageScore / 1) * 100}%` : '100%' }}></div>
              </div>
            </div>
            <div className="score-segment-wrapper">
              <div className="score-segment buy">
                <div className="score-fill" style={{ width: averageScore > 1 && averageScore <= 2 ? `${((averageScore - 1) / 1) * 100}%` : averageScore > 2 ? '100%' : '0%' }}></div>
              </div>
            </div>
            <div className="score-segment-wrapper">
              <div className="score-segment neutral">
                <div className="score-fill" style={{ width: averageScore > 2 && averageScore <= 3 ? `${((averageScore - 2) / 1) * 100}%` : averageScore > 3 ? '100%' : '0%' }}></div>
              </div>
            </div>
            <div className="score-segment-wrapper">
              <div className="score-segment sell">
                <div className="score-fill" style={{ width: averageScore > 3 && averageScore <= 4 ? `${((averageScore - 3) / 1) * 100}%` : averageScore > 4 ? '100%' : '0%' }}></div>
              </div>
            </div>
            <div className="score-segment-wrapper">
              <div className="score-segment strong-sell">
                <div className="score-fill" style={{ width: averageScore > 4 ? `${((averageScore - 4) / 1) * 100}%` : '0%' }}></div>
              </div>
            </div>
          </div>
          <div className="score-labels">
            <span>강력매도</span>
            <span>매도</span>
            <span>중립</span>
            <span>매수</span>
            <span>강력매수</span>
          </div>
        </div>

        <div className="target-price-section">
          <span className="target-price-label">목표주가</span>
          <span className="target-price-value">{targetPrice.toLocaleString()}</span>
        </div>
      </div>

      {/* Opinion List Header */}
      <div className="opinion-info">
        <span className="info-label">추정기관수</span>
        <span className="info-value">{brokersCount ?? opinions.length}</span>
        <span className="info-date">|</span>
        <span className="info-date">{latestDate || '날짜 정보 없음'} 기준</span>
      </div>

      {/* Opinion List */}
      <div className="opinion-list-container">
        <table className="opinion-table">
          <thead>
            <tr>
              <th>발표일</th>
              <th>현재</th>
              <th>직전</th>
              <th>목표가</th>
              <th>증권사</th>
            </tr>
          </thead>
          <tbody>
            {opinions.map((opinion, index) => (
              <tr key={index}>
                <td className="date">{opinion.date}</td>
                <td className={`opinion ${opinion.currentOpinion.toLowerCase()}`}>
                  {opinion.currentOpinion}
                </td>
                <td className={`opinion ${opinion.previousOpinion.toLowerCase()}`}>
                  {opinion.previousOpinion}
                </td>
                <td className="target-price">{opinion.targetPrice.toLocaleString()}</td>
                <td className="broker">{opinion.broker}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default InvestmentOpinion;
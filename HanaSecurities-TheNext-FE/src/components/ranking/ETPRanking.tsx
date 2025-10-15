import React, { useState } from 'react';
import { getETFRankingData, getETNRankingData } from '../../data/mockData/etpData';
import type { ETPItem } from '../../data/mockData/etpData';
import styles from './RealTimeRanking.module.css';

const ETPRanking: React.FC = () => {
  const [activeTab, setActiveTab] = useState<'ETF' | 'ETN'>('ETF');
  const [rankingData, setRankingData] = useState<ETPItem[]>(getETFRankingData());

  const handleTabChange = (tab: 'ETF' | 'ETN') => {
    setActiveTab(tab);
    if (tab === 'ETF') {
      setRankingData(getETFRankingData());
    } else {
      setRankingData(getETNRankingData());
    }
  };

  return (
    <section className={styles.rankingSection}>
      <div className={styles.rankingHeader}>
        <h2>ETP 실시간 랭킹</h2>
        <span className={styles.rankingTime}>실시간 거래량 기준</span>
      </div>
      
      <div className={styles.rankingTabs}>
        <button 
          className={`${styles.rankingTab} ${activeTab === 'ETF' ? styles.active : ''}`}
          onClick={() => handleTabChange('ETF')}
        >
          ETF
        </button>
        <button 
          className={`${styles.rankingTab} ${activeTab === 'ETN' ? styles.active : ''}`}
          onClick={() => handleTabChange('ETN')}
        >
          ETN
        </button>
      </div>
      
      <div className={styles.rankingList}>
        {rankingData.slice(0, 5).map((item) => (
          <div key={item.rank} className={styles.rankingItem}>
            <div className={styles.rankingLeft}>
              <span className={styles.rankingNumber}>{item.rank}</span>
              <div className={styles.companyLogo}>
                {item.iconText ? (
                  <div className={styles.etpIcon}>
                    <span className={styles.etpIconLine1}>{item.iconText.line1}</span>
                    {item.iconText.line2 && (
                      <span className={styles.etpIconLine2}>{item.iconText.line2}</span>
                    )}
                  </div>
                ) : (
                  <div className={styles.stockIconPlaceholder}>
                    {item.name.charAt(0)}
                  </div>
                )}
              </div>
              <div className={styles.rankingInfo}>
                <span className={styles.rankingName}>{item.name}</span>
                <span className={styles.rankingCode}>{item.code}</span>
              </div>
            </div>
            <div className={styles.rankingRight}>
              <div className={styles.rankingPrice}>{item.price}</div>
              <div className={`${styles.rankingChange} ${item.isPositive ? styles.positive : styles.negative}`}>
                <span className={styles.changeArrow}>{item.isPositive ? '▲' : '▼'}</span>
                <span>{Math.abs(parseInt(item.change))}</span>
                <span className={styles.changePercent}>({item.changePercent})</span>
              </div>
            </div>
          </div>
        ))}
      </div>
      
      <button className={styles.rankingMoreBtn}>더보기</button>
    </section>
  );
};

export default ETPRanking;
import React from 'react';
import { getBondRankingData } from '../../data/mockData/bondData';
import styles from './RealTimeRanking.module.css';

const BondRanking: React.FC = () => {
  const bondData = getBondRankingData();

  return (
    <section className={styles.rankingSection}>
      <div className={styles.rankingHeader}>
        <h2>장내채권 실시간 랭킹</h2>
        <span className={styles.rankingTime}>실시간 수익률 기준</span>
      </div>
      
      <div className={styles.rankingList}>
        {bondData.slice(0, 5).map((bond) => (
          <div key={bond.rank} className={styles.rankingItem}>
            <div className={styles.rankingLeft}>
              <span className={styles.rankingNumber}>{bond.rank}</span>
              <div className={styles.companyLogo}>
                {bond.emoji ? (
                  <div className={styles.bondEmojiIcon}>
                    <span className={styles.tossfaceEmoji}>{bond.emoji}</span>
                  </div>
                ) : bond.icon ? (
                  <img 
                    src={bond.icon} 
                    alt={bond.name}
                    className={styles.stockIcon}
                    onError={(e) => {
                      const target = e.target as HTMLImageElement;
                      target.style.display = 'none';
                      const placeholder = target.nextElementSibling;
                      if (placeholder) {
                        (placeholder as HTMLElement).style.display = 'block';
                      }
                    }}
                  />
                ) : (
                  <div className={styles.stockIconPlaceholder}>
                    {bond.name.charAt(0)}
                  </div>
                )}
              </div>
              <div className={styles.rankingInfo}>
                <span className={styles.rankingName}>{bond.name}</span>
                <span className={styles.rankingCode}>만기 {bond.maturity}</span>
              </div>
            </div>
            <div className={styles.rankingRight}>
              <div className={styles.rankingPrice}>{bond.yield}</div>
              <div className={`${styles.rankingChange} ${bond.isPositive ? styles.positive : styles.negative}`}>
                <span className={styles.changeArrow}>{bond.isPositive ? '▲' : '▼'}</span>
                <span>{Math.abs(parseFloat(bond.changePercent))}</span>
                <span className={styles.changePercent}>%</span>
              </div>
            </div>
          </div>
        ))}
      </div>
      
      <button className={styles.rankingMoreBtn}>더보기</button>
    </section>
  );
};

export default BondRanking;
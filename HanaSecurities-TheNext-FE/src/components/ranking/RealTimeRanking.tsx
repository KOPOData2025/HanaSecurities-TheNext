import React, { useState, useMemo, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import styles from "./RealTimeRanking.module.css";
import { getRankingData } from "../../data/mockData/rankingData";
import type { RankingItem } from "../../data/mockData/rankingData";
import { rankingApi } from "../../services/rankingApi";
import type { StockRankingItem, RankingType } from "../../types/ranking.types";
import { foreignRankingApi, type ForeignExchangeCode, type ForeignRankingType, type ForeignStockRankItem } from "../../services/foreignRankingApi";
import { tradeWebSocket } from "../../services/tradeWebSocket";
import type { RealtimeTradeData } from "../../services/tradeWebSocket";
import { getStockIcon } from "../../utils/stockIcons";

interface Exchange {
  code: ForeignExchangeCode;
  name: string;
}

interface RealTimeRankingProps {
  title?: string;
  subtitle?: string;
  showCountrySelector?: boolean;
  countries?: string[];
  exchanges?: Exchange[];
  getRankingDataFn?: (country: string, tab: string) => RankingItem[];
}

const RealTimeRanking: React.FC<RealTimeRankingProps> = ({
  title = "국내 실시간 랭킹",
  subtitle = "",
  showCountrySelector = false,
  countries = [],
  exchanges = [
    { code: 'NYS', name: '뉴욕' },
    { code: 'NAS', name: '나스닥' },
    { code: 'HKS', name: '홍콩' },
    { code: 'TSE', name: '도쿄' }
  ],
  getRankingDataFn,
}) => {
  const [activeTab, setActiveTab] = useState("상승");
  const [activeCountry, setActiveCountry] = useState(countries[0] || exchanges[0].name);
  const [rankingData, setRankingData] = useState<RankingItem[]>([]);
  const [loading, setLoading] = useState(false);
  const [showSkeleton, setShowSkeleton] = useState(false);
  const [currentTime, setCurrentTime] = useState("");
  const [realtimePrices, setRealtimePrices] = useState<{ [code: string]: { currentPrice: string; changePrice: string; changeRate: string; isPositive: boolean } }>({});
  const navigate = useNavigate();

  
  useEffect(() => {
    const updateTime = () => {
      const now = new Date();
      const hours = String(now.getHours()).padStart(2, '0');
      const minutes = String(now.getMinutes()).padStart(2, '0');
      setCurrentTime(`${hours}:${minutes} 통합 시세 기준 랭킹`);
    };

    updateTime(); 
    const interval = setInterval(updateTime, 60000); 

    return () => clearInterval(interval);
  }, []);

  const tabs = ["상승", "하락", "거래량", "거래대금"];

  
  const tabToApiType: { [key: string]: RankingType } = {
    "상승": "RISE",
    "하락": "FALL",
    "거래량": "VOLUME",
    "거래대금": "TRADING_VALUE"
  };

  
  const getExchangeCode = (name: string): ForeignExchangeCode => {
    const exchange = exchanges.find(ex => ex.name === name);
    return exchange?.code || 'NYS';
  };

  
  const convertToRankingItem = (stock: StockRankingItem): RankingItem => {
    
    
    const isPositive = activeTab === '하락' ? false : (stock.changeSign === '2' || stock.changeSign === '1');

    
    const formatPrice = (price: string) => {
      const num = parseFloat(price);
      return num.toLocaleString('ko-KR') + '원';
    };

    
    const formatChange = (change: string) => {
      const num = parseFloat(change);
      return num.toLocaleString('ko-KR') + '원';
    };

    
    const formatChangePercent = (rate: string, isPositive: boolean) => {
      return `${isPositive ? '+' : '-'}${rate}%`;
    };

    return {
      rank: parseInt(stock.rank),
      name: stock.stockName,
      code: stock.stockCode,
      currentPrice: formatPrice(stock.currentPrice),
      change: formatChange(stock.changePrice.replace('-', '')),
      changePercent: formatChangePercent(stock.changeRate.replace('-', ''), isPositive),
      isPositive
    };
  };

  
  const convertForeignToRankingItem = (stock: ForeignStockRankItem, exchangeCode: ForeignExchangeCode): RankingItem => {
    
    const isPositive = activeTab === '하락' ? false : (stock.changeSign === '2' || stock.changeSign === '1');

    
    const getCurrency = (exchange: ForeignExchangeCode): string => {
      if (exchange === 'TSE') return '엔';
      return '달러'; 
    };

    const currency = getCurrency(exchangeCode);

    
    const formatPrice = (price: string) => {
      const num = parseFloat(price);
      return num.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 }) + currency;
    };

    
    const formatChange = (change: string) => {
      const num = Math.abs(parseFloat(change));
      return num.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
    };

    
    const formatChangePercent = (rate: string) => {
      const num = Math.abs(parseFloat(rate));
      return `${isPositive ? '+' : '-'}${num.toFixed(2)}%`;
    };

    return {
      rank: parseInt(stock.rank),
      name: stock.stockName,
      code: stock.stockCode,
      currentPrice: formatPrice(stock.currentPrice),
      change: formatChange(stock.changePrice),
      changePercent: formatChangePercent(stock.changeRate),
      isPositive
    };
  };

  
  const loadRankingData = async () => {
    
    if (showCountrySelector) {
      try {
        setLoading(true);
        setShowSkeleton(true);

        const exchangeCode = getExchangeCode(activeCountry);
        const rankingType = tabToApiType[activeTab] as ForeignRankingType;

        
        const [response] = await Promise.all([
          foreignRankingApi.getForeignRanking(rankingType, exchangeCode),
          new Promise(resolve => setTimeout(resolve, 700))
        ]);

        
        const convertedData = response.stocks
          .slice(0, 5)
          .map(stock => convertForeignToRankingItem(stock, exchangeCode));

        setRankingData(convertedData);
      } catch (error) {
        console.error('Failed to load foreign ranking data:', error);
        
        setRankingData([]);
      } finally {
        setLoading(false);
        setShowSkeleton(false);
      }
      return;
    }

    
    try {
      setLoading(true);
      setShowSkeleton(true);

      
      const [response] = await Promise.all([
        rankingApi.getRanking(tabToApiType[activeTab], 'J'),
        new Promise(resolve => setTimeout(resolve, 700))
      ]);

      
      const convertedData = response.stocks
        .slice(0, 5)
        .map(convertToRankingItem);

      setRankingData(convertedData);
    } catch (error) {
      console.error('Failed to load ranking data:', error);
      
      const mockData = getRankingData(activeTab);
      setRankingData(mockData);
    } finally {
      setLoading(false);
      setShowSkeleton(false);
    }
  };

  
  useEffect(() => {
    loadRankingData();
  }, [activeTab, activeCountry, showCountrySelector]);

  
  useEffect(() => {
    if (showCountrySelector || rankingData.length === 0) return;

    const stockCodes = rankingData.map(item => item.code);

    const connectAndSubscribe = async () => {
      try {
        if (!tradeWebSocket.isConnected()) {
          await tradeWebSocket.connect();
        }


        stockCodes.forEach(code => {
          tradeWebSocket.subscribe(code, handleTradeUpdate);
        });
      } catch (error) {
        console.warn('WebSocket 연결 실패 (무시됨):', error);
      }
    };

    connectAndSubscribe();

    
    return () => {
      stockCodes.forEach(code => {
        tradeWebSocket.unsubscribe(code);
      });
    };
  }, [rankingData, showCountrySelector]);

  
  const handleTradeUpdate = (data: RealtimeTradeData) => {
    if (data.type === 'trade' && data.data) {
      const tradeData = data.data;
      const stockCode = tradeData.stockCode;

      const currentPriceNum = parseInt(tradeData.currentPrice);
      const priceChangeNum = parseInt(tradeData.priceChange);
      const isPositive = priceChangeNum >= 0;

      
      const formatPrice = (price: number) => {
        return price.toLocaleString('ko-KR') + '원';
      };

      const formatChange = (change: number) => {
        return Math.abs(change).toLocaleString('ko-KR') + '원';
      };

      const formatChangePercent = (rate: string, isPositive: boolean) => {
        return `${isPositive ? '+' : '-'}${Math.abs(parseFloat(rate))}%`;
      };

      setRealtimePrices(prev => ({
        ...prev,
        [stockCode]: {
          currentPrice: formatPrice(currentPriceNum),
          changePrice: formatChange(priceChangeNum),
          changeRate: formatChangePercent(tradeData.changeRate, isPositive),
          isPositive
        }
      }));
    }
  };

  const getCountryEmoji = (country: string) => {
    const countryEmojis: { [key: string]: string } = {
      "미국": "🇺🇸",
      "홍콩": "🇭🇰",
      "중국": "🇨🇳",
      "일본": "🇯🇵"
    };
    return countryEmojis[country] || "";
  };

  const handleStockClick = (code: string, name: string) => {
    navigate(`/stock/${code}`, {
      state: { stockName: name }
    });
  };

  return (
    <div className={styles.rankingSection}>
      <div className={styles.rankingHeader}>
        <h2>{title}</h2>
        <span className={styles.rankingTime}>{subtitle || currentTime}</span>
      </div>

      {showCountrySelector && (
        <div className={styles.countrySelectorTabs}>
          {exchanges.map((exchange) => (
            <button
              key={exchange.code}
              className={`${styles.countryTab} ${
                activeCountry === exchange.name ? styles.active : ""
              }`}
              onClick={() => setActiveCountry(exchange.name)}
            >
              {exchange.name}
            </button>
          ))}
        </div>
      )}

      <div className={styles.rankingTabs}>
        {tabs.map((tab) => (
          <button
            key={tab}
            className={`${styles.rankingTab} ${activeTab === tab ? styles.active : ""}`}
            onClick={() => setActiveTab(tab)}
          >
            {tab}
          </button>
        ))}
      </div>

      <div className={styles.rankingList}>
        {showSkeleton ? (
          
          <>
            {[1, 2, 3, 4, 5].map((index) => (
              <div key={`skeleton-${index}`} className={`${styles.rankingItem} ${styles.skeleton}`}>
                <div className={styles.rankingLeft}>
                  <div className={styles.companyLogo}>
                    <div className={styles.skeletonCircle} style={{ width: '48px', height: '48px', background: '#f0f0f0', borderRadius: '50%' }}></div>
                  </div>
                  <div className={styles.rankingInfo}>
                    <div className={styles.skeletonText} style={{ width: '80px', height: '16px', background: '#f0f0f0', borderRadius: '4px', marginBottom: '4px' }}></div>
                    <div className={styles.skeletonText} style={{ width: '60px', height: '12px', background: '#f0f0f0', borderRadius: '4px' }}></div>
                  </div>
                </div>
                <div className={styles.rankingRight}>
                  <div className={styles.skeletonText} style={{ width: '70px', height: '16px', background: '#f0f0f0', borderRadius: '4px', marginBottom: '4px' }}></div>
                  <div className={styles.skeletonText} style={{ width: '80px', height: '14px', background: '#f0f0f0', borderRadius: '4px' }}></div>
                </div>
              </div>
            ))}
          </>
        ) : (
          rankingData.map((item) => {
            
            const realtimeData = realtimePrices[item.code];
            const displayPrice = realtimeData?.currentPrice || item.currentPrice;
            const displayChange = realtimeData?.changePrice || item.change;
            const displayChangePercent = realtimeData?.changeRate || item.changePercent;
            const displayIsPositive = realtimeData?.isPositive !== undefined ? realtimeData.isPositive : item.isPositive;

            return (
              <div
                key={`${
                  showCountrySelector ? activeCountry : "domestic"
                }-${activeTab}-${item.rank}-${item.code}`}
                className={styles.rankingItem}
                onClick={() => handleStockClick(item.code, item.name)}
                style={{ cursor: "pointer" }}
              >
                <div className={styles.rankingLeft}>
                  <div className={styles.companyLogo}>
                    <img
                      src={getStockIcon(item.code)}
                      alt={item.name}
                      className={styles.stockIcon}
                      onError={(e) => {
                        e.currentTarget.style.display = "none";
                        e.currentTarget.nextElementSibling?.classList.remove(
                          styles.hidden
                        );
                      }}
                    />
                    <div className={`${styles.stockIconPlaceholder} ${styles.hidden}`}>
                      {item.name.charAt(0)}
                    </div>
                  </div>
                  <div className={styles.rankingInfo}>
                    <div className={styles.rankingName}>{item.name}</div>
                    <div className={styles.rankingCode}>{item.code}</div>
                  </div>
                </div>

                <div className={styles.rankingRight}>
                  <div className={styles.rankingPrice}>{displayPrice}</div>
                  <div
                    className={`${styles.rankingChange} ${
                      displayIsPositive ? styles.positive : styles.negative
                    }`}
                  >
                    <span className={styles.changeArrow}>{displayIsPositive ? '▲' : '▼'}</span>
                    <span className={styles.changeValue}>{displayChange}</span>
                    <span className={styles.changePercent}>{displayChangePercent}</span>
                  </div>
                </div>
              </div>
            );
          })
        )}
      </div>

      <button className={styles.rankingMoreBtn}>더보기</button>
    </div>
  );
};

export default RealTimeRanking;

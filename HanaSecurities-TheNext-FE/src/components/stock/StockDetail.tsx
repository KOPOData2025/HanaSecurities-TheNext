import React, { useState, useEffect } from 'react';
import { useParams, useNavigate, useLocation } from 'react-router-dom';
import { ArrowLeft, Search, MoreVertical, ChevronDown, Expand, Download, Link2 } from 'lucide-react';
import BottomNavigation from '../navigation/BottomNavigation';
import PageHeader from '../common/PageHeader';
import StockInfoSection from '../common/StockInfoSection';
import CandlestickChart from '../chart/CandlestickChart';
import VolumeChart from '../chart/VolumeChart';
import InvestmentOpinion from './InvestmentOpinion';
import StockOverview from './StockOverview';
import FinancialInfo from './FinancialInfo';
import StockOrderBook from './StockOrderBook';
import StockDetailSkeleton from './StockDetailSkeleton';
import ChartTabSkeleton from './ChartTabSkeleton';
import OverviewTabSkeleton from './OverviewTabSkeleton';
import FinancialTabSkeleton from './FinancialTabSkeleton';
import InvestmentOpinionTabSkeleton from './InvestmentOpinionTabSkeleton';
import StockOrderBookSkeleton from './StockOrderBookSkeleton';
import './StockDetail.css';
import { stockApi } from '../../services/stockApi';
import { transformIntradayToChartData, transformPeriodToChartData, periodToCode, getCurrentInputTime } from '../../utils/chartDataTransformer';
import { tradeWebSocket } from '../../services/tradeWebSocket';
import type { ChartData, InvestOpinionResponse } from '../../types/stock.types';
import type { RealtimeTradeData } from '../../services/tradeWebSocket';

const StockDetail: React.FC = () => {
  const { code } = useParams<{ code: string }>();
  const navigate = useNavigate();
  const location = useLocation();
  const initialStockName = location.state?.stockName || code || '';

  const [chartData, setChartData] = useState<ChartData | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const [activeTab, setActiveTab] = useState('차트');
  const [tabLoading, setTabLoading] = useState(false);
  const [activePeriod, setActivePeriod] = useState('일');
  const [activeTimeRange, setActiveTimeRange] = useState('5');

  
  const [realtimePrice, setRealtimePrice] = useState<number | null>(null);
  const [realtimePriceChange, setRealtimePriceChange] = useState<number | null>(null);
  const [realtimeChangeRate, setRealtimeChangeRate] = useState<string | null>(null);

  
  const [investOpinionData, setInvestOpinionData] = useState<InvestOpinionResponse | null>(null);

  
  const CHART_REFRESH_INTERVAL = 500; 

  
  useEffect(() => {
    if (!code) return;

    let intervalId: NodeJS.Timeout | null = null;
    let isInitialLoad = true;

    const fetchChartData = async () => {
      
      if (isInitialLoad) {
        setLoading(true);
        setError(null);
      }

      const loadingStartTime = Date.now();

      try {
        if (activePeriod === '분') {
          
          const inputTime = getCurrentInputTime();
          const response = await stockApi.getIntradayChart(code, inputTime);
          const transformed = transformIntradayToChartData(response);

          if (isInitialLoad) {
            
            const elapsedTime = Date.now() - loadingStartTime;
            const remainingTime = Math.max(0, 700 - elapsedTime);

            setTimeout(() => {
              setChartData(transformed);
              setLoading(false);
              isInitialLoad = false;
            }, remainingTime);
          } else {
            
            setChartData(transformed);
          }
        } else {
          
          const periodCode = periodToCode(activePeriod);
          const days = activePeriod === '일' ? 30 :
                      activePeriod === '주' ? 20 :
                      activePeriod === '월' ? 20 :
                      20; 
          const response = await stockApi.getRecentPeriodChart(code, days, periodCode);
          const transformed = transformPeriodToChartData(response);

          if (isInitialLoad) {
            
            const elapsedTime = Date.now() - loadingStartTime;
            const remainingTime = Math.max(0, 700 - elapsedTime);

            setTimeout(() => {
              setChartData(transformed);
              setLoading(false);
              isInitialLoad = false;
            }, remainingTime);
          } else {
            
            setChartData(transformed);
          }
        }
      } catch (err) {
        if (isInitialLoad) {
          setError('차트 데이터를 불러오는데 실패했습니다.');
          setLoading(false);
        }
        
      }
    };

    
    fetchChartData();

    
    intervalId = setInterval(() => {
      fetchChartData();
    }, CHART_REFRESH_INTERVAL);

    
    return () => {
      if (intervalId) {
        clearInterval(intervalId);
      }
    };
  }, [code, activePeriod, CHART_REFRESH_INTERVAL]);

  
  useEffect(() => {
    if (!code) return;

    const connectAndSubscribe = async () => {
      try {
        if (!tradeWebSocket.isConnected()) {
          await tradeWebSocket.connect();
        }
        tradeWebSocket.subscribe(code, handleTradeUpdate);
      } catch (error) {
        
      }
    };

    connectAndSubscribe();

    return () => {
      if (code) {
        tradeWebSocket.unsubscribe(code);
      }
    };
  }, [code]);

  
  const handleTradeUpdate = (data: RealtimeTradeData) => {
    if (data.type === 'trade' && data.data) {
      const tradeData = data.data;

      const currentPriceNum = parseInt(tradeData.currentPrice);
      const priceChangeNum = parseInt(tradeData.priceChange);
      const changeRateStr = tradeData.changeRate;

      setRealtimePrice(currentPriceNum);
      setRealtimePriceChange(priceChangeNum);
      setRealtimeChangeRate(changeRateStr);
    }
  };

  
  useEffect(() => {
    if (!code) return;

    const fetchInvestOpinion = async () => {
      try {
        const response = await stockApi.getInvestOpinion(code);
        setInvestOpinionData(response);
      } catch (error) {
        console.error('투자의견 조회 실패:', error);
      }
    };

    fetchInvestOpinion();
  }, [code]);

  
  const getInvestmentOpinionData = () => {
    if (!investOpinionData || !investOpinionData.opinions.length) {
      return {
        opinions: [],
        averageScore: 0,
        targetPrice: 0,
        brokersCount: 0,
        latestDate: ''
      };
    }

    
    const formatDate = (dateStr: string) => {
      if (!dateStr || dateStr.length !== 8) return dateStr;
      return `${dateStr.substring(0, 4)}.${dateStr.substring(4, 6)}.${dateStr.substring(6, 8)}`;
    };

    
    const opinions = investOpinionData.opinions.map(item => ({
      date: formatDate(item.businessDate),
      currentOpinion: item.opinion,
      previousOpinion: item.previousOpinion,
      targetPrice: parseInt(item.targetPrice) || 0,
      broker: item.brokerage
    }));

    
    const buyKeywords = ['매수', 'BUY', 'Buy'];
    const buyCount = opinions.filter(op =>
      buyKeywords.some(keyword => op.currentOpinion.includes(keyword))
    ).length;
    const buyRatio = opinions.length > 0 ? buyCount / opinions.length : 0;
    const averageScore = buyRatio * 5; 

    
    const validPrices = opinions.filter(op => op.targetPrice > 0);
    const averageTargetPrice = validPrices.length > 0
      ? Math.round(validPrices.reduce((sum, op) => sum + op.targetPrice, 0) / validPrices.length)
      : 0;

    
    const latestDate = opinions.length > 0 ? opinions[0].date : '';

    return {
      opinions,
      averageScore,
      targetPrice: averageTargetPrice,
      brokersCount: opinions.length,
      latestDate
    };
  };

  const investmentData = getInvestmentOpinionData();

  const mainTabs = ['개요', '호가', '차트', '재무정보', '투자의견'];
  const periodTabs = ['분', '일', '주', '월', '년'];

  
  const handleTabChange = (tab: string) => {
    if (tab === activeTab) return;

    
    setTabLoading(true);

    
    setTimeout(() => {
      setActiveTab(tab);

      
      setTimeout(() => {
        setTabLoading(false);
      }, 700);
    }, 50); 
  };

  
  if (loading && !chartData) {
    return (
      <div className="stock-detail-page">
        <PageHeader
          title={initialStockName}
          leftAction={
            <button className="back-button" onClick={() => navigate(-1)}>
              <ArrowLeft size={24} />
            </button>
          }
          rightActions={
            <>
              <button className="icon-button">
                <Search size={24} />
              </button>
              <button className="action-button">통합</button>
              <button className="action-button">주문</button>
              <button className="icon-button">
                <MoreVertical size={24} />
              </button>
            </>
          }
        />
        <StockDetailSkeleton />
      </div>
    );
  }

  if (error) {
    return (
      <div className="stock-detail-page">
        <PageHeader
          title={initialStockName}
          leftAction={
            <button className="back-button" onClick={() => navigate(-1)}>
              <ArrowLeft size={24} />
            </button>
          }
          rightActions={
            <>
              <button className="icon-button">
                <Search size={24} />
              </button>
              <button className="action-button">통합</button>
              <button className="action-button">주문</button>
              <button className="icon-button">
                <MoreVertical size={24} />
              </button>
            </>
          }
        />
        <div style={{ padding: '20px', textAlign: 'center', color: '#E84041' }}>
          {error}
        </div>
      </div>
    );
  }

  if (!chartData) {
    return (
      <div className="stock-detail-page">
        <PageHeader
          title={initialStockName}
          leftAction={
            <button className="back-button" onClick={() => navigate(-1)}>
              <ArrowLeft size={24} />
            </button>
          }
          rightActions={
            <>
              <button className="icon-button">
                <Search size={24} />
              </button>
              <button className="action-button">통합</button>
              <button className="action-button">주문</button>
              <button className="icon-button">
                <MoreVertical size={24} />
              </button>
            </>
          }
        />
        <div style={{ padding: '20px', textAlign: 'center' }}>
          데이터가 없습니다.
        </div>
      </div>
    );
  }

  return (
    <div className="stock-detail-page">
      {/* Header */}
      <PageHeader
        title={chartData?.stockName || initialStockName}
        leftAction={
          <button className="back-button" onClick={() => navigate(-1)}>
            <ArrowLeft size={24} />
          </button>
        }
        rightActions={
          <>
            <button className="icon-button">
              <Search size={24} />
            </button>
            <button className="action-button">통합</button>
            <button className="action-button">주문</button>
            <button className="icon-button">
              <MoreVertical size={24} />
            </button>
          </>
        }
      />

      {/* Stock Info Section - Reused from OrderPage */}
      <StockInfoSection
        stockData={{
          ...chartData,
          currentPrice: realtimePrice ?? chartData.currentPrice,
          priceChange: realtimePriceChange ?? chartData.priceChange,
          changePercent: realtimeChangeRate ?? chartData.changePercent,
        }}
      />

      {/* Main Tabs */}
      <div className="main-tabs">
        {mainTabs.map((tab) => (
          <button
            key={tab}
            className={`main-tab ${activeTab === tab ? 'active' : ''}`}
            onClick={() => handleTabChange(tab)}
          >
            {tab}
          </button>
        ))}
      </div>

      {/* Content based on active tab */}
      {tabLoading ? (
        
        activeTab === '차트' ? (
          <ChartTabSkeleton />
        ) : activeTab === '개요' ? (
          <OverviewTabSkeleton />
        ) : activeTab === '호가' ? (
          <StockOrderBookSkeleton />
        ) : activeTab === '투자의견' ? (
          <InvestmentOpinionTabSkeleton />
        ) : activeTab === '재무정보' ? (
          <FinancialTabSkeleton />
        ) : null
      ) : (
        
        activeTab === '차트' ? (
          <div className="chart-content">
            {/* Period and Time Range Selector */}
            <div className="stock-chart-controls">
              <div className="stock-period-selector">
                {periodTabs.map((period) => (
                  <button
                    key={period}
                    className={`stock-period-tab ${activePeriod === period ? 'active' : ''}`}
                    onClick={() => setActivePeriod(period)}
                  >
                    {period}
                  </button>
                ))}
              </div>

              <div className="stock-chart-actions">
                <button className="stock-chart-action-btn">
                  <Expand size={16} />
                </button>
                <button className="stock-chart-action-btn">
                  <Download size={16} />
                </button>
                <button className="stock-chart-action-btn">
                  <Link2 size={16} />
                </button>
              </div>
            </div>

            {/* Charts Container */}
            <div className="charts-container">
              {/* Candlestick Chart */}
              <div className="candlestick-chart-container">
                <CandlestickChart
                  data={chartData.candleData}
                  period={activePeriod}
                  timeRange={activeTimeRange}
                />
              </div>

              {/* Volume Chart */}
              <div className="volume-chart-container">
                <VolumeChart
                  data={chartData.volumeData}
                  period={activePeriod}
                  timeRange={activeTimeRange}
                />
              </div>
            </div>
          </div>
        ) : activeTab === '개요' ? (
          <div className="tab-content overview-content">
            <StockOverview />
          </div>
        ) : activeTab === '호가' ? (
          <div className="tab-content orderbook-tab-content">
            <StockOrderBook stockName={chartData?.stockName || ''} />
          </div>
        ) : activeTab === '투자의견' ? (
          <div className="tab-content investment-opinion-content">
            <InvestmentOpinion
              opinions={investmentData.opinions}
              averageScore={investmentData.averageScore}
              targetPrice={investmentData.targetPrice}
              brokersCount={investmentData.brokersCount}
              latestDate={investmentData.latestDate}
            />
          </div>
        ) : activeTab === '재무정보' ? (
          <div className="tab-content financial-info-content">
            <FinancialInfo />
          </div>
        ) : (
          <div className="tab-content">
            <div style={{ padding: '20px', textAlign: 'center', color: '#666' }}>
              {activeTab} 탭 콘텐츠
            </div>
          </div>
        )
      )}

      <BottomNavigation />
    </div>
  );
};

export default StockDetail;
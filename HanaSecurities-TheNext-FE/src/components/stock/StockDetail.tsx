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
import { foreignStockApi } from '../../services/foreignStockApi';
import {
  transformIntradayToChartData,
  transformPeriodToChartData,
  transformForeignIntradayToChartData,
  transformForeignPeriodToChartData,
  transformGoldToChartData,
  periodToCode,
  getCurrentInputTime
} from '../../utils/chartDataTransformer';
import { tradeWebSocket } from '../../services/tradeWebSocket';
import { foreignQuoteWebSocket } from '../../services/foreignQuoteWebSocket';
import { goldTradeWebSocket, getGoldCurrentPrice, getGoldMinuteChart, getGoldPeriodChart } from '../../services/goldApi';
import type { ChartData, InvestOpinionResponse } from '../../types/stock.types';
import type { RealtimeTradeData } from '../../services/tradeWebSocket';
import type { ForeignQuoteData } from '../../types/foreignStock.types';
import type { GoldTradeData } from '../../services/goldApi';

const StockDetail: React.FC = () => {
  // URL 파라미터: 국내 주식은 /stock/:code, 해외 주식은 /stock/:exchangeCode/:stockCode, 금현물은 /stock/gold/:productCode
  const { code, exchangeCode, stockCode } = useParams<{ code?: string; exchangeCode?: string; stockCode?: string }>();
  const navigate = useNavigate();
  const location = useLocation();

  // 상품 타입 판별
  const isGold = code === 'gold';
  const isForeignStock = !!exchangeCode && !isGold;
  const isDomesticStock = !isForeignStock && !isGold;
  const actualCode = isGold ? stockCode! : (isForeignStock ? stockCode! : code!);
  const initialStockName = location.state?.stockName || actualCode || '';

  const [chartData, setChartData] = useState<ChartData | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // 현재가 정보 (stock-info-section용, 최초 1회만 조회)
  const [stockInfoData, setStockInfoData] = useState<ChartData | null>(null);
  const [stockInfoLoading, setStockInfoLoading] = useState(false);

  const [activeTab, setActiveTab] = useState('차트');
  const [tabLoading, setTabLoading] = useState(false);
  const [activePeriod, setActivePeriod] = useState('일');
  const [activeTimeRange, setActiveTimeRange] = useState('5');


  const [realtimePrice, setRealtimePrice] = useState<number | null>(null);
  const [realtimePriceChange, setRealtimePriceChange] = useState<number | null>(null);
  const [realtimeChangeRate, setRealtimeChangeRate] = useState<string | null>(null);

  
  const [investOpinionData, setInvestOpinionData] = useState<InvestOpinionResponse | null>(null);


  const CHART_REFRESH_INTERVAL = 500;

  // 현재가 정보 조회 (해외주식/금현물: 1초마다 polling, 국내주식: 1회만)
  useEffect(() => {
    if (!actualCode) return;

    const fetchStockInfo = async () => {
      try {
        if (isGold) {
          // 금현물: getCurrentPrice API 사용
          const response = await getGoldCurrentPrice(actualCode);
          const transformed = transformGoldToChartData(response);
          setStockInfoData(transformed);
          if (stockInfoLoading) setStockInfoLoading(false);
        } else if (isForeignStock) {
          // 해외 주식: getCurrentPrice API 사용
          const response = await foreignStockApi.getCurrentPrice(exchangeCode!, actualCode);

          // 현재가와 전일종가 계산
          const currentPrice = parseFloat(response.last || '0');
          const basePrice = parseFloat(response.base || '0');
          const priceChange = currentPrice - basePrice;

          // 등락률 계산 (퍼센트 기호 제거하고 숫자만)
          let changePercent = response.t_xrat || '0';
          changePercent = changePercent.replace('%', '').trim();

          // ChartData 형식으로 변환
          // 해외 주식: totalShares = tvol (거래량이 아닌 총 상장주식수)
          const stockInfo: ChartData = {
            stockCode: response.rsym || actualCode,
            stockName: response.rsym || actualCode,
            currentPrice: currentPrice,
            priceChange: priceChange,
            changePercent: changePercent,
            totalShares: parseFloat(response.tvol || '0'),  // tvol을 상장주수로 사용
            indices: [],
            candleData: [],
            volumeData: []
          };

          setStockInfoData(stockInfo);
          if (stockInfoLoading) setStockInfoLoading(false);
        } else {
          // 국내 주식: 분봉 API로 현재가 조회 (1회만)
          setStockInfoLoading(true);
          const inputTime = getCurrentInputTime();
          const response = await stockApi.getIntradayChart(actualCode, inputTime);
          const transformed = transformIntradayToChartData(response);
          setStockInfoData(transformed);
          setStockInfoLoading(false);
        }
      } catch (err) {
        console.error('현재가 조회 실패:', err);
        if (stockInfoLoading) setStockInfoLoading(false);
      }
    };

    // 최초 조회
    fetchStockInfo();

    // 해외 주식과 금현물은 1초마다 polling
    let intervalId: NodeJS.Timeout | null = null;
    if (isForeignStock || isGold) {
      intervalId = setInterval(() => {
        fetchStockInfo();
      }, 1000);
    }

    return () => {
      if (intervalId) {
        clearInterval(intervalId);
      }
    };
  }, [actualCode, exchangeCode, isForeignStock, isGold]);

  // 차트 데이터 조회 (기간 변경시마다 조회)
  useEffect(() => {
    if (!actualCode) return;

    let intervalId: NodeJS.Timeout | null = null;
    let isInitialLoad = true;

    const fetchChartData = async () => {

      if (isInitialLoad) {
        setLoading(true);
        setError(null);
      }

      const loadingStartTime = Date.now();

      try {
        if (isForeignStock) {
          // 해외 주식 차트
          if (activePeriod === '분') {
            const response = await foreignStockApi.getIntradayChart(exchangeCode!, actualCode, 5);
            const transformed = transformForeignIntradayToChartData(response);

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
            // 해외 주식 기간별 차트
            const periodCode = periodToCode(activePeriod);
            const days = activePeriod === '일' ? 30 : activePeriod === '주' ? 20 : activePeriod === '월' ? 20 : 20;
            const response = await foreignStockApi.getRecentPeriodChart(exchangeCode!, actualCode, days, periodCode);
            const transformed = transformForeignPeriodToChartData(response);

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
        } else {
          // 국내 주식 차트
          if (activePeriod === '분') {
            const inputTime = getCurrentInputTime();
            const response = await stockApi.getIntradayChart(actualCode, inputTime);
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
            const days = activePeriod === '일' ? 30 : activePeriod === '주' ? 20 : activePeriod === '월' ? 20 : 20;
            const response = await stockApi.getRecentPeriodChart(actualCode, days, periodCode);
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
  }, [actualCode, exchangeCode, isForeignStock, activePeriod, CHART_REFRESH_INTERVAL]);


  useEffect(() => {
    if (!actualCode) return;

    const connectAndSubscribe = async () => {
      try {
        if (isForeignStock) {
          // 해외 주식 WebSocket - 체결가 구독
          if (!foreignQuoteWebSocket.isConnected()) {
            await foreignQuoteWebSocket.connect();
          }
          foreignQuoteWebSocket.subscribe(exchangeCode!, actualCode, 'trade', handleForeignQuoteUpdate);
        } else {
          // 국내 주식 WebSocket
          if (!tradeWebSocket.isConnected()) {
            await tradeWebSocket.connect();
          }
          tradeWebSocket.subscribe(actualCode, handleTradeUpdate);
        }
      } catch (error) {
        console.error('WebSocket 연결 실패:', error);
      }
    };

    connectAndSubscribe();

    return () => {
      if (isForeignStock) {
        foreignQuoteWebSocket.unsubscribe(exchangeCode!, actualCode, 'trade');
      } else {
        tradeWebSocket.unsubscribe(actualCode);
      }
    };
  }, [actualCode, exchangeCode, isForeignStock]);

  // 국내 주식 실시간 업데이트 핸들러
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

  // 해외 주식 실시간 업데이트 핸들러
  const handleForeignQuoteUpdate = (data: any) => {
    console.log('[해외주식 WebSocket] 수신 데이터:', data);

    // currentPrice가 있으면 사용 (백엔드가 currentPrice로 보냄)
    if (data.currentPrice) {
      setRealtimePrice(parseFloat(data.currentPrice));
    }
    // changeRate가 있으면 사용
    if (data.changeRate && data.changeRate !== "0") {
      setRealtimeChangeRate(data.changeRate);
    }
  };


  useEffect(() => {
    // 국내 주식일 때만 투자의견 로드
    if (!actualCode || isForeignStock) return;

    const fetchInvestOpinion = async () => {
      try {
        const response = await stockApi.getInvestOpinion(actualCode);
        setInvestOpinionData(response);
      } catch (error) {
        console.error('투자의견 조회 실패:', error);
      }
    };

    fetchInvestOpinion();
  }, [actualCode, isForeignStock]);

  
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

  // 해외 주식은 투자의견/재무정보 없음, 년봉 차트 없음
  const mainTabs = isForeignStock
    ? ['개요', '호가', '차트']
    : ['개요', '호가', '차트', '재무정보', '투자의견'];
  const periodTabs = isForeignStock
    ? ['분', '일', '주', '월']
    : ['분', '일', '주', '월', '년'];

  
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


  // 초기 로딩: 현재가 정보와 차트 데이터 둘 다 없을 때
  if ((stockInfoLoading && !stockInfoData) || (loading && !chartData)) {
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

  if (!chartData || !stockInfoData) {
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

      {/* Stock Info Section - 현재가 정보 (최초 1회 조회, 실시간 업데이트) */}
      {stockInfoData && (
        <StockInfoSection
          stockData={{
            ...stockInfoData,
            currentPrice: realtimePrice ?? stockInfoData.currentPrice,
            priceChange: realtimePriceChange ?? stockInfoData.priceChange,
            changePercent: realtimeChangeRate ?? stockInfoData.changePercent,
          }}
          exchangeCode={exchangeCode}
          isForeignStock={isForeignStock}
          classPrefix="stock-detail"
        />
      )}

      {/* Main Tabs */}
      <div className={`main-tabs ${isForeignStock ? 'foreign-tabs' : ''}`}>
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
                  isForeignStock={isForeignStock}
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
            <StockOverview
              isForeignStock={isForeignStock}
              exchangeCode={exchangeCode}
              stockCode={actualCode}
            />
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
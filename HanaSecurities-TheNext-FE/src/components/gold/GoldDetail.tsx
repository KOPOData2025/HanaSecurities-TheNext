import React, { useState, useEffect } from 'react';
import { useParams, useNavigate, useLocation } from 'react-router-dom';
import { ArrowLeft, Search, MoreVertical, Expand, Download, Link2 } from 'lucide-react';
import BottomNavigation from '../navigation/BottomNavigation';
import PageHeader from '../common/PageHeader';
import StockInfoSection from '../common/StockInfoSection';
import CandlestickChart from '../chart/CandlestickChart';
import VolumeChart from '../chart/VolumeChart';
import GoldOrderBook from './GoldOrderBook';
import StockDetailSkeleton from '../stock/StockDetailSkeleton';
import ChartTabSkeleton from '../stock/ChartTabSkeleton';
import OverviewTabSkeleton from '../stock/OverviewTabSkeleton';
import StockOrderBookSkeleton from '../stock/StockOrderBookSkeleton';
import '../stock/StockDetail.css';
import './GoldDetail.css';
import './GoldOverview.css';
import {
  transformGoldToChartData,
  periodToCode
} from '../../utils/chartDataTransformer';
import { goldTradeWebSocket, getGoldCurrentPrice, getGoldMinuteChart, getGoldPeriodChart, GOLD_PRODUCTS } from '../../services/goldApi';
import type { ChartData } from '../../types/stock.types';
import type { GoldTradeData } from '../../services/goldApi';

const GoldDetail: React.FC = () => {
  // URL 파라미터: /gold/:productCode
  const { productCode } = useParams<{ productCode?: string }>();
  const navigate = useNavigate();
  const location = useLocation();

  const productName = productCode === GOLD_PRODUCTS.GOLD_1KG ? '금 99.99% 1Kg' : '미니금 99.99% 100g';
  const initialProductName = location.state?.productName || productName;

  const [chartData, setChartData] = useState<ChartData | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // 현재가 정보 (stock-info-section용)
  const [stockInfoData, setStockInfoData] = useState<ChartData | null>(null);
  const [stockInfoLoading, setStockInfoLoading] = useState(false);

  const [activeTab, setActiveTab] = useState('차트');
  const [tabLoading, setTabLoading] = useState(false);
  const [activePeriod, setActivePeriod] = useState('일');
  const [activeTimeRange, setActiveTimeRange] = useState('5');

  const [realtimePrice, setRealtimePrice] = useState<number | null>(null);
  const [realtimePriceChange, setRealtimePriceChange] = useState<number | null>(null);
  const [realtimeChangeRate, setRealtimeChangeRate] = useState<string | null>(null);

  const CHART_REFRESH_INTERVAL = 500;

  // 현재가 정보 조회 (1초마다 polling)
  useEffect(() => {
    if (!productCode) return;

    const fetchGoldInfo = async () => {
      try {
        const response = await getGoldCurrentPrice(productCode);
        const transformed = transformGoldToChartData(response);
        setStockInfoData(transformed);
        if (stockInfoLoading) setStockInfoLoading(false);
      } catch (err) {
        console.error('금현물 현재가 조회 실패:', err);
        if (stockInfoLoading) setStockInfoLoading(false);
      }
    };

    // 최초 조회
    fetchGoldInfo();

    // 1초마다 polling
    const intervalId = setInterval(() => {
      fetchGoldInfo();
    }, 1000);

    return () => {
      clearInterval(intervalId);
    };
  }, [productCode]);

  // 차트 데이터 조회 (기간 변경시마다 조회)
  useEffect(() => {
    if (!productCode) return;

    let intervalId: NodeJS.Timeout | null = null;
    let isInitialLoad = true;

    const fetchChartData = async () => {
      if (isInitialLoad) {
        setLoading(true);
        setError(null);
      }

      const loadingStartTime = Date.now();

      try {
        let goldChart;
        let goldPrice;

        if (activePeriod === '분') {
          // 분봉 차트 (100개 → 50개 → 25개로 절반 감소)
          goldChart = await getGoldMinuteChart(productCode, 5, 25);
        } else {
          // 일/주/월봉 차트
          const periodMap: Record<string, 'day' | 'week' | 'month'> = {
            '일': 'day',
            '주': 'week',
            '월': 'month'
          };
          const period = periodMap[activePeriod] || 'day';
          const count = activePeriod === '일' ? 50 : activePeriod === '주' ? 13 : 3;
          goldChart = await getGoldPeriodChart(productCode, period, count);
        }

        // 현재가 정보도 함께 가져오기
        goldPrice = await getGoldCurrentPrice(productCode);
        const transformed = transformGoldToChartData(goldPrice, goldChart);

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
  }, [productCode, activePeriod, CHART_REFRESH_INTERVAL]);

  // WebSocket 실시간 업데이트
  useEffect(() => {
    if (!productCode) return;

    goldTradeWebSocket.subscribe((data: GoldTradeData) => {
      if (data.productCode === productCode) {
        setRealtimePrice(data.price);
        setRealtimePriceChange(data.changeAmount);
        setRealtimeChangeRate(data.changeRate.toString());
      }
    });

    return () => {
      goldTradeWebSocket.unsubscribe();
    };
  }, [productCode]);

  // 금현물은 개요/호가/차트만, 년봉 차트 없음
  const mainTabs = ['개요', '호가', '차트'];
  const periodTabs = ['분', '일', '주', '월'];

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

  // 초기 로딩
  if ((stockInfoLoading && !stockInfoData) || (loading && !chartData)) {
    return (
      <div className="stock-detail-page">
        <PageHeader
          title={initialProductName}
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
          title={initialProductName}
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
          title={initialProductName}
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
        title={chartData?.stockName || initialProductName}
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

      {/* Gold Info Section - 현재가 정보 (실시간 업데이트) */}
      {stockInfoData && (
        <StockInfoSection
          stockData={{
            ...stockInfoData,
            currentPrice: realtimePrice ?? stockInfoData.currentPrice,
            priceChange: realtimePriceChange ?? stockInfoData.priceChange,
            changePercent: realtimeChangeRate ?? stockInfoData.changePercent,
          }}
          isForeignStock={false}
          classPrefix="gold-detail"
        />
      )}

      {/* Main Tabs */}
      <div className="main-tabs gold-main-tabs">
        {mainTabs.map((tab) => (
          <button
            key={tab}
            className={`main-tab gold-main-tab ${activeTab === tab ? 'active' : ''}`}
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
                  isForeignStock={false}
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
            <div className="gold-overview">
              {/* Sub Tabs */}
              <div className="gold-sub-tabs">
                <button
                  className="gold-sub-tab active"
                >
                  상품정보
                </button>
                <button
                  className="gold-sub-tab"
                >
                  시세정보
                </button>
                <span className="gold-sub-tab-link">실시간</span>
              </div>

              {/* Overview Table */}
              <div className="gold-overview-table-container">
                <table className="gold-overview-table">
                  <tbody>
                    <tr>
                      <td className="gold-label">상품명</td>
                      <td className="gold-value">{productName}</td>
                    </tr>
                    <tr>
                      <td className="gold-label">상품코드</td>
                      <td className="gold-value">{productCode}</td>
                    </tr>
                    <tr>
                      <td className="gold-label">시장</td>
                      <td className="gold-value">한국거래소 (KRX)</td>
                    </tr>
                    <tr>
                      <td className="gold-label">상품그룹</td>
                      <td className="gold-value">금현물</td>
                    </tr>
                    <tr>
                      <td className="gold-label">순도</td>
                      <td className="gold-value">99.99%</td>
                    </tr>
                    <tr>
                      <td className="gold-label">중량</td>
                      <td className="gold-value">
                        {productCode === GOLD_PRODUCTS.GOLD_1KG ? '1Kg (1000g)' : '100g'}
                      </td>
                    </tr>
                    <tr>
                      <td className="gold-label">거래단위</td>
                      <td className="gold-value">1g</td>
                    </tr>
                    <tr>
                      <td className="gold-label">결제방식</td>
                      <td className="gold-value">현금결제</td>
                    </tr>
                    <tr>
                      <td className="gold-label">거래시간</td>
                      <td className="gold-value">09:00 ~ 15:30</td>
                    </tr>
                    <tr>
                      <td className="gold-label">거래통화</td>
                      <td className="gold-value">KRW (원)</td>
                    </tr>
                    <tr>
                      <td className="gold-label">최소변동가격</td>
                      <td className="gold-value">10원</td>
                    </tr>
                    <tr>
                      <td className="gold-label">상장일</td>
                      <td className="gold-value">2014-03-24</td>
                    </tr>
                    <tr>
                      <td className="gold-label">발행기관</td>
                      <td className="gold-value">한국거래소</td>
                    </tr>
                    <tr>
                      <td className="gold-label">거래가능여부</td>
                      <td className="gold-value">가능</td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
          </div>
        ) : activeTab === '호가' ? (
          <div className="tab-content orderbook-tab-content">
            {productCode && <GoldOrderBook productCode={productCode} />}
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

export default GoldDetail;

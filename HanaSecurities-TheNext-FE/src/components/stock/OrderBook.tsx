import React from 'react';
import { Line, ResponsiveContainer, LineChart, YAxis } from 'recharts';
import './OrderBook.css';

interface OrderLevel {
  price: number;
  quantity: number;
  changePercent: number;
  totalQuantity?: number;
}

interface OrderBookData {
  code: string;
  name: string;
  currentPrice: number;
  priceChange: number;
  changePercent: number;
  marketCap: number;
  marketCapPercent: number;
  bidOrders: OrderLevel[];
  askOrders: OrderLevel[];
  totalBidQuantity: number;
  totalAskQuantity: number;
  totalBidAmount: number;
  totalAskAmount: number;
  chartData: { time: string; value: number }[];
  brokerInfo: {
    yesterdayBuy: number;
    yesterdayBuyVolume: number;
    weekAvg: number;
    weekAvgVolume: number;
    monthAvg: number;
    monthAvgVolume: number;
    topBuyer: string;
    topBuyerVolume: number;
    topBuyerPercent: number;
    topSeller: string;
    topSellerVolume: number;
    topSellerPercent: number;
  };
}

interface OrderBookProps {
  data: OrderBookData;
}

const OrderBook: React.FC<OrderBookProps> = ({ data }) => {
  const formatPrice = (price: number) => {
    return price.toLocaleString('ko-KR');
  };

  const formatQuantity = (quantity: number) => {
    return quantity.toLocaleString('ko-KR');
  };

  const getBarWidth = (quantity: number, maxQuantity: number) => {
    return (quantity / maxQuantity) * 100;
  };

  const maxAskQuantity = Math.max(...data.askOrders.map(order => order.quantity));
  const maxBidQuantity = Math.max(...data.bidOrders.map(order => order.quantity));

  return (
    <div className="orderbook-container">
      {/* Header Info */}
      <div className="orderbook-header">
        <div className="stock-header-info">
          <div className="stock-codes">
            <span className="code-primary">086790</span>
            <span className="code-secondary">KOSPI200</span>
            <span className="code-type">KRX+NXT</span>
          </div>
          <button className="favorite-btn">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"></path>
            </svg>
          </button>
        </div>

        <div className="price-section">
          <div className="main-price">
            <span className="price-icon">▮</span>
            <span className="current-price">{formatPrice(data.currentPrice)}</span>
          </div>
          <div className="price-change">
            <span className="change-arrow">▲</span>
            <span className="change-amount">{formatPrice(data.priceChange)}</span>
            <span className="change-percent">{data.changePercent}%</span>
          </div>
          <div className="market-cap">
            {formatPrice(data.marketCap)}주 ({data.marketCapPercent}%)
          </div>
        </div>

        <div className="mini-chart">
          <ResponsiveContainer width="100%" height={50}>
            <LineChart data={data.chartData} margin={{ top: 5, right: 5, left: 5, bottom: 5 }}>
              <YAxis hide domain={['dataMin', 'dataMax']} />
              <Line
                type="monotone"
                dataKey="value"
                stroke="#E60000"
                strokeWidth={1.5}
                dot={false}
              />
            </LineChart>
          </ResponsiveContainer>
          <div className="chart-time">
            <span className="chart-period">일</span>
            <div className="chart-indicators">
              <span>종 20</span>
              <span>신 45</span>
            </div>
          </div>
        </div>
      </div>

      {/* Order Book Table */}
      <div className="orderbook-table">
        {/* Ask Orders (매도) */}
        <div className="ask-orders">
          {[...data.askOrders].reverse().map((order, index) => (
            <div key={index} className="order-row ask-row">
              <div className="order-quantity-left">
                <div className="quantity-bar-container">
                  <div
                    className="quantity-bar ask-bar"
                    style={{ width: `${getBarWidth(order.quantity, maxAskQuantity)}%` }}
                  />
                </div>
                <span className="quantity-text">{formatQuantity(order.quantity)}</span>
              </div>
              <div className="order-price-center">
                <span className="order-price ask-price">{formatPrice(order.price)}</span>
              </div>
              <div className="order-percent-right">
                <span className="order-percent">{order.changePercent}%</span>
              </div>
              <div className="order-empty"></div>
            </div>
          ))}
        </div>

        {/* Current Price Divider */}
        <div className="current-price-divider">
          <div className="divider-content">
            <span className="dots">• • •</span>
            <div className="current-price-box">
              <span className="current-price-text">{formatPrice(data.currentPrice)}</span>
              <span className="current-change-percent">{data.changePercent}%</span>
              <span className="current-quantity">{formatQuantity(3652)}</span>
              <span className="current-count">-10</span>
            </div>
          </div>
        </div>

        {/* Bid Orders (매수) */}
        <div className="bid-orders">
          {data.bidOrders.slice(1).map((order, index) => (
            <div key={index} className="order-row bid-row">
              <div className="order-empty"></div>
              <div className="order-price-center">
                <span className="order-price bid-price">{formatPrice(order.price)}</span>
              </div>
              <div className="order-percent-right">
                <span className="order-percent">{order.changePercent}%</span>
              </div>
              <div className="order-quantity-right">
                <span className="quantity-text">{formatQuantity(order.quantity)}</span>
                <div className="quantity-bar-container">
                  <div
                    className="quantity-bar bid-bar"
                    style={{ width: `${getBarWidth(order.quantity, maxBidQuantity)}%` }}
                  />
                </div>
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* Broker Info Section */}
      <div className="broker-info">
        <div className="broker-grid">
          <div className="broker-item">
            <span className="broker-label">전일거래량</span>
            <span className="broker-value">{formatQuantity(data.brokerInfo.yesterdayBuy)}</span>
          </div>
          <div className="broker-item">
            <span className="broker-label">전일가</span>
            <span className="broker-value">{formatQuantity(data.brokerInfo.yesterdayBuyVolume)}</span>
          </div>
          <div className="broker-item">
            <span className="broker-label">52주최고</span>
            <span className="broker-value">{formatQuantity(data.brokerInfo.weekAvg)}</span>
          </div>
          <div className="broker-item">
            <span className="broker-label">52주최저</span>
            <span className="broker-value">{formatQuantity(data.brokerInfo.weekAvgVolume)}</span>
          </div>
          <div className="broker-item">
            <span className="broker-label">5일이평</span>
            <span className="broker-value">{formatQuantity(data.brokerInfo.monthAvg)}</span>
          </div>
          <div className="broker-item">
            <span className="broker-label">20일이평</span>
            <span className="broker-value">{formatQuantity(data.brokerInfo.monthAvgVolume)}</span>
          </div>
        </div>

        <div className="broker-special">
          <div className="broker-special-item">
            <span className="broker-label">삼환기</span>
            <span className="broker-value red">{formatQuantity(data.brokerInfo.topBuyerVolume)}</span>
          </div>
          <div className="broker-special-item">
            <span className="broker-label">하환기</span>
            <span className="broker-value blue">{formatQuantity(data.brokerInfo.topSellerVolume)}</span>
          </div>
        </div>

        <div className="price-info">
          <div className="price-info-item">
            <span className="broker-label">시</span>
            <span className="broker-value red">{formatQuantity(89500)}</span>
            <span className="broker-percent">(0.90%)</span>
          </div>
          <div className="price-info-item">
            <span className="broker-label">고</span>
            <span className="broker-value red">{formatQuantity(91900)}</span>
            <span className="broker-percent">(3.61%)</span>
          </div>
          <div className="price-info-item">
            <span className="broker-label">저</span>
            <span className="broker-value blue">{formatQuantity(88000)}</span>
            <span className="broker-percent">(-0.79%)</span>
          </div>
        </div>

        <button className="more-info-btn">
          <span>10호가</span>
          <span className="dropdown-arrow">▼</span>
        </button>
      </div>

      {/* Bottom Summary */}
      <div className="orderbook-summary">
        <div className="summary-header">
          <div className="summary-left">
            <span className="summary-label">총갈</span>
          </div>
          <div className="summary-center">
            <span className="summary-label">매도</span>
            <span className="summary-time">14:27:58</span>
            <span className="summary-label">매수</span>
          </div>
          <div className="summary-right">
            <span className="summary-label">총갈</span>
          </div>
        </div>
        <div className="summary-values">
          <div className="summary-bid">
            <span className="summary-value">{formatQuantity(26542)}</span>
          </div>
          <div className="summary-center-values">
            <button className="total-button">총친량</button>
          </div>
          <div className="summary-ask">
            <span className="summary-value">{formatQuantity(20454)}</span>
          </div>
        </div>
        <div className="balance-bar">
          <div className="bid-balance" style={{ width: '56%' }}>
            <span>{formatQuantity(data.totalBidAmount)}</span>
          </div>
          <div className="bar-center">•</div>
          <div className="ask-balance" style={{ width: '44%' }}>
            <span>{formatQuantity(data.totalAskAmount)}</span>
          </div>
        </div>
      </div>
    </div>
  );
};

export default OrderBook;
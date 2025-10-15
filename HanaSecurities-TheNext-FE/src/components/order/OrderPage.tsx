import React, { useState, useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { ArrowLeft, Search, ChevronDown, Info, RefreshCw } from 'lucide-react';
import BottomNavigation from '../navigation/BottomNavigation';
import PageHeader from '../common/PageHeader';
import OrderConfirmModal from './OrderConfirmModal';
import OrderExecutionToast from './OrderExecutionToast';
import OrderPageSkeleton from './OrderPageSkeleton';
import './OrderPage.css';
import { getOrderData } from '../../data/mockData/orderData';
import { quoteWebSocket } from '../../services/quoteWebSocket';
import { tradeWebSocket } from '../../services/tradeWebSocket';
import type { RealtimeQuoteData } from '../../services/quoteWebSocket';
import type { RealtimeTradeData } from '../../services/tradeWebSocket';
import { orderApi, type StockOrderRequest } from '../../services/orderApi';
import { stockApi } from '../../services/stockApi';
import type { BuyableAmountResponse, SellableQuantityResponse } from '../../types/stock.types';

const OrderPage: React.FC = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const orderData = getOrderData();

  
  const stockCode = (location.state as any)?.stockCode || orderData.stockCode;
  const stockName = (location.state as any)?.stockName || orderData.stockName;
  const initialPrice = (location.state as any)?.price || 90800;
  const orderTypeFromNav = (location.state as any)?.type; 

  
  const getInitialTab = () => {
    if (orderTypeFromNav === 'buy') return '매수';
    if (orderTypeFromNav === 'sell') return '매도';
    return '매수';
  };

  const [activeTab, setActiveTab] = useState(getInitialTab());
  const [isLoading, setIsLoading] = useState(true);
  const [orderType, setOrderType] = useState('호가');
  const [orderPrice, setOrderPrice] = useState(initialPrice);
  const [orderQuantity, setOrderQuantity] = useState(0);
  const [marketType, setMarketType] = useState('SOR');
  const [priceType, setPriceType] = useState('지정가');
  const [paymentType, setPaymentType] = useState('현금');
  const [showOrderConfirmModal, setShowOrderConfirmModal] = useState(false);
  const [showExecutionToast, setShowExecutionToast] = useState(false);

  
  const [buyableData, setBuyableData] = useState<BuyableAmountResponse | null>(null);
  
  const [sellableData, setSellableData] = useState<SellableQuantityResponse | null>(null);

  
  const [currentPrice, setCurrentPrice] = useState(orderData.currentPrice);
  const [priceChange, setPriceChange] = useState(orderData.priceChange);
  const [changePercent, setChangePercent] = useState(orderData.changePercent);
  const [isPositive, setIsPositive] = useState(true);
  const [previousClosePrice, setPreviousClosePrice] = useState(0);

  
  const [sellOrders, setSellOrders] = useState(orderData.orderBook.sell);
  const [buyOrders, setBuyOrders] = useState(orderData.orderBook.buy);
  const [upperLimit, setUpperLimit] = useState(orderData.upperLimit);
  const [lowerLimit, setLowerLimit] = useState(orderData.lowerLimit);
  const [totalAskVolume, setTotalAskVolume] = useState<number>(0);
  const [totalBidVolume, setTotalBidVolume] = useState<number>(0);

  const tabs = ['매수', '매도', '정정/취소', '체결/예약', '잔고'];
  const orderTypes = ['호가', '체결'];
  const marketTypes = ['SOR', 'KRX', 'NXT'];
  const priceTypes = ['지정가', '시장가'];

  const formatPrice = (price: number) => {
    return price.toLocaleString('ko-KR');
  };

  const formatQuantity = (quantity: number) => {
    return quantity.toLocaleString('ko-KR');
  };

  const handlePriceChange = (increment: number) => {
    setOrderPrice(prev => Math.max(0, prev + increment));
  };

  const handleQuantityChange = (increment: number) => {
    setOrderQuantity(prev => Math.max(0, prev + increment));
  };

  const calculateOrderAmount = () => {
    return orderPrice * orderQuantity;
  };

  
  const fetchBuyableAmount = async () => {
    if (!stockCode) return;

    try {
      const response = await stockApi.getBuyableAmount(stockCode);
      if (response.success) {
        setBuyableData(response);
      }
    } catch (error) {
      console.error('매수가능조회 실패:', error);
    }
  };

  
  const fetchSellableQuantity = async () => {
    if (!stockCode) return;

    try {
      const response = await stockApi.getSellableQuantity(stockCode);
      if (response.success) {
        setSellableData(response);
      }
    } catch (error) {
      console.error('매도가능수량조회 실패:', error);
    }
  };

  
  const handleQuantityRefresh = () => {
    if (activeTab === '매수') {
      fetchBuyableAmount();
    } else if (activeTab === '매도') {
      fetchSellableQuantity();
    }
  };

  
  useEffect(() => {
    if (!stockCode) return;

    if (activeTab === '매수') {
      fetchBuyableAmount();
    } else if (activeTab === '매도') {
      fetchSellableQuantity();
    }
  }, [stockCode, activeTab]);

  
  const calculateBarWidth = (quantity: number): number => {
    const allQuantities = [...sellOrders.map(o => o.quantity), ...buyOrders.map(o => o.quantity)];
    const maxQuantity = Math.max(...allQuantities);
    const baseValue = maxQuantity * 1.5;
    return Math.min((quantity / baseValue) * 100, 100);
  };

  
  useEffect(() => {
    if (!stockCode) return;

    
    setIsLoading(true);
    const loadingTimer = setTimeout(() => {
      setIsLoading(false);
    }, 700);

    
    const connectAndSubscribeQuote = async () => {
      try {
        if (!quoteWebSocket.isConnected()) {
          await quoteWebSocket.connect();
        }
        quoteWebSocket.subscribe(stockCode, handleQuoteUpdate);
      } catch (error) {
        
      }
    };

    connectAndSubscribeQuote();

    return () => {
      clearTimeout(loadingTimer);
      quoteWebSocket.unsubscribe(stockCode);
    };
  }, [stockCode]);

  
  useEffect(() => {
    if (!stockCode) return;

    const connectAndSubscribeTrade = async () => {
      try {
        if (!tradeWebSocket.isConnected()) {
          await tradeWebSocket.connect();
        }
        tradeWebSocket.subscribe(stockCode, handleTradeUpdate);
      } catch (error) {
        
      }
    };

    connectAndSubscribeTrade();

    return () => {
      tradeWebSocket.unsubscribe(stockCode);
    };
  }, [stockCode]);

  
  const handleQuoteUpdate = (data: RealtimeQuoteData) => {
    if (data.type === 'quote' && data.data) {
      const { askPrices, bidPrices, askVolumes, bidVolumes, totalAskVolume, totalBidVolume } = data.data;

      
      const newSellOrders = askPrices.map((price, idx) => ({
        price,
        quantity: askVolumes[idx]
      })).reverse();

      
      const newBuyOrders = bidPrices.map((price, idx) => ({
        price,
        quantity: bidVolumes[idx]
      }));

      setSellOrders(newSellOrders);
      setBuyOrders(newBuyOrders);
      setTotalAskVolume(totalAskVolume);
      setTotalBidVolume(totalBidVolume);
    }
  };

  
  const handleTradeUpdate = (data: RealtimeTradeData) => {
    if (data.type === 'trade' && data.data) {
      const tradeData = data.data;

      const currentPriceNum = parseInt(tradeData.currentPrice);
      const priceChangeNum = parseInt(tradeData.priceChange);
      const changeRateStr = tradeData.changeRate;

      setCurrentPrice(currentPriceNum);
      setPriceChange(priceChangeNum);
      setChangePercent(changeRateStr);
      setIsPositive(priceChangeNum >= 0);

      
      const prevClose = currentPriceNum - priceChangeNum;
      setPreviousClosePrice(prevClose);

      
      setUpperLimit(Math.floor(prevClose * 1.3));
      setLowerLimit(Math.floor(prevClose * 0.7));
    }
  };

  
  const handleOrderSubmit = async () => {
    try {
      
      const orderRequest: StockOrderRequest = {
        pdno: stockCode,
        ordDvsn: priceType === '지정가' ? '00' : '01',  
        ordQty: String(orderQuantity),
        ordUnpr: String(orderPrice),
      };

      
      const response = activeTab === '매수'
        ? await orderApi.buyStock(orderRequest)
        : await orderApi.sellStock(orderRequest);

      
      if (response.success) {
        setShowOrderConfirmModal(false);
        setShowExecutionToast(true);
      } else {
        alert(`주문 실패: ${response.message}`);
      }
    } catch (error) {
      console.error('주문 실패:', error);
      alert('주문 처리 중 오류가 발생했습니다.');
    }
  };

  
  if (isLoading) {
    return <OrderPageSkeleton />;
  }

  return (
    <div className="order-page">
      {/* Header */}
      <PageHeader
        title="주문"
        leftAction={
          <button className="back-button" onClick={() => navigate(-1)}>
            <ArrowLeft size={24} />
          </button>
        }
      />

      {/* Stock Info */}
      <div className="stock-info-section">
        <div className="stock-codes">
          <span className="code-primary">{orderData.stockCode}</span>
          <span className="code-secondary">KOSPI200</span>
          <span className="code-secondary">KRX+NXT</span>
          <div className="market-time">
            <span>종 20</span>
            <span>신 45</span>
          </div>
        </div>
        <div className="stock-price-info">
          <div className="current-price">
            <span className="price-value">{formatPrice(currentPrice)}</span>
            <span className={`price-change ${isPositive ? 'positive' : 'negative'}`}>
              <span className="arrow">{isPositive ? '▲' : '▼'}</span>
              <span className="change-amount">{formatPrice(Math.abs(priceChange))}</span>
              <span className="change-percent">{isPositive ? '+' : '-'}{Math.abs(parseFloat(changePercent))}%</span>
            </span>
          </div>
          <div className="total-shares">
            {formatPrice(orderData.totalShares)}주
          </div>
        </div>
      </div>

      {/* Account Selector */}
      <div className="account-selector">
        <button className="account-dropdown">
          <span>{orderData.account.id} {orderData.account.name}, {orderData.account.type}</span>
          <ChevronDown size={20} />
        </button>
      </div>

      {/* Tabs */}
      <div className="order-tabs">
        {tabs.map((tab) => (
          <button
            key={tab}
            className={`order-tab ${activeTab === tab ? 'active' : ''}`}
            onClick={() => setActiveTab(tab)}
          >
            {tab}
          </button>
        ))}
      </div>

      {/* Order Content */}
      <div className="order-content">
        <div className="order-layout">
          {/* Left Side - Order Book */}
          <div className="order-book-section">
            <div className="order-type-selector">
              {orderTypes.map((type) => (
                <button
                  key={type}
                  className={`order-type-btn ${orderType === type ? 'active' : ''}`}
                  onClick={() => setOrderType(type)}
                >
                  {type}
                </button>
              ))}
            </div>

            <div className="order-book">
              {/* Upper Limit */}
              <div className="limit-row upper">
                <span className="limit-label">상한가</span>
                <span className="limit-arrow">↑</span>
                <span className="limit-price">{formatPrice(upperLimit)}</span>
              </div>

              {/* Sell Orders */}
              <div className="sell-orders">
                {sellOrders.map((order, index) => (
                  <div
                    key={index}
                    className={`order-row sell ${order.price === currentPrice ? 'current-price-match' : ''}`}
                  >
                    <div className="price-column">
                      <span className="order-price">{formatPrice(order.price)}</span>
                    </div>
                    <div className="quantity-column">
                      <span className="order-quantity">{formatQuantity(order.quantity)}</span>
                      <div className="quantity-bar sell-bar" style={{ width: `${calculateBarWidth(order.quantity)}%` }} />
                    </div>
                  </div>
                ))}
              </div>

              {/* Buy Orders */}
              <div className="buy-orders">
                {buyOrders.map((order, index) => (
                  <div
                    key={index}
                    className={`order-row buy ${order.price === currentPrice ? 'current-price-match' : ''}`}
                  >
                    <div className="price-column">
                      <span className="order-price">{formatPrice(order.price)}</span>
                    </div>
                    <div className="quantity-column">
                      <span className="order-quantity">{formatQuantity(order.quantity)}</span>
                      <div className="quantity-bar buy-bar" style={{ width: `${calculateBarWidth(order.quantity)}%` }} />
                    </div>
                  </div>
                ))}
              </div>

              {/* Lower Limit */}
              <div className="limit-row lower">
                <span className="limit-label">하한가</span>
                <span className="limit-arrow">↓</span>
                <span className="limit-price">{formatPrice(lowerLimit)}</span>
              </div>
            </div>

            {/* Balance Info */}
            <div className="balance-info">
              <div className="balance-row">
                <span className="balance-label">총잔량</span>
                <span className={`balance-value ${totalBidVolume > totalAskVolume ? 'buy-dominant' : 'sell-dominant'}`}>
                  {formatQuantity(Math.abs(totalBidVolume - totalAskVolume))}
                </span>
              </div>
            </div>
          </div>

          {/* Right Side - Order Form */}
          <div className="order-form-section">
            <div className="form-controls">
              <div className="form-header">
                <span
                  className={`form-title ${paymentType === '현금' ? 'active' : ''}`}
                  onClick={() => setPaymentType('현금')}
                >
                  현금
                </span>
                <span
                  className={`form-subtitle ${paymentType === '신용' ? 'active' : ''}`}
                  onClick={() => setPaymentType('신용')}
                >
                  신용
                </span>
              </div>

              {/* Market Type Selector */}
              <div className="market-selector">
                {marketTypes.map((type) => (
                  <button
                    key={type}
                    className={`market-btn ${marketType === type ? 'active' : ''}`}
                    onClick={() => setMarketType(type)}
                  >
                    {type}
                  </button>
                ))}
              </div>

              {/* Price Type Selector */}
              <div className="price-type-selector">
                {priceTypes.map((type) => (
                  <button
                    key={type}
                    className={`price-type-btn ${priceType === type ? 'active' : ''}`}
                    onClick={() => setPriceType(type)}
                  >
                    {type}
                  </button>
                ))}
              </div>

              {/* Price Input */}
              <div className="input-group">
                <div className="input-field">
                  <button className="adjust-btn-inner left" onClick={() => handlePriceChange(-100)}>
                    －
                  </button>
                  <div className="input-wrapper">
                    <input
                      type="number"
                      value={orderPrice}
                      onChange={(e) => setOrderPrice(Number(e.target.value))}
                      className="price-input"
                    />
                    <span className="input-unit">원</span>
                  </div>
                  <button className="adjust-btn-inner right" onClick={() => handlePriceChange(100)}>
                    ＋
                  </button>
                </div>
              </div>

              {/* Quantity Input */}
              <div className="input-group">
                <div className="input-field">
                  <button className="adjust-btn-inner left" onClick={() => handleQuantityChange(-1)}>
                    －
                  </button>
                  <div className="input-wrapper">
                    <input
                      type="number"
                      value={orderQuantity}
                      onChange={(e) => setOrderQuantity(Number(e.target.value))}
                      className="quantity-input"
                    />
                    <span className="input-unit">주</span>
                  </div>
                  <button className="adjust-btn-inner right" onClick={() => handleQuantityChange(1)}>
                    ＋
                  </button>
                </div>
              </div>

              {/* Order Options */}
              <div className="order-options">
                <button className="option-button" onClick={handleQuantityRefresh}>
                  <RefreshCw size={14} className="refresh-icon" />
                  <span>수량조회</span>
                </button>
              </div>

              {/* Available Balance */}
              <div className="balance-section">
                <div className="balance-item">
                  <span className="balance-label">
                    {paymentType === '신용' ? '신용최대가능' : '현금최대가능'}
                  </span>
                  <div className="balance-value-wrapper">
                    <div className="balance-value">
                      {activeTab === '매수'
                        ? (buyableData && buyableData.data
                            ? formatPrice(
                                paymentType === '신용'
                                  ? parseInt(buyableData.data.maxBuyQuantity)
                                  : parseInt(buyableData.data.noCreditBuyQuantity)
                              )
                            : '0')
                        : (sellableData && sellableData.data
                            ? formatPrice(parseInt(sellableData.data.orderableQuantity))
                            : '0')
                      }주
                      <ChevronDown size={16} style={{ transform: 'rotate(-90deg)' }} />
                    </div>
                    <div className="balance-amount">
                      ({activeTab === '매수'
                        ? (buyableData && buyableData.data
                            ? formatPrice(parseInt(buyableData.data.orderableCash))
                            : '0')
                        : (sellableData && sellableData.data
                            ? formatPrice(parseInt(sellableData.data.evaluationAmount))
                            : '0')
                      }원)
                    </div>
                  </div>
                </div>
              </div>

              {/* Order Amount */}
              <div className="order-amount">
                <span className="amount-label">주문금액</span>
                <span className="amount-value">
                  {orderQuantity > 0 ? `${formatPrice(calculateOrderAmount())}원` : '-'}
                </span>
              </div>

              {/* Action Buttons */}
              <div className="action-buttons">
                <button className="btn-preliminary">
                  {activeTab === '매도' ? '예약매도' : '예약매수'}
                </button>
                <button
                  className={`btn-order ${activeTab === '매도' ? 'sell' : ''}`}
                  onClick={() => setShowOrderConfirmModal(true)}
                >
                  {activeTab === '매도' ? '현금매도' : '현금매수'}
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <BottomNavigation />

      {/* Order Confirm Modal */}
      <OrderConfirmModal
        isOpen={showOrderConfirmModal}
        onClose={() => setShowOrderConfirmModal(false)}
        onConfirm={handleOrderSubmit}
        orderData={{
          stockName: stockName,
          stockCode: stockCode,
          accountNumber: orderData.account.id,
          orderType: activeTab === '매수' ? '현금매수' : '현금매도',
          market: marketType,
          orderCategory: priceType,
          quantity: orderQuantity || 1,
          price: orderPrice,
          totalAmount: orderPrice * (orderQuantity || 1)
        }}
      />

      {/* Order Execution Toast */}
      <OrderExecutionToast
        isVisible={showExecutionToast}
        onClose={() => setShowExecutionToast(false)}
        executionData={{
          orderType: activeTab === '매수' ? '매수체결' : '매도체결',
          market: marketType,
          stockName: stockName,
          accountNumber: orderData.account.id,
          orderedQuantity: orderQuantity || 1,
          executedQuantity: orderQuantity || 1,
          executionPrice: orderPrice
        }}
      />
    </div>
  );
};

export default OrderPage;
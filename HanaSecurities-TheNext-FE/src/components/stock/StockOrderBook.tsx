import React, { useState, useEffect, useCallback } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import './StockOrderBook.css';
import { quoteWebSocket } from '../../services/quoteWebSocket';
import { tradeWebSocket } from '../../services/tradeWebSocket';
import { foreignQuoteWebSocket } from '../../services/foreignQuoteWebSocket';
import StockOrderBookSkeleton from './StockOrderBookSkeleton';

import type {RealtimeQuoteData} from '../../services/quoteWebSocket';
import type {RealtimeTradeData} from '../../services/tradeWebSocket';
import type {ForeignQuoteData} from '../../types/foreignStock.types';

interface Transaction {
  price: number;
  quantity: number;
  timestamp: string;
}

interface OrderBookItem {
  price: number;
  quantity: number;
  changeRate: string;
}

interface StockOrderBookProps {
  stockName: string;
}

const StockOrderBook: React.FC<StockOrderBookProps> = ({ stockName }) => {
  const navigate = useNavigate();
  const { code, exchangeCode, stockCode } = useParams<{ code?: string; exchangeCode?: string; stockCode?: string }>();

  // 해외 주식 여부 판별
  const isForeignStock = !!exchangeCode;
  const actualStockCode = isForeignStock ? stockCode! : code!;

  const [selectedPrice, setSelectedPrice] = useState<number | null>(null);
  const [selectedChangeRate, setSelectedChangeRate] = useState<string | null>(null);
  const [popupPosition, setPopupPosition] = useState<{ top: number; height: number } | null>(null);

  const [sellOrders, setSellOrders] = useState<OrderBookItem[]>([
    { price: 91000, quantity: 312, changeRate: '1.11%' },
    { price: 90900, quantity: 445, changeRate: '1.00%' },
    { price: 90800, quantity: 223, changeRate: '0.89%' },
    { price: 90700, quantity: 167, changeRate: '0.78%' },
    { price: 90600, quantity: 892, changeRate: '0.67%' },
    { price: 90500, quantity: 498, changeRate: '0.56%' },
    { price: 90400, quantity: 275, changeRate: '0.44%' },
    { price: 90300, quantity: 263, changeRate: '0.33%' },
    { price: 90200, quantity: 725, changeRate: '0.22%' },
    { price: 90100, quantity: 195, changeRate: '0.11%' },
  ]);

  const [buyOrders, setBuyOrders] = useState<OrderBookItem[]>([
    { price: 90000, quantity: 1250, changeRate: '0.00%' },
    { price: 89900, quantity: 982, changeRate: '-0.11%' },
    { price: 89800, quantity: 4012, changeRate: '-0.22%' },
    { price: 89700, quantity: 4779, changeRate: '-0.33%' },
    { price: 89600, quantity: 1395, changeRate: '-0.44%' },
    { price: 89500, quantity: 1826, changeRate: '-0.56%' },
    { price: 89400, quantity: 2234, changeRate: '-0.67%' },
    { price: 89300, quantity: 1567, changeRate: '-0.78%' },
    { price: 89200, quantity: 989, changeRate: '-0.89%' },
    { price: 89100, quantity: 3421, changeRate: '-1.00%' },
  ]);

  const [totalAskVolume, setTotalAskVolume] = useState<number>(12557);
  const [totalBidVolume, setTotalBidVolume] = useState<number>(16931);
  const [currentTime, setCurrentTime] = useState<string>('');

  
  const [isLoading, setIsLoading] = useState<boolean>(false);

  
  const [previousClosePrice, setPreviousClosePrice] = useState<number>(0);
  const [currentPrice, setCurrentPrice] = useState<number>(0);
  const [priceChange, setPriceChange] = useState<string>('0');
  const [changeRate, setChangeRate] = useState<string>('0.00');
  const [tradeStrength, setTradeStrength] = useState<string>('100.00');
  const [openPrice, setOpenPrice] = useState<string>('0');
  const [highPrice, setHighPrice] = useState<string>('0');
  const [lowPrice, setLowPrice] = useState<string>('0');
  const [upperLimit, setUpperLimit] = useState<string>('0');
  const [lowerLimit, setLowerLimit] = useState<string>('0');

  
  const [transactions, setTransactions] = useState<Transaction[]>([]);

  
  useEffect(() => {
    const updateTime = () => {
      const now = new Date();
      const hours = String(now.getHours()).padStart(2, '0');
      const minutes = String(now.getMinutes()).padStart(2, '0');
      const seconds = String(now.getSeconds()).padStart(2, '0');
      setCurrentTime(`${hours}:${minutes}:${seconds}`);
    };

    updateTime(); 
    const timer = setInterval(updateTime, 1000);

    return () => clearInterval(timer);
  }, []);


  useEffect(() => {
    if (!actualStockCode) return;


    const connectAndSubscribe = async () => {
      try {
        if (isForeignStock) {
          // 해외 주식: quote 타입으로 호가 구독
          if (!foreignQuoteWebSocket.isConnected()) {
            await foreignQuoteWebSocket.connect();
          }
          foreignQuoteWebSocket.subscribe(exchangeCode!, actualStockCode, 'quote', handleForeignQuoteUpdate);
        } else {
          // 국내 주식: 호가 + 체결 WebSocket
          if (!quoteWebSocket.isConnected()) {
            await quoteWebSocket.connect();
          }
          quoteWebSocket.subscribe(actualStockCode, handleQuoteUpdate);

          if (!tradeWebSocket.isConnected()) {
            await tradeWebSocket.connect();
          }
          tradeWebSocket.subscribe(actualStockCode, handleTradeUpdate);
        }
      } catch (error) {
        // WebSocket 연결 실패는 조용히 무시
      }
    };

    connectAndSubscribe();


    return () => {
      if (isForeignStock) {
        foreignQuoteWebSocket.unsubscribe(exchangeCode!, actualStockCode, 'quote');
      } else {
        quoteWebSocket.unsubscribe(actualStockCode);
        tradeWebSocket.unsubscribe(actualStockCode);
      }
    };
  }, [actualStockCode, exchangeCode, isForeignStock]);

  
  const handleQuoteUpdate = useCallback((data: RealtimeQuoteData) => {
    if (data.type === 'quote' && data.data) {
      const { askPrices, bidPrices, askVolumes, bidVolumes, totalAskVolume: totalAsk, totalBidVolume: totalBid } = data.data;

      
      const newSellOrders: OrderBookItem[] = askPrices.map((price, idx) => ({
        price,
        quantity: askVolumes[idx],
        changeRate: '' 
      })).reverse();

      
      const newBuyOrders: OrderBookItem[] = bidPrices.map((price, idx) => ({
        price,
        quantity: bidVolumes[idx],
        changeRate: '' 
      }));

      setSellOrders(newSellOrders);
      setBuyOrders(newBuyOrders);
      setTotalAskVolume(totalAsk);
      setTotalBidVolume(totalBid);
    }
  }, []);


  const handleTradeUpdate = (data: RealtimeTradeData) => {
    if (data.type === 'trade' && data.data) {
      const tradeData = data.data;


      const currentPriceNum = parseInt(tradeData.currentPrice);
      const priceChangeNum = parseInt(tradeData.priceChange);

      setCurrentPrice(currentPriceNum);
      setPriceChange(tradeData.priceChange);
      setChangeRate(tradeData.changeRate);


      setTradeStrength(tradeData.tradeStrength);


      setOpenPrice(tradeData.openPrice);
      setHighPrice(tradeData.highPrice);
      setLowPrice(tradeData.lowPrice);


      const prevClose = currentPriceNum - priceChangeNum;
      setPreviousClosePrice(prevClose);


      setUpperLimit(String(Math.floor(prevClose * 1.3)));
      setLowerLimit(String(Math.floor(prevClose * 0.7)));


      const newTransaction: Transaction = {
        price: currentPriceNum,
        quantity: parseInt(tradeData.tradeVolume),
        timestamp: tradeData.tradeTime
      };

      setTransactions(prev => {
        const updated = [newTransaction, ...prev];
        return updated.slice(0, 15);
      });

      setIsLoading(false);
    }
  };

  // 해외 주식 호가 데이터 업데이트 핸들러
  const handleForeignQuoteUpdate = (data: ForeignQuoteData) => {
    // 해외 주식은 호가 데이터가 없으므로 현재가 정보만 업데이트
    if (data.last) {
      setCurrentPrice(parseFloat(data.last));
    }
  };

  
  const calculateChangeRate = (price: number, basePrice: number): string => {
    const rate = ((price - basePrice) / basePrice) * 100;
    return `${rate >= 0 ? '+' : ''}${rate.toFixed(2)}%`;
  };

  
  const getPriceColor = (price: number): string => {
    if (previousClosePrice === 0) return ''; 
    if (price > previousClosePrice) return 'stock-detail-orderbook-red';
    if (price < previousClosePrice) return 'stock-detail-orderbook-blue';
    return ''; 
  };

  
  const getTransactionPriceColor = (price: number): string => {
    if (previousClosePrice === 0) return 'stock-detail-orderbook-trans-neutral';
    if (price > previousClosePrice) return 'stock-detail-orderbook-trans-buy';
    if (price < previousClosePrice) return 'stock-detail-orderbook-trans-sell';
    return 'stock-detail-orderbook-trans-neutral';
  };

  
  const calculateOHLCChangeRate = (price: string): string => {
    const priceNum = parseInt(price);
    const rate = ((priceNum - previousClosePrice) / previousClosePrice) * 100;
    return `(${rate >= 0 ? '+' : ''}${rate.toFixed(2)}%)`;
  };

  const marketInfo = {
    previousVolume: '619,044',
    previousPrice: previousClosePrice.toLocaleString('ko-KR'),
    week52High: '97,300',
    week52Low: '51,500',
    ma5: '90,160',
    ma20: '84,770',
    upperLimit: parseInt(upperLimit).toLocaleString('ko-KR'),
    lowerLimit: parseInt(lowerLimit).toLocaleString('ko-KR'),
    openPrice: parseInt(openPrice).toLocaleString('ko-KR'),
    openChangeRate: calculateOHLCChangeRate(openPrice),
    highPrice: parseInt(highPrice).toLocaleString('ko-KR'),
    highChangeRate: calculateOHLCChangeRate(highPrice),
    lowPrice: parseInt(lowPrice).toLocaleString('ko-KR'),
    lowChangeRate: calculateOHLCChangeRate(lowPrice),
  };

  /**
   * 거래소별 소수점 자릿수 결정
   * - 도쿄(TSE/TKSE): 0자리 (엔화)
   * - 중국(SHS/SZS): 0자리 (위안화)
   * - 베트남(HSX/HNX): 0자리 (동화)
   * - 홍콩(HKS/SEHK): 2자리 (홍콩달러)
   * - 미국(NAS/NYS/AMS): 2자리 (달러)
   */
  const getDecimalPlaces = (exchangeCode?: string): number => {
    if (!exchangeCode) return 0;

    const code = exchangeCode.toUpperCase();
    // 소수점 없는 통화 (정수 표시)
    if (code.match(/^(TSE|TKSE|SHS|SZS|HSX|HNX)$/)) {
      return 0;
    }
    // 소수점 2자리 통화
    return 2;
  };

  const formatPrice = (price: number) => {
    if (isForeignStock) {
      const decimalPlaces = getDecimalPlaces(exchangeCode);
      return price.toLocaleString('en-US', {
        minimumFractionDigits: decimalPlaces,
        maximumFractionDigits: decimalPlaces
      });
    }
    return price.toLocaleString('ko-KR');
  };

  const formatQuantity = (quantity: number) => {
    return quantity.toLocaleString('ko-KR');
  };

  
  const calculateBarWidth = (quantity: number): number => {
    const allQuantities = [...sellOrders.map(o => o.quantity), ...buyOrders.map(o => o.quantity)];
    const maxQuantity = Math.max(...allQuantities);
    const baseValue = maxQuantity * 1.5;
    return Math.min((quantity / baseValue) * 100, 100);
  };

  const handlePriceClick = (price: number, changeRate: string, event: React.MouseEvent<HTMLDivElement>) => {
    const rect = event.currentTarget.getBoundingClientRect();
    setSelectedPrice(price);
    setSelectedChangeRate(changeRate);
    setPopupPosition({ top: rect.top, height: rect.height });
  };

  const handleClose = () => {
    setSelectedPrice(null);
    setSelectedChangeRate(null);
    setPopupPosition(null);
  };

  const handleBuyClick = () => {
    navigate('/order', { state: { type: 'buy', price: selectedPrice, stockCode: actualStockCode, stockName } });
    handleClose();
  };

  const handleSellClick = () => {
    navigate('/order', { state: { type: 'sell', price: selectedPrice, stockCode: actualStockCode, stockName } });
    handleClose();
  };

  
  if (isLoading) {
    return <StockOrderBookSkeleton />;
  }

  return (
    <div className="stock-detail-orderbook">
      {/* Header Row */}
      <div className="stock-detail-orderbook-header-row">
        <span className="stock-detail-orderbook-header-label">증감</span>
        <span className="stock-detail-orderbook-header-label">매도</span>
        <span className="stock-detail-orderbook-header-time">{currentTime}</span>
        <span className="stock-detail-orderbook-header-label">매수</span>
        <span className="stock-detail-orderbook-header-label">증감</span>
      </div>

      {/* Main Content Grid */}
      <div className="stock-detail-orderbook-main-grid">
        {/* Left Section */}
        <div className="stock-detail-orderbook-left-section">
          {/* Upper Left - Sell Orders */}
          <div className="stock-detail-orderbook-sell-orders-section">
            {sellOrders.map((order, index) => (
              <div key={index} className="stock-detail-orderbook-order-row-left">
                <div className="stock-detail-orderbook-quantity-cell-left">
                  <div
                    className="stock-detail-orderbook-quantity-bar-left stock-detail-orderbook-sell-bar"
                    style={{ width: `${calculateBarWidth(order.quantity)}%` }}
                  />
                  <span className="stock-detail-orderbook-quantity-value">{formatQuantity(order.quantity)}</span>
                </div>
              </div>
            ))}
          </div>

          {/* Lower Left - Transaction History */}
          <div className="stock-detail-orderbook-transaction-history">
            <div className="stock-detail-orderbook-transaction-header">
              <span className="stock-detail-orderbook-transaction-label">체결강도</span>
              <span className="stock-detail-orderbook-transaction-value">{tradeStrength}%</span>
            </div>
            <div className="stock-detail-orderbook-transaction-list">
              {transactions.length === 0 ? (
                <div className="stock-detail-orderbook-transaction-row" style={{ justifyContent: 'center', color: '#999' }}>
                  체결 대기 중...
                </div>
              ) : (
                transactions.map((trans, index) => (
                  <div key={`${trans.timestamp}-${index}`} className="stock-detail-orderbook-transaction-row">
                    <span className={`stock-detail-orderbook-trans-price ${getTransactionPriceColor(trans.price)}`}>
                      {formatPrice(trans.price)}
                    </span>
                    <span className={`stock-detail-orderbook-trans-quantity ${getTransactionPriceColor(trans.price)}`}>
                      {trans.quantity}
                    </span>
                  </div>
                ))
              )}
            </div>
          </div>
        </div>

        {/* Center Section - Prices */}
        <div className="stock-detail-orderbook-center-section">
          {/* Sell Prices */}
          {sellOrders.map((order, index) => {
            const changeRate = previousClosePrice > 0 ? calculateChangeRate(order.price, previousClosePrice) : '0.00%';
            return (
              <div
                key={index}
                className={`stock-detail-orderbook-price-row stock-detail-orderbook-sell ${
                  order.price === previousClosePrice ? 'stock-detail-orderbook-previous-close' : ''
                } ${order.price === currentPrice ? 'stock-detail-orderbook-current-price' : ''}`}
                onClick={(e) => handlePriceClick(order.price, changeRate, e)}
              >
                <span className={`stock-detail-orderbook-price-value ${getPriceColor(order.price)}`}>{formatPrice(order.price)}</span>
                <span className={`stock-detail-orderbook-change-rate ${getPriceColor(order.price)}`}>{changeRate}</span>
              </div>
            );
          })}

          {/* Buy Prices */}
          {buyOrders.map((order, index) => {
            const changeRate = previousClosePrice > 0 ? calculateChangeRate(order.price, previousClosePrice) : '0.00%';
            return (
              <div
                key={index}
                className={`stock-detail-orderbook-price-row stock-detail-orderbook-buy ${
                  order.price === previousClosePrice ? 'stock-detail-orderbook-previous-close' : ''
                } ${order.price === currentPrice ? 'stock-detail-orderbook-current-price' : ''}`}
                onClick={(e) => handlePriceClick(order.price, changeRate, e)}
              >
                <span className={`stock-detail-orderbook-price-value ${getPriceColor(order.price)}`}>{formatPrice(order.price)}</span>
                <span className={`stock-detail-orderbook-change-rate ${getPriceColor(order.price)}`}>{changeRate}</span>
              </div>
            );
          })}
        </div>

        {/* Right Section */}
        <div className="stock-detail-orderbook-right-section">
          {/* Upper Right - Market Info */}
          <div className="stock-detail-orderbook-market-info-section">
            <div className="stock-detail-orderbook-info-row">
              <span className="stock-detail-orderbook-info-label">전일거래량</span>
              <span className="stock-detail-orderbook-info-value">{marketInfo.previousVolume}</span>
            </div>
            <div className="stock-detail-orderbook-info-row">
              <span className="stock-detail-orderbook-info-label">전일가</span>
              <span className="stock-detail-orderbook-info-value">{marketInfo.previousPrice}</span>
            </div>
            <div className="stock-detail-orderbook-info-row">
              <span className="stock-detail-orderbook-info-label">52주최고</span>
              <span className="stock-detail-orderbook-info-value">{marketInfo.week52High}</span>
            </div>
            <div className="stock-detail-orderbook-info-row">
              <span className="stock-detail-orderbook-info-label">52주최저</span>
              <span className="stock-detail-orderbook-info-value">{marketInfo.week52Low}</span>
            </div>
            <div className="stock-detail-orderbook-info-row">
              <span className="stock-detail-orderbook-info-label">상한가</span>
              <span className="stock-detail-orderbook-info-value stock-detail-orderbook-red">{marketInfo.upperLimit}</span>
            </div>
            <div className="stock-detail-orderbook-info-row">
              <span className="stock-detail-orderbook-info-label">하한가</span>
              <span className="stock-detail-orderbook-info-value stock-detail-orderbook-blue">{marketInfo.lowerLimit}</span>
            </div>
            <div className="stock-detail-orderbook-info-row">
              <span className="stock-detail-orderbook-info-label">시</span>
              <div className="stock-detail-orderbook-info-value-wrapper">
                <span className="stock-detail-orderbook-info-value">{marketInfo.openPrice}</span>
                <span className="stock-detail-orderbook-info-extra stock-detail-orderbook-red">{marketInfo.openChangeRate}</span>
              </div>
            </div>
            <div className="stock-detail-orderbook-info-row">
              <span className="stock-detail-orderbook-info-label">고</span>
              <div className="stock-detail-orderbook-info-value-wrapper">
                <span className="stock-detail-orderbook-info-value">{marketInfo.highPrice}</span>
                <span className="stock-detail-orderbook-info-extra stock-detail-orderbook-red">{marketInfo.highChangeRate}</span>
              </div>
            </div>
            <div className="stock-detail-orderbook-info-row">
              <span className="stock-detail-orderbook-info-label">저</span>
              <div className="stock-detail-orderbook-info-value-wrapper">
                <span className="stock-detail-orderbook-info-value">{marketInfo.lowPrice}</span>
                <span className="stock-detail-orderbook-info-extra stock-detail-orderbook-blue">{marketInfo.lowChangeRate}</span>
              </div>
            </div>
            <div className="stock-detail-orderbook-depth-button-container">
              <button className="stock-detail-orderbook-depth-button">10호가</button>
            </div>
          </div>

          {/* Lower Right - Buy Orders */}
          <div className="stock-detail-orderbook-buy-orders-section">
            {buyOrders.map((order, index) => (
              <div key={index} className="stock-detail-orderbook-order-row-right">
                <div className="stock-detail-orderbook-quantity-cell-right">
                  <div
                    className="stock-detail-orderbook-quantity-bar-right stock-detail-orderbook-buy-bar"
                    style={{ width: `${calculateBarWidth(order.quantity)}%` }}
                  />
                  <span className="stock-detail-orderbook-quantity-value">{formatQuantity(order.quantity)}</span>
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>

      {/* Footer Row */}
      <div className="stock-detail-orderbook-footer-row">
        <span className="stock-detail-orderbook-footer-label">증감</span>
        <span className="stock-detail-orderbook-footer-label">매도</span>
        <span className="stock-detail-orderbook-footer-time">{currentTime}</span>
        <span className="stock-detail-orderbook-footer-label">매수</span>
        <span className="stock-detail-orderbook-footer-label">증감</span>
      </div>

      {/* Bottom Summary */}
      <div className="stock-detail-orderbook-bottom-summary">
        <div className="stock-detail-orderbook-summary-content">
          <div className="stock-detail-orderbook-summary-item stock-detail-orderbook-summary-sell">
            <span className="stock-detail-orderbook-summary-value stock-detail-orderbook-sell-total">{formatQuantity(totalAskVolume)}</span>
          </div>
          <div className="stock-detail-orderbook-summary-item stock-detail-orderbook-summary-center">
            <div className="stock-detail-orderbook-summary-center-content">
              <span className="stock-detail-orderbook-summary-label">총잔량</span>
              <span className={`stock-detail-orderbook-summary-value ${
                totalBidVolume > totalAskVolume ? 'stock-detail-orderbook-buy-total' : 'stock-detail-orderbook-sell-total'
              }`}>
                {formatQuantity(Math.abs(totalBidVolume - totalAskVolume))}
              </span>
            </div>
          </div>
          <div className="stock-detail-orderbook-summary-item stock-detail-orderbook-summary-buy">
            <span className="stock-detail-orderbook-summary-value stock-detail-orderbook-buy-total">{formatQuantity(totalBidVolume)}</span>
          </div>
        </div>
      </div>

      {/* Price Selection Overlay */}
      {selectedPrice && popupPosition && (
        <>
          <div className="stock-detail-orderbook-overlay" onClick={handleClose} />
          <div
            className="stock-detail-orderbook-price-popup"
            style={{
              top: `${popupPosition.top}px`,
              height: `${popupPosition.height}px`
            }}
          >
            <button className="stock-detail-orderbook-sell-button" onClick={handleSellClick}>매도</button>
            <div className="stock-detail-orderbook-selected-price">
              <span className="stock-detail-orderbook-selected-price-value">{formatPrice(selectedPrice)}</span>
              <span className="stock-detail-orderbook-selected-change-rate">{selectedChangeRate}</span>
            </div>
            <button className="stock-detail-orderbook-buy-button" onClick={handleBuyClick}>매수</button>
          </div>
        </>
      )}
    </div>
  );
};

export default StockOrderBook;
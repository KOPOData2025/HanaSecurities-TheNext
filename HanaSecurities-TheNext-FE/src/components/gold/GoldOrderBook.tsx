import React, { useState, useEffect, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import './GoldOrderBook.css';
import { goldQuoteWebSocket } from '../../services/goldApi';
import type { GoldQuoteData } from '../../services/goldApi';

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

interface GoldOrderBookProps {
  productCode: string;
}

const GoldOrderBook: React.FC<GoldOrderBookProps> = ({ productCode }) => {
  const navigate = useNavigate();

  const [selectedPrice, setSelectedPrice] = useState<number | null>(null);
  const [selectedChangeRate, setSelectedChangeRate] = useState<string | null>(null);
  const [popupPosition, setPopupPosition] = useState<{ top: number; height: number } | null>(null);

  // 금현물 더미 데이터 (원/g 기준)
  const [sellOrders, setSellOrders] = useState<OrderBookItem[]>([
    { price: 92100, quantity: 450, changeRate: '1.33%' },
    { price: 92000, quantity: 320, changeRate: '1.22%' },
    { price: 91900, quantity: 280, changeRate: '1.11%' },
    { price: 91800, quantity: 510, changeRate: '1.00%' },
    { price: 91700, quantity: 390, changeRate: '0.89%' },
    { price: 91600, quantity: 420, changeRate: '0.78%' },
    { price: 91500, quantity: 350, changeRate: '0.67%' },
    { price: 91400, quantity: 480, changeRate: '0.56%' },
    { price: 91300, quantity: 310, changeRate: '0.44%' },
    { price: 91200, quantity: 290, changeRate: '0.33%' },
  ]);

  const [buyOrders, setBuyOrders] = useState<OrderBookItem[]>([
    { price: 91100, quantity: 520, changeRate: '0.22%' },
    { price: 91000, quantity: 680, changeRate: '0.11%' },
    { price: 90900, quantity: 430, changeRate: '0.00%' },
    { price: 90800, quantity: 750, changeRate: '-0.11%' },
    { price: 90700, quantity: 590, changeRate: '-0.22%' },
    { price: 90600, quantity: 410, changeRate: '-0.33%' },
    { price: 90500, quantity: 640, changeRate: '-0.44%' },
    { price: 90400, quantity: 370, changeRate: '-0.56%' },
    { price: 90300, quantity: 490, changeRate: '-0.67%' },
    { price: 90200, quantity: 560, changeRate: '-0.78%' },
  ]);

  const [totalAskVolume, setTotalAskVolume] = useState<number>(3800);
  const [totalBidVolume, setTotalBidVolume] = useState<number>(5440);
  const [currentTime, setCurrentTime] = useState<string>('');

  const [isLoading, setIsLoading] = useState<boolean>(false);

  // 금현물 시세 정보
  const [previousClosePrice, setPreviousClosePrice] = useState<number>(90900);
  const [currentPrice, setCurrentPrice] = useState<number>(91000);
  const [priceChange, setPriceChange] = useState<string>('100');
  const [changeRate, setChangeRate] = useState<string>('0.11');
  const [tradeStrength, setTradeStrength] = useState<string>('125.50');
  const [openPrice, setOpenPrice] = useState<string>('90800');
  const [highPrice, setHighPrice] = useState<string>('92300');
  const [lowPrice, setLowPrice] = useState<string>('90500');
  const [upperLimit, setUpperLimit] = useState<string>('118170');
  const [lowerLimit, setLowerLimit] = useState<string>('63630');

  // 체결 내역 (더미 데이터)
  const [transactions, setTransactions] = useState<Transaction[]>([
    { price: 91050, quantity: 25, timestamp: '15:29:58' },
    { price: 91000, quantity: 15, timestamp: '15:29:52' },
    { price: 91100, quantity: 30, timestamp: '15:29:45' },
    { price: 91000, quantity: 20, timestamp: '15:29:38' },
    { price: 90950, quantity: 18, timestamp: '15:29:31' },
    { price: 91000, quantity: 22, timestamp: '15:29:24' },
    { price: 91050, quantity: 28, timestamp: '15:29:17' },
    { price: 91100, quantity: 16, timestamp: '15:29:10' },
    { price: 91000, quantity: 24, timestamp: '15:29:03' },
    { price: 90950, quantity: 19, timestamp: '15:28:56' },
    { price: 91000, quantity: 21, timestamp: '15:28:49' },
    { price: 91050, quantity: 26, timestamp: '15:28:42' },
    { price: 91100, quantity: 17, timestamp: '15:28:35' },
    { price: 91000, quantity: 23, timestamp: '15:28:28' },
    { price: 90950, quantity: 20, timestamp: '15:28:21' },
  ]);

  // 현재 시각 업데이트
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

  // WebSocket 연결 (실제 데이터 수신시 더미 데이터 대체)
  useEffect(() => {
    if (!productCode) return;

    goldQuoteWebSocket.subscribe((data: GoldQuoteData) => {
      if (data.productCode === productCode) {
        // 실제 호가 데이터로 업데이트
        const asks = [
          { price: data.askPrice10, quantity: data.askQuantity10 },
          { price: data.askPrice9, quantity: data.askQuantity9 },
          { price: data.askPrice8, quantity: data.askQuantity8 },
          { price: data.askPrice7, quantity: data.askQuantity7 },
          { price: data.askPrice6, quantity: data.askQuantity6 },
          { price: data.askPrice5, quantity: data.askQuantity5 },
          { price: data.askPrice4, quantity: data.askQuantity4 },
          { price: data.askPrice3, quantity: data.askQuantity3 },
          { price: data.askPrice2, quantity: data.askQuantity2 },
          { price: data.askPrice1, quantity: data.askQuantity1 },
        ];

        const bids = [
          { price: data.bidPrice1, quantity: data.bidQuantity1 },
          { price: data.bidPrice2, quantity: data.bidQuantity2 },
          { price: data.bidPrice3, quantity: data.bidQuantity3 },
          { price: data.bidPrice4, quantity: data.bidQuantity4 },
          { price: data.bidPrice5, quantity: data.bidQuantity5 },
          { price: data.bidPrice6, quantity: data.bidQuantity6 },
          { price: data.bidPrice7, quantity: data.bidQuantity7 },
          { price: data.bidPrice8, quantity: data.bidQuantity8 },
          { price: data.bidPrice9, quantity: data.bidQuantity9 },
          { price: data.bidPrice10, quantity: data.bidQuantity10 },
        ];

        const newSellOrders: OrderBookItem[] = asks
          .filter(ask => ask.price && ask.quantity)
          .map(ask => ({
            price: ask.price!,
            quantity: ask.quantity!,
            changeRate: calculateChangeRate(ask.price!, previousClosePrice)
          }))
          .reverse();

        const newBuyOrders: OrderBookItem[] = bids
          .filter(bid => bid.price && bid.quantity)
          .map(bid => ({
            price: bid.price!,
            quantity: bid.quantity!,
            changeRate: calculateChangeRate(bid.price!, previousClosePrice)
          }));

        if (newSellOrders.length > 0) setSellOrders(newSellOrders);
        if (newBuyOrders.length > 0) setBuyOrders(newBuyOrders);

        setIsLoading(false);
      }
    });

    return () => {
      goldQuoteWebSocket.unsubscribe();
    };
  }, [productCode, previousClosePrice]);

  // 등락률 계산
  const calculateChangeRate = (price: number, basePrice: number): string => {
    const rate = ((price - basePrice) / basePrice) * 100;
    return `${rate >= 0 ? '+' : ''}${rate.toFixed(2)}%`;
  };

  // 가격 색상
  const getPriceColor = (price: number): string => {
    if (previousClosePrice === 0) return '';
    if (price > previousClosePrice) return 'gold-detail-orderbook-red';
    if (price < previousClosePrice) return 'gold-detail-orderbook-blue';
    return '';
  };

  // 체결 가격 색상
  const getTransactionPriceColor = (price: number): string => {
    if (previousClosePrice === 0) return 'gold-detail-orderbook-trans-neutral';
    if (price > previousClosePrice) return 'gold-detail-orderbook-trans-buy';
    if (price < previousClosePrice) return 'gold-detail-orderbook-trans-sell';
    return 'gold-detail-orderbook-trans-neutral';
  };

  // OHLC 등락률 계산
  const calculateOHLCChangeRate = (price: string): string => {
    const priceNum = parseInt(price);
    const rate = ((priceNum - previousClosePrice) / previousClosePrice) * 100;
    return `(${rate >= 0 ? '+' : ''}${rate.toFixed(2)}%)`;
  };

  // 금현물 시장 정보
  const marketInfo = {
    previousVolume: '8,240', // g 단위
    previousPrice: previousClosePrice.toLocaleString('ko-KR'),
    week52High: '97,800',
    week52Low: '78,500',
    ma5: '91,200',
    ma20: '89,400',
    upperLimit: parseInt(upperLimit).toLocaleString('ko-KR'),
    lowerLimit: parseInt(lowerLimit).toLocaleString('ko-KR'),
    openPrice: parseInt(openPrice).toLocaleString('ko-KR'),
    openChangeRate: calculateOHLCChangeRate(openPrice),
    highPrice: parseInt(highPrice).toLocaleString('ko-KR'),
    highChangeRate: calculateOHLCChangeRate(highPrice),
    lowPrice: parseInt(lowPrice).toLocaleString('ko-KR'),
    lowChangeRate: calculateOHLCChangeRate(lowPrice),
  };

  const formatPrice = (price: number) => {
    return price.toLocaleString('ko-KR');
  };

  const formatQuantity = (quantity: number) => {
    return quantity.toLocaleString('ko-KR');
  };

  // 거래량 막대 그래프 너비 계산
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
    navigate('/gold-order', {
      state: {
        type: 'buy',
        price: selectedPrice,
        productCode,
        productName: productCode === 'M04020000' ? '금 99.99% 1Kg' : '미니금 99.99% 100g'
      }
    });
    handleClose();
  };

  const handleSellClick = () => {
    navigate('/gold-order', {
      state: {
        type: 'sell',
        price: selectedPrice,
        productCode,
        productName: productCode === 'M04020000' ? '금 99.99% 1Kg' : '미니금 99.99% 100g'
      }
    });
    handleClose();
  };

  return (
    <div className="gold-detail-orderbook">
      {/* Header Row */}
      <div className="gold-detail-orderbook-header-row">
        <span className="gold-detail-orderbook-header-label">증감</span>
        <span className="gold-detail-orderbook-header-label">매도</span>
        <span className="gold-detail-orderbook-header-time">{currentTime}</span>
        <span className="gold-detail-orderbook-header-label">매수</span>
        <span className="gold-detail-orderbook-header-label">증감</span>
      </div>

      {/* Main Content Grid */}
      <div className="gold-detail-orderbook-main-grid">
        {/* Left Section */}
        <div className="gold-detail-orderbook-left-section">
          {/* Upper Left - Sell Orders */}
          <div className="gold-detail-orderbook-sell-orders-section">
            {sellOrders.map((order, index) => (
              <div key={index} className="gold-detail-orderbook-order-row-left">
                <div className="gold-detail-orderbook-quantity-cell-left">
                  <div
                    className="gold-detail-orderbook-quantity-bar-left gold-detail-orderbook-sell-bar"
                    style={{ width: `${calculateBarWidth(order.quantity)}%` }}
                  />
                  <span className="gold-detail-orderbook-quantity-value">{formatQuantity(order.quantity)}g</span>
                </div>
              </div>
            ))}
          </div>

          {/* Lower Left - Transaction History */}
          <div className="gold-detail-orderbook-transaction-history">
            <div className="gold-detail-orderbook-transaction-header">
              <span className="gold-detail-orderbook-transaction-label">체결강도</span>
              <span className="gold-detail-orderbook-transaction-value">{tradeStrength}%</span>
            </div>
            <div className="gold-detail-orderbook-transaction-list">
              {transactions.map((trans, index) => (
                <div key={`${trans.timestamp}-${index}`} className="gold-detail-orderbook-transaction-row">
                  <span className={`gold-detail-orderbook-trans-price ${getTransactionPriceColor(trans.price)}`}>
                    {formatPrice(trans.price)}
                  </span>
                  <span className={`gold-detail-orderbook-trans-quantity ${getTransactionPriceColor(trans.price)}`}>
                    {trans.quantity}g
                  </span>
                </div>
              ))}
            </div>
          </div>
        </div>

        {/* Center Section - Prices */}
        <div className="gold-detail-orderbook-center-section">
          {/* Sell Prices */}
          {sellOrders.map((order, index) => {
            const changeRate = previousClosePrice > 0 ? calculateChangeRate(order.price, previousClosePrice) : '0.00%';
            return (
              <div
                key={index}
                className={`gold-detail-orderbook-price-row gold-detail-orderbook-sell ${
                  order.price === previousClosePrice ? 'gold-detail-orderbook-previous-close' : ''
                } ${order.price === currentPrice ? 'gold-detail-orderbook-current-price' : ''}`}
                onClick={(e) => handlePriceClick(order.price, changeRate, e)}
              >
                <span className={`gold-detail-orderbook-price-value ${getPriceColor(order.price)}`}>{formatPrice(order.price)}</span>
                <span className={`gold-detail-orderbook-change-rate ${getPriceColor(order.price)}`}>{changeRate}</span>
              </div>
            );
          })}

          {/* Buy Prices */}
          {buyOrders.map((order, index) => {
            const changeRate = previousClosePrice > 0 ? calculateChangeRate(order.price, previousClosePrice) : '0.00%';
            return (
              <div
                key={index}
                className={`gold-detail-orderbook-price-row gold-detail-orderbook-buy ${
                  order.price === previousClosePrice ? 'gold-detail-orderbook-previous-close' : ''
                } ${order.price === currentPrice ? 'gold-detail-orderbook-current-price' : ''}`}
                onClick={(e) => handlePriceClick(order.price, changeRate, e)}
              >
                <span className={`gold-detail-orderbook-price-value ${getPriceColor(order.price)}`}>{formatPrice(order.price)}</span>
                <span className={`gold-detail-orderbook-change-rate ${getPriceColor(order.price)}`}>{changeRate}</span>
              </div>
            );
          })}
        </div>

        {/* Right Section */}
        <div className="gold-detail-orderbook-right-section">
          {/* Upper Right - Market Info */}
          <div className="gold-detail-orderbook-market-info-section">
            <div className="gold-detail-orderbook-info-row">
              <span className="gold-detail-orderbook-info-label">전일거래량</span>
              <span className="gold-detail-orderbook-info-value">{marketInfo.previousVolume}g</span>
            </div>
            <div className="gold-detail-orderbook-info-row">
              <span className="gold-detail-orderbook-info-label">전일가</span>
              <span className="gold-detail-orderbook-info-value">{marketInfo.previousPrice}원</span>
            </div>
            <div className="gold-detail-orderbook-info-row">
              <span className="gold-detail-orderbook-info-label">52주최고</span>
              <span className="gold-detail-orderbook-info-value">{marketInfo.week52High}원</span>
            </div>
            <div className="gold-detail-orderbook-info-row">
              <span className="gold-detail-orderbook-info-label">52주최저</span>
              <span className="gold-detail-orderbook-info-value">{marketInfo.week52Low}원</span>
            </div>
            <div className="gold-detail-orderbook-info-row">
              <span className="gold-detail-orderbook-info-label">상한가</span>
              <span className="gold-detail-orderbook-info-value gold-detail-orderbook-red">{marketInfo.upperLimit}원</span>
            </div>
            <div className="gold-detail-orderbook-info-row">
              <span className="gold-detail-orderbook-info-label">하한가</span>
              <span className="gold-detail-orderbook-info-value gold-detail-orderbook-blue">{marketInfo.lowerLimit}원</span>
            </div>
            <div className="gold-detail-orderbook-info-row">
              <span className="gold-detail-orderbook-info-label">시</span>
              <div className="gold-detail-orderbook-info-value-wrapper">
                <span className="gold-detail-orderbook-info-value">{marketInfo.openPrice}원</span>
                <span className="gold-detail-orderbook-info-extra gold-detail-orderbook-red">{marketInfo.openChangeRate}</span>
              </div>
            </div>
            <div className="gold-detail-orderbook-info-row">
              <span className="gold-detail-orderbook-info-label">고</span>
              <div className="gold-detail-orderbook-info-value-wrapper">
                <span className="gold-detail-orderbook-info-value">{marketInfo.highPrice}원</span>
                <span className="gold-detail-orderbook-info-extra gold-detail-orderbook-red">{marketInfo.highChangeRate}</span>
              </div>
            </div>
            <div className="gold-detail-orderbook-info-row">
              <span className="gold-detail-orderbook-info-label">저</span>
              <div className="gold-detail-orderbook-info-value-wrapper">
                <span className="gold-detail-orderbook-info-value">{marketInfo.lowPrice}원</span>
                <span className="gold-detail-orderbook-info-extra gold-detail-orderbook-blue">{marketInfo.lowChangeRate}</span>
              </div>
            </div>
            <div className="gold-detail-orderbook-depth-button-container">
              <button className="gold-detail-orderbook-depth-button">10호가</button>
            </div>
          </div>

          {/* Lower Right - Buy Orders */}
          <div className="gold-detail-orderbook-buy-orders-section">
            {buyOrders.map((order, index) => (
              <div key={index} className="gold-detail-orderbook-order-row-right">
                <div className="gold-detail-orderbook-quantity-cell-right">
                  <div
                    className="gold-detail-orderbook-quantity-bar-right gold-detail-orderbook-buy-bar"
                    style={{ width: `${calculateBarWidth(order.quantity)}%` }}
                  />
                  <span className="gold-detail-orderbook-quantity-value">{formatQuantity(order.quantity)}g</span>
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>

      {/* Footer Row */}
      <div className="gold-detail-orderbook-footer-row">
        <span className="gold-detail-orderbook-footer-label">증감</span>
        <span className="gold-detail-orderbook-footer-label">매도</span>
        <span className="gold-detail-orderbook-footer-time">{currentTime}</span>
        <span className="gold-detail-orderbook-footer-label">매수</span>
        <span className="gold-detail-orderbook-footer-label">증감</span>
      </div>

      {/* Bottom Summary */}
      <div className="gold-detail-orderbook-bottom-summary">
        <div className="gold-detail-orderbook-summary-content">
          <div className="gold-detail-orderbook-summary-item gold-detail-orderbook-summary-sell">
            <span className="gold-detail-orderbook-summary-value gold-detail-orderbook-sell-total">{formatQuantity(totalAskVolume)}g</span>
          </div>
          <div className="gold-detail-orderbook-summary-item gold-detail-orderbook-summary-center">
            <div className="gold-detail-orderbook-summary-center-content">
              <span className="gold-detail-orderbook-summary-label">총잔량</span>
              <span className={`gold-detail-orderbook-summary-value ${
                totalBidVolume > totalAskVolume ? 'gold-detail-orderbook-buy-total' : 'gold-detail-orderbook-sell-total'
              }`}>
                {formatQuantity(Math.abs(totalBidVolume - totalAskVolume))}g
              </span>
            </div>
          </div>
          <div className="gold-detail-orderbook-summary-item gold-detail-orderbook-summary-buy">
            <span className="gold-detail-orderbook-summary-value gold-detail-orderbook-buy-total">{formatQuantity(totalBidVolume)}g</span>
          </div>
        </div>
      </div>

      {/* Price Selection Overlay */}
      {selectedPrice && popupPosition && (
        <>
          <div className="gold-detail-orderbook-overlay" onClick={handleClose} />
          <div
            className="gold-detail-orderbook-price-popup"
            style={{
              top: `${popupPosition.top}px`,
              height: `${popupPosition.height}px`
            }}
          >
            <button className="gold-detail-orderbook-sell-button" onClick={handleSellClick}>매도</button>
            <div className="gold-detail-orderbook-selected-price">
              <span className="gold-detail-orderbook-selected-price-value">{formatPrice(selectedPrice)}원</span>
              <span className="gold-detail-orderbook-selected-change-rate">{selectedChangeRate}</span>
            </div>
            <button className="gold-detail-orderbook-buy-button" onClick={handleBuyClick}>매수</button>
          </div>
        </>
      )}
    </div>
  );
};

export default GoldOrderBook;

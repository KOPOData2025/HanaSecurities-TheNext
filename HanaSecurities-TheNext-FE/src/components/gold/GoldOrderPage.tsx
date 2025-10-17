import React, { useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { GOLD_PRODUCTS, buyGoldOrder, sellGoldOrder } from '../../services/goldApi';
import type { GoldOrderRequest } from '../../services/goldApi';
import OrderConfirmModal from '../order/OrderConfirmModal';
import OrderExecutionToast from '../order/OrderExecutionToast';
import './GoldOrderPage.css';

type OrderType = 'buy' | 'sell';

const GoldOrderPage: React.FC = () => {
  const location = useLocation();
  const navigate = useNavigate();

  const initialPrice = location.state?.price || 0;
  const initialType = location.state?.orderType || 'buy';
  const productCode = location.state?.productCode || GOLD_PRODUCTS.GOLD_1KG;

  const [orderType, setOrderType] = useState<OrderType>(initialType);
  const [price, setPrice] = useState<number>(initialPrice);
  const [quantity, setQuantity] = useState<number>(1);
  const [showConfirmModal, setShowConfirmModal] = useState(false);
  const [showExecutionToast, setShowExecutionToast] = useState(false);
  const [executionData, setExecutionData] = useState<any>(null);
  const [isSubmitting, setIsSubmitting] = useState(false);

  const productName = productCode === GOLD_PRODUCTS.GOLD_1KG ? '금 99.99% 1Kg' : '미니금 99.99% 100g';
  const unitWeight = productCode === GOLD_PRODUCTS.GOLD_1KG ? 1000 : 100;

  const totalAmount = price * quantity;
  const totalWeight = unitWeight * quantity;

  // 계좌번호는 실제로는 로그인된 사용자 정보에서 가져와야 함
  const accountNumber = '12345-67890';

  const handleSubmit = () => {
    if (price <= 0 || quantity <= 0) {
      alert('가격과 수량을 입력해주세요');
      return;
    }

    // 주문 확인 모달 표시
    setShowConfirmModal(true);
  };

  const handleConfirmOrder = async () => {
    setShowConfirmModal(false);
    setIsSubmitting(true);

    try {
      const orderRequest: GoldOrderRequest = {
        accountNumber: accountNumber,
        productCode: productCode,
        quantity: quantity,
        price: price,
        orderType: '01', // 지정가
      };

      const response = orderType === 'buy'
        ? await buyGoldOrder(orderRequest)
        : await sellGoldOrder(orderRequest);

      if (response.success) {
        // 주문 성공 토스트 표시
        setExecutionData({
          orderType: orderType === 'buy' ? '매수' : '매도',
          market: '금현물',
          stockName: productName,
          accountNumber: accountNumber,
          orderedQuantity: response.orderedQuantity,
          executedQuantity: response.executedQuantity,
          executionPrice: response.executionPrice,
        });
        setShowExecutionToast(true);

        // 3초 후 이전 페이지로 이동
        setTimeout(() => {
          navigate(-1);
        }, 3000);
      } else {
        alert(response.message || '주문 처리 중 오류가 발생했습니다');
      }
    } catch (error) {
      console.error('주문 실패:', error);
      alert('주문 처리 중 오류가 발생했습니다');
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="gold-order-page">
      <div className="order-header">
        <button className="back-button" onClick={() => navigate(-1)}>←</button>
        <h2>금현물 주문</h2>
      </div>

      <div className="product-info-section">
        <div className="product-name">{productName}</div>
        <div className="product-code">{productCode}</div>
      </div>

      <div className="order-type-tabs">
        <button
          className={`order-type-btn ${orderType === 'buy' ? 'active buy' : ''}`}
          onClick={() => setOrderType('buy')}
        >
          매수
        </button>
        <button
          className={`order-type-btn ${orderType === 'sell' ? 'active sell' : ''}`}
          onClick={() => setOrderType('sell')}
        >
          매도
        </button>
      </div>

      <div className="order-form">
        <div className="form-group">
          <label>주문 가격 (원)</label>
          <input
            type="number"
            value={price}
            onChange={(e) => setPrice(Number(e.target.value))}
            placeholder="가격 입력"
          />
        </div>

        <div className="form-group">
          <label>주문 수량 (개)</label>
          <input
            type="number"
            value={quantity}
            onChange={(e) => setQuantity(Number(e.target.value))}
            min={1}
            placeholder="수량 입력"
          />
          <div className="quantity-info">
            {quantity}개 = {totalWeight.toLocaleString()}g
          </div>
        </div>

        <div className="order-summary">
          <div className="summary-row">
            <span>주문 금액</span>
            <span className="amount">{totalAmount.toLocaleString()}원</span>
          </div>
          <div className="summary-row">
            <span>수수료</span>
            <span className="fee">0원</span>
          </div>
          <div className="summary-row total">
            <span>총 결제 금액</span>
            <span className="total-amount">{totalAmount.toLocaleString()}원</span>
          </div>
        </div>
      </div>

      <div className="order-actions">
        <button
          className={`order-submit-btn ${orderType}`}
          onClick={handleSubmit}
          disabled={isSubmitting}
        >
          {isSubmitting ? '처리중...' : `${orderType === 'buy' ? '매수' : '매도'} 주문`}
        </button>
      </div>

      {/* 주문 확인 모달 */}
      <OrderConfirmModal
        isOpen={showConfirmModal}
        onClose={() => setShowConfirmModal(false)}
        onConfirm={handleConfirmOrder}
        orderData={{
          stockName: productName,
          stockCode: productCode,
          accountNumber: accountNumber,
          orderType: orderType === 'buy' ? '매수' : '매도',
          market: '금현물',
          orderCategory: '지정가',
          quantity: quantity,
          price: price,
          totalAmount: totalAmount,
        }}
      />

      {/* 주문 체결 토스트 */}
      {executionData && (
        <OrderExecutionToast
          isVisible={showExecutionToast}
          onClose={() => setShowExecutionToast(false)}
          executionData={executionData}
        />
      )}
    </div>
  );
};

export default GoldOrderPage;

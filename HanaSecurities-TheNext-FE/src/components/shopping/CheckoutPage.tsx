import React, { useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { ArrowLeft, ChevronRight, Check } from 'lucide-react';
import './CheckoutPage.css';

const CheckoutPage: React.FC = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const { product } = location.state || { product: { title: '토스쇼핑', price: '6,900', quantity: 1 } };

  const [selectedPayment, setSelectedPayment] = useState('card');
  const [usePoint, setUsePoint] = useState(false);

  const handlePayment = () => {
    navigate('/shopping/payment/complete', {
      state: {
        product: product,
        paymentMethod: selectedPayment === 'card' ? 'Npay 머니 하나 체크카드' : '하나페이 후불결제',
        totalAmount: '225,000'
      }
    });
  };

  const priceNum = parseInt(product.price.replace(/,/g, ''));
  const pointDiscount = usePoint ? 640 : 0;
  const finalPrice = priceNum;

  return (
    <div className="checkout-page">
      {/* Header */}
      <div className="checkout-header">
        <button className="checkout-back-btn" onClick={() => navigate(-1)}>
          <ArrowLeft size={24} />
        </button>
      </div>

      {/* Content */}
      <div className="checkout-content">
        {/* Price Display */}
        <div className="checkout-price-section">
          <div className="checkout-label">하나쇼핑</div>
          <div className="checkout-amount">225,000원</div>
        </div>

        {/* Points Card */}
        <div className="checkout-card">
          <div className="checkout-point-row">
            <span className="checkout-point-label">하나머니 포인트</span>
            <button
              className={`checkout-point-button ${usePoint ? 'active' : ''}`}
              onClick={() => setUsePoint(!usePoint)}
            >
              <span className="checkout-point-amount">0P</span>
              <span className="checkout-divider">|</span>
              <span className="checkout-point-action">모두사용</span>
            </button>
          </div>
        </div>

        {/* Payment Method Card */}
        <div className="checkout-card">
          <div className="checkout-payment-item" onClick={() => setSelectedPayment('toss')}>
            <div className="checkout-payment-left">
              <div className="checkout-payment-icon toss">
                <img src="/stockIcon/086790.png" alt="하나머니" />
              </div>
              <div className="checkout-payment-info">
                <div className="checkout-payment-name">하나페이 후불결제</div>
                <div className="checkout-payment-desc">보너스 포인트 최대 10% 적립</div>
              </div>
            </div>
            <div className={`checkout-radio ${selectedPayment === 'toss' ? 'selected' : ''}`}>
              <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
                <circle cx="12" cy="12" r="11" stroke="#dde2e8" strokeWidth="2" fill={selectedPayment === 'toss' ? '#dde2e8' : 'white'}/>
                {selectedPayment === 'toss' && <path d="M7 12L10 15L17 8" stroke="white" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>}
              </svg>
            </div>
          </div>

          <div className="checkout-payment-item" onClick={() => setSelectedPayment('card')}>
            <div className="checkout-payment-left">
              <div className="checkout-card-image">
                <img src="/card/npay_hana_check.png" alt="NPay 하나체크카드" />
              </div>
              <div className="checkout-card-name">Npay 머니 하나 체크카드</div>
            </div>
            <div className={`checkout-radio-check ${selectedPayment === 'card' ? 'selected' : ''}`}>
              {selectedPayment === 'card' && <Check size={16} color="white" />}
            </div>
          </div>

          <button className="checkout-installment">
            <span>일시불 결제</span>
            <ChevronRight size={18} color="#999" />
          </button>

          <div className="checkout-payment-divider"></div>

          <button className="checkout-settings">
            결제수단 변경 · 설정
          </button>
        </div>
      </div>

      {/* Bottom Button */}
      <div className="checkout-bottom">
        <button className="checkout-submit-btn" onClick={handlePayment}>
          <div className="checkout-hana-logo">
            <img src="/ci/hana.png" alt="Hana" />
          </div>
          <span>pay</span>
          <span className="checkout-pay-divider">|</span>
          <span>결제하기</span>
        </button>
        <div className="checkout-notice">
          개인(신용)정보 제공 동의서 필수 항목에 동의합니다
        </div>
      </div>
    </div>
  );
};

export default CheckoutPage;
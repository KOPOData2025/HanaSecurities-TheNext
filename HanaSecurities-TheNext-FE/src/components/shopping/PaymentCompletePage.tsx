import React, { useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { CheckCircle } from 'lucide-react';
import './PaymentCompletePage.css';

const PaymentCompletePage: React.FC = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const { product, paymentMethod, totalAmount } = location.state || {
    product: { title: '상품' },
    paymentMethod: '결제수단',
    totalAmount: '0'
  };

  useEffect(() => {
    const timer = setTimeout(() => {
      navigate('/shopping');
    }, 2500);

    return () => clearTimeout(timer);
  }, [navigate]);

  return (
    <div className="payment-complete-page">
      <div className="payment-complete-content">
        <div className="payment-complete-icon">
          <CheckCircle size={80} color="#00857D" />
        </div>

        <h1 className="payment-complete-title">결제 완료!</h1>
        <p className="payment-complete-message">
          {totalAmount}원 결제가
        </p>
        <p className="payment-complete-message">
          성공적으로 완료되었습니다.
        </p>
        <p className="payment-complete-sub">
          잠시 후 쇼핑 목록으로 이동합니다.
        </p>
      </div>
    </div>
  );
};

export default PaymentCompletePage;

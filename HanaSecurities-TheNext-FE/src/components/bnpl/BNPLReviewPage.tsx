import React, { useEffect, useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { X } from 'lucide-react';
import Lottie from 'lottie-react';
import './BNPLReviewPage.css';

const BNPLReviewPage: React.FC = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const { creditLimit, ram, paymentDay, paymentAccount } = location.state || {};
  const [animationData, setAnimationData] = useState(null);
  const [dots, setDots] = useState('');

  useEffect(() => {
    
    fetch('/etc/loading-spinner.json')
      .then(response => response.json())
      .then(data => setAnimationData(data));
  }, []);

  useEffect(() => {
    
    const interval = setInterval(() => {
      setDots(prevDots => {
        if (prevDots.length >= 3) return '.';
        return prevDots + '.';
      });
    }, 500);

    return () => clearInterval(interval);
  }, []);

  useEffect(() => {

    const timer = setTimeout(() => {
      navigate('/bnpl-complete', {
        state: {
          creditLimit,
          ram,
          paymentDay,
          paymentAccount
        }
      });
    }, 3000);

    return () => clearTimeout(timer);
  }, [navigate, creditLimit, ram, paymentDay, paymentAccount]);

  return (
    <div className="bnpl-review-page">
      <div className="bnpl-review-header">
        <div className="bnpl-review-header-left">
          <img src="/ci/hana.png" alt="Hana" className="bnpl-review-hana-logo" />
          <span className="bnpl-review-pay-text">Pay</span>
        </div>
        <h2 className="bnpl-review-title">후불결제 가입</h2>
        <button className="bnpl-review-close" onClick={() => navigate('/bnpl-application')}>
          <X size={24} />
        </button>
      </div>

      <div className="bnpl-review-progress">
        <div className="bnpl-progress-step completed">
          <span className="bnpl-step-number">1</span>
          <span className="bnpl-step-label">이용동의</span>
        </div>
        <div className="bnpl-progress-line active"></div>
        <div className="bnpl-progress-step completed">
          <span className="bnpl-step-number">2</span>
          <span className="bnpl-step-label">정보입력</span>
        </div>
        <div className="bnpl-progress-line active"></div>
        <div className="bnpl-progress-step active">
          <span className="bnpl-step-number">3</span>
          <span className="bnpl-step-label">가입심사</span>
        </div>
      </div>

      <div className="bnpl-review-content">
        <div className="bnpl-review-loading">
          {animationData && (
            <Lottie
              animationData={animationData}
              loop={true}
              className="bnpl-loading-spinner"
            />
          )}
          <h3 className="bnpl-review-message">
            가입 심사 중입니다<span className="bnpl-dots">{dots}</span>
          </h3>
          <p className="bnpl-review-submessage">
            잠시만 기다려 주세요.<br />
            신용 정보를 확인하고 있습니다.
          </p>
        </div>
      </div>
    </div>
  );
};

export default BNPLReviewPage;
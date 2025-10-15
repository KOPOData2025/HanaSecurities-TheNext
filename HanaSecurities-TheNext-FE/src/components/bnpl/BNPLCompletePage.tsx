import React from 'react';
import { useNavigate } from 'react-router-dom';
import { X, Check } from 'lucide-react';
import './BNPLCompletePage.css';

const BNPLCompletePage: React.FC = () => {
  const navigate = useNavigate();

  const handleConfirm = () => {
    navigate('/');
  };

  return (
    <div className="bnpl-complete-page">
      <div className="bnpl-complete-header">
        <div className="bnpl-complete-header-left">
          <img src="/ci/hana.png" alt="Hana" className="bnpl-complete-hana-logo" />
          <span className="bnpl-complete-pay-text">Pay</span>
        </div>
        <h2 className="bnpl-complete-title">후불결제 가입</h2>
        <button className="bnpl-complete-close" onClick={() => navigate('/')}>
          <X size={24} />
        </button>
      </div>

      <div className="bnpl-complete-content">
        <div className="bnpl-complete-success">
          <div className="bnpl-success-icon">
            <Check size={40} />
          </div>
          <h2 className="bnpl-success-title">
            하나페이 후불결제<br />
            이제 바로 이용하실 수 있어요
          </h2>
        </div>

        <div className="bnpl-complete-info-card">
          <div className="bnpl-info-row">
            <span className="bnpl-info-label"><span style={{fontWeight: 700}}>이상현</span>님의 이용한도</span>
            <span className="bnpl-info-value">300,000원</span>
          </div>
        </div>

        <div className="bnpl-complete-details">
          <div className="bnpl-detail-row">
            <span className="bnpl-detail-label">이용기간</span>
            <span className="bnpl-detail-value">전월 1일 ~ 전월 말일</span>
          </div>
          <div className="bnpl-detail-row">
            <span className="bnpl-detail-label">납부일</span>
            <span className="bnpl-detail-value">매월 15일</span>
          </div>
          <div className="bnpl-detail-row">
            <span className="bnpl-detail-label">납부계좌</span>
            <span className="bnpl-detail-value">하나증권 270-910234-56789</span>
          </div>
        </div>

        <div className="bnpl-complete-features">
          <div className="bnpl-feature-item">
            <Check size={20} className="bnpl-feature-check" />
            <p>이용한도는 내부 기준에 따라 정해지며, 후불결제를 꾸준히 이용하면 최대 30만원까지 오를 수 있어요.</p>
          </div>
          <div className="bnpl-feature-item">
            <Check size={20} className="bnpl-feature-check" />
            <p>하나쇼핑의 스마트스토어 등에서 결제할 때 후불결제를 선택하여 이용할 수 있어요.</p>
          </div>
          <div className="bnpl-feature-item">
            <Check size={20} className="bnpl-feature-check" />
            <p>금융위원회 정책에 따라 갖고 있는 포인트를 모두 사용해야 후불결제를 이용할 수 있어요.</p>
          </div>
          <div className="bnpl-feature-item">
            <Check size={20} className="bnpl-feature-check" />
            <p>이용한 금액은 납부일 10일전에 이메일 등으로 청구되며, 납부일에 납부계좌에서 자동으로 출금돼요.</p>
          </div>
          <div className="bnpl-feature-item">
            <Check size={20} className="bnpl-feature-check" />
            <p>연체 시, 후불결제 서비스를 이용할 수 없으며 연체 수수료가 부과돼요. (연 12%, 일 0.0328%)</p>
          </div>
        </div>
      </div>

      <div className="bnpl-complete-bottom">
        <button className="bnpl-complete-confirm" onClick={handleConfirm}>
          확인
        </button>
      </div>
    </div>
  );
};

export default BNPLCompletePage;
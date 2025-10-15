import React from 'react';
import { X } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import './BNPLApplicationPage.css';

const BNPLApplicationPage: React.FC = () => {
  const navigate = useNavigate();

  const handleApply = () => {
    navigate('/bnpl-terms');
  };

  return (
    <div className="bnpl-page">
      <div className="bnpl-header">
        <div className="bnpl-header-left">
          <img src="/ci/hana.png" alt="Hana" className="bnpl-hana-logo" />
          <span className="bnpl-pay-text">Pay</span>
        </div>
        <button className="bnpl-close-btn" onClick={() => navigate(-1)}>
          <X size={24} />
        </button>
      </div>

      <div className="bnpl-content">
        <div className="bnpl-title-section">
          <h1 className="bnpl-main-title">Buy Now, Pay Later !</h1>
          <div className="bnpl-subtitle">
            <h2>하나페이 후불결제</h2>
            <span className="bnpl-beta-badge">Beta</span>
          </div>

          <div className="bnpl-benefits">
            <div className="bnpl-benefit-item">
              <span className="bnpl-check">✓</span>
              <span>지금 당장 <span className="bnpl-highlight">돈이 부족</span>해도</span>
            </div>
            <div className="bnpl-benefit-item">
              <span className="bnpl-check">✓</span>
              <span><span className="bnpl-highlight">최대 30만원</span>까지 쇼핑 가능!</span>
            </div>
            <div className="bnpl-benefit-item">
              <span className="bnpl-check">✓</span>
              <span>결제금액은 <span className="bnpl-highlight">다음에</span> 내세요.</span>
            </div>
          </div>
        </div>

        <div className="bnpl-section">
          <h3 className="bnpl-section-title">신용점수에 영향 없으니<br/>안심하고 가입하세요!</h3>
          <p className="bnpl-section-desc">
            후불결제 가입 신청을 해도<br/>
            신용점수에는 영향을 주지 않아요!
          </p>
          <div className="bnpl-phone-image">
            <span className="tossface u-phone" style={{ fontSize: '120px' }}></span>
            <span className="bnpl-check-mark">✓</span>
          </div>
        </div>

        <div className="bnpl-section">
          <h3 className="bnpl-section-title">주문할 때 바로 사용하세요!</h3>
          <p className="bnpl-section-desc">
            하나쇼핑의 스마트스토어 등에서 주문 시<br/>
            후불결제를 선택해서 결제하세요.
          </p>

          <div className="bnpl-phone-mockup">
            <div className="bnpl-phone-frame">
              <div className="bnpl-phone-header">
                <div className="bnpl-phone-logo">
                  <img src="/ci/hana.png" alt="Hana" className="bnpl-hana-small-logo" />
                  <span className="bnpl-pay-text-small">Pay</span>
                </div>
                <span className="bnpl-phone-title">결제하기</span>
              </div>
              <div className="bnpl-phone-body">
                <div className="bnpl-cart-items">
                  <span className="tossface u-cart"></span>
                  <div className="bnpl-cart-lines">
                    <div className="bnpl-line-gray"></div>
                    <div className="bnpl-line-gray short"></div>
                  </div>
                </div>
                <div className="bnpl-payment-option">
                  <div className="bnpl-payment-selected">
                    <span className="bnpl-check-circle">✓</span>
                    <span className="bnpl-payment-label">후불결제</span>
                    <span className="bnpl-payment-available">이용가능: 300,000원</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div className="bnpl-section">
          <h3 className="bnpl-section-title">결제하신 금액은<br/>납부일에 자동으로 출금돼요.</h3>
          <p className="bnpl-section-desc">
            결제 금액은 가입 시 선택한 남부일에 자동 출금돼요.<br/>
            출금 전에 미리 알려드리니 연체 걱정도 없어요!
          </p>

          <div className="bnpl-payment-info">
            <div className="bnpl-payment-card">
              <div className="bnpl-payment-header">
                <span className="tossface u-bell"></span>
                <span className="bnpl-payment-title">후불결제 납부예정 안내</span>
              </div>
              <div className="bnpl-payment-details">
                <div className="bnpl-payment-account">○○은행 110********</div>
                <div className="bnpl-payment-amount">150,000원 납부예정</div>
              </div>
            </div>
            <p className="bnpl-payment-note">* 납부일은 가입 시 <span className="bnpl-note-bold">5일</span>, <span className="bnpl-note-bold">15일</span>, <span className="bnpl-note-bold">25일</span> 중 선택가능</p>
          </div>
        </div>

        <div className="bnpl-section bnpl-reasons-section">
          <h3 className="bnpl-section-title">하나페이 후불결제<br/>더 좋은 이유</h3>

          <div className="bnpl-reasons">
            <div className="bnpl-reason-card">
              <span className="bnpl-reason-check">✓</span>
              <div className="bnpl-reason-content">
                <h4>수수료 걱정없이 무료로 누려요!</h4>
                <p>이자, 연회비 등 이용수수료도 없어요.</p>
              </div>
            </div>
            <div className="bnpl-reason-card">
              <span className="bnpl-reason-check">✓</span>
              <div className="bnpl-reason-content">
                <h4>신용점수 낮아도 후불결제 가능해요!</h4>
                <p>하나페이 이용이 곧 나의 신용이에요.</p>
              </div>
            </div>
            <div className="bnpl-reason-card">
              <span className="bnpl-reason-check">✓</span>
              <div className="bnpl-reason-content">
                <h4>연말 소득공제도 돼요!</h4>
                <p>현금영수증 발급되니 소득공제 받으세요.</p>
              </div>
            </div>
          </div>
        </div>

        <div className="bnpl-footer">
          <div className="bnpl-footer-content">
            <img src="/ci/fin.png" alt="금융위원회" className="bnpl-fin-logo" />
            <div className="bnpl-innovation-badge">
              <span className="bnpl-innovation-text">혁신금융서비스</span>
              <span className="bnpl-innovation-icon">?</span>
            </div>
          </div>
        </div>
      </div>

      <div className="bnpl-bottom">
        <button className="bnpl-apply-btn" onClick={handleApply}>
          30초만에 신청하고 바로 사용
        </button>
      </div>
    </div>
  );
};

export default BNPLApplicationPage;
import React from 'react';
import { X } from 'lucide-react';
import './BNPLInfoModal.css';

interface BNPLInfoModalProps {
  isOpen: boolean;
  onClose: () => void;
}

const BNPLInfoModal: React.FC<BNPLInfoModalProps> = ({ isOpen, onClose }) => {
  if (!isOpen) return null;

  return (
    <>
      <div className="bnpl-modal-overlay" onClick={onClose} />
      <div className="bnpl-modal-container">
        <div className="bnpl-modal-content">
          <div className="bnpl-modal-header">
            <h2 className="bnpl-modal-title">
              이제 <span className="bnpl-highlight">후불결제</span>를 사용할 수 있어요!
            </h2>
            <p className="bnpl-modal-subtitle">2가지만 기억하세요.</p>
          </div>

          <div className="bnpl-modal-sections">
            <div className="bnpl-modal-section section-one">
              <span className="bnpl-section-label">하나!</span>
              <img src="/ci/hana-money.png" alt="Hana Money" className="bnpl-section-icon" />
              <h3 className="bnpl-section-title">
                보유포인트를<br />
                모두 사용해야<br />
                후불결제 이용이 가능해요!
              </h3>
              <p className="bnpl-section-desc">
                후불결제는 금융위원회 정책에 따라 주문 시 보유포인트를 모두 사용해야 이용할 수 있어요.
                보유포인트가 주문금액보다 많으면 후불결제 이용이 어려워요.
              </p>
            </div>

            <div className="bnpl-modal-section section-two">
              <span className="bnpl-section-label">둘!</span>
              <span className="bnpl-section-icon tossface">📅</span>
              <h3 className="bnpl-section-title">
                이용한 금액은<br />
                선택하신 납부일에<br />
                자동으로 출금 돼요.
              </h3>
              <p className="bnpl-section-desc">
                오늘 결제금액은 0원! 납부일 전에 계좌 잔액을 확인해주세요.
                이용현황 및 청구서는 하나페이 홈, 내자산에서 '후불결제'를 눌러 언제든지 확인할 수 있어요.
              </p>
            </div>
          </div>

          <button className="bnpl-modal-confirm" onClick={onClose}>
            확인
          </button>
        </div>
      </div>
    </>
  );
};

export default BNPLInfoModal;
import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { X, ChevronDown } from 'lucide-react';
import { bnplApi } from '../../services/bnplService';
import './BNPLInfoPage.css';

const BNPLInfoPage: React.FC = () => {
  const navigate = useNavigate();
  const [email] = useState('dltkdgus482@hanati.com');
  const [selectedDate, setSelectedDate] = useState('');
  const [selectedAccount, setSelectedAccount] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);

  const accounts = [
    { id: 'hana-securities', name: '하나증권', number: '270-910234-56789' },
    { id: 'hana-bank', name: '하나은행', number: '142-123456-78901' }
  ];
  const [showDateModal, setShowDateModal] = useState(false);

  const convertDateToNumber = (dateText: string): number => {
    if (dateText.includes('5일')) return 5;
    if (dateText.includes('15일')) return 15;
    if (dateText.includes('25일')) return 25;
    return 5; 
  };

  const getSelectedAccountNumber = (): string => {
    const account = accounts.find(acc => acc.id === selectedAccount);
    return account ? account.number : '';
  };

  const handleNext = async () => {
    if (!selectedDate || !selectedAccount) {
      alert('모든 정보를 입력해 주세요.');
      return;
    }

    setIsSubmitting(true);
    try {
      
      const userId = 'test_user';
      const paymentDay = convertDateToNumber(selectedDate);
      const paymentAccount = getSelectedAccountNumber();

      const response = await bnplApi.applyBnpl({
        userId,
        paymentDay,
        paymentAccount
      });

      if (response.success) {
        navigate('/bnpl-review');
      } else {
        alert(response.message || '후불결제 신청에 실패했습니다.');
      }
    } catch (error) {
      console.error('후불결제 신청 오류:', error);
      alert('후불결제 신청 중 오류가 발생했습니다.');
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleDateSelect = (date: string) => {
    setSelectedDate(date);
    setShowDateModal(false);
  };


  return (
    <>
      <div className="bnpl-info-page">
        <div className="bnpl-info-header">
          <div className="bnpl-info-header-left">
            <img src="/ci/hana.png" alt="Hana" className="bnpl-info-hana-logo" />
            <span className="bnpl-info-pay-text">Pay</span>
          </div>
          <h2 className="bnpl-info-title">후불결제 가입</h2>
          <button className="bnpl-info-close" onClick={() => navigate('/bnpl-application')}>
            <X size={24} />
          </button>
        </div>

        <div className="bnpl-info-progress">
          <div className="bnpl-progress-step completed">
            <span className="bnpl-step-number">1</span>
            <span className="bnpl-step-label">이용동의</span>
          </div>
          <div className="bnpl-progress-line active"></div>
          <div className="bnpl-progress-step active">
            <span className="bnpl-step-number">2</span>
            <span className="bnpl-step-label">정보입력</span>
          </div>
          <div className="bnpl-progress-line"></div>
          <div className="bnpl-progress-step">
            <span className="bnpl-step-number">3</span>
            <span className="bnpl-step-label">가입심사</span>
          </div>
        </div>

        <div className="bnpl-info-content">
          <div className="bnpl-info-section">
            <h3 className="bnpl-section-title">청구서 받을 이메일</h3>
            <div className="bnpl-email-field">
              <span className="bnpl-email-text">{email}</span>
              <button className="bnpl-email-change">이메일 변경</button>
            </div>
          </div>

          <div className="bnpl-info-section">
            <h3 className="bnpl-section-title">매월 납부할 날짜</h3>
            <button className="bnpl-date-selector" onClick={() => setShowDateModal(true)}>
              <span className={`bnpl-date-text ${selectedDate ? 'selected' : ''}`}>
                {selectedDate || '납부일을 선택해주세요.'}
              </span>
              <ChevronDown size={20} />
            </button>
          </div>

          <div className="bnpl-info-section">
            <h3 className="bnpl-section-title">납부계좌</h3>
            <div className="bnpl-account-list">
              {accounts.map(account => (
                <button
                  key={account.id}
                  className={`bnpl-account-item ${selectedAccount === account.id ? 'selected' : ''}`}
                  onClick={() => setSelectedAccount(account.id)}
                >
                  <div className="bnpl-account-left">
                    <img src="/ci/hana.png" alt="Hana" className="bnpl-account-logo" />
                    <div className="bnpl-account-info">
                      <span className="bnpl-account-name">{account.name}</span>
                      <span className="bnpl-account-number">{account.number}</span>
                    </div>
                  </div>
                  <div className={`bnpl-account-radio ${selectedAccount === account.id ? 'checked' : ''}`}>
                    {selectedAccount === account.id && <span className="bnpl-radio-dot"></span>}
                  </div>
                </button>
              ))}
            </div>
          </div>

          <div className="bnpl-info-notice">
            <ul>
              <li>납부일은 가입 후 변경할 수 없습니다.</li>
              <li>납부일에 납부계좌에서 청구금액이 자동 출금됩니다.</li>
              <li>납부일까지 청구금액이 납부되지 않으면 연체 상태가 됩니다.</li>
              <li>연체상태에서 납부계좌 자금이 부족한 경우 하나페이 결제/송금 계좌로 등록된 다른 계좌에서 자동 출금 될 수 있습니다.</li>
              <li>새로 등록한 납부계좌는 하나페이 결제/송금 계좌로도 사용됩니다.</li>
            </ul>
          </div>
        </div>

        <div className="bnpl-info-bottom">
          <button
            className={`bnpl-info-next ${selectedDate && selectedAccount ? 'active' : ''}`}
            onClick={handleNext}
            disabled={!selectedDate || !selectedAccount || isSubmitting}
          >
            {isSubmitting ? '신청 중...' : '다음'}
          </button>
        </div>
      </div>

      {showDateModal && (
        <div className="bnpl-date-modal-overlay" onClick={() => setShowDateModal(false)}>
          <div className="bnpl-date-modal" onClick={(e) => e.stopPropagation()}>
            <div className="bnpl-date-modal-header">
              <h3>언제 납부할까요?</h3>
              <button className="bnpl-date-modal-close" onClick={() => setShowDateModal(false)}>
                <X size={24} />
              </button>
            </div>
            <div className="bnpl-date-modal-subtitle">
              <p className="bnpl-date-modal-notice">
                <span className="bnpl-notice-icon">!</span>
                납부일은 가입 후 변경할 수 없어요.
              </p>
            </div>
            <div className="bnpl-date-modal-content">
              <div className="bnpl-date-options">
                <button
                  className="bnpl-date-option"
                  onClick={() => handleDateSelect('매월 5일')}
                >
                  <div className="bnpl-date-option-title">매월 5일</div>
                  <div className="bnpl-date-option-desc">이용기간 : 전전월 21일 ~ 전월 20일</div>
                </button>
                <button
                  className="bnpl-date-option"
                  onClick={() => handleDateSelect('매월 15일')}
                >
                  <div className="bnpl-date-option-title">매월 15일</div>
                  <div className="bnpl-date-option-desc">이용기간 : 전월 1일 ~ 전월 말일</div>
                </button>
                <button
                  className="bnpl-date-option"
                  onClick={() => handleDateSelect('매월 25일')}
                >
                  <div className="bnpl-date-option-title">매월 25일</div>
                  <div className="bnpl-date-option-desc">이용기간 : 전월 11일 ~ 당월 10일</div>
                </button>
              </div>
            </div>
          </div>
        </div>
      )}
    </>
  );
};

export default BNPLInfoPage;
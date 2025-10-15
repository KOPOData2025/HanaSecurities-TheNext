import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { ArrowLeft, Menu, ChevronRight, ChevronDown, ChevronUp, Copy } from 'lucide-react';
import BottomNavigation from '../navigation/BottomNavigation';
import BNPLInfoModal from './BNPLInfoModal';
import { bnplApi } from '../../services/bnplService';
import type { UsageItem, BnplInfoData } from '../../types/bnpl.types';
import './BNPLUsagePage.css';

const BNPLUsagePage: React.FC = () => {
  const navigate = useNavigate();
  const [expandedFaq, setExpandedFaq] = useState<number | null>(null);
  const [showInfoModal, setShowInfoModal] = useState(true);
  const [usageItems, setUsageItems] = useState<UsageItem[]>([]);
  const [bnplInfo, setBnplInfo] = useState<BnplInfoData | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    
    setShowInfoModal(true);
    
    fetchBnplData();
  }, []);

  const fetchBnplData = async () => {
    setIsLoading(true);
    try {
      
      const userId = 'test_user';

      
      const [historyResponse, infoResponse] = await Promise.all([
        bnplApi.getUsageHistory(userId),
        bnplApi.getBnplInfo(userId)
      ]);

      if (historyResponse.success) {
        setUsageItems(historyResponse.usageHistory);
      }

      if (infoResponse.success && infoResponse.data) {
        setBnplInfo(infoResponse.data);
      }
    } catch (error) {
      console.error('후불결제 데이터 조회 실패:', error);
    } finally {
      setIsLoading(false);
    }
  };

  const toggleFaq = (index: number) => {
    setExpandedFaq(expandedFaq === index ? null : index);
  };

  return (
    <div className="bnpl-usage-page">
      <div className="bnpl-usage-header">
        <button className="bnpl-back-btn" onClick={() => navigate(-1)}>
          <ArrowLeft size={24} />
        </button>
        <h2 className="bnpl-usage-title">후불결제</h2>
        <button className="bnpl-menu-btn">
          <Menu size={24} />
        </button>
      </div>

      <div className="bnpl-usage-content">
        {isLoading ? (
          <div style={{ padding: '40px', textAlign: 'center', color: '#999' }}>
            데이터를 불러오는 중...
          </div>
        ) : (
          <>
            <div className="bnpl-payment-summary">
              <div className="bnpl-payment-info">
                <h3 className="bnpl-payment-date">
                  {bnplInfo ? `${new Date().getMonth() + 1}월 ${bnplInfo.paymentDay}일 납부할 금액` : '납부할 금액'}
                </h3>
                <p className="bnpl-payment-period">
                  {bnplInfo?.applicationDate ? `${bnplInfo.applicationDate.substring(5).replace('-', '. ')} 이용금액 기준` : '이용금액 기준'}
                </p>
              </div>
              <div className="bnpl-payment-amount">
                <span className="bnpl-amount">
                  {bnplInfo ? `${bnplInfo.usageAmount.toLocaleString()}원` : '0원'}
                </span>
              </div>
              <button className="bnpl-pay-now-btn">즉시 납부</button>
            </div>

            <div className="bnpl-payment-details">
              <div className="bnpl-payment-method">
                <span className="bnpl-method-icon tossface">📄</span>
                <span className="bnpl-method-label">납부정보</span>
                <span className="bnpl-method-info">변경</span>
              </div>
              <div className="bnpl-method-detail">
                <span>{bnplInfo?.paymentAccount || '납부계좌 정보 없음'}</span>
                <span className="bnpl-method-date">
                  매월 {bnplInfo?.paymentDay || ''}일 납부
                </span>
              </div>
            </div>

            <div className="bnpl-usage-limit">
              <h4 className="bnpl-section-title">이용금액</h4>
              <div className="bnpl-limit-bar">
                <div
                  className="bnpl-limit-progress"
                  style={{
                    width: bnplInfo
                      ? `${(bnplInfo.usageAmount / bnplInfo.creditLimit * 100).toFixed(1)}%`
                      : '0%'
                  }}
                ></div>
              </div>
              <div className="bnpl-limit-info">
                <span className="bnpl-limit-text">
                  이용한도 {bnplInfo ? bnplInfo.creditLimit.toLocaleString() : '0'}원
                </span>
              </div>
              <div className="bnpl-limit-details">
                <div className="bnpl-limit-row">
                  <div className="bnpl-limit-item">
                    <span className="bnpl-limit-dot used"></span>
                    <span>이용한 금액</span>
                  </div>
                  <span className="bnpl-limit-amount used">
                    {bnplInfo ? bnplInfo.usageAmount.toLocaleString() : '0'}원
                  </span>
                </div>
                <div className="bnpl-limit-row">
                  <div className="bnpl-limit-item">
                    <span className="bnpl-limit-dot available"></span>
                    <span>이용 가능한 금액</span>
                  </div>
                  <span className="bnpl-limit-amount">
                    {bnplInfo ? (bnplInfo.creditLimit - bnplInfo.usageAmount).toLocaleString() : '0'}원
                  </span>
                </div>
              </div>
            </div>
          </>
        )}

        {!isLoading && (
          <div className="bnpl-usage-history">
            <div className="bnpl-history-header">
              <h4 className="bnpl-section-title">최근 이용 내역</h4>
              <button className="bnpl-history-more">
                전체보기 <ChevronRight size={16} />
              </button>
            </div>
            <div className="bnpl-history-list">
              {usageItems.length > 0 ? (
                usageItems.map((item, index) => (
                  <div key={index} className="bnpl-history-item">
                    <div className="bnpl-history-left">
                      <span className="bnpl-history-date">{item.usageDate}.</span>
                      <span className="bnpl-history-store">
                        {item.merchantName}
                        <ChevronRight size={14} className="bnpl-history-arrow" />
                      </span>
                    </div>
                    <span className="bnpl-history-amount">{item.amount.toLocaleString()}원</span>
                  </div>
                ))
              ) : (
                <div style={{ padding: '40px', textAlign: 'center', color: '#999' }}>
                  이용내역이 없습니다
                </div>
              )}
            </div>
          </div>
        )}

        <div className="bnpl-faq-section">
          <div className="bnpl-faq-header">
            <h4 className="bnpl-section-title">자주묻는질문</h4>
            <button className="bnpl-faq-more">
              전체보기 <ChevronRight size={16} />
            </button>
          </div>

          <div className="bnpl-faq-list">
            <div className="bnpl-faq-item">
              <button
                className="bnpl-faq-question"
                onClick={() => toggleFaq(0)}
              >
                <span>후불결제 서비스는 무엇인가요?</span>
                {expandedFaq === 0 ? <ChevronUp size={20} /> : <ChevronDown size={20} />}
              </button>
              {expandedFaq === 0 && (
                <div className="bnpl-faq-answer">
                  <p>후불결제는 상품을 먼저 받고 나중에 결제하는 서비스입니다.</p>
                </div>
              )}
            </div>

            <div className="bnpl-faq-item">
              <button
                className="bnpl-faq-question"
                onClick={() => toggleFaq(1)}
              >
                <span>후불결제 서비스는 어떻게 이용할 수 있나요?</span>
                {expandedFaq === 1 ? <ChevronUp size={20} /> : <ChevronDown size={20} />}
              </button>
              {expandedFaq === 1 && (
                <div className="bnpl-faq-answer">
                  <p>결제 시 후불결제 옵션을 선택하여 이용할 수 있습니다.</p>
                </div>
              )}
            </div>

            <div className="bnpl-faq-item">
              <button
                className="bnpl-faq-question"
                onClick={() => toggleFaq(2)}
              >
                <span>후불결제 면제됐는데 어떻게 되나요?</span>
                {expandedFaq === 2 ? <ChevronUp size={20} /> : <ChevronDown size={20} />}
              </button>
              {expandedFaq === 2 && (
                <div className="bnpl-faq-answer">
                  <p>면제된 금액은 자동으로 처리되며 납부하실 필요가 없습니다.</p>
                </div>
              )}
            </div>
          </div>
        </div>
      </div>

      <BottomNavigation />

      <BNPLInfoModal
        isOpen={showInfoModal}
        onClose={() => setShowInfoModal(false)}
      />
    </div>
  );
};

export default BNPLUsagePage;
import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { X, ChevronDown, ChevronUp, Check } from 'lucide-react';
import './BNPLTermsPage.css';

interface TermItem {
  id: string;
  title: string;
  required: boolean;
  content?: string;
}

const BNPLTermsPage: React.FC = () => {
  const navigate = useNavigate();
  const [checkedItems, setCheckedItems] = useState<string[]>([]);
  const [expandedItems, setExpandedItems] = useState<string[]>([]);

  const hanaTerms: TermItem[] = [
    { id: 'hana1', title: '(필수) 개인(신용)정보 제공 동의', required: true },
  ];

  const hanaFinancialTerms: TermItem[] = [
    { id: 'hana2', title: '(필수) 하나금융서비스 고객사항 동의', required: true },
    { id: 'hana3', title: '(필수) 후불결제서비스 이용 약관', required: true },
    { id: 'hana4', title: '(필수) 개인(신용)정보 수집 및 이용동의', required: true },
    { id: 'hana5', title: '(필수) 개인(신용)정보 조회 동의', required: true },
    { id: 'hana6', title: '(필수) 개인(신용)정보 제공 동의', required: true },
    { id: 'hana7', title: '(필수) 고유식별정보(개인식별번호) 처리 동의', required: true },
  ];

  const allTerms = [...hanaTerms, ...hanaFinancialTerms];
  const requiredTerms = allTerms.filter(term => term.required);

  const handleToggle = (id: string) => {
    setCheckedItems(prev =>
      prev.includes(id) ? prev.filter(item => item !== id) : [...prev, id]
    );
  };

  const handleToggleAll = () => {
    if (checkedItems.length === allTerms.length) {
      setCheckedItems([]);
    } else {
      setCheckedItems(allTerms.map(term => term.id));
    }
  };

  const handleExpand = (id: string) => {
    setExpandedItems(prev =>
      prev.includes(id) ? prev.filter(item => item !== id) : [...prev, id]
    );
  };

  const handleNext = () => {
    const allRequiredChecked = requiredTerms.every(term => checkedItems.includes(term.id));
    if (allRequiredChecked) {
      navigate('/bnpl-info');
    } else {
      alert('필수 약관에 모두 동의해 주세요.');
    }
  };

  const isAllChecked = checkedItems.length === allTerms.length;
  const canProceed = requiredTerms.every(term => checkedItems.includes(term.id));

  return (
    <div className="bnpl-terms-page">
      <div className="bnpl-terms-header">
        <div className="bnpl-terms-header-left">
          <img src="/ci/hana.png" alt="Hana" className="bnpl-terms-hana-logo" />
          <span className="bnpl-terms-pay-text">Pay</span>
        </div>
        <h2 className="bnpl-terms-title">후불결제 가입</h2>
        <button className="bnpl-terms-close" onClick={() => navigate('/bnpl-application')}>
          <X size={24} />
        </button>
      </div>

      <div className="bnpl-terms-progress">
        <div className="bnpl-progress-step active">
          <span className="bnpl-step-number">1</span>
          <span className="bnpl-step-label">이용동의</span>
        </div>
        <div className="bnpl-progress-line"></div>
        <div className="bnpl-progress-step">
          <span className="bnpl-step-number">2</span>
          <span className="bnpl-step-label">정보입력</span>
        </div>
        <div className="bnpl-progress-line"></div>
        <div className="bnpl-progress-step">
          <span className="bnpl-step-number">3</span>
          <span className="bnpl-step-label">가입심사</span>
        </div>
      </div>

      <div className="bnpl-terms-content">
        <div className="bnpl-terms-all-check" onClick={handleToggleAll}>
          <div className={`bnpl-checkbox ${isAllChecked ? 'checked' : ''}`}>
            {isAllChecked && <Check size={16} color="#fff" />}
          </div>
          <span className="bnpl-all-check-text">모두 동의합니다.</span>
        </div>

        <div className="bnpl-terms-section">
          <h3 className="bnpl-section-title">HANA</h3>
          {hanaTerms.map(term => (
            <div key={term.id} className="bnpl-term-item">
              <div className="bnpl-term-header">
                <div className="bnpl-term-left" onClick={() => handleToggle(term.id)}>
                  <div className={`bnpl-checkbox ${checkedItems.includes(term.id) ? 'checked' : ''}`}>
                    {checkedItems.includes(term.id) && <Check size={16} color="#fff" />}
                  </div>
                  <span className="bnpl-term-title">{term.title}</span>
                </div>
                <button className="bnpl-term-expand" onClick={() => handleExpand(term.id)}>
                  {expandedItems.includes(term.id) ? <ChevronUp size={20} /> : <ChevronDown size={20} />}
                </button>
              </div>
              {expandedItems.includes(term.id) && (
                <div className="bnpl-term-content">
                  개인정보 제공 동의 내용이 들어갑니다.
                </div>
              )}
            </div>
          ))}
        </div>

        <div className="bnpl-terms-section">
          <h3 className="bnpl-section-title">HANA FINANCIAL</h3>
          {hanaFinancialTerms.map(term => (
            <div key={term.id} className="bnpl-term-item">
              <div className="bnpl-term-header">
                <div className="bnpl-term-left" onClick={() => handleToggle(term.id)}>
                  <div className={`bnpl-checkbox ${checkedItems.includes(term.id) ? 'checked' : ''}`}>
                    {checkedItems.includes(term.id) && <Check size={16} color="#fff" />}
                  </div>
                  <span className="bnpl-term-title">{term.title}</span>
                </div>
                <button className="bnpl-term-expand" onClick={() => handleExpand(term.id)}>
                  {expandedItems.includes(term.id) ? <ChevronUp size={20} /> : <ChevronDown size={20} />}
                </button>
              </div>
              {expandedItems.includes(term.id) && (
                <div className="bnpl-term-content">
                  약관 내용이 들어갑니다.
                </div>
              )}
            </div>
          ))}
        </div>
      </div>

      <div className="bnpl-terms-bottom">
        <button
          className={`bnpl-terms-next ${canProceed ? 'active' : ''}`}
          onClick={handleNext}
          disabled={!canProceed}
        >
          다음
        </button>
      </div>
    </div>
  );
};

export default BNPLTermsPage;
import React, { useState, useRef, useEffect } from 'react';
import { Settings, X, ChevronRight } from 'lucide-react';
import './PaymentPage.css';

interface PaymentPageProps {
  onClose?: () => void;
}

const PaymentPage: React.FC<PaymentPageProps> = ({ onClose }) => {
  const [activeTab, setActiveTab] = useState('바코드');
  const [isPointActive, setIsPointActive] = useState(true);
  const [centerCardIndex, setCenterCardIndex] = useState(2);
  const scrollRef = useRef<HTMLDivElement>(null);

  const cards = [
    { id: 1, type: '신용', name: '국민', color: '#8B4513', logo: '국민' },
    { id: 2, type: '체크', name: '우리', color: '#0066CC', logo: '우리' },
    { id: 3, type: '신용', name: '하나', color: '#008485', logo: '하나' },
    { id: 4, type: '페이머니', name: '카카오페이', color: '#FFEB3B', logo: '카카오' },
    { id: 5, type: '체크', name: '신한', color: '#001F3F', logo: '신한' },
  ];

  useEffect(() => {
    
    if (scrollRef.current) {
      const container = scrollRef.current;
      const cards = container.children;
      if (cards[centerCardIndex]) {
        const card = cards[centerCardIndex] as HTMLElement;
        const cardRect = card.getBoundingClientRect();
        const containerRect = container.getBoundingClientRect();
        const scrollLeft = card.offsetLeft - (containerRect.width / 2) + (card.offsetWidth / 2);
        container.scrollLeft = scrollLeft;
      }
    }
  }, []);

  const scrollToCard = (index: number) => {
    if (scrollRef.current && index !== centerCardIndex) {
      setCenterCardIndex(index);
      
      const container = scrollRef.current;
      const cards = container.children;
      if (cards[index]) {
        const card = cards[index] as HTMLElement;
        const containerRect = container.getBoundingClientRect();
        const scrollLeft = card.offsetLeft - (containerRect.width / 2) + (card.offsetWidth / 2);
        
        container.scrollTo({
          left: scrollLeft,
          behavior: 'smooth'
        });
      }
    }
  };

  return (
    <div className="payment-page">
      <div className="payment-header">
        <div className="header-left">
          <span className="hana-pay-logo">하나pay</span>
        </div>
        <div className="header-right">
          <button className="icon-button">
            <Settings size={24} />
          </button>
          <button className="icon-button" onClick={onClose}>
            <X size={24} />
          </button>
        </div>
      </div>

      <div className="payment-content">
        <div className="payment-card-container">
          <div className="payment-card">
            <div className="payment-tabs">
              <button 
                className={`payment-tab ${activeTab === '바코드' ? 'active' : ''}`}
                onClick={() => setActiveTab('바코드')}
              >
                바코드
              </button>
              <button 
                className={`payment-tab ${activeTab === '삼성페이' ? 'active' : ''}`}
                onClick={() => setActiveTab('삼성페이')}
              >
                삼성페이
              </button>
              <button 
                className={`payment-tab ${activeTab === 'QR스캔' ? 'active' : ''}`}
                onClick={() => setActiveTab('QR스캔')}
              >
                QR스캔
              </button>
            </div>

            <div className="code-container">
              <div className="barcode-section">
                <img src="/code/barcode.png" alt="바코드" className="barcode-image" />
              </div>
              <div className="qr-section">
                <img src="/code/qrcode.png" alt="QR코드" className="qr-image" />
              </div>
            </div>

            <div className="payment-info">
              <div className="payment-method">
                <span className="label">M 하나머니</span>
                <span className="pay-badge">충전</span>
              </div>
              <div className="payment-amount">
                <span className="amount">250,000원</span>
                <button className="more-button">⋮</button>
              </div>
              <div className="bank-info">
                하나은행 3333 <span className="dropdown">▼</span>
              </div>
            </div>

            <div className="point-section">
              <div className="point-header">
                <span>하나머니 사용</span>
                <label className="toggle-switch">
                  <input 
                    type="checkbox" 
                    checked={isPointActive}
                    onChange={(e) => setIsPointActive(e.target.checked)}
                  />
                  <span className="slider"></span>
                </label>
              </div>
              <div className="point-amount">5,000원</div>
            </div>

            <div className="benefit-link">
              <span>사용 가능한 혜택</span>
              <button className="link-button">
                보기 <ChevronRight size={16} />
              </button>
            </div>
          </div>
        </div>

        <div className="cards-section">
          <div className="cards-scroll" ref={scrollRef}>
            {cards.map((card, index) => (
              <div 
                key={card.id} 
                className={`mini-card ${index === centerCardIndex ? 'center' : ''}`}
                style={{ backgroundColor: card.color }}
                onClick={() => scrollToCard(index)}
              >
                <div className="mini-card-type">{card.type}</div>
                <div className="mini-card-logo">{card.logo}</div>
              </div>
            ))}
            <div className="mini-card add-card">
              <div className="add-card-icon">+</div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default PaymentPage;
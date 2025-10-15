import React from 'react';
import './ProductPension.css';

const ProductPension: React.FC = () => {
  const categories = [
    { icon: '🏦', label: '채권', subLabel: '상품' },
    { icon: '💼', label: 'RP', subLabel: '상품' },
    { icon: '📊', label: 'CMA', subLabel: '계좌' },
    { icon: '💰', label: '발행어음', subLabel: '상품' },
    { icon: '📈', label: '파생결합', subLabel: '상품' },
    { icon: '🔄', label: 'WRAP', subLabel: '상품' },
    { icon: '🌐', label: 'ELS/DLS', subLabel: '상품' },
    { icon: '📝', label: 'CMA', subLabel: '간편계좌' }
  ];

  const popularFunds = [
    { name: '삼성클로벌11-2', rate: '+5.50%', color: '#e74c3c', flag: 'tossface', flagClass: 'u-kr' },
    { name: '에셋플러스러시아24-2', rate: '+3.90%', color: '#27ae60', flag: 'tossface', flagClass: 'u-ru' },
    { name: '왕양비전넘버3A-2', rate: '+3.60%', color: '#e74c3c', flag: 'tossface', flagClass: 'u-cn' },
    { name: '왕카래처장384-3', rate: '+3.76%', color: '#2ecc71', flag: 'tossface', flagClass: 'u-jp' }
  ];

  const overseasFunds = [
    { name: '미국 국채 500달 6월 만기', rate: '+7.09%', flag: 'tossface', flagClass: 'u-us', subText: '34억 9천' },
    { name: '미국 국채 40년 8월 만기', rate: '+6.04%', flag: 'tossface', flagClass: 'u-us', subText: '3억 5천' },
    { name: '미국 국채 25년 10월 만기', rate: '+5.01%', flag: 'tossface', flagClass: 'u-us', subText: '31억 5천' },
    { name: '미국 국채 54년 5월 만기', rate: '+4.49%', flag: 'tossface', flagClass: 'u-us', subText: '13억 8천' }
  ];

  const pensionData = [
    { name: 'ELS/DLS', growth: '+63.04%' },
    { name: 'DB410k(NIH)\n오픈+하이브', growth: '+62.99%' },
    { name: '삼성중국본토\n중소형FOCUS', growth: '+62.15%' }
  ];

  const pensionProducts = [
    { name: 'KODEX 200', value: '45,345원', change: '+176원 +0.39%', isUp: true },
    { name: 'TIGER 차이나', value: '3,975원', change: '+46원 +0.17%', isUp: true },
    { name: 'KODEX 은행', value: '14,420원', change: '-80원 -0.14%', isUp: false },
    { name: 'TIGER 미디어&엔터', value: '22,545원', change: '-205원 -0.42%', isUp: false }
  ];

  const overseasISAFunds = [
    { name: '우리클로벌11-2', rate: '+5.50%', color: '#e74c3c', flag: 'tossface', flagClass: 'u-kr' },
    { name: '에셋플러스러시아24-2', rate: '+3.90%', color: '#27ae60', flag: 'tossface', flagClass: 'u-ru' },
    { name: '왕양비전넘버3A-2', rate: '+3.60%', color: '#e74c3c', flag: 'tossface', flagClass: 'u-cn' },
    { name: '왕카래처장384-3', rate: '+3.76%', color: '#2ecc71', flag: 'tossface', flagClass: 'u-jp' }
  ];

  return (
    <div className="product-pension-container">
      {/* Category Icons */}
      <section className="category-section">
        <div className="category-title">
          <span className="emoji">💜</span>
          <span>캐시스 재금 이벤트</span>
          <span className="subtitle">최근 종료된 최대 3.95%+120% 배픽</span>
        </div>
        <div className="category-grid">
          {categories.map((cat, index) => (
            <div key={index} className="category-item">
              <div className="category-icon">{cat.icon}</div>
              <div className="category-label">{cat.label}</div>
              <div className="category-sublabel">{cat.subLabel}</div>
            </div>
          ))}
        </div>
      </section>

      {/* Popular Products */}
      <section className="popular-section">
        <div className="section-header">
          <h3>이자를 주는 연세상품</h3>
          <div className="section-tabs">
            <span className="tab active">채권</span>
            <span className="tab">발행어음</span>
            <span className="tab">RP</span>
            <span className="more">더 보기 &gt;</span>
          </div>
        </div>
        <div className="sub-header">
          <span>연기저트 보유자는 채권별 최저기준 👏</span>
        </div>
        <div className="product-list">
          {popularFunds.map((fund, index) => (
            <div key={index} className="product-item">
              <div className="product-icon">
                {fund.flag === 'tossface' ? (
                  <span className={`tossface ${fund.flagClass}`} style={{ fontSize: '24px' }}></span>
                ) : (
                  fund.flag || (index === 0 ? '🇰🇷' : index === 1 ? '🌍' : index === 2 ? '🎯' : '🎯')
                )}
              </div>
              <div className="product-info">
                <div className="product-name">{fund.name}</div>
                <div className="product-subtitle">
                  {index === 0 ? '보통예금' : index === 1 ? '해외채권' : '개발/이렇드'}
                </div>
              </div>
              <div className="product-rate" style={{ color: fund.color }}>
                {fund.rate}
              </div>
            </div>
          ))}
        </div>
        <button className="more-button">더보기</button>
      </section>

      {/* Fund Cards */}
      <section className="fund-cards">
        <div className="section-header">
          <h3>지금 가입 가능한 펀드</h3>
        </div>
        <div className="fund-card-container">
          <div className="fund-card card1">
            <div className="card-badge">오픈예정</div>
            <div className="card-title">신한은행펀드예매타자룸<br/>연금저축계좌10A-4</div>
            <div className="card-info">
              <span>예정일: 1월 5일</span>
              <span>목표: 20억원</span>
            </div>
            <div className="card-progress">가입하기</div>
          </div>
          <div className="fund-card card2">
            <div className="card-badge">투자중</div>
            <div className="card-title">신한은행펀드예매타룸<br/>원금보장형계좌10A-4</div>
            <div className="card-info">
              <span>예정일: 1월 5일</span>
              <span>목표: 17억원</span>
            </div>
            <div className="card-progress">가입하기</div>
          </div>
        </div>
      </section>

      {/* Overseas Products */}
      <section className="overseas-section">
        <div className="section-header">
          <h3>이자를 투자하는 해외상품</h3>
          <div className="section-tabs">
            <span className="tab active">채권</span>
            <span className="tab">발행어음</span>
            <span className="tab">RP</span>
            <span className="more">더 보기 &gt;</span>
          </div>
        </div>
        <div className="sub-header">
          <span>해외펀드 발행을 저금리로 투자자로 👍</span>
        </div>
        <div className="product-list">
          {overseasFunds.map((fund, index) => (
            <div key={index} className="product-item">
              <div className="product-icon">
                {fund.flag === 'tossface' ? (
                  <span className={`tossface ${fund.flagClass}`} style={{ fontSize: '24px' }}></span>
                ) : (
                  fund.flag
                )}
              </div>
              <div className="product-info">
                <div className="product-name">{fund.name}</div>
                <div className="product-subtitle">{fund.subText}</div>
              </div>
              <div className="product-rate" style={{ color: '#e74c3c' }}>
                {fund.rate}
              </div>
            </div>
          ))}
        </div>
        <button className="more-button">더보기</button>
      </section>

      {/* Promotion Banner */}
      <section className="promotion-banner">
        <div className="banner-content">
          <span className="banner-emoji">😊</span>
          <div className="banner-text">
            <div>알려또 & 제하는 이자!</div>
            <div>연 3.90%, 하루만 맡겨도 OK!</div>
          </div>
        </div>
      </section>

      {/* Pension Summary */}
      <section className="pension-summary">
        <div className="section-header">
          <h3>높은 수익률 추구하는 상품</h3>
        </div>
        <div className="pension-cards">
          {pensionData.map((item, index) => (
            <div key={index} className="pension-card">
              <div className="pension-label">{index === 0 ? '펀드' : index === 1 ? 'ETN' : 'ETP'}</div>
              <div className="pension-name">{item.name}</div>
              <div className="pension-growth">{item.growth}</div>
            </div>
          ))}
        </div>
      </section>

      {/* Pension Products */}
      <section className="pension-products">
        <div className="section-header">
          <h3>연말정산 노후를 위한 연금상품</h3>
        </div>
        <div className="section-tabs-full">
          <span className="tab active">IRP</span>
          <span className="tab">연금저축</span>
          <span className="more">더 보기 &gt;</span>
        </div>
        <div className="sub-header">
          <span>연금저축은 세테크로 뜨고 포켓몬과 투자까지 👍</span>
        </div>
        <div className="product-list">
          {pensionProducts.map((product, index) => (
            <div key={index} className="pension-product-item">
              <div className="pension-product-icon">
                {index === 0 ? '🔵' : index === 1 ? '🟠' : index === 2 ? '🔵' : '🟠'}
              </div>
              <div className="pension-product-info">
                <div className="pension-product-name">{product.name}</div>
                <div className="pension-product-subtitle">주식형</div>
              </div>
              <div className="pension-product-value">
                <div className="value">{product.value}</div>
                <div className={`change ${product.isUp ? 'up' : 'down'}`}>
                  {product.change}
                </div>
              </div>
            </div>
          ))}
        </div>
        <button className="more-button">더보기</button>
      </section>

      {/* Overseas ISA */}
      <section className="overseas-isa">
        <div className="section-header">
          <h3>세금을 아껴주는 ISA 중계좌</h3>
        </div>
        <div className="section-tabs">
          <span className="tab active">국내펀드</span>
          <span className="tab">해외</span>
          <span className="tab">ETF</span>
          <span className="tab">ELS</span>
          <span className="more">더 보기 &gt;</span>
        </div>
        <div className="sub-header">
          <span>ISA 계좌로 세금도 아끼고 세테크하는 투자까지 👏</span>
        </div>
        <div className="product-list">
          {overseasISAFunds.map((fund, index) => (
            <div key={index} className="product-item">
              <div className="product-icon">
                {fund.flag === 'tossface' ? (
                  <span className={`tossface ${fund.flagClass}`} style={{ fontSize: '24px' }}></span>
                ) : (
                  fund.flag || (index === 0 ? '🔵' : index === 1 ? '🌍' : index === 2 ? '🎯' : '🎯')
                )}
              </div>
              <div className="product-info">
                <div className="product-name">{fund.name}</div>
                <div className="product-subtitle">
                  {index === 0 ? '주식혼합형' : index === 1 ? '해외펀드' : '채권혼합형'}
                </div>
              </div>
              <div className="product-rate" style={{ color: fund.color }}>
                {fund.rate}
              </div>
            </div>
          ))}
        </div>
        <button className="more-button">더보기</button>
      </section>

      {/* Bottom Information */}
      <section className="bottom-info">
        <div className="info-card">
          <span className="info-icon">📋</span>
          <span className="info-text">투자시 고려사항 상품 발간정보</span>
        </div>
      </section>
    </div>
  );
};

export default ProductPension;
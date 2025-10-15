import React, { useState, useEffect, useRef } from 'react';
import './AdBanner.css';

const AdBanner: React.FC = () => {
  const [currentSlide, setCurrentSlide] = useState(0);
  const [isAutoPlaying, setIsAutoPlaying] = useState(true);
  const intervalRef = useRef<NodeJS.Timeout | null>(null);
  
  const ads = [
    {
      title: "뱅키스 체크 이벤트",
      subtitle: "체크 충족시 최대 3.3만원+5200 배픽",
      emoji: "🎁",
      bgColor: "#fef3c7"
    },
    {
      title: "연금저축펀드 세액공제",
      subtitle: "최대 900만원까지 16.5% 세액공제",
      emoji: "💰",
      bgColor: "#ede9fe"
    },
    {
      title: "ISA 계좌 특별혜택",
      subtitle: "비과세 한도 최대 1억원까지",
      emoji: "📈",
      bgColor: "#fce7f3"
    },
    {
      title: "퇴직연금 IRP 가입",
      subtitle: "연 700만원 세액공제 혜택",
      emoji: "🏦",
      bgColor: "#e0f2fe"
    }
  ];

  const nextSlide = () => {
    setCurrentSlide((prev) => (prev + 1) % ads.length);
  };

  const prevSlide = () => {
    setCurrentSlide((prev) => (prev - 1 + ads.length) % ads.length);
  };

  const goToSlide = (index: number) => {
    setCurrentSlide(index);
    setIsAutoPlaying(false);
    setTimeout(() => setIsAutoPlaying(true), 10000);
  };

  useEffect(() => {
    if (isAutoPlaying) {
      intervalRef.current = setInterval(() => {
        nextSlide();
      }, 5000);
    }

    return () => {
      if (intervalRef.current) {
        clearInterval(intervalRef.current);
      }
    };
  }, [isAutoPlaying, currentSlide]);

  return (
    <div className="ad-banner-container">
      <div className="ad-banner-carousel">
        <div 
          className="ad-banner-wrapper"
          style={{ transform: `translateX(-${currentSlide * 100}%)` }}
        >
          {ads.map((ad, index) => (
            <div 
              key={index} 
              className="ad-banner"
              style={{ background: ad.bgColor }}
            >
              <div className="ad-content">
                <div className="ad-text">
                  <div className="ad-title">{ad.title}</div>
                  <div className="ad-subtitle">{ad.subtitle}</div>
                </div>
                <div className="ad-emoji">
                  <span className="tossface">{ad.emoji}</span>
                </div>
              </div>
            </div>
          ))}
        </div>
        
        <div className="carousel-dots">
          {ads.map((_, index) => (
            <button
              key={index}
              className={`dot ${currentSlide === index ? 'active' : ''}`}
              onClick={() => goToSlide(index)}
            />
          ))}
        </div>
      </div>
    </div>
  );
};

export default AdBanner;
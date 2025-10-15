import React, { useState, useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { ArrowLeft, Menu } from 'lucide-react';
import BottomNavigation from '../navigation/BottomNavigation';
import MenuDrawer from '../menu/MenuDrawer';
import { newsApi } from '../../services/newsApi';
import type { NewsSummaryData } from '../../types/news.types';
import './NewsDetail.css';

const NewsDetail: React.FC = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const [isMenuOpen, setIsMenuOpen] = useState(false);
  const [summary, setSummary] = useState<NewsSummaryData | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  const { link, pubDate } = location.state || {};

  useEffect(() => {
    const fetchSummary = async () => {
      if (!link) {
        setIsLoading(false);
        return;
      }

      try {
        setIsLoading(true);
        const response = await newsApi.getNewsSummary(link);
        if (response.success && response.data) {
          setSummary(response.data);
        }
      } catch (error) {
        console.error('뉴스 요약 조회 실패:', error);
      } finally {
        setIsLoading(false);
      }
    };

    fetchSummary();
  }, [link]);

  const formatDate = (pubDate: string): string => {
    const date = new Date(pubDate);
    const month = date.getMonth() + 1;
    const day = date.getDate();
    const hours = date.getHours();
    const minutes = date.getMinutes();
    const ampm = hours >= 12 ? '오후' : '오전';
    const displayHours = hours % 12 || 12;
    const displayMinutes = minutes.toString().padStart(2, '0');

    return `${month}월 ${day}일 ${ampm} ${displayHours}:${displayMinutes}`;
  };

  return (
    <div className="nd-page">
      <div className="nd-header">
        <button className="nd-back-btn" onClick={() => navigate(-1)}>
          <ArrowLeft size={24} />
        </button>
        <span className="nd-header-title">뉴스</span>
        <button className="nd-menu-btn" onClick={() => setIsMenuOpen(true)}>
          <Menu size={24} />
        </button>
      </div>

      <div className="nd-content">
        {isLoading ? (
          <p style={{ textAlign: 'center', padding: '20px', color: '#999' }}>
            뉴스를 불러오는 중...
          </p>
        ) : summary ? (
          <>
            <h1 className="nd-title">
              {summary.title}
            </h1>

            <div className="nd-meta">
              <span className="nd-source">네이버 뉴스</span>
              <span className="nd-date">{pubDate ? formatDate(pubDate) : ''}</span>
            </div>

            <div className="nd-image-container">
              <img
                src="/newsThumnail/hanati.png"
                alt="뉴스 이미지"
                className="nd-main-image"
              />
            </div>

            <div className="nd-body">
              {summary.summary.split('\n').map((paragraph, index) => (
                <p key={index}>{paragraph}</p>
              ))}
            </div>
          </>
        ) : (
          <p style={{ textAlign: 'center', padding: '20px', color: '#999' }}>
            뉴스 정보를 불러올 수 없습니다.
          </p>
        )}
      </div>
      
      <BottomNavigation />
      <MenuDrawer isOpen={isMenuOpen} onClose={() => setIsMenuOpen(false)} />
    </div>
  );
};

export default NewsDetail;
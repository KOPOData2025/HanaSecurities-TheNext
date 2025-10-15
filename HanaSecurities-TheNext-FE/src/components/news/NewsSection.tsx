import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import './NewsSection.css';
import { newsApi } from '../../services/newsApi';
import type { NewsItem } from '../../types/news.types';

interface NewsSectionProps {
  title?: string;
  query: string;
}

const NewsSection: React.FC<NewsSectionProps> = ({
  title = '오늘의 뉴스',
  query
}) => {
  const navigate = useNavigate();
  const [newsItems, setNewsItems] = useState<NewsItem[]>([]);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const fetchNews = async () => {
      try {
        setIsLoading(true);
        const response = await newsApi.getNews(query, 30);
        if (response.success && response.data.items) {
          
          setNewsItems(response.data.items.slice(0, 5));
        }
      } catch (error) {
        console.error('뉴스 조회 실패:', error);
      } finally {
        setIsLoading(false);
      }
    };

    fetchNews();
  }, [query]);

  const handleNewsClick = (item: NewsItem, index: number) => {
    navigate(`/news/${index + 1}`, {
      state: {
        link: item.link,
        pubDate: item.pubDate
      }
    });
  };

  const formatTime = (pubDate: string): string => {
    const date = new Date(pubDate);
    const now = new Date();
    const diffMs = now.getTime() - date.getTime();
    const diffMins = Math.floor(diffMs / 60000);
    const diffHours = Math.floor(diffMins / 60);

    if (diffMins < 60) {
      return `${diffMins}분 전`;
    } else if (diffHours < 24) {
      return `${diffHours}시간 전`;
    } else {
      const month = date.getMonth() + 1;
      const day = date.getDate();
      return `${month}.${day}`;
    }
  };

  const removeHtmlTags = (text: string): string => {
    return text
      .replace(/<[^>]*>/g, '')
      .replace(/&quot;/g, '"')
      .replace(/&apos;/g, "'")
      .replace(/&amp;/g, '&')
      .replace(/&lt;/g, '<')
      .replace(/&gt;/g, '>');
  };

  if (isLoading) {
    return (
      <div className="ns-section">
        <div className="ns-header">
          <h2>{title}</h2>
        </div>
        <div className="ns-list">
          <p style={{ textAlign: 'center', padding: '20px', color: '#999' }}>
            뉴스를 불러오는 중...
          </p>
        </div>
      </div>
    );
  }

  return (
    <div className="ns-section">
      <div className="ns-header">
        <h2>{title}</h2>
      </div>

      <div className="ns-list">
        {newsItems.map((item, index) => (
          <div
            key={index}
            className="ns-item"
            onClick={() => handleNewsClick(item, index)}
          >
            <div className="ns-content">
              <p className="ns-title">{removeHtmlTags(item.title)}</p>
              <div className="ns-meta">
                <span className="ns-source">네이버 뉴스</span>
                <span className="ns-time">{formatTime(item.pubDate)}</span>
              </div>
            </div>
            <img
              src="/stockIcon/086790.png"
              alt="News"
              className="ns-thumbnail"
            />
          </div>
        ))}
      </div>

      <button className="ns-more-btn">
        더보기
      </button>
    </div>
  );
};

export default NewsSection;
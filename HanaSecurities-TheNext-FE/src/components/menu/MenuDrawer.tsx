import React, { useState, useRef, useEffect } from 'react';
import { Search, X, Headphones, ShieldCheck, Settings, Power } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import './MenuDrawer.css';
import { authenticateWithPasskey } from '../auth/PasskeyAuth';
import SearchPage from '../search/SearchPage';
import { useAuth } from '../../contexts/AuthContext';

interface MenuDrawerProps {
  isOpen: boolean;
  onClose: () => void;
}

interface MenuItem {
  label: string;
}

interface TabContent {
  leftMenu: MenuItem[];
  rightMenu: { [key: string]: MenuItem[] };
}

interface MenuData {
  [key: string]: TabContent;
}

const menuData: MenuData = {
  '국내': {
    leftMenu: [
      { label: '주식' },
      { label: '선물옵션' },
      { label: '채권' }
    ],
    rightMenu: {
      '주식': [
        { label: '국내주식 관심종목' },
        { label: '국내주식 현재가' },
        { label: '국내주식 차트' },
        { label: '국내주식 주문' },
        { label: '국내주식 잔고' }
      ],
      '선물옵션': [
        { label: '선물옵션 현재가' },
        { label: '선물옵션 차트' },
        { label: '선물옵션 주문' },
        { label: '선물옵션 잔고' }
      ],
      '채권': [
        { label: '채권 현재가' },
        { label: '채권 주문' }
      ]
    }
  },
  '해외': {
    leftMenu: [
      { label: '주식' },
      { label: '선물옵션' },
      { label: '채권' }
    ],
    rightMenu: {
      '주식': [
        { label: '해외주식 관심종목' },
        { label: '해외주식 현재가' },
        { label: '해외주식 차트' },
        { label: '해외주식 주문' },
        { label: '해외주식 잔고' }
      ],
      '선물옵션': [
        { label: '해외선물 현재가' },
        { label: '해외선물 차트' },
        { label: '해외선물 주문' },
        { label: '해외선물 잔고' }
      ],
      '채권': [
        { label: '해외채권 현재가' },
        { label: '해외채권 주문' }
      ]
    }
  },
  '후불결제': {
    leftMenu: [
      { label: '약정/신청' },
      { label: '조회' },
      { label: '결제' },
      { label: '알림/관리' },
      { label: '쇼핑' }
    ],
    rightMenu: {
      '약정/신청': [
        { label: '후불결제 서비스 안내' },
        { label: '후불결제 신청/변경' }
      ],
      '조회': [
        { label: '이용내역 조회' },
        { label: '이자/수수료 조회' },
        { label: '한도/약정 조회' }
      ],
      '결제': [
        { label: '즉시결제/상환' },
        { label: '자동결제 관리' }
      ],
      '알림/관리': [
        { label: '알림 설정' },
        { label: '이용 가이드 & FAQ' }
      ],
      '쇼핑': [
        { label: '쇼핑 홈' },
        { label: '특가' },
        { label: '전자제품' },
        { label: '식품' },
        { label: '생활용품' }
      ]
    }
  },
  '자산·뱅킹': {
    leftMenu: [
      { label: '자산/내역' }
    ],
    rightMenu: {
      '자산/내역': [
        { label: '총 자산현황' },
        { label: '거래내역' }
      ]
    }
  }
};

const MenuDrawer: React.FC<MenuDrawerProps> = ({ isOpen, onClose }) => {
  const [selectedTab, setSelectedTab] = useState('국내');
  const [selectedLeftItem, setSelectedLeftItem] = useState('주식');
  const [isSearchOpen, setIsSearchOpen] = useState(false);
  const navigate = useNavigate();
  const { isAuthenticated, user, logout } = useAuth();
  const rightColumnRef = useRef<HTMLDivElement>(null);
  const rightSectionRefs = useRef<{ [key: string]: HTMLDivElement | null }>({});

  const handleMenuItemClick = (item: MenuItem) => {
    if (item.label === '쇼핑 홈') {
      navigate('/shopping');
      onClose();
    } else if (item.label === '후불결제 신청/변경') {
      navigate('/bnpl-application');
      onClose();
    } else if (item.label === '이용내역 조회') {
      navigate('/bnpl-usage');
      onClose();
    } else if (item.label === '국내주식 관심종목') {
      navigate('/watchlist');
      onClose();
    } else if (item.label === '해외주식 관심종목') {
      navigate('/foreign-watchlist');
      onClose();
    } else if (item.label === '해외주식 현재가') {
      navigate('/foreign-search');
      onClose();
    }
  };

  const handleLeftItemClick = (itemLabel: string) => {
    setSelectedLeftItem(itemLabel);

    
    setTimeout(() => {
      if (rightSectionRefs.current[itemLabel] && rightColumnRef.current) {
        const sectionElement = rightSectionRefs.current[itemLabel];
        const columnElement = rightColumnRef.current;

        if (sectionElement) {
          
          const sectionTop = sectionElement.offsetTop;

          
          columnElement.scrollTo({
            top: sectionTop,
            behavior: 'smooth'
          });
        }
      }
    }, 100); 
  };

  
  useEffect(() => {
    if (rightColumnRef.current) {
      rightColumnRef.current.scrollTop = 0;
    }
  }, [selectedTab]);

  const handleLogout = () => {
    logout();
    onClose();
  };

  const handleNavigateToRegister = () => {
    navigate('/register');
    onClose();
  };

  const handleNavigateToLogin = () => {
    navigate('/login');
    onClose();
  };

  const handleNavigateToProfile = () => {
    
    alert('내 정보 페이지는 준비 중입니다.');
  };

  const bottomMenuItems = [
    { icon: <Headphones size={20} />, label: '고객지원' },
    { icon: <ShieldCheck size={20} />, label: '인증' },
    { icon: <Settings size={20} />, label: '설정' },
    { icon: <Power size={20} />, label: '종료' }
  ];

  return (
    <>
      {/* Overlay */}
      <div
        className={`md-overlay ${isOpen ? 'active' : ''}`}
        onClick={onClose}
      />

      {/* Drawer */}
      <div className={`md-drawer ${isOpen ? 'open' : ''}`}>
        {/* Header */}
        <div className="md-header">
          <div className="md-header-actions">
            <button className="md-search" onClick={() => setIsSearchOpen(true)}>
              <Search size={22} color="#999" />
            </button>
            <button className="md-close" onClick={onClose}>
              <X size={22} color="#999" />
            </button>
          </div>
        </div>

        {/* Login Info */}
        <div className="md-login-info">
          {isAuthenticated && user ? (
            <>
              <div className="md-user-info">
                <span className="md-name">{user.userName}</span>
                <span className="md-badge">하나증권인증</span>
              </div>
              <div className="md-account-actions">
                <button onClick={handleNavigateToProfile} style={{ background: 'none', border: 'none', color: 'inherit', cursor: 'pointer' }}>
                  내 정보
                </button>
                <span className="md-divider">|</span>
                <button onClick={handleLogout} style={{ background: 'none', border: 'none', color: 'inherit', cursor: 'pointer' }}>
                  로그아웃
                </button>
              </div>
            </>
          ) : (
            <>
              <div className="md-user-info">
                <span className="md-name">로그인이 필요합니다</span>
              </div>
              <div className="md-account-actions">
                <button onClick={handleNavigateToRegister} style={{ background: 'none', border: 'none', color: 'inherit', cursor: 'pointer' }}>
                  회원가입
                </button>
                <span className="md-divider">|</span>
                <button onClick={handleNavigateToLogin} style={{ background: 'none', border: 'none', color: 'inherit', cursor: 'pointer' }}>
                  로그인
                </button>
              </div>
            </>
          )}
        </div>

        {/* Tab Navigation */}
        <div className="md-tabs">
          {Object.keys(menuData).map((tab) => (
            <button
              key={tab}
              className={`md-tab ${selectedTab === tab ? 'active' : ''}`}
              onClick={() => {
                setSelectedTab(tab);
                setSelectedLeftItem(menuData[tab].leftMenu[0].label);
              }}
            >
              {tab}
            </button>
          ))}
        </div>

        {/* Content */}
        <div className="md-content">
          <div className="md-grid">
            {/* Left Column */}
            <div className="md-column left">
              <div className="md-left-column-content">
                {menuData[selectedTab]?.leftMenu.map((item) => (
                  <button
                    key={item.label}
                    className={`md-left-item ${selectedLeftItem === item.label ? 'selected' : ''}`}
                    onClick={() => handleLeftItemClick(item.label)}
                  >
                    {item.label}
                  </button>
                ))}
              </div>

              {/* Bottom Menu - Only in left column */}
              <div className="md-bottom">
                {bottomMenuItems.map((item, index) => (
                  <button key={index} className="md-bottom-item">
                    <span className="md-bottom-icon">
                      {item.icon}
                    </span>
                    <span className="md-bottom-label">{item.label}</span>
                  </button>
                ))}
              </div>
            </div>

            {/* Right Column */}
            <div className="md-column right" ref={rightColumnRef} style={{ overflowY: 'auto', maxHeight: 'calc(100vh - 140px)' }}>
              {selectedTab && menuData[selectedTab]?.leftMenu.map((leftMenuItem) => (
                <div
                  key={leftMenuItem.label}
                  ref={(el) => rightSectionRefs.current[leftMenuItem.label] = el}
                  className="md-section"
                >
                  <h4 className="md-subsection-title">
                    {leftMenuItem.label}
                  </h4>
                  {menuData[selectedTab]?.rightMenu[leftMenuItem.label]?.map((item) => (
                    <button
                      key={item.label}
                      className="md-right-item"
                      onClick={() => handleMenuItemClick(item)}
                    >
                      {item.label}
                    </button>
                  ))}
                </div>
              ))}
              {/* 스크롤을 위한 빈 섹션 추가 - 마지막 항목도 최상단으로 올릴 수 있도록 */}
              <div className="md-section md-section-empty" style={{ height: 'calc(100vh - 150px)', minHeight: '600px' }} />
            </div>
          </div>
        </div>
      </div>

      {/* Search Page */}
      <SearchPage isOpen={isSearchOpen} onClose={() => setIsSearchOpen(false)} />
    </>
  );
};

export default MenuDrawer;
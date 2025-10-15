import React, { useState, useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { Menu, Home, Star, TrendingUp, ShoppingCart, Wallet, ArrowRightLeft, PieChart } from 'lucide-react';
import MenuDrawer from '../menu/MenuDrawer';
import SecondaryPasswordModal from '../auth/SecondaryPasswordModal';
import { useAuth } from '../../contexts/AuthContext';
import { verifySecondaryPassword } from '../../services/webauthnService';
import './BottomNavigation.css';

interface NavItem {
  id: string;
  label: string;
  icon: React.ReactNode;
}

const BottomNavigation: React.FC = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const { user } = useAuth();

  
  useEffect(() => {
    const match = location.pathname.match(/\/stock\/(\d+)/);
    if (match) {
      localStorage.setItem('lastViewedStock', match[1]);
    }
  }, [location.pathname]);

  
  const getActiveTab = () => {
    if (location.pathname === '/payment') return 'payment';
    if (location.pathname === '/shopping') return 'payment'; 
    if (location.pathname === '/watchlist') return 'favorite';
    if (location.pathname === '/asset' || location.pathname.startsWith('/asset/')) return 'asset';
    if (location.pathname === '/order') return 'order';
    if (location.pathname.startsWith('/stock')) return 'price';
    return 'home';
  };

  const [activeTab, setActiveTab] = useState(getActiveTab());
  const [isMenuOpen, setIsMenuOpen] = useState(false);
  const [showPasswordModal, setShowPasswordModal] = useState(false);
  const [isAssetAuthenticated, setIsAssetAuthenticated] = useState(false);

  useEffect(() => {
    setActiveTab(getActiveTab());
  }, [location.pathname]);

  const navItems: NavItem[] = [
    
    { id: 'home', label: '홈', icon: <Home size={22} /> },
    
    { id: 'price', label: '현재가', icon: <TrendingUp size={22} /> },
    { id: 'payment', label: '결제', icon: <ArrowRightLeft size={22} /> },
    { id: 'order', label: '주문', icon: <ShoppingCart size={22} /> },
    { id: 'asset', label: '자산', icon: <PieChart size={22} /> }
  ];

  const handleNavClick = (itemId: string) => {
    if (itemId === 'menu') {
      setIsMenuOpen(true);
    } else if (itemId === 'payment') {
      navigate('/payment');
    } else if (itemId === 'home') {
      navigate('/');
      setActiveTab(itemId);
    } else if (itemId === 'favorite') {
      navigate('/watchlist');
      setActiveTab(itemId);
    } else if (itemId === 'asset') {
      
      if (isAssetAuthenticated) {
        navigate('/asset');
        setActiveTab(itemId);
      } else {
        setShowPasswordModal(true);
      }
    } else if (itemId === 'order') {
      navigate('/order');
      setActiveTab(itemId);
    } else if (itemId === 'price') {
      
      const lastViewedStock = localStorage.getItem('lastViewedStock') || '086790';
      navigate(`/stock/${lastViewedStock}`);
      setActiveTab(itemId);
    } else {
      setActiveTab(itemId);
    }
  };

  const handlePasswordConfirm = async (password: string) => {
    if (!user) {
      alert('로그인이 필요합니다.');
      setShowPasswordModal(false);
      navigate('/login');
      return;
    }

    try {
      
      const response = await verifySecondaryPassword(user.userId, password);

      if (response.success) {
        
        setIsAssetAuthenticated(true);
        setShowPasswordModal(false);
        navigate('/asset');
        setActiveTab('asset');
      } else {
        
        alert(response.message || '비밀번호가 일치하지 않습니다.');
      }
    } catch (error: any) {
      console.error('2차 비밀번호 검증 실패:', error);
      alert(error.message || '비밀번호 검증에 실패했습니다.');
    }
  };

  return (
    <>
      <nav className="bottom-nav">
        <div className="nav-items-container">
          {navItems.map((item) => (
            <button
              key={item.id}
              className={`nav-item ${activeTab === item.id ? 'active' : ''}`}
              onClick={() => handleNavClick(item.id)}
            >
              {item.icon}
              <span className="nav-label">{item.label}</span>
            </button>
          ))}
        </div>
      </nav>

      <MenuDrawer isOpen={isMenuOpen} onClose={() => setIsMenuOpen(false)} />

      <SecondaryPasswordModal
        isOpen={showPasswordModal}
        onClose={() => setShowPasswordModal(false)}
        onConfirm={handlePasswordConfirm}
        accountNumber="40123419-010(종합매매)"
      />
    </>
  );
};

export default BottomNavigation;
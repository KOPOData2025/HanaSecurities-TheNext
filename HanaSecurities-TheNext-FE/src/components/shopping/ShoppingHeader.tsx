import React from 'react';
import { Menu, ChevronLeft } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import './ShoppingHeader.css';

interface ShoppingHeaderProps {
  onMenuClick: () => void;
}

const ShoppingHeader: React.FC<ShoppingHeaderProps> = ({ onMenuClick }) => {
  const navigate = useNavigate();

  return (
    <header className="shopping-header">
      <div className="shopping-header-content">
        <button className="shopping-back-btn" onClick={() => navigate(-1)}>
          <ChevronLeft size={24} />
        </button>
        <h1 className="shopping-header-title">쇼핑 홈</h1>
        <button className="shopping-menu-btn" onClick={onMenuClick}>
          <Menu size={24} />
        </button>
      </div>
    </header>
  );
};

export default ShoppingHeader;
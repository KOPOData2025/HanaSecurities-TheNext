import React from 'react';
import './ProductMenu.css';

const ProductMenu: React.FC = () => {
  const menuItems = [
    { id: 1, icon: '🏦', label: 'ISA' },
    { id: 2, icon: '💼', label: '퇴직연금' },
    { id: 3, icon: '❤️', label: '개인연금' },
    { id: 4, icon: '💰', label: '채권' },
    { id: 5, icon: '📊', label: 'CMA' },
    { id: 6, icon: '🔄', label: 'RP' }
  ];

  return (
    <div className="product-menu-container">
      <div className="product-menu-grid">
        {menuItems.map((item) => (
          <button key={item.id} className="product-menu-item">
            <div className="product-menu-icon">{item.icon}</div>
            <div className="product-menu-label">{item.label}</div>
          </button>
        ))}
      </div>
    </div>
  );
};

export default ProductMenu;
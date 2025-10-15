import React, { useState } from 'react';
import { Search, Bell, Menu } from 'lucide-react';
import MenuDrawer from '../menu/MenuDrawer';
import './Header.css';

interface HeaderProps {
  activeTab: string;
  setActiveTab: (tab: string) => void;
}

const Header: React.FC<HeaderProps> = ({ activeTab, setActiveTab }) => {
  const [isMenuOpen, setIsMenuOpen] = useState(false);
  const tabs = ['국내', '해외', '채권/ETP'];

  return (
    <>
      <header className="header">
        <div className="header-main">
          <div className="header-tabs">
            {tabs.map((tab) => (
              <button
                key={tab}
                className={`header-tab ${activeTab === tab ? 'active' : ''}`}
                onClick={() => setActiveTab(tab)}
              >
                {tab}
              </button>
            ))}
          </div>
          <div className="header-actions">
            <Bell size={24} className="header-icon" />
            <Menu size={24} className="header-icon" onClick={() => setIsMenuOpen(true)} style={{ cursor: 'pointer' }} />
          </div>
        </div>
      </header>
    <MenuDrawer isOpen={isMenuOpen} onClose={() => setIsMenuOpen(false)} />
    </>
  );
};

export default Header;
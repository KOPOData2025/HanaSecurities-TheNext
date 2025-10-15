import React, { useState } from 'react';
import { Menu } from 'lucide-react';
import MenuDrawer from '../menu/MenuDrawer';
import './PageHeader.css';

interface PageHeaderProps {
  title: string;
  leftAction?: React.ReactNode;
  rightAction?: React.ReactNode;
  showMenu?: boolean;
}

const PageHeader: React.FC<PageHeaderProps> = ({
  title,
  leftAction,
  rightAction,
  showMenu = true
}) => {
  const [isMenuOpen, setIsMenuOpen] = useState(false);

  return (
    <>
      <div className="page-header">
        <div className="page-header-left">
          {leftAction}
        </div>
        <h1 className="page-header-title">{title}</h1>
        <div className="page-header-right">
          {rightAction}
          {showMenu && (
            <Menu
              size={24}
              className="page-header-menu-icon"
              onClick={() => setIsMenuOpen(true)}
            />
          )}
        </div>
      </div>
      <MenuDrawer isOpen={isMenuOpen} onClose={() => setIsMenuOpen(false)} />
    </>
  );
};

export default PageHeader;
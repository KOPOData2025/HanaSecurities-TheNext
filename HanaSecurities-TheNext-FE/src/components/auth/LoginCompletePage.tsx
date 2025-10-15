import React, { useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { CheckCircle } from 'lucide-react';
import './LoginCompletePage.css';

const LoginCompletePage: React.FC = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const userName = location.state?.userName || '회원';

  useEffect(() => {
    
    const timer = setTimeout(() => {
      navigate('/');
    }, 2000);

    return () => clearTimeout(timer);
  }, [navigate]);

  return (
    <div className="login-complete-page">
      <div className="login-complete-content">
        <div className="login-complete-icon">
          <CheckCircle size={80} color="#00857D" />
        </div>

        <h1 className="login-complete-title">로그인 성공!</h1>
        <p className="login-complete-message">
          {userName}님, 환영합니다.
        </p>
        <p className="login-complete-sub">
          잠시 후 홈 화면으로 이동합니다...
        </p>
      </div>
    </div>
  );
};

export default LoginCompletePage;

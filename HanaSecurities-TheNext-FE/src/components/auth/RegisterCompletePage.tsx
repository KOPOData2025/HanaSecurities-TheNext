import React from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { CheckCircle } from 'lucide-react';
import './RegisterCompletePage.css';

const RegisterCompletePage: React.FC = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const userName = location.state?.userName || '회원';

  return (
    <div className="register-complete-page">
      <div className="register-complete-content">
        <div className="register-complete-icon">
          <CheckCircle size={80} color="#00857D" />
        </div>

        <h1 className="register-complete-title">회원가입 완료!</h1>
        <p className="register-complete-message">
          {userName}님, 하나증권에 오신 것을 환영합니다.
        </p>
        <p className="register-complete-sub">
          이제 지문 인증으로 간편하게 로그인하실 수 있습니다.
        </p>

        <div className="register-complete-actions">
          <button
            className="register-complete-btn-primary"
            onClick={() => navigate('/login')}
          >
            로그인하기
          </button>
          <button
            className="register-complete-btn-secondary"
            onClick={() => navigate('/')}
          >
            홈으로 가기
          </button>
        </div>
      </div>
    </div>
  );
};

export default RegisterCompletePage;

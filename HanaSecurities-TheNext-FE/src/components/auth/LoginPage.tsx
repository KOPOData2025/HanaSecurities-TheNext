import React, { useState } from 'react';
import { ArrowLeft, Fingerprint } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../contexts/AuthContext';
import { loginWithFingerprint } from '../../services/webauthnService';
import PageHeader from '../common/PageHeader';
import './LoginPage.css';

const LoginPage: React.FC = () => {
  const navigate = useNavigate();
  const { login } = useAuth();
  const [mobileNo, setMobileNo] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string>('');

  const handleLogin = async () => {
    setError('');

    if (!mobileNo.match(/^01[0-9]{8,9}$/)) {
      setError('올바른 휴대폰 번호를 입력해주세요.');
      return;
    }

    setIsLoading(true);

    try {
      
      const response = await loginWithFingerprint(mobileNo);

      if (response.success) {
        
        login(
          {
            userId: response.userId,
            userName: response.userName,
            mobileNo: mobileNo,
            email: '', 
          },
          response.accessToken
        );

        
        navigate('/login-complete', { state: { userName: response.userName } });
      } else {
        setError(response.message || '로그인에 실패했습니다.');
      }
    } catch (err: any) {
      console.error('로그인 실패:', err);
      setError(err.message || '로그인에 실패했습니다.');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="login-page">
      <PageHeader
        title="로그인"
        leftAction={
          <button className="back-btn" onClick={() => navigate(-1)}>
            <ArrowLeft size={24} />
          </button>
        }
      />

      <div className="login-content">
        <div className="login-title-section">
          <h1 className="login-main-title">로그인</h1>
          <p className="login-subtitle">지문 인증으로 간편하게 로그인하세요</p>
        </div>

        <div className="login-form">
          <div className="login-form-group">
            <label>휴대폰 번호</label>
            <input
              type="tel"
              value={mobileNo}
              onChange={(e) => setMobileNo(e.target.value)}
              placeholder="01012345678"
              maxLength={11}
            />
          </div>

          {error && <div className="login-error">{error}</div>}

          <div className="login-fingerprint-section">
            <div className="login-fingerprint-icon">
              <Fingerprint size={80} color="#00857D" />
            </div>
            <p className="login-fingerprint-text">휴대폰 번호를 입력하고</p>
            <p className="login-fingerprint-text">로그인 버튼을 누르면</p>
            <p className="login-fingerprint-text">지문 인증 화면이 나타납니다</p>
          </div>

          <div className="login-register-link">
            <span>아직 회원이 아니신가요?</span>
            <button onClick={() => navigate('/register')}>회원가입</button>
          </div>
        </div>
      </div>

      <div className="login-bottom">
        <button
          className="login-submit-btn"
          onClick={handleLogin}
          disabled={isLoading}
        >
          {isLoading ? '인증 중...' : '지문 인증으로 로그인'}
        </button>
      </div>
    </div>
  );
};

export default LoginPage;

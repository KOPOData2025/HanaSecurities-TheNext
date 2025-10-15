import React, { useState } from "react";
import { ArrowLeft, Fingerprint } from "lucide-react";
import { useNavigate } from "react-router-dom";
import { registerWithFingerprint } from "../../services/webauthnService";
import type { RegisterStartRequest } from "../../services/webauthnService";
import PageHeader from "../common/PageHeader";
import "./RegisterPage.css";

const RegisterPage: React.FC = () => {
  const navigate = useNavigate();
  const [formData, setFormData] = useState<RegisterStartRequest>({
    userName: "",
    mobileNo: "",
    gender: "M",
    birth: "",
    email: "",
    address: "",
    secondaryPassword: "",
  });
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string>("");

  const handleInputChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>
  ) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const validateForm = (): boolean => {
    if (!formData.userName.trim()) {
      setError("이름을 입력해주세요.");
      return false;
    }
    if (!formData.mobileNo.match(/^01[0-9]{8,9}$/)) {
      setError("올바른 휴대폰 번호를 입력해주세요.");
      return false;
    }
    if (!formData.birth.match(/^\d{4}-\d{2}-\d{2}$/)) {
      setError("생년월일을 YYYY-MM-DD 형식으로 입력해주세요.");
      return false;
    }
    if (!formData.email.match(/^[^\s@]+@[^\s@]+\.[^\s@]+$/)) {
      setError("올바른 이메일을 입력해주세요.");
      return false;
    }
    if (
      formData.secondaryPassword.length !== 4 ||
      !formData.secondaryPassword.match(/^\d{4}$/)
    ) {
      setError("2차 비밀번호는 4자리 숫자여야 합니다.");
      return false;
    }
    return true;
  };

  const handleRegister = async () => {
    setError("");

    if (!validateForm()) {
      return;
    }

    setIsLoading(true);

    try {
      
      await registerWithFingerprint(formData);

      
      navigate("/register-complete", { state: { userName: formData.userName } });
    } catch (err: any) {
      console.error("회원가입 실패:", err);
      setError(err.message || "회원가입에 실패했습니다.");
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="register-page">
      <PageHeader
        title="회원가입"
        leftAction={
          <button className="back-btn" onClick={() => navigate(-1)}>
            <ArrowLeft size={24} />
          </button>
        }
      />

      <div className="register-content">
        <div className="register-title-section">
          <h1 className="register-main-title">회원가입</h1>
          <p className="register-subtitle">지문 인증으로 간편하게 가입하세요</p>
        </div>

        <div className="register-form">
          <div className="register-form-group">
            <label>이름</label>
            <input
              type="text"
              name="userName"
              value={formData.userName}
              onChange={handleInputChange}
              placeholder="이름을 입력하세요"
            />
          </div>

          <div className="register-form-group">
            <label>휴대폰 번호</label>
            <input
              type="tel"
              name="mobileNo"
              value={formData.mobileNo}
              onChange={handleInputChange}
              placeholder="01012345678"
              maxLength={11}
            />
          </div>

          <div className="register-form-group">
            <label>성별</label>
            <select
              name="gender"
              value={formData.gender}
              onChange={handleInputChange}
            >
              <option value="M">남성</option>
              <option value="F">여성</option>
            </select>
          </div>

          <div className="register-form-group">
            <label>생년월일</label>
            <input
              type="date"
              name="birth"
              value={formData.birth}
              onChange={handleInputChange}
            />
          </div>

          <div className="register-form-group">
            <label>이메일</label>
            <input
              type="email"
              name="email"
              value={formData.email}
              onChange={handleInputChange}
              placeholder="example@email.com"
            />
          </div>

          <div className="register-form-group">
            <label>주소</label>
            <input
              type="text"
              name="address"
              value={formData.address}
              onChange={handleInputChange}
              placeholder="주소를 입력하세요"
            />
          </div>

          <div className="register-form-group">
            <label>2차 비밀번호 (4자리)</label>
            <input
              type="password"
              name="secondaryPassword"
              value={formData.secondaryPassword}
              onChange={handleInputChange}
              placeholder="0000"
              maxLength={4}
            />
          </div>

          {error && <div className="register-error">{error}</div>}

          <div className="register-fingerprint-notice">
            <Fingerprint size={48} color="#00857D" />
            <p>가입 버튼을 누르면 지문 등록 화면이 나타납니다.</p>
            <p className="register-notice-sub">
              지문을 등록하면 비밀번호 없이 간편하게 로그인할 수 있습니다.
            </p>
          </div>
        </div>
      </div>

      <div className="register-bottom">
        <button
          className="register-submit-btn"
          onClick={handleRegister}
          disabled={isLoading}
        >
          {isLoading ? "처리 중..." : "지문 등록하고 가입하기"}
        </button>
      </div>
    </div>
  );
};

export default RegisterPage;

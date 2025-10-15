import React, { useState, useEffect } from 'react';
import styles from './SecondaryPasswordModal.module.css';

interface SecondaryPasswordModalProps {
  isOpen: boolean;
  onClose: () => void;
  onConfirm: (password: string) => void;
  accountNumber: string;
}

const SecondaryPasswordModal: React.FC<SecondaryPasswordModalProps> = ({
  isOpen,
  onClose,
  onConfirm,
  accountNumber
}) => {
  const [password, setPassword] = useState('');
  const [autoSave, setAutoSave] = useState(false);

  useEffect(() => {
    if (isOpen) {
      setPassword('');
    }
  }, [isOpen]);

  if (!isOpen) return null;

  const handleNumberClick = (num: number) => {
    if (password.length < 4) {
      setPassword(prev => prev + num);
    }
  };

  const handleDelete = () => {
    setPassword(prev => prev.slice(0, -1));
  };

  const handleConfirm = () => {
    if (password.length === 4) {
      onConfirm(password);
    }
  };

  const renderPasswordDots = () => {
    return (
      <div className={styles.passwordDots}>
        {[0, 1, 2, 3].map((index) => (
          <div
            key={index}
            className={`${styles.dot} ${password.length > index ? styles.filled : ''}`}
          />
        ))}
      </div>
    );
  };

  return (
    <div className={styles.modalOverlay} onClick={onClose}>
      <div className={styles.modal} onClick={(e) => e.stopPropagation()}>
        {/* Title */}
        <div className={styles.modalHeader}>
          <h2 className={styles.modalTitle}>계좌 비밀번호 입력</h2>
        </div>

        {/* Account Number */}
        <div className={styles.accountSection}>
          <div className={styles.accountNumber}>{accountNumber}</div>
        </div>

        {/* Password Dots */}
        {renderPasswordDots()}

        {/* Auto Save Toggle */}
        <div className={styles.autoSaveSection}>
          <span className={styles.autoSaveLabel}>앱 종료 시까지 자동저장</span>
          <label className={styles.toggleSwitch}>
            <input
              type="checkbox"
              checked={autoSave}
              onChange={(e) => setAutoSave(e.target.checked)}
            />
            <span className={styles.toggleSlider}></span>
          </label>
        </div>

        {/* Number Keypad */}
        <div className={styles.keypad}>
          <div className={styles.keypadRow}>
            <button className={`${styles.keyButton} ${styles.dummy}`} disabled></button>
            <button className={`${styles.keyButton} ${styles.dummy}`} disabled></button>
            <button className={styles.keyButton} onClick={() => handleNumberClick(1)}>
              1
            </button>
            <button className={styles.keyButton} onClick={() => handleNumberClick(2)}>
              2
            </button>
          </div>
          <div className={styles.keypadRow}>
            <button className={styles.keyButton} onClick={() => handleNumberClick(3)}>
              3
            </button>
            <button className={styles.keyButton} onClick={() => handleNumberClick(4)}>
              4
            </button>
            <button className={styles.keyButton} onClick={() => handleNumberClick(5)}>
              5
            </button>
            <button className={styles.keyButton} onClick={() => handleNumberClick(6)}>
              6
            </button>
          </div>
          <div className={styles.keypadRow}>
            <button className={styles.keyButton} onClick={() => handleNumberClick(7)}>
              7
            </button>
            <button className={styles.keyButton} onClick={() => handleNumberClick(8)}>
              8
            </button>
            <button className={styles.keyButton} onClick={() => handleNumberClick(9)}>
              9
            </button>
            <button className={styles.keyButton} onClick={() => handleNumberClick(0)}>
              0
            </button>
          </div>
        </div>

        {/* Action Buttons */}
        <div className={styles.actionButtons}>
          <button className={styles.deleteButton} onClick={handleDelete}>
            <img src="/etc/backArrow.png" alt="삭제" className={styles.deleteIcon} />
          </button>
          <button
            className={styles.confirmButton}
            onClick={handleConfirm}
            disabled={password.length !== 4}
          >
            입력완료
          </button>
        </div>
      </div>
    </div>
  );
};

export default SecondaryPasswordModal;
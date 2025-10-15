import React, { useState } from 'react';
import { Info } from 'lucide-react';
import styles from './OrderConfirmModal.module.css';

interface OrderConfirmModalProps {
  isOpen: boolean;
  onClose: () => void;
  onConfirm: () => void;
  orderData: {
    stockName: string;
    stockCode: string;
    accountNumber: string;
    orderType: string; 
    market: string; 
    orderCategory: string; 
    quantity: number;
    price: number;
    totalAmount: number;
  };
}

const OrderConfirmModal: React.FC<OrderConfirmModalProps> = ({
  isOpen,
  onClose,
  onConfirm,
  orderData
}) => {
  const [skipConfirmation, setSkipConfirmation] = useState(false);

  if (!isOpen) return null;

  const formatPrice = (price: number) => {
    return price.toLocaleString('ko-KR');
  };

  
  const isBuy = orderData.orderType.includes('매수');
  const orderTypeText = isBuy ? '매수' : '매도';

  return (
    <div className={styles.orderConfirmModalOverlay} onClick={onClose}>
      <div className={styles.orderConfirmModal} onClick={(e) => e.stopPropagation()}>
        {/* Header */}
        <div className={styles.modalHeader}>
          <h2 className={styles.modalTitle}>
            <span className={isBuy ? styles.titleRed : styles.titleBlue}>{orderData.orderType}</span> 주문 확인
          </h2>
        </div>

        {/* Stock Info */}
        <div className={styles.stockInfoSection}>
          <div className={styles.stockName}>
            {orderData.stockName} ({orderData.stockCode})
          </div>
        </div>

        {/* Order Details */}
        <div className={styles.orderDetails}>
          <div className={styles.detailRow}>
            <span className={styles.detailLabel}>계좌</span>
            <span className={styles.detailValue}>{orderData.accountNumber}</span>
          </div>

          <div className={styles.detailRow}>
            <span className={styles.detailLabel}>매매구분</span>
            <span className={`${styles.detailValue} ${isBuy ? styles.detailValueRed : styles.detailValueBlue}`}>{orderData.orderType}</span>
          </div>

          <div className={styles.detailRow}>
            <span className={styles.detailLabel}>거래소</span>
            <span className={styles.detailValue}>{orderData.market}</span>
          </div>

          <div className={styles.detailRow}>
            <span className={styles.detailLabel}>주문유형</span>
            <span className={styles.detailValue}>{orderData.orderCategory}</span>
          </div>

          <div className={styles.detailRow}>
            <span className={styles.detailLabel}>수량</span>
            <span className={styles.detailValue}>{formatPrice(orderData.quantity)}주</span>
          </div>

          <div className={`${styles.detailRow} ${styles.detailRowPrice}`}>
            <span className={styles.detailLabel}>단가</span>
            <span className={`${styles.detailValue} ${isBuy ? styles.detailValueRed : styles.detailValueBlue}`}>{formatPrice(orderData.price)}원</span>
          </div>

          <div className={styles.detailDivider}></div>

          <div className={`${styles.detailRow} ${styles.totalRow} ${styles.detailRowNoBorder}`}>
            <span className={styles.detailLabel}>주문금액</span>
            <span className={`${styles.detailValue} ${styles.totalAmount}`}>{formatPrice(orderData.totalAmount)}원</span>
          </div>
        </div>

        {/* Action Buttons */}
        <div className={styles.modalActions}>
          <button className={styles.btnCancel} onClick={onClose}>
            취소
          </button>
          <button className={`${styles.btnConfirm} ${isBuy ? '' : styles.btnConfirmSell}`} onClick={onConfirm}>
            {orderTypeText}주문
          </button>
        </div>
      </div>
    </div>
  );
};

export default OrderConfirmModal;
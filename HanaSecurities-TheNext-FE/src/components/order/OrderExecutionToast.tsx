import React, { useEffect } from 'react';
import styles from './OrderExecutionToast.module.css';

interface OrderExecutionToastProps {
  isVisible: boolean;
  onClose: () => void;
  executionData: {
    orderType: string; 
    market: string; 
    stockName: string;
    accountNumber: string;
    orderedQuantity: number;
    executedQuantity: number;
    executionPrice: number;
  };
}

const OrderExecutionToast: React.FC<OrderExecutionToastProps> = ({
  isVisible,
  onClose,
  executionData
}) => {
  useEffect(() => {
    if (isVisible) {
      const timer = setTimeout(() => {
        onClose();
      }, 3000); 

      return () => clearTimeout(timer);
    }
  }, [isVisible, onClose]);

  if (!isVisible) return null;

  const formatPrice = (price: number) => {
    return price.toLocaleString('ko-KR');
  };

  
  const isSell = executionData.orderType.includes('매도');

  return (
    <div className={styles.toastContainer}>
      <div className={styles.toast}>
        <div className={styles.toastHeader}>
          <span className={`${styles.orderTypeLabel} ${isSell ? styles.orderTypeSell : ''}`}>
            {executionData.orderType} ({executionData.market})
          </span>
        </div>
        <div className={styles.toastContent}>
          <div className={styles.stockInfo}>
            [{executionData.stockName}] {executionData.accountNumber}
          </div>
          <div className={styles.executionInfo}>
            주문 {executionData.orderedQuantity}주 | 체결 {executionData.executedQuantity}주 | 체결가 {formatPrice(executionData.executionPrice)}
          </div>
        </div>
      </div>
    </div>
  );
};

export default OrderExecutionToast;
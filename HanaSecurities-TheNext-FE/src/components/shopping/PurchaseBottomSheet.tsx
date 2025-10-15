import React, { useState } from 'react';
import { Home, CreditCard, Coins } from 'lucide-react';
import './PurchaseBottomSheet.css';

interface PurchaseBottomSheetProps {
  isOpen: boolean;
  onClose: () => void;
  product: {
    title: string;
    price: string;
  };
}

const PurchaseBottomSheet: React.FC<PurchaseBottomSheetProps> = ({
  isOpen,
  onClose,
  product
}) => {
  const [quantity, setQuantity] = useState(1);

  if (!isOpen) return null;

  const priceNum = parseInt(product.price.replace(/,/g, ''));
  const totalPrice = (priceNum * quantity).toLocaleString();

  const handlePayment = () => {
    onClose();
    window.location.href = '/shopping/payment';
  };

  const increaseQuantity = () => {
    setQuantity(prev => prev + 1);
  };

  const decreaseQuantity = () => {
    if (quantity > 1) {
      setQuantity(prev => prev - 1);
    }
  };

  return (
    <>
      <div className="purchase-sheet-overlay" onClick={onClose} />
      <div className={`purchase-sheet-container ${isOpen ? 'open' : ''}`}>
        <div className="purchase-sheet-handle-wrapper">
          <div className="purchase-sheet-handle"></div>
        </div>
        <div className="purchase-sheet-content">
          {/* Product Info */}
          <div className="purchase-sheet-product">
            <div className="purchase-sheet-title">{product.title}</div>
            <div className="purchase-sheet-price-row">
              <span className="purchase-sheet-price">{totalPrice}원</span>
              <span className="purchase-sheet-delivery">무료배송</span>
              <div className="purchase-sheet-quantity">
                <button onClick={decreaseQuantity} disabled={quantity === 1}>-</button>
                <span>{quantity}</span>
                <button onClick={increaseQuantity}>+</button>
              </div>
            </div>
          </div>

          {/* Delivery Info */}
          <div className="purchase-sheet-info">
            <div className="purchase-sheet-info-item">
              <span className="purchase-sheet-icon location">
                <Home size={20} color="#666" />
              </span>
              <div className="purchase-sheet-info-text">
                <div className="purchase-sheet-info-label">
                  <strong>집</strong> · 서울특별시 금천구 서부샛길 528
                </div>
                <div className="purchase-sheet-info-sub">(가산동) 640호</div>
              </div>
              <button className="purchase-sheet-change">변경</button>
            </div>

            <div className="purchase-sheet-info-item">
              <span className="purchase-sheet-icon payment">
                <CreditCard size={20} color="#666" />
              </span>
              <div className="purchase-sheet-info-text">
                <div className="purchase-sheet-info-label">
                  <strong>결제 수단</strong> · 결제 화면에서 선택할 수 있어요
                </div>
              </div>
            </div>

            <div className="purchase-sheet-info-item">
              <span className="purchase-sheet-icon money">
                <Coins size={20} color="#666" />
              </span>
              <div className="purchase-sheet-info-text">
                <div className="purchase-sheet-info-label">
                  <strong>하나머니</strong> · 결제 화면에서 사용할 수 있어요
                </div>
              </div>
            </div>
          </div>

          {/* Payment Button */}
          <button className="purchase-sheet-button" onClick={handlePayment}>
            결제하러 가기
          </button>

          <div className="purchase-sheet-footer">
            주문 내용을 확인했습니다
          </div>
        </div>
      </div>
    </>
  );
};

export default PurchaseBottomSheet;
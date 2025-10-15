import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { ArrowLeft } from 'lucide-react';
import PageHeader from '../common/PageHeader';
import PurchaseBottomSheet from './PurchaseBottomSheet';
import { getProductDetail } from '../../services/productService';
import './ProductDetail.css';

interface ProductDetailData {
  id: number;
  title: string;
  price: string;
  originalPrice: string;
  discount: string;
  rating: number;
  reviews: number;
  image: string;
  description?: string;
  delivery?: string;
  seller?: string;
  selectedOption?: string;
  quantity?: number;
}

const ProductDetail: React.FC = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [product, setProduct] = useState<ProductDetailData | null>(null);
  const [loading, setLoading] = useState(true);
  const [isBottomSheetOpen, setIsBottomSheetOpen] = useState(false);

  
  const getProductOption = (productId: number): { option: string; quantity: number } => {
    switch (productId) {
      case 1: 
        return { option: '실버', quantity: 1 };
      case 2: 
        return { option: '화이트', quantity: 1 };
      case 3: 
        return { option: '실버', quantity: 1 };
      case 4: 
        return { option: '그린', quantity: 1 };
      default:
        return { option: '기본', quantity: 1 };
    }
  };

  
  useEffect(() => {
    const fetchProduct = async () => {
      if (!id) return;

      try {
        setLoading(true);
        const productId = parseInt(id);
        const response = await getProductDetail(productId);

        if (response.success && response.product) {
          const apiProduct = response.product;
          const { option, quantity } = getProductOption(productId);

          const formattedProduct: ProductDetailData = {
            id: apiProduct.productId,
            title: apiProduct.productName,
            price: apiProduct.price.toLocaleString(),
            originalPrice: apiProduct.originalPrice.toLocaleString(),
            discount: `${Math.round(apiProduct.discountRate)}%`,
            rating: Number(apiProduct.rating),
            reviews: apiProduct.reviewCount,
            image: apiProduct.productImageUrl,
            description: '프리미엄 갤럭시 액세서리',
            delivery: '베스트 판매자', 
            seller: apiProduct.seller,
            selectedOption: option, 
            quantity: quantity 
          };

          setProduct(formattedProduct);
        }
      } catch (error) {
        console.error('상품 상세 조회 실패:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchProduct();
  }, [id]);

  const handleAddToCart = () => {
    
    alert('장바구니에 추가되었습니다.');
  };

  const handlePurchase = () => {
    setIsBottomSheetOpen(true);
  };

  if (loading) {
    return (
      <div className="shop-detail-container">
        <PageHeader
          title="쇼핑"
          leftAction={
            <button className="back-btn" onClick={() => navigate(-1)}>
              <ArrowLeft size={24} />
            </button>
          }
        />
        <div style={{ textAlign: 'center', padding: '40px' }}>
          로딩 중...
        </div>
      </div>
    );
  }

  if (!product) {
    return (
      <div className="shop-detail-container">
        <PageHeader
          title="쇼핑"
          leftAction={
            <button className="back-btn" onClick={() => navigate(-1)}>
              <ArrowLeft size={24} />
            </button>
          }
        />
        <div style={{ textAlign: 'center', padding: '40px' }}>
          상품을 찾을 수 없습니다.
        </div>
      </div>
    );
  }

  return (
    <div className="shop-detail-container">
      <PageHeader
        title="쇼핑"
        leftAction={
          <button className="back-btn" onClick={() => navigate(-1)}>
            <ArrowLeft size={24} />
          </button>
        }
      />

      <div className="shop-detail-content">
        {/* Product Image Section */}
        <div className="shop-detail-image-section">
          <img src={product.image} alt={product.title} />
        </div>

        {/* Product Info Section */}
        <div className="shop-detail-info-section">
          <div className="shop-detail-seller-rating-line">
            <span className="shop-detail-seller-name">{product.seller} &gt;</span>
            <div className="shop-detail-rating-info">
              <span className="shop-detail-stars">
                {'★'.repeat(Math.floor(product.rating))}{'☆'.repeat(5 - Math.floor(product.rating))}
              </span>
              <span className="shop-detail-rating-text">({product.reviews})</span>
            </div>
          </div>

          <h1 className="shop-detail-title">{product.title}</h1>

          <div className="shop-detail-price-section">
            <span className="shop-detail-discount-rate">{product.discount}</span>
            <span className="shop-detail-original-price">{product.originalPrice}원</span>
          </div>

          <div className="shop-detail-current-price">{product.price}원</div>

          <div className="shop-detail-delivery-badge">
            {product.delivery}
          </div>
        </div>

        {/* Option Selection */}
        <div className="shop-detail-option-section">
          <div className="shop-detail-option-header">
            <span>선택한 옵션</span>
            <button className="shop-detail-option-link">옵션 선택 &gt;</button>
          </div>
          <div className="shop-detail-selected-option">
            <div className="shop-detail-option-item">
              <img src={product.image} alt="옵션" className="shop-detail-option-image" />
              <div className="shop-detail-option-details">
                <div className="shop-detail-option-label">선택한 옵션</div>
                <div className="shop-detail-option-quantity">
                  {product.selectedOption}, {product.quantity}개
                </div>
              </div>
            </div>
          </div>
        </div>

        {/* Additional space for scrolling */}
        <div className="shop-detail-description">
          <h3>상품 설명</h3>
          <p>{product.description}</p>
        </div>

        {/* Related Products or Recommendations */}
        <div className="shop-detail-related-section">
          <h3>추천 상품</h3>
          <div className="shop-detail-related-products">
            {/* Add related products here */}
          </div>
        </div>

        {/* Bottom padding for fixed buttons */}
        <div className="shop-detail-bottom-padding"></div>
      </div>

      {/* Fixed Bottom Buttons */}
      <div className="fixed-bottom-buttons">
        <button className="cart-button" onClick={handleAddToCart}>
          장바구니
        </button>
        <button className="purchase-button" onClick={handlePurchase}>
          구매하기
        </button>
      </div>

      {/* Purchase Bottom Sheet */}
      <PurchaseBottomSheet
        isOpen={isBottomSheetOpen}
        onClose={() => setIsBottomSheetOpen(false)}
        product={{
          title: product.title,
          price: product.price
        }}
      />
    </div>
  );
};

export default ProductDetail;
import React, { useState, useEffect, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import Footer from '../common/Footer';
import { getProductList } from '../../services/productService';
import './ShoppingHome.css';

interface ShoppingProduct {
  id: number;
  title: string;
  price: string;
  originalPrice?: string;
  discount?: string;
  rating?: number;
  reviews?: string;
  deliveryType?: string;
  badge?: string;
  image?: string;
}

const ShoppingHome: React.FC = () => {
  const [currentSlide, setCurrentSlide] = useState(0);
  const [isAutoPlaying, setIsAutoPlaying] = useState(true);
  const [activeCategory, setActiveCategory] = useState("쇼핑 홈");
  const [products, setProducts] = useState<ShoppingProduct[]>([]);
  const [loading, setLoading] = useState(true);
  const intervalRef = useRef<NodeJS.Timeout | null>(null);
  const navigate = useNavigate();

  const categories = ["쇼핑 홈", "특가", "식품", "더보기"];

  const ads = [
    {
      title: "뱅키스 체크 이벤트",
      subtitle: "체크 충족시 최대 3.3만원+5200 배픽",
      emoji: "🎁", 
      bgColor: "#fef3c7"
    },
    {
      title: "연금저축펀드 세액공제",
      subtitle: "최대 900만원까지 16.5% 세액공제",
      emoji: "💰", 
      bgColor: "#ede9fe"
    },
    {
      title: "ISA 계좌 특별혜택",
      subtitle: "비과세 한도 최대 1억원까지",
      emoji: "📈", 
      bgColor: "#fce7f3"
    }
  ];

  
  useEffect(() => {
    const fetchProducts = async () => {
      try {
        setLoading(true);
        const response = await getProductList();

        if (response.success) {
          
          const formattedProducts: ShoppingProduct[] = response.products.map((product) => ({
            id: product.productId,
            title: product.productName,
            price: `${product.price.toLocaleString()}원`,
            originalPrice: `${product.originalPrice.toLocaleString()}원`,
            discount: `${Math.round(product.discountRate)}%`,
            rating: Number(product.rating),
            reviews: product.reviewCount.toLocaleString(),
            deliveryType: '당일발송', 
            badge: `${Math.round(product.discountRate)}% 할인`, 
            image: product.productImageUrl
          }));

          setProducts(formattedProducts);
        }
      } catch (error) {
        console.error('상품 목록 조회 실패:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchProducts();
  }, []);

  const nextSlide = () => {
    setCurrentSlide((prev) => (prev + 1) % ads.length);
  };

  const goToSlide = (index: number) => {
    setCurrentSlide(index);
    setIsAutoPlaying(false);
    setTimeout(() => setIsAutoPlaying(true), 10000);
  };

  useEffect(() => {
    if (isAutoPlaying) {
      intervalRef.current = setInterval(() => {
        nextSlide();
      }, 5000);
    }

    return () => {
      if (intervalRef.current) {
        clearInterval(intervalRef.current);
      }
    };
  }, [isAutoPlaying, currentSlide]);

  return (
    <div className="shop-home-container">
      {/* Category Selector Tabs */}
      <div className="shop-home-category-tabs">
        {categories.map((category) => (
          <button
            key={category}
            className={`shop-home-category-tab ${activeCategory === category ? "active" : ""}`}
            onClick={() => setActiveCategory(category)}
          >
            {category}
          </button>
        ))}
      </div>

      {/* Ad Banner Carousel */}
      <div className="shop-home-banner-container">
        <div className="shop-home-banner-carousel">
          <div
            className="shop-home-banner-wrapper"
            style={{ transform: `translateX(-${currentSlide * 100}%)` }}
          >
            {ads.map((ad, index) => (
              <div
                key={index}
                className="shop-home-banner"
                style={{ background: ad.bgColor }}
              >
                <div className="shop-home-banner-content">
                  <div className="shop-home-banner-text">
                    <div className="shop-home-banner-title">{ad.title}</div>
                    <div className="shop-home-banner-subtitle">{ad.subtitle}</div>
                  </div>
                  <div className="shop-home-banner-emoji">
                    {ad.emoji === "🎁" ? <span className="tossface u-gift"></span> :
                     ad.emoji === "💰" ? <span className="tossface u-money-bag"></span> :
                     <span className="tossface u-chart-increasing"></span>}
                  </div>
                </div>
              </div>
            ))}
          </div>

          <div className="shop-home-carousel-dots">
            {ads.map((_, index) => (
              <button
                key={index}
                className={`shop-home-dot ${currentSlide === index ? 'active' : ''}`}
                onClick={() => goToSlide(index)}
              />
            ))}
          </div>
        </div>
      </div>

      {/* Product Grid */}
      <div className="shop-home-product-grid">
        {loading ? (
          <div style={{ textAlign: 'center', padding: '40px', gridColumn: '1 / -1' }}>
            로딩 중...
          </div>
        ) : products.length === 0 ? (
          <div style={{ textAlign: 'center', padding: '40px', gridColumn: '1 / -1' }}>
            상품이 없습니다.
          </div>
        ) : (
          products.map((product) => (
          <div
            key={product.id}
            className="shop-home-product-card"
            onClick={() => navigate(`/shopping/product/${product.id}`)}
            style={{ cursor: 'pointer' }}
          >
            {product.badge && (
              <div className="shop-home-product-badge">{product.badge}</div>
            )}
            <div className="shop-home-product-image">
              <img src={product.image || '/placeholder.jpg'} alt={product.title} />
            </div>
            <div className="shop-home-product-info">
              <h3 className="shop-home-product-title">{product.title}</h3>
              <div className="shop-home-price-container">
                <span className="shop-home-product-price">{product.price}</span>
                {product.originalPrice && (
                  <span className="shop-home-original-price">{product.originalPrice}</span>
                )}
              </div>
              {product.rating && (
                <div className="shop-home-product-meta">
                  <span className="shop-home-rating">
                    <span className="tossface u-star" style={{ fontSize: '14px', verticalAlign: 'middle', marginRight: '2px' }}></span>
                    {product.rating}
                  </span>
                  <span className="shop-home-reviews">({product.reviews})</span>
                  {product.deliveryType && (
                    <span className="shop-home-delivery-type">{product.deliveryType}</span>
                  )}
                </div>
              )}
            </div>
          </div>
          ))
        )}
      </div>
      <Footer />
    </div>
  );
};

export default ShoppingHome;
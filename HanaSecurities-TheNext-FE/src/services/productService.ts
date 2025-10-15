const API_BASE_URL = '/api';

export interface Product {
  productId: number;
  productName: string;
  productImageUrl: string;
  price: number;
  originalPrice: number;
  discountRate: number;
  seller: string;
  rating: number;
  reviewCount: number;
}

export interface ProductDetail extends Product {
  createdAt: string;
  updatedAt: string;
}

export interface ProductListResponse {
  success: boolean;
  message: string;
  products: Product[];
}

export interface ProductDetailResponse {
  success: boolean;
  message: string;
  product: ProductDetail;
}

/**
 * 상품 목록 조회
 */
export async function getProductList(): Promise<ProductListResponse> {
  const response = await fetch(`${API_BASE_URL}/products`, {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json',
    },
  });

  if (!response.ok) {
    throw new Error('상품 목록 조회 실패');
  }

  return response.json();
}

/**
 * 상품 상세 조회
 */
export async function getProductDetail(productId: number): Promise<ProductDetailResponse> {
  const response = await fetch(`${API_BASE_URL}/products/${productId}`, {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json',
    },
  });

  if (!response.ok) {
    throw new Error('상품 상세 조회 실패');
  }

  return response.json();
}

const BASE_URL = '/api';

export interface StockOrderRequest {
  pdno: string;       
  ordDvsn: string;    
  ordQty: string;     
  ordUnpr: string;    
}

export interface StockOrderResponse {
  orderNumber: string;    
  orderTime: string;      
  exchangeCode: string;   
  message: string;        
  success: boolean;       
}

export const orderApi = {
  /**
   * 국내 주식 현금 매수
   * @param request 주문 요청 데이터
   */
  buyStock: async (request: StockOrderRequest): Promise<StockOrderResponse> => {
    const response = await fetch(`${BASE_URL}/stock/order/buy`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(request),
    });

    if (!response.ok) {
      throw new Error(`Failed to buy stock: ${request.pdno}`);
    }

    return response.json();
  },

  /**
   * 국내 주식 현금 매도
   * @param request 주문 요청 데이터
   */
  sellStock: async (request: StockOrderRequest): Promise<StockOrderResponse> => {
    const response = await fetch(`${BASE_URL}/stock/order/sell`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(request),
    });

    if (!response.ok) {
      throw new Error(`Failed to sell stock: ${request.pdno}`);
    }

    return response.json();
  }
};

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';
const WS_BASE_URL = import.meta.env.VITE_WS_BASE_URL || 'ws://localhost:8080';

// 금현물 상품 코드
export const GOLD_PRODUCTS = {
  GOLD_1KG: 'M04020000',    // 금 99.99 1Kg
  GOLD_100G: 'M04020100',   // 미니금 99.99 100g
} as const;

// 금현물 체결 데이터 타입
export interface GoldTradeData {
  productCode: string;
  price: number;
  quantity: number;
  changeAmount: number;
  changeRate: number;
  volume: number;
  timestamp: string;
}

// 금현물 호가 데이터 타입 (10단 호가)
export interface GoldQuoteData {
  productCode: string;
  bidPrice1: number;
  bidQuantity1: number;
  bidPrice2: number;
  bidQuantity2: number;
  bidPrice3: number;
  bidQuantity3: number;
  bidPrice4: number;
  bidQuantity4: number;
  bidPrice5: number;
  bidQuantity5: number;
  bidPrice6: number;
  bidQuantity6: number;
  bidPrice7: number;
  bidQuantity7: number;
  bidPrice8: number;
  bidQuantity8: number;
  bidPrice9: number;
  bidQuantity9: number;
  bidPrice10: number;
  bidQuantity10: number;
  askPrice1: number;
  askQuantity1: number;
  askPrice2: number;
  askQuantity2: number;
  askPrice3: number;
  askQuantity3: number;
  askPrice4: number;
  askQuantity4: number;
  askPrice5: number;
  askQuantity5: number;
  askPrice6: number;
  askQuantity6: number;
  askPrice7: number;
  askQuantity7: number;
  askPrice8: number;
  askQuantity8: number;
  askPrice9: number;
  askQuantity9: number;
  askPrice10: number;
  askQuantity10: number;
  timestamp: string;
}

// 금현물 현재가 응답
export interface GoldCurrentPriceResponse {
  productCode: string;
  currentPrice: number;
  changeAmount: number;
  changeRate: number;
  highPrice: number;
  lowPrice: number;
  openPrice: number;
  volume: number;
  timestamp: string;
}

// 금현물 차트 데이터
export interface GoldCandleData {
  timestamp: string;
  open: number;
  high: number;
  low: number;
  close: number;
  volume: number;
}

export interface GoldChartResponse {
  interval: string;
  data: GoldCandleData[];
}

// 금현물 주문 요청
export interface GoldOrderRequest {
  accountNumber: string;
  productCode: string;
  quantity: number;
  price: number;
  orderType: string;
}

// 금현물 주문 응답
export interface GoldOrderResponse {
  success: boolean;
  orderNumber: string;
  accountNumber: string;
  productCode: string;
  productName: string;
  orderSide: string;
  orderedQuantity: number;
  executedQuantity: number;
  orderPrice: number;
  executionPrice: number;
  totalAmount: number;
  message: string;
  errorCode?: string;
}

/**
 * 금현물 현재가 조회
 */
export const getGoldCurrentPrice = async (productCode: string = GOLD_PRODUCTS.GOLD_1KG): Promise<GoldCurrentPriceResponse> => {
  const params = new URLSearchParams({
    productCode: productCode
  });
  const response = await fetch(`${API_BASE_URL}/api/gold/current-price?${params}`);
  return response.json();
};

/**
 * 금현물 호가 조회
 */
export const getGoldOrderBook = async (productCode: string = GOLD_PRODUCTS.GOLD_1KG): Promise<GoldQuoteData> => {
  const params = new URLSearchParams({
    productCode: productCode
  });
  const response = await fetch(`${API_BASE_URL}/api/gold/orderbook?${params}`);
  return response.json();
};

/**
 * 금현물 분봉 차트 조회
 */
export const getGoldMinuteChart = async (
  productCode: string = GOLD_PRODUCTS.GOLD_1KG,
  interval: number = 5,
  count: number = 100
): Promise<GoldChartResponse> => {
  const params = new URLSearchParams({
    productCode: productCode,
    interval: interval.toString(),
    count: count.toString()
  });
  const response = await fetch(`${API_BASE_URL}/api/gold/chart/minute?${params}`);
  return response.json();
};

/**
 * 금현물 일/주/월봉 차트 조회
 */
export const getGoldPeriodChart = async (
  productCode: string = GOLD_PRODUCTS.GOLD_1KG,
  period: 'day' | 'week' | 'month',
  count: number = 100
): Promise<GoldChartResponse> => {
  const params = new URLSearchParams({
    productCode: productCode,
    count: count.toString()
  });
  const response = await fetch(`${API_BASE_URL}/api/gold/chart/${period}?${params}`);
  return response.json();
};

/**
 * 금현물 매수 주문
 */
export const buyGoldOrder = async (orderRequest: GoldOrderRequest): Promise<GoldOrderResponse> => {
  const response = await fetch(`${API_BASE_URL}/api/gold/order/buy`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(orderRequest),
  });
  return response.json();
};

/**
 * 금현물 매도 주문
 */
export const sellGoldOrder = async (orderRequest: GoldOrderRequest): Promise<GoldOrderResponse> => {
  const response = await fetch(`${API_BASE_URL}/api/gold/order/sell`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(orderRequest),
  });
  return response.json();
};

/** 금현물 체결 데이터 콜백 함수 타입 */
type GoldTradeCallback = (data: GoldTradeData) => void;

/**
 * 금현물 실시간 체결 WebSocket 서비스
 */
class GoldTradeWebSocketService {
  private ws: WebSocket | null = null;
  private callback: GoldTradeCallback | null = null;
  private reconnectAttempts = 0;
  private maxReconnectAttempts = 5;
  private reconnectDelay = 3000;
  private readonly WS_URL = `${WS_BASE_URL}/ws/gold-trade`;

  /**
   * WebSocket 연결
   * @returns 연결 완료 Promise
   */
  connect(): Promise<void> {
    return new Promise((resolve, reject) => {
      // 이미 연결되어 있으면 즉시 완료
      if (this.ws && this.ws.readyState === WebSocket.OPEN) {
        resolve();
        return;
      }

      try {
        this.ws = new WebSocket(this.WS_URL);

        this.ws.onopen = () => {
          this.reconnectAttempts = 0;
          resolve();
        };

        this.ws.onmessage = (event) => {
          try {
            const data: GoldTradeData = JSON.parse(event.data);
            if (this.callback) {
              this.callback(data);
            }
          } catch (error) {
            console.error('[금현물 체결 WebSocket] 메시지 파싱 오류:', error);
          }
        };

        this.ws.onerror = (error) => {
          reject(error);
        };

        this.ws.onclose = () => {
          this.handleReconnect();
        };

      } catch (error) {
        reject(error);
      }
    });
  }

  /**
   * 재연결 처리 (최대 5회 시도, 3초 간격)
   */
  private handleReconnect() {
    if (this.reconnectAttempts < this.maxReconnectAttempts) {
      this.reconnectAttempts++;

      setTimeout(() => {
        this.connect().catch(() => {});
      }, this.reconnectDelay);
    }
  }

  /**
   * 실시간 체결 데이터 수신 시작
   * @param callback 체결 데이터 수신 콜백
   */
  subscribe(callback: GoldTradeCallback): void {
    this.callback = callback;

    if (!this.ws || this.ws.readyState !== WebSocket.OPEN) {
      this.connect().catch(() => {});
    }
  }

  /**
   * 구독 해제 (연결은 유지)
   */
  unsubscribe(): void {
    this.callback = null;
  }

  /**
   * WebSocket 연결 종료
   */
  disconnect(): void {
    this.reconnectAttempts = this.maxReconnectAttempts; // 재연결 방지
    if (this.ws) {
      this.ws.close();
      this.ws = null;
      this.callback = null;
    }
  }

  /**
   * WebSocket 연결 상태 확인
   * @returns 연결 상태 (true: 연결됨, false: 연결 안 됨)
   */
  isConnected(): boolean {
    return this.ws !== null && this.ws.readyState === WebSocket.OPEN;
  }
}

// 싱글톤 인스턴스 export
export const goldTradeWebSocket = new GoldTradeWebSocketService();

/** 금현물 호가 데이터 콜백 함수 타입 */
type GoldQuoteCallback = (data: GoldQuoteData) => void;

/**
 * 금현물 실시간 호가 WebSocket 서비스
 */
class GoldQuoteWebSocketService {
  private ws: WebSocket | null = null;
  private callback: GoldQuoteCallback | null = null;
  private reconnectAttempts = 0;
  private maxReconnectAttempts = 5;
  private reconnectDelay = 3000;
  private readonly WS_URL = `${WS_BASE_URL}/ws/gold-quote`;

  /**
   * WebSocket 연결
   * @returns 연결 완료 Promise
   */
  connect(): Promise<void> {
    return new Promise((resolve, reject) => {
      // 이미 연결되어 있으면 즉시 완료
      if (this.ws && this.ws.readyState === WebSocket.OPEN) {
        resolve();
        return;
      }

      try {
        this.ws = new WebSocket(this.WS_URL);

        this.ws.onopen = () => {
          this.reconnectAttempts = 0;
          resolve();
        };

        this.ws.onmessage = (event) => {
          try {
            const data: GoldQuoteData = JSON.parse(event.data);
            if (this.callback) {
              this.callback(data);
            }
          } catch (error) {
            console.error('[금현물 호가 WebSocket] 메시지 파싱 오류:', error);
          }
        };

        this.ws.onerror = (error) => {
          reject(error);
        };

        this.ws.onclose = () => {
          this.handleReconnect();
        };

      } catch (error) {
        reject(error);
      }
    });
  }

  /**
   * 재연결 처리 (최대 5회 시도, 3초 간격)
   */
  private handleReconnect() {
    if (this.reconnectAttempts < this.maxReconnectAttempts) {
      this.reconnectAttempts++;

      setTimeout(() => {
        this.connect().catch(() => {});
      }, this.reconnectDelay);
    }
  }

  /**
   * 실시간 호가 데이터 수신 시작
   * @param callback 호가 데이터 수신 콜백
   */
  subscribe(callback: GoldQuoteCallback): void {
    this.callback = callback;

    if (!this.ws || this.ws.readyState !== WebSocket.OPEN) {
      this.connect().catch(() => {});
    }
  }

  /**
   * 구독 해제 (연결은 유지)
   */
  unsubscribe(): void {
    this.callback = null;
  }

  /**
   * WebSocket 연결 종료
   */
  disconnect(): void {
    this.reconnectAttempts = this.maxReconnectAttempts; // 재연결 방지
    if (this.ws) {
      this.ws.close();
      this.ws = null;
      this.callback = null;
    }
  }

  /**
   * WebSocket 연결 상태 확인
   * @returns 연결 상태 (true: 연결됨, false: 연결 안 됨)
   */
  isConnected(): boolean {
    return this.ws !== null && this.ws.readyState === WebSocket.OPEN;
  }
}

// 싱글톤 인스턴스 export
export const goldQuoteWebSocket = new GoldQuoteWebSocketService();

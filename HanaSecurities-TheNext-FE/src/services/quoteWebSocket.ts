/**
 * 실시간 호가 데이터 인터페이스
 */
export interface RealtimeQuoteData {
  /** 메시지 타입 */
  type: string;
  /** 호가 데이터 */
  data?: {
    /** 종목 코드 */
    stockCode: string;
    /** 타임스탬프 */
    timestamp: string;
    /** 매도호가 목록 (10호가) */
    askPrices: number[];
    /** 매수호가 목록 (10호가) */
    bidPrices: number[];
    /** 매도호가 잔량 목록 */
    askVolumes: number[];
    /** 매수호가 잔량 목록 */
    bidVolumes: number[];
    /** 총 매도 잔량 */
    totalAskVolume: number;
    /** 총 매수 잔량 */
    totalBidVolume: number;
  };
  /** 응답 상태 */
  status?: string;
  /** 종목 코드 */
  stockCode?: string;
  /** 메시지 */
  message?: string;
}

/** 호가 데이터 콜백 함수 타입 */
type QuoteCallback = (data: RealtimeQuoteData) => void;

/**
 * 실시간 호가 WebSocket 서비스
 */
class QuoteWebSocketService {
  private ws: WebSocket | null = null;
  private callbacks: Map<string, QuoteCallback> = new Map();
  private reconnectAttempts = 0;
  private maxReconnectAttempts = 5;
  private reconnectDelay = 3000;
  private readonly WS_URL = 'ws://localhost:8080/ws/quote';

  /**
   * WebSocket 연결
   * @returns 연결 완료 Promise
   */
  connect(): Promise<void> {
    return new Promise((resolve, reject) => {
      try {
        this.ws = new WebSocket(this.WS_URL);

        this.ws.onopen = () => {
          this.reconnectAttempts = 0;
          resolve();
        };

        this.ws.onmessage = (event) => {
          try {
            const data: RealtimeQuoteData = JSON.parse(event.data);

            
            if (data.type === 'subscribe' || data.type === 'unsubscribe') {
              return;
            }

            
            if (data.type === 'quote' && data.data) {
              const stockCode = data.data.stockCode;
              const callback = this.callbacks.get(stockCode);
              if (callback) {
                callback(data);
              }
            }
          } catch (error) {
            
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
   * 종목 실시간 호가 구독
   * @param stockCode 종목 코드
   * @param callback 호가 데이터 수신 콜백
   */
  subscribe(stockCode: string, callback: QuoteCallback): void {
    this.callbacks.set(stockCode, callback);

    if (this.ws && this.ws.readyState === WebSocket.OPEN) {
      const message = {
        action: 'subscribe',
        stockCode: stockCode
      };
      this.ws.send(JSON.stringify(message));
    } else {
      
      this.connect().then(() => {
        if (this.ws && this.ws.readyState === WebSocket.OPEN) {
          const message = {
            action: 'subscribe',
            stockCode: stockCode
          };
          this.ws.send(JSON.stringify(message));
        }
      });
    }
  }

  /**
   * 종목 실시간 호가 구독 해제
   * @param stockCode 종목 코드
   */
  unsubscribe(stockCode: string): void {
    this.callbacks.delete(stockCode);

    if (this.ws && this.ws.readyState === WebSocket.OPEN) {
      const message = {
        action: 'unsubscribe',
        stockCode: stockCode
      };
      this.ws.send(JSON.stringify(message));
    }
  }

  /**
   * WebSocket 연결 종료 및 모든 구독 취소
   */
  disconnect(): void {
    if (this.ws) {
      this.ws.close();
      this.ws = null;
      this.callbacks.clear();
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


export const quoteWebSocket = new QuoteWebSocketService();

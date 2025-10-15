/**
 * 실시간 체결 데이터 인터페이스
 */
export interface RealtimeTradeData {
  /** 메시지 타입 */
  type: string;
  /** 체결 데이터 */
  data?: {
    /** 종목 코드 */
    stockCode: string;
    /** 타임스탬프 */
    timestamp: string;
    /** 체결 시간 */
    tradeTime: string;

    /** 현재가 */
    currentPrice: string;
    /** 전일 대비 부호 */
    priceChangeSign: string;
    /** 전일 대비 가격 */
    priceChange: string;
    /** 등락률 */
    changeRate: string;

    /** 시가 */
    openPrice: string;
    /** 고가 */
    highPrice: string;
    /** 저가 */
    lowPrice: string;

    /** 체결량 */
    tradeVolume: string;
    /** 누적 거래량 */
    accumulatedVolume: string;
    /** 누적 거래대금 */
    accumulatedAmount: string;

    /** 매도호가1 */
    askPrice1: string;
    /** 매수호가1 */
    bidPrice1: string;
    /** 총 매도 잔량 */
    totalAskRemain: string;
    /** 총 매수 잔량 */
    totalBidRemain: string;

    /** 체결 강도 */
    tradeStrength: string;
    /** 매도 건수 */
    sellCount: string;
    /** 매수 건수 */
    buyCount: string;
  };
}

/** 체결 데이터 콜백 함수 타입 */
type TradeCallback = (data: RealtimeTradeData) => void;

/**
 * 실시간 체결 WebSocket 서비스
 */
class TradeWebSocketService {
  private ws: WebSocket | null = null;
  private callbacks: Map<string, TradeCallback> = new Map();
  private reconnectTimer: NodeJS.Timeout | null = null;
  private readonly url = 'ws://localhost:8080/ws/trade';

  /**
   * WebSocket 연결
   * @returns 연결 완료 Promise
   */
  connect(): Promise<void> {
    return new Promise((resolve, reject) => {
      try {
        this.ws = new WebSocket(this.url);

        this.ws.onopen = () => {
          resolve();
        };

        this.ws.onmessage = (event) => {
          try {
            const message: RealtimeTradeData = JSON.parse(event.data);

            if (message.type === 'trade' && message.data) {
              const callback = this.callbacks.get(message.data.stockCode);
              if (callback) {
                callback(message);
              }
            }
          } catch (error) {
            
          }
        };

        this.ws.onerror = (error) => {
          reject(error);
        };

        this.ws.onclose = () => {
          this.scheduleReconnect();
        };
      } catch (error) {
        reject(error);
      }
    });
  }

  /**
   * 종목 실시간 체결 구독
   * @param stockCode 종목 코드
   * @param callback 체결 데이터 수신 콜백
   */
  subscribe(stockCode: string, callback: TradeCallback): void {
    this.callbacks.set(stockCode, callback);

    if (this.ws && this.ws.readyState === WebSocket.OPEN) {
      const message = {
        action: 'subscribe',
        stockCode: stockCode
      };
      this.ws.send(JSON.stringify(message));
    }
  }

  /**
   * 종목 실시간 체결 구독 해제
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
   * WebSocket 연결 상태 확인
   * @returns 연결 상태 (true: 연결됨, false: 연결 안 됨)
   */
  isConnected(): boolean {
    return this.ws !== null && this.ws.readyState === WebSocket.OPEN;
  }

  /**
   * 재연결 스케줄링 (3초 후 재연결 시도)
   */
  private scheduleReconnect(): void {
    if (this.reconnectTimer) {
      clearTimeout(this.reconnectTimer);
    }

    this.reconnectTimer = setTimeout(() => {
      this.connect().catch(() => {});
    }, 3000);
  }

  /**
   * WebSocket 연결 종료 및 모든 구독 취소
   */
  disconnect(): void {
    if (this.reconnectTimer) {
      clearTimeout(this.reconnectTimer);
      this.reconnectTimer = null;
    }

    if (this.ws) {
      this.ws.close();
      this.ws = null;
    }

    this.callbacks.clear();
  }
}

export const tradeWebSocket = new TradeWebSocketService();

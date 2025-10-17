import type { ForeignQuoteData, ForeignQuoteMessage } from '../types/foreignStock.types';

/** 해외 주식 데이터 콜백 함수 타입 */
type ForeignQuoteCallback = (data: ForeignQuoteData) => void;

/** 데이터 타입: trade(체결가) 또는 quote(호가) */
type DataType = 'trade' | 'quote';

/**
 * 해외 주식 실시간 WebSocket 서비스
 * - trade: 실시간 체결가 (HDFSCNT0)
 * - quote: 실시간 호가 (미국: HDFSASP0, 아시아: HDFSASP1)
 */
class ForeignQuoteWebSocketService {
  private ws: WebSocket | null = null;
  private listeners: Map<string, Set<ForeignQuoteCallback>> = new Map();
  private reconnectAttempts = 0;
  private maxReconnectAttempts = 5;
  private reconnectDelay = 3000;
  private readonly WS_URL = 'ws://localhost:8080/ws/foreign-quote';

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
            const message: ForeignQuoteMessage = JSON.parse(event.data);

            // subscribe/unsubscribe 응답 메시지는 무시
            if (message.type === 'subscribe' || message.type === 'unsubscribe') {
              return;
            }

            // 데이터 처리 (trade 또는 quote)
            if ((message.type === 'quote' || message.type === 'trade') && message.data) {
              const key = `${message.data.exchangeCode}:${message.data.stockCode}:${message.type}`;
              const callbacks = this.listeners.get(key);

              if (callbacks) {
                callbacks.forEach(callback => {
                  callback(message.data!);
                });
              }
            }
          } catch (error) {
            // JSON 파싱 에러 무시
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
   * 해외 종목 실시간 데이터 구독
   * @param exchangeCode 거래소코드 (NAS/NYS/HKS/TSE)
   * @param stockCode 종목코드
   * @param dataType 데이터 타입 ('trade': 체결가, 'quote': 호가)
   * @param callback 데이터 수신 콜백
   */
  subscribe(exchangeCode: string, stockCode: string, dataType: DataType, callback: ForeignQuoteCallback): void {
    const key = `${exchangeCode}:${stockCode}:${dataType}`;

    // 콜백 등록
    if (!this.listeners.has(key)) {
      this.listeners.set(key, new Set());
    }
    this.listeners.get(key)!.add(callback);

    // WebSocket 연결 후 구독 메시지 전송
    const sendSubscribe = () => {
      if (this.ws && this.ws.readyState === WebSocket.OPEN) {
        const message = {
          action: 'subscribe',
          exchangeCode,
          stockCode,
          dataType
        };
        this.ws.send(JSON.stringify(message));
      }
    };

    if (this.ws && this.ws.readyState === WebSocket.OPEN) {
      sendSubscribe();
    } else {
      // 연결되어 있지 않으면 연결 후 구독
      this.connect().then(() => {
        sendSubscribe();
      }).catch(() => {});
    }
  }

  /**
   * 해외 종목 실시간 데이터 구독 해제
   * @param exchangeCode 거래소코드
   * @param stockCode 종목코드
   * @param dataType 데이터 타입
   * @param callback 제거할 콜백 (선택, 없으면 모든 콜백 제거)
   */
  unsubscribe(exchangeCode: string, stockCode: string, dataType: DataType, callback?: ForeignQuoteCallback): void {
    const key = `${exchangeCode}:${stockCode}:${dataType}`;

    if (callback) {
      // 특정 콜백만 제거
      const callbacks = this.listeners.get(key);
      if (callbacks) {
        callbacks.delete(callback);
        if (callbacks.size === 0) {
          this.listeners.delete(key);
        }
      }
    } else {
      // 모든 콜백 제거
      this.listeners.delete(key);
    }

    // 더 이상 구독하는 콜백이 없으면 서버에 구독 해제 메시지 전송
    if (!this.listeners.has(key) && this.ws && this.ws.readyState === WebSocket.OPEN) {
      const message = {
        action: 'unsubscribe',
        exchangeCode,
        stockCode,
        dataType
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
      this.listeners.clear();
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
export const foreignQuoteWebSocket = new ForeignQuoteWebSocketService();

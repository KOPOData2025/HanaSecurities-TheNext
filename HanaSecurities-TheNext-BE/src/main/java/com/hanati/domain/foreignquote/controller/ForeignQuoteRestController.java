package com.hanati.domain.foreignquote.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 해외주식 실시간 호가 WebSocket REST Controller
 */
@RestController
@RequestMapping("/api/v1/foreign-quote")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Foreign Realtime Quote WebSocket API", description = "해외주식 실시간 호가 WebSocket API 정보")
public class ForeignQuoteRestController {

    @Operation(
            summary = "해외주식 WebSocket 연결 정보 조회",
            description = """
                    해외주식 실시간 호가 WebSocket 연결 정보를 제공합니다.

                    ## WebSocket 연결 방법

                    ### 1. 연결 URL
                    - **엔드포인트**: `ws://localhost:8080/ws-foreign-quote`
                    - **프로토콜**: Native WebSocket

                    ### 2. JavaScript 연결 예제
                    ```javascript
                    // Native WebSocket 사용
                    const socket = new WebSocket('ws://localhost:8080/ws-foreign-quote');

                    socket.onopen = function() {
                        console.log('연결 성공');

                        // 종목 구독 (예: 나스닥 AAPL)
                        const subscribeMessage = {
                            action: 'subscribe',
                            exchangeCode: 'NAS',
                            stockCode: 'AAPL'
                        };
                        socket.send(JSON.stringify(subscribeMessage));
                    };

                    socket.onmessage = function(event) {
                        const message = JSON.parse(event.data);
                        if (message.type === 'quote') {
                            console.log('실시간 호가:', message.data);
                        }
                    };
                    ```

                    ### 3. 구독 메시지 형식
                    ```json
                    // 구독
                    {
                        "action": "subscribe",
                        "exchangeCode": "NAS",
                        "stockCode": "AAPL"
                    }

                    // 구독 해제
                    {
                        "action": "unsubscribe",
                        "exchangeCode": "NAS",
                        "stockCode": "AAPL"
                    }
                    ```

                    ### 4. 응답 데이터 형식
                    ```json
                    {
                        "type": "quote",
                        "data": {
                            "stockCode": "AAPL",
                            "currentPrice": "182.85",
                            "bidPrice1": "182.84",
                            "bidQuantity1": "350",
                            "askPrice1": "182.87",
                            "askQuantity1": "57",
                            "executionTime": "092223",
                            "volume": "0",
                            "changeRate": "0"
                        }
                    }
                    ```

                    ### 5. 지원 거래소
                    - **NYS/NYSE**: 뉴욕증권거래소
                    - **NAS/NASD**: 나스닥
                    - **AMS/AMEX**: 아멕스
                    - **HKS/SEHK**: 홍콩
                    - **TSE/TKSE**: 도쿄
                    - **SHS**: 상해
                    - **SZS**: 심천
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "WebSocket 연결 정보 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "wsEndpoint": "ws://localhost:8080/ws-foreign-quote",
                                                "protocol": "Native WebSocket",
                                                "messageFormat": "JSON",
                                                "supportedExchanges": "NYS, NAS, AMS, HKS, TSE, SHS, SZS",
                                                "description": "해외주식 실시간 호가 WebSocket 연결 정보"
                                            }
                                            """
                            )
                    )
            )
    })
    @GetMapping("/ws-info")
    public ResponseEntity<Map<String, String>> getWebSocketInfo() {
        Map<String, String> info = new HashMap<>();
        info.put("wsEndpoint", "ws://localhost:8080/ws-foreign-quote");
        info.put("protocol", "Native WebSocket");
        info.put("messageFormat", "JSON");
        info.put("supportedExchanges", "NYS, NAS, AMS, HKS, TSE, SHS, SZS");
        info.put("description", "해외주식 실시간 호가 WebSocket 연결 정보");

        return ResponseEntity.ok(info);
    }

    @Operation(
            summary = "WebSocket 연결 테스트용 HTML",
            description = "해외주식 WebSocket 연결을 테스트할 수 있는 간단한 HTML 페이지를 반환합니다."
    )
    @GetMapping(value = "/test", produces = "text/html")
    public ResponseEntity<String> getTestPage() {
        String html = """
                <!DOCTYPE html>
                <html>
                <head>
                    <title>해외주식 실시간 호가 WebSocket 테스트</title>
                    <style>
                        body { font-family: Arial, sans-serif; margin: 20px; }
                        .container { max-width: 800px; }
                        .input-group { margin: 10px 0; }
                        label { display: inline-block; width: 120px; }
                        input, select { padding: 5px; margin: 5px; }
                        button { padding: 8px 15px; margin: 5px; cursor: pointer; }
                        #quote { background: #f5f5f5; padding: 15px; border-radius: 5px; white-space: pre-wrap; }
                        .status { padding: 10px; margin: 10px 0; border-radius: 5px; }
                        .status.connected { background: #d4edda; color: #155724; }
                        .status.disconnected { background: #f8d7da; color: #721c24; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <h1>해외주식 실시간 호가 WebSocket 테스트</h1>

                        <div id="status" class="status disconnected">연결 대기 중...</div>

                        <div class="input-group">
                            <label>거래소:</label>
                            <select id="exchangeCode">
                                <option value="NAS">나스닥 (NAS)</option>
                                <option value="NYS">뉴욕 (NYS)</option>
                                <option value="AMS">아멕스 (AMS)</option>
                                <option value="HKS">홍콩 (HKS)</option>
                                <option value="TSE">도쿄 (TSE)</option>
                            </select>
                        </div>

                        <div class="input-group">
                            <label>종목코드:</label>
                            <input type="text" id="stockCode" value="AAPL" placeholder="예: AAPL" />
                        </div>

                        <div>
                            <button onclick="connect()">연결</button>
                            <button onclick="subscribe()">구독</button>
                            <button onclick="unsubscribe()">구독해제</button>
                            <button onclick="disconnect()">연결종료</button>
                        </div>

                        <h2>실시간 호가</h2>
                        <pre id="quote">데이터 없음</pre>
                    </div>

                    <script>
                        let socket = null;

                        function connect() {
                            if (socket && socket.readyState === WebSocket.OPEN) {
                                alert('이미 연결되어 있습니다.');
                                return;
                            }

                            socket = new WebSocket('ws://localhost:8080/ws-foreign-quote');

                            socket.onopen = function() {
                                console.log('WebSocket 연결 성공');
                                document.getElementById('status').textContent = '연결됨';
                                document.getElementById('status').className = 'status connected';
                            };

                            socket.onmessage = function(event) {
                                const message = JSON.parse(event.data);
                                console.log('수신:', message);

                                if (message.type === 'quote') {
                                    document.getElementById('quote').textContent = JSON.stringify(message.data, null, 2);
                                } else if (message.type === 'subscribe') {
                                    alert('구독 완료: ' + message.stockCode);
                                } else if (message.type === 'unsubscribe') {
                                    alert('구독 해제: ' + message.stockCode);
                                } else if (message.type === 'error') {
                                    alert('오류: ' + message.message);
                                }
                            };

                            socket.onclose = function() {
                                console.log('WebSocket 연결 종료');
                                document.getElementById('status').textContent = '연결 끊김';
                                document.getElementById('status').className = 'status disconnected';
                            };

                            socket.onerror = function(error) {
                                console.error('WebSocket 오류:', error);
                                alert('WebSocket 오류 발생');
                            };
                        }

                        function subscribe() {
                            if (!socket || socket.readyState !== WebSocket.OPEN) {
                                alert('먼저 연결해주세요.');
                                return;
                            }

                            const exchangeCode = document.getElementById('exchangeCode').value;
                            const stockCode = document.getElementById('stockCode').value;

                            const message = {
                                action: 'subscribe',
                                exchangeCode: exchangeCode,
                                stockCode: stockCode
                            };

                            socket.send(JSON.stringify(message));
                            console.log('구독 요청:', message);
                        }

                        function unsubscribe() {
                            if (!socket || socket.readyState !== WebSocket.OPEN) {
                                alert('먼저 연결해주세요.');
                                return;
                            }

                            const exchangeCode = document.getElementById('exchangeCode').value;
                            const stockCode = document.getElementById('stockCode').value;

                            const message = {
                                action: 'unsubscribe',
                                exchangeCode: exchangeCode,
                                stockCode: stockCode
                            };

                            socket.send(JSON.stringify(message));
                            console.log('구독 해제 요청:', message);
                        }

                        function disconnect() {
                            if (socket) {
                                socket.close();
                                socket = null;
                            }
                        }

                        // 페이지 로드 시 자동 연결
                        window.onload = function() {
                            connect();
                        };
                    </script>
                </body>
                </html>
                """;

        return ResponseEntity.ok(html);
    }
}

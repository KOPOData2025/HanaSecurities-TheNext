package com.hanati.domain.quote.controller;

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

@RestController
@RequestMapping("/api/v1/quote")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Realtime Quote WebSocket API", description = "국내주식 실시간 호가 WebSocket API 정보")
public class QuoteRestController {

    @Operation(
            summary = "WebSocket 연결 정보 조회",
            description = """
                    국내주식 실시간 호가 WebSocket 연결 정보를 제공합니다.

                    ## WebSocket 연결 방법

                    ### 1. 연결 URL
                    - **엔드포인트**: `ws://localhost:8080/ws-quote`
                    - **프로토콜**: STOMP over WebSocket (SockJS 지원)

                    ### 2. JavaScript 연결 예제
                    ```javascript
                    // SockJS + STOMP 라이브러리 필요
                    const socket = new SockJS('http://localhost:8080/ws-quote');
                    const stompClient = Stomp.over(socket);

                    stompClient.connect({}, function(frame) {
                        console.log('Connected: ' + frame);

                        // 종목 구독
                        stompClient.send('/app/subscribe/005930');  // 삼성전자

                        // 실시간 호가 수신
                        stompClient.subscribe('/topic/quote/005930', function(message) {
                            const quote = JSON.parse(message.body);
                            console.log('실시간 호가:', quote);
                        });
                    });
                    ```

                    ### 3. 구독 메시지
                    - **구독**: `/app/subscribe/{stockCode}`
                    - **구독해제**: `/app/unsubscribe/{stockCode}`
                    - **데이터수신**: `/topic/quote/{stockCode}`

                    ### 4. 응답 데이터 형식
                    ```json
                    {
                      "stockCode": "005930",
                      "timestamp": "2025-10-01 10:00:00",
                      "askPrices": [71900, 72000, 72100, ...],
                      "bidPrices": [71800, 71700, 71600, ...],
                      "askVolumes": [91918, 117942, 92673, ...],
                      "bidVolumes": [95221, 159371, 220746, ...],
                      "totalAskVolume": 1159362,
                      "totalBidVolume": 2095167
                    }
                    ```

                    ### 5. 필요 라이브러리
                    - SockJS: https://cdn.jsdelivr.net/npm/sockjs-client@1/dist/sockjs.min.js
                    - STOMP: https://cdn.jsdelivr.net/npm/@stomp/stompjs@5/bundles/stomp.umd.min.js
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
                                              "wsEndpoint": "ws://localhost:8080/ws-quote",
                                              "protocol": "STOMP over WebSocket",
                                              "subscribePrefix": "/app/subscribe/{stockCode}",
                                              "unsubscribePrefix": "/app/unsubscribe/{stockCode}",
                                              "dataTopicPrefix": "/topic/quote/{stockCode}",
                                              "description": "실시간 호가 WebSocket 연결 정보"
                                            }
                                            """
                            )
                    )
            )
    })
    @GetMapping("/ws-info")
    public ResponseEntity<Map<String, String>> getWebSocketInfo() {
        Map<String, String> info = new HashMap<>();
        info.put("wsEndpoint", "ws://localhost:8080/ws-quote");
        info.put("protocol", "STOMP over WebSocket");
        info.put("subscribePrefix", "/app/subscribe/{stockCode}");
        info.put("unsubscribePrefix", "/app/unsubscribe/{stockCode}");
        info.put("dataTopicPrefix", "/topic/quote/{stockCode}");
        info.put("description", "실시간 호가 WebSocket 연결 정보");

        return ResponseEntity.ok(info);
    }

    @Operation(
            summary = "WebSocket 연결 테스트용 HTML",
            description = "WebSocket 연결을 테스트할 수 있는 간단한 HTML 페이지를 반환합니다."
    )
    @GetMapping(value = "/test", produces = "text/html")
    public ResponseEntity<String> getTestPage() {
        String html = """
                <!DOCTYPE html>
                <html>
                <head>
                    <title>실시간 호가 WebSocket 테스트</title>
                    <script src="https://cdn.jsdelivr.net/npm/sockjs-client@1/dist/sockjs.min.js"></script>
                    <script src="https://cdn.jsdelivr.net/npm/@stomp/stompjs@5/bundles/stomp.umd.min.js"></script>
                </head>
                <body>
                    <h1>국내주식 실시간 호가 WebSocket 테스트</h1>

                    <div>
                        <label>종목코드: <input type="text" id="stockCode" value="005930" /></label>
                        <button onclick="subscribe()">구독</button>
                        <button onclick="unsubscribe()">구독해제</button>
                    </div>

                    <h2>실시간 호가</h2>
                    <pre id="quote"></pre>

                    <script>
                        const socket = new SockJS('http://localhost:8080/ws-quote');
                        const stompClient = Stomp.over(socket);
                        let subscription = null;

                        stompClient.connect({}, function(frame) {
                            console.log('Connected: ' + frame);
                        });

                        function subscribe() {
                            const stockCode = document.getElementById('stockCode').value;

                            // 구독 요청
                            stompClient.send('/app/subscribe/' + stockCode);

                            // 데이터 수신
                            subscription = stompClient.subscribe('/topic/quote/' + stockCode, function(message) {
                                const quote = JSON.parse(message.body);
                                document.getElementById('quote').textContent = JSON.stringify(quote, null, 2);
                            });

                            console.log('구독 완료:', stockCode);
                        }

                        function unsubscribe() {
                            const stockCode = document.getElementById('stockCode').value;

                            if (subscription) {
                                subscription.unsubscribe();
                                stompClient.send('/app/unsubscribe/' + stockCode);
                                console.log('구독 해제:', stockCode);
                            }
                        }
                    </script>
                </body>
                </html>
                """;

        return ResponseEntity.ok(html);
    }
}

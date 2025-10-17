# Frontend 구조

## 개요
React와 TypeScript 기반의 하나증권 THE NEXT 프론트엔드 애플리케이션입니다.

## 기술 스택
- **Framework**: React
- **Language**: TypeScript
- **Styling**: CSS3
- **Build Tool**: Vite

## 프로젝트 구조

```
HanaSecurities-TheNext-FE/
├── src/
│   ├── components/              # UI 컴포넌트
│   │   ├── asset/              # 자산 관리 (포트폴리오, 보유종목)
│   │   ├── auth/               # 회원 인증 (WebAuthn 지문인증)
│   │   ├── bnpl/               # BNPL 후불결제
│   │   ├── chart/              # 차트 컴포넌트 (캔들스틱, 볼륨)
│   │   ├── foreignStock/       # 해외 주식 거래
│   │   ├── gold/               # 금 현물 투자
│   │   ├── market/             # 시장 지수 (코스피, 나스닥 등)
│   │   ├── news/               # 금융 뉴스
│   │   ├── order/              # 주문 체결 관리
│   │   ├── payment/            # 결제 처리
│   │   ├── ranking/            # 종목 랭킹
│   │   ├── shopping/           # 커머스 플랫폼
│   │   ├── stock/              # 국내 주식 거래
│   │   └── watchlist/          # 관심종목
│   │
│   ├── services/               # API 통신 레이어
│   │   ├── stockApi.ts         # 국내 주식 API
│   │   ├── foreignStockApi.ts  # 해외 주식 API
│   │   ├── goldApi.ts          # 금 현물 API
│   │   ├── bnplService.ts      # BNPL 서비스
│   │   ├── webauthnService.ts  # 인증 서비스
│   │   ├── quoteWebSocket.ts   # 실시간 호가 WebSocket
│   │   └── tradeWebSocket.ts   # 실시간 체결 WebSocket
│   │
│   ├── types/                  # TypeScript 타입 정의
│   ├── utils/                  # 유틸리티 함수
│   ├── contexts/               # React Context (전역 상태)
│   └── data/                   # Mock 데이터
│
├── public/                     # 정적 파일
├── package.json                # 프로젝트 의존성
└── vite.config.ts              # Vite 설정
```

## 주요 기능

### 인증 (WebAuthn)
- FIDO2 표준 기반 패스워드리스 생체인증
- 지문 인증을 통한 안전한 로그인/회원가입

### 주식 거래
- **국내 주식**: 실시간 호가, 차트, 재무정보, 투자의견
- **해외 주식**: 미국, 일본, 중국, 홍콩 시장 거래 지원

### BNPL 후불결제
- AI 기반 신용평가 모델
- 최대 30만원 한도
- 투자내역 기반 신용 심사

### 금 현물 투자
- 실시간 금 시세
- 1g 단위 소액투자
- 미니금 종목 제공

### 실시간 데이터
- WebSocket 기반 실시간 호가/체결
- 시장 지수 실시간 업데이트
- 금융 뉴스 및 종목 랭킹

## 개발 환경 설정

### 설치
```bash
npm install
# 또는
yarn install
```

### 개발 서버 실행
```bash
npm run dev
# 또는
yarn dev
```

### 빌드
```bash
npm run build
# 또는
yarn build
```
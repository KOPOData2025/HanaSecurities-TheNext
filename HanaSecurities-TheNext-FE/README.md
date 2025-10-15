# 하나증권 프론트엔드 프로젝트 구조

## 개요
하나증권 모바일 트레이딩 플랫폼의 React + TypeScript 기반 프론트엔드 애플리케이션입니다.

## 프로젝트 정보
- **프레임워크**: React 18 + TypeScript
- **빌드 도구**: Vite
- **개발 서버**: localhost:5173
- **백엔드 API**: localhost:8080/api/v1/*
- **WebSocket**: ws://localhost:8080/ws/*

---

## 폴더 구조

```
src/
├── assets/                    # 정적 자산 (이미지, 아이콘, 폰트 등)
├── components/                # React 컴포넌트 (기능별 분류)
│   ├── asset/                # 자산 관리 컴포넌트
│   │   ├── AssetTab.tsx     # 자산 탭 (주식, 채권, ETP, 금 등)
│   │   └── AssetDetail.tsx  # 자산 상세 정보
│   │
│   ├── auth/                 # 인증 관련 컴포넌트
│   │   ├── LoginPage.tsx    # 로그인 페이지
│   │   ├── RegisterPage.tsx # 회원가입 페이지
│   │   ├── PasskeyAuth.tsx  # Passkey 인증 (WebAuthn)
│   │   └── SecondaryPasswordModal.tsx  # 2차 비밀번호 입력
│   │
│   ├── banner/               # 배너 및 광고 컴포넌트
│   │   └── AdBanner.tsx     # 광고 배너
│   │
│   ├── bnpl/                 # BNPL (Buy Now Pay Later) 기능
│   │   ├── BNPLApplicationPage.tsx  # BNPL 신청
│   │   ├── BNPLInfoPage.tsx        # BNPL 정보
│   │   ├── BNPLReviewPage.tsx      # BNPL 검토
│   │   ├── BNPLTermsPage.tsx       # BNPL 약관
│   │   ├── BNPLCompletePage.tsx    # BNPL 완료
│   │   ├── BNPLUsagePage.tsx       # BNPL 사용 내역
│   │   └── BNPLInfoModal.tsx       # BNPL 정보 모달
│   │
│   ├── chart/                # 차트 컴포넌트
│   │   ├── CandlestickChart.tsx  # 캔들스틱 차트
│   │   ├── VolumeChart.tsx       # 거래량 차트
│   │   └── ChartDetail.tsx       # 차트 상세 설정
│   │
│   ├── common/               # 공통 컴포넌트
│   │   ├── Header.tsx            # 헤더
│   │   ├── PageHeader.tsx        # 페이지 헤더
│   │   ├── Footer.tsx            # 푸터
│   │   ├── Skeleton.tsx          # 스켈레톤 UI
│   │   ├── StockInfoSection.tsx  # 주식 정보 섹션
│   │   └── StockInfoSkeleton.tsx # 주식 정보 스켈레톤
│   │
│   ├── market/               # 시장 지수 컴포넌트
│   │   ├── MarketIndices.tsx         # 국내 시장 지수 (코스피, 코스닥)
│   │   ├── ForeignIndices.tsx        # 해외 시장 지수
│   │   ├── MarketIndicesSkeleton.tsx # 시장 지수 스켈레톤
│   │   ├── ForeignIndicesSkeleton.tsx # 해외 지수 스켈레톤
│   │   └── IndexCardSkeleton.tsx     # 지수 카드 스켈레톤
│   │
│   ├── menu/                 # 메뉴 및 네비게이션
│   │   └── MenuDrawer.tsx   # 사이드 메뉴 드로어
│   │
│   ├── navigation/           # 하단 네비게이션
│   │   └── BottomNavigation.tsx  # 하단 탭 네비게이션
│   │
│   ├── news/                 # 뉴스 컴포넌트
│   │   ├── NewsSection.tsx  # 뉴스 섹션
│   │   └── NewsDetail.tsx   # 뉴스 상세
│   │
│   ├── order/                # 주문/매매 컴포넌트
│   │   ├── OrderPage.tsx             # 주문 페이지 (매수/매도)
│   │   ├── OrderConfirmModal.tsx     # 주문 확인 모달
│   │   ├── OrderExecutionToast.tsx   # 주문 체결 토스트
│   │   └── OrderPageSkeleton.tsx     # 주문 페이지 스켈레톤
│   │
│   ├── payment/              # 결제 컴포넌트
│   │   └── PaymentPage.tsx  # 결제 페이지
│   │
│   ├── product/              # 금융상품 컴포넌트
│   │   ├── ProductMenu.tsx     # 상품 메뉴
│   │   └── ProductPension.tsx  # 연금 상품
│   │
│   ├── ranking/              # 실시간 랭킹 컴포넌트
│   │   ├── RealTimeRanking.tsx  # 실시간 랭킹 (상승/하락/거래량/거래대금)
│   │   ├── ETPRanking.tsx       # ETP 랭킹
│   │   └── BondRanking.tsx      # 채권 랭킹
│   │
│   ├── search/               # 검색 컴포넌트
│   │   ├── SearchPage.tsx       # 검색 페이지
│   │   └── SearchResultPage.tsx # 검색 결과 페이지
│   │
│   ├── shopping/             # 쇼핑 컴포넌트
│   │   ├── ShoppingHome.tsx          # 쇼핑 홈
│   │   ├── ShoppingHeader.tsx        # 쇼핑 헤더
│   │   ├── ProductDetail.tsx         # 상품 상세
│   │   ├── PurchaseBottomSheet.tsx   # 구매 바텀시트
│   │   ├── CheckoutPage.tsx          # 결제 페이지
│   │   └── PaymentCompletePage.tsx   # 결제 완료 페이지
│   │
│   ├── stock/                # 주식 상세 컴포넌트
│   │   ├── StockDetail.tsx                 # 주식 상세 메인
│   │   ├── StockTicker.tsx                 # 주식 티커
│   │   ├── StockOverview.tsx               # 주식 개요 탭
│   │   ├── StockOrderBook.tsx              # 실시간 호가창
│   │   ├── OrderBook.tsx                   # 호가 데이터 표시
│   │   ├── FinancialInfo.tsx               # 재무정보 탭
│   │   ├── InvestmentOpinion.tsx           # 투자의견 탭
│   │   ├── StockDetailSkeleton.tsx         # 주식 상세 스켈레톤
│   │   ├── StockOrderBookSkeleton.tsx      # 호가창 스켈레톤
│   │   ├── OverviewTabSkeleton.tsx         # 개요 탭 스켈레톤
│   │   ├── ChartTabSkeleton.tsx            # 차트 탭 스켈레톤
│   │   ├── FinancialTabSkeleton.tsx        # 재무정보 탭 스켈레톤
│   │   └── InvestmentOpinionTabSkeleton.tsx # 투자의견 탭 스켈레톤
│   │
│   └── watchlist/            # 관심종목 컴포넌트
│       └── WatchlistTab.tsx # 관심종목 탭
│
├── contexts/                 # React Context API
│   └── AuthContext.tsx      # 인증 상태 관리 Context
│
├── data/                     # 데이터 관련
│   └── mockData/            # Mock 데이터 (개발/테스트용)
│       ├── assetData.ts             # 자산 Mock 데이터
│       ├── assetDetailData.ts       # 자산 상세 Mock 데이터
│       ├── bondData.ts              # 채권 Mock 데이터
│       ├── chartData.ts             # 차트 Mock 데이터
│       ├── etpData.ts               # ETP Mock 데이터
│       ├── internationalData.ts     # 해외주식 Mock 데이터
│       ├── internationalMarketData.ts # 해외시장 Mock 데이터
│       ├── newsData.ts              # 뉴스 Mock 데이터
│       ├── orderBookData.ts         # 호가 Mock 데이터
│       ├── orderData.ts             # 주문 Mock 데이터
│       ├── rankingData.ts           # 랭킹 Mock 데이터
│       ├── stockDetailData.ts       # 주식 상세 Mock 데이터
│       └── watchlistData.ts         # 관심종목 Mock 데이터
│
├── hooks/                    # Custom React Hooks
│   └── (현재 비어있음 - 필요 시 추가)
│
├── services/                 # API 서비스 레이어
│   ├── indexApi.ts          # 지수 API (코스피, 코스닥 등)
│   ├── stockApi.ts          # 주식 API (차트, 재무정보, 잔고 등)
│   ├── newsApi.ts           # 뉴스 API (검색, 요약)
│   ├── rankingApi.ts        # 국내 랭킹 API
│   ├── foreignIndexApi.ts   # 해외 지수 API
│   ├── foreignRankingApi.ts # 해외 랭킹 API
│   ├── orderApi.ts          # 주문 API (매수/매도)
│   ├── bnplService.ts       # BNPL 서비스
│   ├── productService.ts    # 금융상품 서비스
│   ├── webauthnService.ts   # WebAuthn (Passkey) 서비스
│   ├── tradeWebSocket.ts    # 실시간 체결 WebSocket (ws://localhost:8080/ws/trade)
│   └── quoteWebSocket.ts    # 실시간 호가 WebSocket (ws://localhost:8080/ws/quote)
│
├── styles/                   # 전역 스타일
│   └── global.css           # 전역 CSS 스타일
│
├── types/                    # TypeScript 타입 정의
│   ├── index.ts             # 타입 Export 모음
│   ├── index.types.ts       # 지수 관련 타입
│   ├── stock.types.ts       # 주식 관련 타입
│   ├── news.types.ts        # 뉴스 관련 타입
│   ├── ranking.types.ts     # 랭킹 관련 타입
│   ├── bnpl.types.ts        # BNPL 관련 타입
│   └── menu.types.ts        # 메뉴 관련 타입
│
├── utils/                    # 유틸리티 함수
│   ├── chartDataTransformer.ts  # 차트 데이터 변환 유틸
│   └── stockIcons.ts            # 주식 아이콘 경로 유틸
│
├── App.tsx                   # 메인 App 컴포넌트 (라우팅)
└── main.tsx                  # 애플리케이션 진입점

```

---

## 주요 기능별 컴포넌트 매핑

### 1. 인증 (Authentication)
- **로그인/회원가입**: `components/auth/`
- **Passkey 인증**: `components/auth/PasskeyAuth.tsx`
- **인증 상태 관리**: `contexts/AuthContext.tsx`

### 2. 시장 정보 (Market Data)
- **국내 지수**: `components/market/MarketIndices.tsx`
- **해외 지수**: `components/market/ForeignIndices.tsx`
- **실시간 랭킹**: `components/ranking/RealTimeRanking.tsx`

### 3. 주식 거래 (Stock Trading)
- **주식 상세**: `components/stock/StockDetail.tsx`
- **실시간 호가**: `components/stock/StockOrderBook.tsx` + `services/quoteWebSocket.ts`
- **실시간 체결**: `services/tradeWebSocket.ts`
- **주문/매매**: `components/order/OrderPage.tsx`

### 4. 자산 관리 (Asset Management)
- **자산 현황**: `components/asset/AssetTab.tsx`
- **자산 상세**: `components/asset/AssetDetail.tsx`
- **관심종목**: `components/watchlist/WatchlistTab.tsx`

### 5. 금융상품 (Financial Products)
- **상품 메뉴**: `components/product/`
- **쇼핑**: `components/shopping/`
- **BNPL**: `components/bnpl/`

### 6. 뉴스 및 정보
- **뉴스 섹션**: `components/news/NewsSection.tsx`
- **뉴스 상세**: `components/news/NewsDetail.tsx`
- **검색**: `components/search/`

---

## API 구조

### REST API (Vite Proxy 사용)
```
Frontend: /api/*
   ↓ (Vite Proxy)
Backend: http://localhost:8080/api/v1/*
```

**vite.config.ts 설정**:
```typescript
server: {
  proxy: {
    '/api': {
      target: 'http://localhost:8080',
      changeOrigin: true,
      rewrite: (path) => path.replace(/^\/api/, '/api/v1'),
    }
  }
}
```

### WebSocket
```
실시간 체결: ws://localhost:8080/ws/trade
실시간 호가: ws://localhost:8080/ws/quote
```

---

## 개발 패턴

### 1. 컴포넌트 구조
- **기능별 폴더 분리**: 각 기능(auth, stock, order 등)을 독립된 폴더로 관리
- **CSS 모듈**: 각 컴포넌트에 `.module.css` 또는 `.css` 파일 병행 사용
- **Skeleton UI**: 로딩 상태를 위한 Skeleton 컴포넌트 제공

### 2. API 레이어
- **서비스 분리**: `services/` 폴더에 API 로직 집중
- **타입 안정성**: `types/` 폴더의 TypeScript 타입으로 API 응답 정의
- **에러 처리**: API 실패 시 Mock 데이터 Fallback

### 3. 상태 관리
- **Context API**: 인증 상태 (`AuthContext`)
- **Local State**: 컴포넌트 레벨 상태는 `useState`, `useEffect` 사용

### 4. 실시간 데이터
- **WebSocket 서비스**:
  - `tradeWebSocket`: 체결 데이터 실시간 구독
  - `quoteWebSocket`: 호가 데이터 실시간 구독
- **자동 재연결**: 연결 끊김 시 자동 재연결 로직 포함

---

## 스켈레톤 UI 패턴
로딩 상태를 위한 스켈레톤 컴포넌트:
- `MarketIndicesSkeleton.tsx` - 시장 지수 로딩
- `StockDetailSkeleton.tsx` - 주식 상세 로딩
- `OrderPageSkeleton.tsx` - 주문 페이지 로딩
- `StockOrderBookSkeleton.tsx` - 호가창 로딩
- 각 탭별 Skeleton 컴포넌트 제공

---

## Mock 데이터 활용
개발/테스트 환경을 위한 Mock 데이터:
- **위치**: `src/data/mockData/`
- **용도**: API 실패 시 Fallback, 오프라인 개발
- **종류**: 주식, 뉴스, 랭킹, 호가, 차트 등 모든 도메인 커버

---

## 빌드 및 실행

### 개발 서버
```bash
yarn dev
# http://localhost:5173
```

```bash
yarn build
```

---

## 주요 기술 스택
- **React 18** - UI 프레임워크
- **TypeScript** - 타입 안정성
- **Vite** - 빌드 도구 (Fast HMR)
- **React Router** - 클라이언트 라우팅
- **WebSocket** - 실시간 데이터 통신
- **WebAuthn** - Passkey 생체 인증
- **CSS Modules** - 스타일 캡슐화

---

## 프로젝트 규칙

### 1. 파일 명명 규칙
- **컴포넌트**: PascalCase (예: `StockDetail.tsx`)
- **서비스**: camelCase (예: `stockApi.ts`)
- **타입**: camelCase + `.types.ts` (예: `stock.types.ts`)

### 2. Import 순서
1. React 및 외부 라이브러리
2. 타입 import
3. 서비스/API
4. 컴포넌트
5. 스타일

### 3. JSDoc 주석
모든 타입 정의와 서비스 함수에 한국어 JSDoc 주석 작성
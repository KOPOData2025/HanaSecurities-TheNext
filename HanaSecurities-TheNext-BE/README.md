# Backend 구조

## 개요
Spring Boot 기반의 하나증권 THE NEXT 백엔드 API 서버입니다. DDD(Domain-Driven Design) 패턴을 적용하여 도메인별로 모듈화된 구조를 가지고 있습니다.

## 기술 스택
- **Framework**: Spring Boot 3.5.6
- **Language**: Java 17+
- **ORM**: Spring Data JPA
- **Database**: Oracle, Redis
- **Authentication**: WebAuthn (FIDO2)
- **Real-time**: WebSocket (STOMP)
- **API Documentation**: Swagger/OpenAPI
- **Build Tool**: Gradle (Kotlin DSL)

## 프로젝트 구조

```
HanaSecurities-TheNext-BE/
├── src/main/java/com/hanati/
│   ├── common/                      # 공통 모듈
│   │   ├── config/                  # 설정 (WebSocket, Token, Swagger)
│   │   ├── service/                 # 공통 서비스 (TokenService)
│   │   ├── scheduler/               # 스케줄러 (토큰 자동 갱신)
│   │   └── dto/                     # 공통 DTO
│   │
│   └── domain/                      # 도메인별 모듈 (DDD 패턴)
│       ├── auth/                    # 회원 인증 (WebAuthn)
│       │   ├── controller/          # REST API 컨트롤러
│       │   ├── service/             # 비즈니스 로직
│       │   ├── repository/          # 데이터 접근 (JPA)
│       │   ├── entity/              # 엔티티 (User, Credential)
│       │   └── dto/                 # 데이터 전송 객체
│       │
│       ├── stock/                   # 국내 주식
│       ├── foreignstock/            # 해외 주식
│       ├── gold/                    # 금 현물
│       ├── bnpl/                    # BNPL 후불결제
│       ├── quote/                   # 실시간 호가 (WebSocket)
│       ├── foreignquote/            # 해외 실시간 호가
│       ├── watchlist/               # 관심종목
│       ├── foreignwatchlist/        # 해외 관심종목
│       ├── news/                    # 뉴스
│       ├── ranking/                 # 랭킹
│       ├── index/                   # 지수
│       └── product/                 # 상품 (쇼핑몰)
│
├── src/main/resources/
│   ├── application.yml              # 설정 파일
│   └── application.properties
│
└── build.gradle.kts                 # 빌드 설정 (Gradle)
```

## 주요 기능

### 회원 인증 (WebAuthn)
- FIDO2 표준 기반 패스워드리스 인증
- 생체인증 지원 (지문, 얼굴 인식)
- Credential 안전 관리

### 주식 거래
- **국내 주식**: 한국투자증권 OpenAPI 연동
  - 실시간 호가/체결 정보
  - 주문 접수/체결
  - 계좌 잔고/보유종목 조회
  - 차트 데이터 (분/일/주/월봉)

- **해외 주식**: 미국, 일본, 중국, 홍콩 시장 지원
  - 실시간 해외 시세
  - 해외 주문 처리
  - 국가별 시장 시간대 관리

### BNPL 후불결제
- Django AI 서버와 연동한 신용평가
- 결제 내역 관리
- 한도 관리 및 검증

### 금 현물 투자
- 금 시세 조회
- 금 매수/매도 처리
- 보유 금 관리

### 실시간 데이터 (WebSocket)
- STOMP 프로토콜 기반 실시간 통신
- 실시간 호가 정보 (국내/해외)
- 실시간 체결 정보

### 뉴스 및 랭킹
- 금융 뉴스 수집 및 제공
- 종목별 거래량/등락률 랭킹

## 아키텍처 특징

### DDD (Domain-Driven Design)
- 도메인별 독립적인 모듈 구성
- Controller - Service - Repository 계층 분리
- Entity와 DTO 명확한 분리

### 공통 모듈
- **TokenService**: 한국투자증권 API 토큰 관리
- **Scheduler**: 토큰 자동 갱신 (매일 자정)
- **Config**: WebSocket, CORS, Swagger 설정

### 외부 API 연동
- 한국투자증권 OpenAPI
- 키움증권 OpenAPI
- 네이버 뉴스 API
- Django AI 서버 (신용평가)

## 개발 환경 설정

### 사전 요구사항
- Java 17 이상
- Oracle Database
- Redis

### 빌드 및 실행

```bash
# 빌드
./gradlew build

# 실행
./gradlew bootRun
```

### 환경 변수 설정
`application.yml` 또는 `application.properties`에서 다음 정보를 설정해야 합니다:
- 데이터베이스 연결 정보
- 한국투자증권 API 키
- 키움증권 API 키
- 네이버 뉴스 API 키
- Redis 연결 정보

## API 문서
서버 실행 후 Swagger UI를 통해 API 문서를 확인할 수 있습니다:
```
http://localhost:8080/swagger-ui.html
```

## 데이터베이스
- **Oracle**: 주요 비즈니스 데이터 저장
- **Redis**: 세션 및 캐시 데이터 관리

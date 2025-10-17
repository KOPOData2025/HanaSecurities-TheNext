# Database 구조

## 개요
Oracle Database 기반의 하나증권 THE NEXT 데이터베이스 스키마입니다. 회원 정보, 거래 내역, BNPL 정보 등 주요 비즈니스 데이터를 관리합니다.

## 기술 스택
- **RDBMS**: Oracle Database
- **Cache**: Redis

## 프로젝트 구조

```
HanaSecurities-TheNext-DB/
├── 01_hana_securities_ddl.sql       # 테이블 생성 스크립트 (DDL)
├── 02_hana_securities_data.sql      # 초기 데이터 삽입 (DML)
└── insert_stock_data.sql            # 주식 데이터 삽입
```

## 주요 테이블

### 회원 및 인증
- **USERS**: 회원 기본 정보
  - 회원 ID, 이름, 계좌번호, 가입일 등
- **WEBAUTHN_CREDENTIAL**: WebAuthn 인증 정보
  - Credential ID, Public Key, Counter 등
  - FIDO2 표준 기반 생체인증 데이터

### 주식 거래
- **STOCK_DATA**: 주식 기본 정보
  - 종목 코드, 종목명, 시장 구분 등
- **WATCHLIST**: 국내 관심종목
  - 사용자별 관심종목 관리
- **FOREIGN_WATCHLIST**: 해외 관심종목
  - 해외 주식 관심종목 관리

### BNPL (후불결제)
- **BNPL**: 후불결제 거래 내역
  - 결제 ID, 사용자 ID, 금액, 상태
  - 신용평가 결과, 승인/거부 정보
  - 연체 여부 및 상환 정보

### 금 현물
- **GOLD_HOLDINGS**: 금 보유 내역
  - 사용자별 금 보유량
  - 평균 매입가, 총 평가액

### 상품 (커머스)
- **PRODUCTS**: 상품 정보
  - 상품명, 가격, 재고, 카테고리

## 데이터베이스 설정

### 1. 테이블 생성

```bash
# Oracle SQL*Plus 또는 SQL Developer에서 실행
@01_hana_securities_ddl.sql
```

이 스크립트는 다음을 수행합니다:
- 모든 테이블 생성
- Primary Key 설정
- Foreign Key 관계 설정
- 인덱스 생성
- 제약조건 설정

### 2. 초기 데이터 삽입

```bash
@02_hana_securities_data.sql
```

이 스크립트는 다음을 수행합니다:
- 테스트용 사용자 데이터 삽입
- 샘플 상품 데이터 삽입
- 기본 설정 데이터 삽입

### 3. 주식 데이터 삽입

```bash
@insert_stock_data.sql
```

이 스크립트는 다음을 수행합니다:
- 국내 주요 종목 데이터 삽입
- 코스피, 코스닥 종목 정보

## 데이터베이스 설계 특징

### 정규화
- 3NF(Third Normal Form) 준수
- 데이터 중복 최소화
- 무결성 보장

### 인덱싱
- Primary Key 자동 인덱스
- 자주 조회되는 컬럼에 인덱스 설정
- 외래키 컬럼 인덱스

### 제약조건
- NOT NULL 제약
- UNIQUE 제약
- CHECK 제약
- Foreign Key 제약

## Redis 캐시

### 용도
- 세션 데이터 캐싱
- 실시간 시세 데이터 임시 저장
- API 응답 캐싱
- 토큰 정보 저장

### 주요 키 패턴
- `session:{user_id}`: 사용자 세션
- `quote:{stock_code}`: 실시간 호가
- `token:{type}`: API 토큰

## 데이터베이스 연결 정보

Spring Boot의 `application.yml` 또는 `application.properties`에서 다음 정보를 설정합니다:

```yaml
spring:
  datasource:
    url: jdbc:oracle:thin:@localhost:1521:xe
    username: your_username
    password: your_password
    driver-class-name: oracle.jdbc.OracleDriver

  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: true
    properties:
      hibernate:
        format_sql: true
```
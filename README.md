<h1>하나증권 : THE NEXT</h1>

<!-- 프로젝트 대표 이미지 -->
<div align="center">
  <img src="assets/images/logo.png" alt="하나증권 THE NEXT 로고" width="800"/>
</div>

<br/>

## 목차

1. [프로젝트 소개](#1-프로젝트-소개)
2. [주요 기능](#2-주요-기능)
3. [기술 스택](#3-기술-스택)
4. [프로젝트 구조](#4-프로젝트-구조)
5. [시작하기](#5-시작하기)
6. [개발 환경 설정](#6-개발-환경-설정)
7. [사용 방법](#7-사용-방법)
8. [연락처](#8-연락처)

<br/>

## 1. 프로젝트 소개

하나증권 THE NEXT는 차세대 금융 서비스 플랫폼입니다.
사용자 친화적인 인터페이스와 강력한 백엔드 시스템을 통해 최상의 증권 거래 경험을 제공합니다.

### 1-1. 프로젝트 목표

- 직관적이고 사용하기 쉬운 증권 거래 플랫폼 구축
- 안전하고 신뢰할 수 있는 금융 서비스 제공
- 실시간 시장 데이터 분석 및 제공
- 모던한 기술 스택을 활용한 확장 가능한 아키텍처 구현

<br/>

## 2. 주요 기능

### 2-1. 핵심 기능
- 실시간 주식 시세 조회
- 포트폴리오 관리
- 주문 및 거래 실행
- 시장 분석 도구
- 사용자 계정 관리

### 2-2. 차별화된 기능
- 개인화된 투자 인사이트
- 고급 차트 분석 도구
- 모바일 최적화 UI/UX
- 실시간 알림 시스템

<br/>

## 3. 기술 스택

### 3-1. Frontend
- **Framework**: React / Next.js
- **Language**: TypeScript
- **Styling**: Tailwind CSS / Styled-components
- **State Management**: Redux / Zustand
- **Data Fetching**: React Query

### 3-2. Backend
- **Runtime**: Node.js
- **Framework**: Express / NestJS
- **Language**: TypeScript
- **Authentication**: JWT
- **API**: RESTful API / GraphQL

### 3-3. Database
- **Primary DB**: PostgreSQL
- **Cache**: Redis
- **ORM**: Prisma / TypeORM

### 3-4. DevOps
- **Container**: Docker
- **CI/CD**: GitHub Actions
- **Cloud**: AWS / GCP
- **Monitoring**: Prometheus / Grafana

<br/>

## 4. 프로젝트 구조

```
HanaSecurities-TheNext/
├── HanaSecurities-TheNext-FE/    # Frontend 애플리케이션
│   ├── src/
│   ├── public/
│   └── package.json
│
├── HanaSecurities-TheNext-BE/    # Backend 애플리케이션
│   ├── src/
│   ├── tests/
│   └── package.json
│
├── HanaSecurities-TheNext-DB/    # Database 스키마 및 마이그레이션
│   ├── migrations/
│   ├── seeds/
│   └── schema/
│
├── assets/                        # 공통 에셋
│   ├── icons/
│   └── images/
│
└── README.md
```

<br/>

## 5. 시작하기

### 5-1. 사전 요구사항

- Node.js >= 18.0.0
- npm >= 9.0.0 or yarn >= 1.22.0
- Docker >= 20.10.0
- PostgreSQL >= 14.0

### 5-2. 설치

1. 레포지토리 클론

```bash
git clone https://github.com/dltkdgus482/HanaSecurities-TheNext.git
cd HanaSecurities-TheNext
```

2. Frontend 설정

```bash
cd HanaSecurities-TheNext-FE
npm install
```

3. Backend 설정

```bash
cd ../HanaSecurities-TheNext-BE
npm install
```

4. Database 설정

```bash
cd ../HanaSecurities-TheNext-DB
# 데이터베이스 마이그레이션 실행
npm run migrate
```

<br/>

## 6. 개발 환경 설정

### 6-1. 환경 변수 설정

각 서브 프로젝트에 `.env` 파일을 생성하여 필요한 환경 변수를 설정합니다.

**Frontend (.env.local)**
```env
NEXT_PUBLIC_API_URL=http://localhost:3001
NEXT_PUBLIC_WS_URL=ws://localhost:3001
```

**Backend (.env)**
```env
DATABASE_URL=postgresql://user:password@localhost:5432/hana_securities
JWT_SECRET=your-secret-key
PORT=3001
```

### 6-2. 개발 서버 실행

**Frontend**
```bash
cd HanaSecurities-TheNext-FE
npm run dev
```

**Backend**
```bash
cd HanaSecurities-TheNext-BE
npm run dev
```

<br/>

## 7. 사용 방법

1. 브라우저에서 `http://localhost:3000` 접속
2. 회원가입 또는 로그인
3. 대시보드에서 주요 기능 확인
4. 원하는 주식 검색 및 거래

자세한 사용 방법은 [사용자 가이드](docs/user-guide.md)를 참조하세요.

<br/>

## 8. 연락처

프로젝트 관련 문의사항이 있으시면 언제든지 아래로 연락주세요!

- **Email**: dltkdgus482@naver.com
- **GitHub**: https://github.com/dltkdgus482
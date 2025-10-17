# AI Server 구조

## 개요
Django 기반의 하나증권 THE NEXT AI 서버입니다. 증권계좌 거래내역 기반 대안신용평가 모델을 제공하며, BNPL 후불결제 서비스의 신용평가를 담당합니다.

## 기술 스택
- **Framework**: Django 4.2+
- **API**: Django REST Framework 3.14+
- **Language**: Python 3.9+
- **ML Libraries**: scikit-learn, XGBoost, LightGBM
- **Model**: Ensemble Model (Random Forest + XGBoost + LightGBM)

## 프로젝트 구조

```
HanaSecurities-TheNext-AI/
├── backend/                         # Django 서버
│   ├── config/                      # Django 설정
│   │   ├── settings.py             # 서버 설정
│   │   └── urls.py                 # URL 라우팅
│   │
│   ├── predictions/                 # 신용평가 앱
│   │   ├── views.py                # API 엔드포인트
│   │   ├── services.py             # ML 모델 로직
│   │   ├── serializers.py          # 데이터 직렬화
│   │   ├── urls.py                 # 앱 URL 라우팅
│   │   └── migrations/             # DB 마이그레이션
│   │
│   ├── manage.py                   # Django 관리 스크립트
│   └── requirements.txt            # Python 패키지 의존성
│
├── ml_models/                       # 머신러닝 모델
│   ├── models/                     # 학습된 모델 파일
│   │   ├── ensemble_model.pkl      # 앙상블 모델 (4.15MB)
│   │   ├── xgb_model.pkl          # XGBoost 모델 (4.15MB)
│   │   └── feature_importance.npy  # 특성 중요도
│   │
│   ├── data/                       # 전처리 데이터
│   │   ├── scaler.pkl             # 데이터 스케일러
│   │   └── feature_names.txt      # 특성 이름 목록
│   │
│   └── scripts/                    # ML 스크립트
│       └── predict.py             # 예측 로직
│
├── test_sample_data.py             # 테스트 스크립트
└── README.md
```

## 주요 기능

### 1. 연체 예측 API
- 단일 고객 연체 확률 예측
- 리스크 등급 분류 (LOW/MEDIUM/HIGH/CRITICAL)
- 주요 위험 요인 분석
- 투자 데이터 기반 신용평가

### 2. RAM (Risk Adjusted Margin) 계산 API
- 위험조정마진 자동 계산
- 리스크 프리미엄 계수(k) 조정 가능
- 수익성 분석 및 해석 제공

### 3. 샘플 데이터 API
- 우량 고객 프로필 샘플 제공
- 테스트 및 데모용 데이터

### 4. 헬스체크 API
- 서버 상태 확인
- 모델 로드 상태 검증

## 머신러닝 모델

### 앙상블 모델 구성
- Random Forest
- XGBoost
- LightGBM

### 평가 지표
모델은 다음 지표를 기반으로 평가되었습니다:
- Accuracy
- Precision
- Recall
- F1-Score
- AUC-ROC

### 특성 중요도
투자 거래 패턴 기반 신용평가를 위해 다음 특성들이 사용됩니다:
- 거래 빈도
- 평균 거래 금액
- 보유 종목 수
- 수익률
- 변동성 지표

## 개발 환경 설정

### 1. 가상환경 생성 및 활성화

```bash
cd backend
python -m venv venv

# Windows
venv\Scripts\activate

# macOS/Linux
source venv/bin/activate
```

### 2. 패키지 설치

```bash
pip install -r requirements.txt
```

### 3. 서버 실행

```bash
python manage.py runserver
```

서버가 정상적으로 실행되면 `http://localhost:8000`에서 접속 가능합니다.

## API 테스트

### 테스트 스크립트 실행

프로젝트 루트 디렉토리에서 다음 명령어를 실행하세요:

```bash
python test_sample_data.py
```

**테스트 내용:**
- 우량 고객 샘플 데이터 조회
- 샘플 데이터로 연체 예측 실행
- 샘플 데이터로 RAM 계산 (k=0.313, k=0.626 비교)
- 결과 요약 출력

## API 엔드포인트

| 엔드포인트 | 메서드 | 설명 |
|-----------|--------|------|
| `/api/v1/predictions/` | POST | 단일 고객 연체 예측 |
| `/api/v1/ram/` | POST | RAM 계산 |
| `/api/v1/sample-data/` | GET | 우량 고객 샘플 데이터 |
| `/api/v1/health/` | GET | 서버 상태 확인 |

## Spring Boot 서버 연동

Spring Boot 백엔드 서버에서 BNPL 신용평가 요청 시 이 AI 서버의 예측 API를 호출합니다.

### 연동 흐름
1. 사용자가 BNPL 결제 요청
2. Spring Boot 서버가 거래 데이터 수집
3. AI 서버 `/api/v1/predictions/` 호출
4. 신용평가 결과 수신
5. 승인/거부 결정 및 한도 설정

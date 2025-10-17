# HanaSecurities-TheNext-ACSS
## Alternative Credit Scoring System - 연체 예측 및 RAM 계산 API

---

## 📋 목차

1. [주요 기능](#주요-기능)
2. [기술 스택](#기술-스택)
3. [설치 및 실행](#설치-및-실행)
4. [API 테스트](#api-테스트)
5. [프로젝트 구조](#프로젝트-구조)
6. [API 문서](#api-문서)

---

## 주요 기능

### 1. 연체 예측 API
- 단일 고객 연체 확률 예측
- 리스크 등급 분류 (LOW/MEDIUM/HIGH/CRITICAL)
- 주요 위험 요인 분석

### 2. RAM 계산 API
- 위험조정마진 자동 계산
- 리스크 프리미엄 계수(k) 조정 가능
- 수익성 분석 및 해석 제공

### 3. 샘플 데이터 API
- 우량 고객 프로필 샘플 제공
- 테스트 및 데모용 데이터

---

## 기술 스택

### Backend
- **Framework**: Django 4.2+
- **API**: Django REST Framework 3.14+
- **Language**: Python 3.9+

### Machine Learning
- **Libraries**: scikit-learn, XGBoost, LightGBM
- **Model**: Ensemble Model (Random Forest + XGBoost + LightGBM)

---

## 설치 및 실행

### 1. 프로젝트 클론
```bash
git clone https://github.com/your-repo/HanaSecurities-TheNext-ACSS.git
cd HanaSecurities-TheNext-ACSS
```

### 2. 가상환경 설정 (Windows)

#### 2-1. 가상환경 생성
```bash
cd backend
python -m venv venv
```

#### 2-2. 가상환경 활성화
```bash
# Windows
venv\Scripts\activate

# macOS/Linux
source venv/bin/activate
```

### 3. 패키지 설치
```bash
pip install -r requirements.txt
```

### 4. 서버 실행
```bash
python manage.py runserver
```

서버가 정상적으로 실행되면 `http://localhost:8000`에서 접속 가능합니다.

---

## API 테스트

### 테스트 스크립트 실행

프로젝트 루트 디렉토리에서 다음 명령어를 실행하세요:

```bash
python test_sample_data.py
```

**테스트 내용:**
- ✅ 우량 고객 샘플 데이터 조회
- ✅ 샘플 데이터로 연체 예측 실행
- ✅ 샘플 데이터로 RAM 계산 (k=0.313, k=0.626 비교)
- ✅ 결과 요약 출력

---

## 프로젝트 구조

```
HanaSecurities-TheNext-ACSS/
├── backend/                    # Django 백엔드
│   ├── config/                # Django 설정
│   │   ├── settings.py
│   │   └── urls.py           # 메인 URL 라우팅
│   ├── predictions/           # 예측 앱
│   │   ├── views.py          # API 뷰
│   │   ├── urls.py           # 예측 API URL
│   │   ├── services.py       # 비즈니스 로직
│   │   └── serializers.py    # 데이터 검증
│   ├── manage.py
│   └── requirements.txt
├── ml_models/                 # 머신러닝 모델
│   ├── models/               # 학습된 모델 파일
│   │   ├── ensemble_model.pkl
│   │   └── feature_importance.npy
│   ├── data/                 # 전처리 데이터
│   │   ├── scaler.pkl
│   │   └── feature_names.txt
│   └── scripts/              # ML 스크립트
│       └── predict.py        # 예측 로직
├── docs/                      # 문서
│   └── API_SPECIFICATION.md  # API 명세서
├── test_sample_data.py       # API 테스트 스크립트
└── README.md
```

---

## API 문서

### API 엔드포인트 목록

| 엔드포인트 | 메서드 | 설명 |
|-----------|--------|------|
| `/api/v1/predictions/` | POST | 단일 고객 연체 예측 |
| `/api/v1/ram/` | POST | RAM 계산 |
| `/api/v1/sample-data/` | GET | 우량 고객 샘플 데이터 |
| `/api/v1/health/` | GET | 서버 상태 확인 |
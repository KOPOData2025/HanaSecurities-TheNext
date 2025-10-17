"""
API Views for prediction endpoints
"""

from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework import status
from django.conf import settings

from .services import PredictionService
from .serializers import (
    PredictionRequestSerializer,
    BatchPredictionRequestSerializer
)


class PredictionView(APIView):
    """단일 고객 연체 예측 API"""

    def __init__(self, **kwargs):
        super().__init__(**kwargs)
        self.prediction_service = PredictionService()

    def post(self, request):
        """
        POST /api/v1/predictions/
        단일 고객 연체 확률 예측
        """
        # 요청 데이터 검증
        serializer = PredictionRequestSerializer(data=request.data)

        if not serializer.is_valid():
            return Response(
                {
                    'success': False,
                    'error': {
                        'code': 'INVALID_INPUT',
                        'message': '입력 데이터 검증 실패',
                        'details': serializer.errors
                    }
                },
                status=status.HTTP_400_BAD_REQUEST
            )

        try:
            # 예측 수행
            validated_data = serializer.validated_data
            customer_data = validated_data['customer_data']
            threshold = validated_data.get('threshold', 0.5)

            result = self.prediction_service.predict_single(
                customer_data=customer_data,
                threshold=threshold
            )

            return Response(
                {
                    'success': True,
                    'data': result
                },
                status=status.HTTP_200_OK
            )

        except Exception as e:
            return Response(
                {
                    'success': False,
                    'error': {
                        'code': 'PREDICTION_ERROR',
                        'message': '예측 처리 중 오류가 발생했습니다.',
                        'details': str(e)
                    }
                },
                status=status.HTTP_500_INTERNAL_SERVER_ERROR
            )


class BatchPredictionView(APIView):
    """배치 예측 API (다수 고객)"""

    def __init__(self, **kwargs):
        super().__init__(**kwargs)
        self.prediction_service = PredictionService()

    def post(self, request):
        """
        POST /api/v1/predictions/batch/
        다수 고객 연체 확률 예측
        """
        # 요청 데이터 검증
        serializer = BatchPredictionRequestSerializer(data=request.data)

        if not serializer.is_valid():
            return Response(
                {
                    'success': False,
                    'error': {
                        'code': 'INVALID_INPUT',
                        'message': '입력 데이터 검증 실패',
                        'details': serializer.errors
                    }
                },
                status=status.HTTP_400_BAD_REQUEST
            )

        try:
            # 배치 예측 수행
            validated_data = serializer.validated_data
            customers = validated_data['customers']
            threshold = validated_data.get('threshold', 0.5)

            result = self.prediction_service.predict_batch(
                customers=customers,
                threshold=threshold
            )

            return Response(
                {
                    'success': True,
                    'data': result
                },
                status=status.HTTP_200_OK
            )

        except Exception as e:
            return Response(
                {
                    'success': False,
                    'error': {
                        'code': 'BATCH_PREDICTION_ERROR',
                        'message': '배치 예측 처리 중 오류가 발생했습니다.',
                        'details': str(e)
                    }
                },
                status=status.HTTP_500_INTERNAL_SERVER_ERROR
            )


class RAMCalculationView(APIView):
    """RAM(Risk-Adjusted Margin) 계산 API"""

    def __init__(self, **kwargs):
        super().__init__(**kwargs)
        self.prediction_service = PredictionService()

    def post(self, request):
        """
        POST /api/v1/ram/
        RAM 계산: MDR + PD * (연체이자율 x k) - PD * LGD

        Parameters:
        - customer_data: 고객 데이터
        - threshold: 연체 예측 임계값 (기본값: 0.5)
        - k: 리스크 프리미엄 계수 (기본값: 0.626, 범위: 0.313 ~ 0.626)
        """
        # 요청 데이터 검증
        serializer = PredictionRequestSerializer(data=request.data)

        if not serializer.is_valid():
            return Response(
                {
                    'success': False,
                    'error': {
                        'code': 'INVALID_INPUT',
                        'message': '입력 데이터 검증 실패',
                        'details': serializer.errors
                    }
                },
                status=status.HTTP_400_BAD_REQUEST
            )

        try:
            # 예측 수행하여 PD 계산
            validated_data = serializer.validated_data
            customer_data = validated_data['customer_data']
            threshold = validated_data.get('threshold', 0.5)

            # k값 검증 (0.313 ~ 0.626)
            k = request.data.get('k', 0.626)
            if not isinstance(k, (int, float)) or k < 0.313 or k > 0.626:
                return Response(
                    {
                        'success': False,
                        'error': {
                            'code': 'INVALID_K_VALUE',
                            'message': 'k값은 0.313 ~ 0.626 사이여야 합니다.',
                            'details': f'입력된 k값: {k}'
                        }
                    },
                    status=status.HTTP_400_BAD_REQUEST
                )

            # 연체 예측 수행
            prediction_result = self.prediction_service.predict_single(
                customer_data=customer_data,
                threshold=threshold
            )

            # RAM 계산 상수
            MDR = 0.035  # 가맹점 수수료율
            DEFAULT_INTEREST_RATE = 0.1  # 연체이자율
            LGD = 0.626  # 연체 시 손실률 (1 - 회수율)

            # PD (Probability of Default)
            PD = prediction_result['default_probability']

            # RAM 공식: MDR + PD * (연체이자율 x k) - PD * LGD
            ram = MDR + PD * (DEFAULT_INTEREST_RATE * k) - PD * LGD

            return Response(
                {
                    'success': True,
                    'data': {
                        'ram': round(ram, 6),
                        'ram_percent': f"{round(ram * 100, 4)}%",
                        'components': {
                            'mdr': MDR,
                            'pd': round(PD, 6),
                            'default_interest_rate': DEFAULT_INTEREST_RATE,
                            'k': k,
                            'lgd': LGD,
                            'revenue_component': round(PD * (DEFAULT_INTEREST_RATE * k), 6),
                            'loss_component': round(PD * LGD, 6)
                        },
                        'prediction': {
                            'will_default': prediction_result['will_default'],
                            'default_probability': prediction_result['default_probability'],
                            'default_probability_percent': prediction_result['default_probability_percent'],
                            'risk_level': prediction_result['risk_level']
                        },
                        'interpretation': self._interpret_ram(ram)
                    }
                },
                status=status.HTTP_200_OK
            )

        except Exception as e:
            return Response(
                {
                    'success': False,
                    'error': {
                        'code': 'RAM_CALCULATION_ERROR',
                        'message': 'RAM 계산 중 오류가 발생했습니다.',
                        'details': str(e)
                    }
                },
                status=status.HTTP_500_INTERNAL_SERVER_ERROR
            )

    def _interpret_ram(self, ram):
        """RAM 값 해석"""
        if ram >= 0.03:
            return "높은 수익성 - 우수 고객"
        elif ram >= 0.02:
            return "중간 수익성 - 양호 고객"
        elif ram >= 0.01:
            return "낮은 수익성 - 주의 고객"
        else:
            return "마이너스 수익성 - 위험 고객"


class SampleDataView(APIView):
    """우량 고객 샘플 데이터 API"""

    def get(self, request):
        """
        GET /api/v1/sample-data/
        우량 고객(저위험) 샘플 데이터 반환
        """
        # 우량 고객 샘플 데이터 (저위험 프로필)
        sample_data = {
            "customer_data": {
                "SCORE": 850,              # KCB Score (우수)
                "SCORE_6M": 840,           # 6개월전 KCB Score (상승 추세)
                "L10173000": 3650,         # 신용거래 가능계좌 수 (10개)
                "L10231000": 50000000,     # 신용융자 실행건수 (5천만원)
                "C1Z001373": 2000000,      # 3년내 신용융자 실행건수 (200만원)
                "C1L120167": 1825,         # 최근계좌개설경과일수 (5년)
                "C1L120237": 0.15,         # 신용거래한도소진율 (15% - 매우 낮음)
                "C1M2B4W03": 1500000,      # 3개월내 증권계좌 투자액 (150만원)
                "L1021000C": 0,            # 1년내 신규투자건수 (없음)
                "L1021000F": 1,            # 3년내 신규투자건수 (1건)
                "L10220800": 0,            # 타증권사계좌수 (없음)
                "C1Z001386": 8000000,      # 1년내 증권계좌 투자액 (800만원)
                "C1L120161": 2920,         # 최초계좌개설경과일수 (8년)
                "C1L120004": 2920,         # 최초투자계좌개설경과일수 (8년)
                "C1L120041": 4,            # 보유계좌건수 (4개)
                "AL012G005": 1,            # 3년내 주소이력건수 (안정적)
                "C1M2B5W03": 500000,       # 3개월내 신용융자 이용액 (50만원)
                "AL012G019": 0,            # 3년내 휴대폰번호이력건수 (안정적)
                "C1L120163": 730,          # 최근투자계좌개설경과일수 (2년)
                "AGE_BAND": 4,             # 연령대 (40대)
                "C1L120084": 4,            # 유효거래계좌건수 (4개)
                "C1L120049": 0.1,          # 융자한도소진율 (10% - 낮음)
                "L10216000": 1,            # 신용융자건수 (1건)
                "U81301010": 800000000     # 예수금 (8억원)
            },
            "threshold": 0.05,             # 연체 판정 임계값
            "description": {
                "profile": "우량 고객 샘플",
                "characteristics": [
                    "높은 신용점수 (850점)",
                    "낮은 신용거래한도소진율 (15%)",
                    "안정적인 투자 이력 (10년)",
                    "적은 신규 투자 건수",
                    "높은 예수금 (8억원)",
                    "안정적인 주소/연락처 이력"
                ],
                "expected_result": {
                    "will_default": False,
                    "risk_level": "LOW",
                    "default_probability_range": "< 5%"
                }
            }
        }

        return Response(
            {
                'success': True,
                'data': sample_data
            },
            status=status.HTTP_200_OK
        )


class HealthCheckView(APIView):
    """헬스 체크 API"""

    def __init__(self, **kwargs):
        super().__init__(**kwargs)
        self.prediction_service = PredictionService()

    def get(self, request):
        """
        GET /api/v1/health/
        API 서버 상태 확인
        """
        try:
            from datetime import datetime
            from django.db import connection

            # 데이터베이스 연결 확인
            db_connected = True
            try:
                connection.ensure_connection()
            except:
                db_connected = False

            # 모델 정보 조회
            model_info = self.prediction_service.get_model_info()

            return Response(
                {
                    'status': 'healthy',
                    'timestamp': datetime.now().isoformat(),
                    'version': '1.0.0',
                    'model': model_info,
                    'database': {
                        'connected': db_connected
                    }
                },
                status=status.HTTP_200_OK
            )

        except Exception as e:
            return Response(
                {
                    'status': 'unhealthy',
                    'error': str(e)
                },
                status=status.HTTP_503_SERVICE_UNAVAILABLE
            )

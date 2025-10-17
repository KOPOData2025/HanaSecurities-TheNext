"""
URL Configuration for predictions app
"""

from django.urls import path
from .views import PredictionView, BatchPredictionView, HealthCheckView, RAMCalculationView, SampleDataView

app_name = 'predictions'

urlpatterns = [
    # 단일 예측
    path('predictions/', PredictionView.as_view(), name='predict-single'),

    # 배치 예측
    path('predictions/batch/', BatchPredictionView.as_view(), name='predict-batch'),

    # RAM 계산
    path('ram/', RAMCalculationView.as_view(), name='ram-calculation'),

    # 샘플 데이터
    path('sample-data/', SampleDataView.as_view(), name='sample-data'),

    # 헬스 체크
    path('health/', HealthCheckView.as_view(), name='health-check'),
]

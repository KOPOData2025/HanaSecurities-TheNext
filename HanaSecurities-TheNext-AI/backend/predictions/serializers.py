"""
DRF Serializers for API request/response validation
"""

from rest_framework import serializers


class CustomerDataSerializer(serializers.Serializer):
    """고객 신용정보 데이터 검증"""

    # 필수 필드
    SCORE = serializers.IntegerField(required=True, min_value=300, max_value=1000)
    SCORE_6M = serializers.IntegerField(required=True, min_value=300, max_value=1000)
    L10173000 = serializers.IntegerField(required=True, min_value=0)
    L10231000 = serializers.IntegerField(required=True, min_value=0)
    C1Z001373 = serializers.IntegerField(required=True, min_value=0)
    C1L120167 = serializers.IntegerField(required=True, min_value=0)
    C1L120237 = serializers.FloatField(required=True, min_value=0, max_value=1)
    C1M2B4W03 = serializers.IntegerField(required=True, min_value=0)
    L1021000C = serializers.IntegerField(required=True, min_value=0)
    L1021000F = serializers.IntegerField(required=True, min_value=0)
    L10220800 = serializers.IntegerField(required=True, min_value=0)
    C1Z001386 = serializers.IntegerField(required=True, min_value=0)
    C1L120161 = serializers.IntegerField(required=True, min_value=0)
    C1L120004 = serializers.IntegerField(required=True, min_value=0)
    C1L120041 = serializers.IntegerField(required=True, min_value=0)
    AL012G005 = serializers.IntegerField(required=True, min_value=0)
    C1M2B5W03 = serializers.IntegerField(required=True, min_value=0)
    AL012G019 = serializers.IntegerField(required=True, min_value=0)
    C1L120163 = serializers.IntegerField(required=True, min_value=0)
    AGE_BAND = serializers.IntegerField(required=True, min_value=1, max_value=7)
    C1L120084 = serializers.IntegerField(required=True, min_value=0)
    C1L120049 = serializers.FloatField(required=True, min_value=0, max_value=1)
    L10216000 = serializers.IntegerField(required=True, min_value=0)
    U81301010 = serializers.IntegerField(required=True, min_value=0)


class PredictionRequestSerializer(serializers.Serializer):
    """단일 예측 요청 검증"""

    customer_data = CustomerDataSerializer(required=True)
    threshold = serializers.FloatField(required=False, default=0.5, min_value=0, max_value=1)


class BatchCustomerSerializer(serializers.Serializer):
    """배치 예측 고객 데이터 검증"""

    customer_id = serializers.CharField(required=True, max_length=100)
    customer_data = CustomerDataSerializer(required=True)


class BatchPredictionRequestSerializer(serializers.Serializer):
    """배치 예측 요청 검증"""

    customers = serializers.ListField(
        child=BatchCustomerSerializer(),
        required=True,
        min_length=1,
        max_length=1000
    )
    threshold = serializers.FloatField(required=False, default=0.5, min_value=0, max_value=1)


class PredictionResponseSerializer(serializers.Serializer):
    """예측 응답 직렬화"""

    success = serializers.BooleanField()
    data = serializers.DictField()


class ErrorResponseSerializer(serializers.Serializer):
    """에러 응답 직렬화"""

    success = serializers.BooleanField(default=False)
    error = serializers.DictField()

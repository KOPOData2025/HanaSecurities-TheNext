"""
연체 예측 서비스 레이어
ML 모델 통합 및 예측 로직 처리
"""

import os
import sys
from typing import Dict, List
from datetime import datetime
import uuid

# ML 모델 경로 추가
ML_MODELS_PATH = os.path.join(os.path.dirname(os.path.dirname(os.path.dirname(__file__))), 'ml_models', 'scripts')
sys.path.append(ML_MODELS_PATH)

from predict import DefaultPredictor


class PredictionService:
    """연체 예측 서비스"""

    _instance = None
    _predictor = None

    def __new__(cls):
        if cls._instance is None:
            cls._instance = super().__new__(cls)
        return cls._instance

    def __init__(self):
        if self._predictor is None:
            self._load_model()

    def _load_model(self):
        """ML 모델 로드 (싱글톤 패턴)"""
        try:
            base_path = os.path.dirname(os.path.dirname(os.path.dirname(__file__)))
            model_dir = os.path.join(base_path, 'ml_models', 'models')
            data_dir = os.path.join(base_path, 'ml_models', 'data')

            self._predictor = DefaultPredictor(model_dir=model_dir, data_dir=data_dir)
            print(" ML 모델 로드 완료")
        except Exception as e:
            print(f"❌ 모델 로드 실패: {e}")
            raise

    def predict_single(self, customer_data: Dict, threshold: float = 0.5) -> Dict:
        """
        단일 고객 연체 예측

        Args:
            customer_data: 고객 신용정보
            threshold: 연체 판정 임계값

        Returns:
            예측 결과 딕셔너리
        """
        try:
            # 예측 수행
            result = self._predictor.predict(customer_data)

            # 연체 여부 판정
            will_default = result['default_probability'] >= threshold

            # 예측 ID 생성
            prediction_id = f"pred_{datetime.now().strftime('%Y%m%d')}_{uuid.uuid4().hex[:8]}"

            # 응답 데이터 구성
            response = {
                'prediction_id': prediction_id,
                'timestamp': datetime.now().isoformat(),
                'will_default': will_default,
                'default_probability': result['default_probability'],
                'default_probability_percent': result['default_probability_percent'],
                'risk_level': result['risk_level'],
                'confidence': result['confidence'],
                'threshold_used': threshold,
                'top_risk_factors': self._format_risk_factors(result['top_risk_factors']),
                'recommendation': self._generate_recommendation(result)
            }

            return response

        except Exception as e:
            raise Exception(f"예측 처리 중 오류 발생: {str(e)}")

    def predict_batch(self, customers: List[Dict], threshold: float = 0.5) -> Dict:
        """
        배치 예측 (다수 고객)

        Args:
            customers: 고객 데이터 리스트 (customer_id, customer_data 포함)
            threshold: 연체 판정 임계값

        Returns:
            배치 예측 결과
        """
        try:
            batch_id = f"batch_{datetime.now().strftime('%Y%m%d')}_{uuid.uuid4().hex[:8]}"
            results = []
            failed_count = 0

            # 각 고객별 예측
            for customer in customers:
                try:
                    customer_id = customer.get('customer_id')
                    customer_data = customer.get('customer_data')

                    # 예측 수행
                    prediction = self._predictor.predict(customer_data)
                    will_default = prediction['default_probability'] >= threshold

                    results.append({
                        'customer_id': customer_id,
                        'success': True,
                        'will_default': will_default,
                        'default_probability': prediction['default_probability'],
                        'default_probability_percent': prediction['default_probability_percent'],
                        'risk_level': prediction['risk_level']
                    })

                except Exception as e:
                    failed_count += 1
                    results.append({
                        'customer_id': customer.get('customer_id'),
                        'success': False,
                        'error': str(e)
                    })

            # 요약 통계
            successful_results = [r for r in results if r.get('success')]
            summary = self._calculate_batch_summary(successful_results)

            return {
                'batch_id': batch_id,
                'timestamp': datetime.now().isoformat(),
                'total_count': len(customers),
                'processed_count': len(successful_results),
                'failed_count': failed_count,
                'results': results,
                'summary': summary
            }

        except Exception as e:
            raise Exception(f"배치 예측 처리 중 오류 발생: {str(e)}")

    def _format_risk_factors(self, risk_factors: List[Dict]) -> List[Dict]:
        """위험 요인 포맷팅"""

        # 변수명 매핑 (주식/투자 관련 용어)
        feature_name_mapping = {
            'SCORE': 'KCB Score',
            'SCORE_6M': '6개월전 KCB Score',
            'L10173000': '신용거래 가능계좌 수',
            'L10231000': '신용융자 실행건수',
            'C1Z001373': '3년내 신용융자 실행건수',
            'C1L120167': '최근계좌개설경과일수',
            'C1L120237': '신용거래한도소진율',
            'C1M2B4W03': '3개월내 증권계좌 투자액',
            'L1021000C': '1년내 신규투자건수',
            'L1021000F': '3년내 신규투자건수',
            'L10220800': '타증권사계좌수',
            'C1Z001386': '1년내 증권계좌 투자액',
            'C1L120161': '최초계좌개설경과일수',
            'C1L120004': '최초투자계좌개설경과일수',
            'C1L120041': '보유계좌건수',
            'AL012G005': '3년내 주소이력건수',
            'C1M2B5W03': '3개월내 신용융자 이용액',
            'AL012G019': '3년내 휴대폰번호이력건수',
            'C1L120163': '최근투자계좌개설경과일수',
            'AGE_BAND': '연령대',
            'C1L120084': '유효거래계좌건수',
            'C1L120049': '융자한도소진율',
            'L10216000': '신용융자건수',
            'U81301010': '예수금'
        }

        formatted_factors = []
        for factor in risk_factors[:5]:  # 상위 5개만
            feature_code = factor['feature']
            feature_name = feature_name_mapping.get(feature_code, feature_code)

            formatted_factors.append({
                'feature': feature_code,
                'feature_name': feature_name,
                'value': factor['value'],
                'importance': factor['importance'],
                'contribution': factor['contribution'],
                'description': self._get_factor_description(feature_code, factor['value'], feature_name)
            })

        return formatted_factors

    def _get_factor_description(self, feature_code: str, value: float, feature_name: str) -> str:
        """위험 요인 설명 생성"""

        # 비율 변수
        if feature_code in ['C1L120237', 'C1L120049']:
            return f"{feature_name}: {value*100:.1f}%"

        # 금액 변수
        elif feature_code in ['L10231000', 'C1Z001373', 'C1M2B4W03', 'C1Z001386', 'C1M2B5W03', 'U81301010']:
            return f"{feature_name}: {int(value):,}원"

        # 일수 변수
        elif 'L10' in feature_code or 'C1L120' in feature_code:
            return f"{feature_name}: {int(value)}일"

        # 건수 변수
        elif '건수' in feature_name:
            return f"{feature_name}: {int(value)}건"

        # 기타
        else:
            return f"{feature_name}: {value}"

    def _generate_recommendation(self, prediction_result: Dict) -> Dict:
        """예측 결과 기반 추천 메시지 생성"""

        risk_level = prediction_result['risk_level']
        probability = prediction_result['default_probability']

        if risk_level == 'Very Low':
            return {
                'level': 'LOW_RISK',
                'message': '신용 상태가 매우 양호합니다.',
                'actions': [
                    '현재 수준의 신용 관리를 지속하시기 바랍니다.',
                    '정기적인 신용 점검을 권장합니다.'
                ]
            }
        elif risk_level == 'Low':
            return {
                'level': 'LOW_RISK',
                'message': '신용 상태가 양호합니다.',
                'actions': [
                    '카드 한도 소진율을 30% 이하로 유지하시면 더욱 안정적입니다.',
                    '신규 대출은 신중하게 결정하시기 바랍니다.'
                ]
            }
        elif risk_level == 'Medium':
            return {
                'level': 'MEDIUM_RISK',
                'message': '신용 관리에 주의가 필요합니다.',
                'actions': [
                    '카드 사용을 줄이고 한도 소진율을 낮추시기 바랍니다.',
                    '신규 대출을 자제하고 기존 대출 상환에 집중하세요.',
                    '정기적인 신용 점수 모니터링을 권장합니다.'
                ]
            }
        elif risk_level == 'High':
            return {
                'level': 'HIGH_RISK',
                'message': '신용 관리에 각별한 주의가 필요합니다.',
                'actions': [
                    '즉시 카드 사용을 최소화하시기 바랍니다.',
                    '대출 상환 계획을 재검토하세요.',
                    '금융 상담을 받으시길 권장합니다.'
                ]
            }
        else:  # Very High
            return {
                'level': 'VERY_HIGH_RISK',
                'message': '신용 위험도가 매우 높습니다.',
                'actions': [
                    '긴급 재무 상담이 필요합니다.',
                    '채무 조정 프로그램을 검토하세요.',
                    '전문가의 도움을 받으시길 강력히 권장합니다.'
                ]
            }

    def _calculate_batch_summary(self, results: List[Dict]) -> Dict:
        """배치 예측 요약 통계"""

        if not results:
            return {
                'high_risk_count': 0,
                'medium_risk_count': 0,
                'low_risk_count': 0,
                'average_probability': 0.0
            }

        risk_counts = {
            'Very High': 0,
            'High': 0,
            'Medium': 0,
            'Low': 0,
            'Very Low': 0
        }

        total_probability = 0.0

        for result in results:
            risk_level = result.get('risk_level')
            risk_counts[risk_level] = risk_counts.get(risk_level, 0) + 1
            total_probability += result.get('default_probability', 0.0)

        return {
            'high_risk_count': risk_counts['Very High'] + risk_counts['High'],
            'medium_risk_count': risk_counts['Medium'],
            'low_risk_count': risk_counts['Low'] + risk_counts['Very Low'],
            'average_probability': total_probability / len(results) if results else 0.0
        }

    def get_model_info(self) -> Dict:
        """모델 정보 조회"""

        return {
            'loaded': self._predictor is not None,
            'version': 'ensemble_v1',
            'features_count': len(self._predictor.feature_names) if self._predictor and hasattr(self._predictor, 'feature_names') else 0
        }

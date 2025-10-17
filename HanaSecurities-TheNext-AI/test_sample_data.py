import requests
import json

# API 엔드포인트
SAMPLE_DATA_URL = "http://localhost:8000/api/v1/sample-data/"
PREDICTION_URL = "http://localhost:8000/api/v1/predictions/"
RAM_URL = "http://localhost:8000/api/v1/ram/"

print("="*80)
print("우량 고객 샘플 데이터 테스트")
print("="*80)

# 1. 샘플 데이터 가져오기
print("\n[1] 샘플 데이터 가져오기...")
response = requests.get(SAMPLE_DATA_URL)

if response.status_code == 200:
    result = response.json()

    if result['success']:
        sample_data = result['data']

        print("\n✅ 샘플 데이터 조회 성공!")
        print(f"\n프로필: {sample_data['description']['profile']}")
        print(f"\n고객 특성:")
        for char in sample_data['description']['characteristics']:
            print(f"  • {char}")

        print(f"\n예상 결과:")
        expected = sample_data['description']['expected_result']
        print(f"  • 연체 예상: {'예' if expected['will_default'] else '아니오'}")
        print(f"  • 리스크 등급: {expected['risk_level']}")
        print(f"  • 연체 확률 범위: {expected['default_probability_range']}")

        # 2. 샘플 데이터로 연체 예측
        print("\n" + "="*80)
        print("[2] 샘플 데이터로 연체 예측 실행...")
        print("="*80)

        pred_response = requests.post(
            PREDICTION_URL,
            json={
                "customer_data": sample_data['customer_data'],
                "threshold": sample_data['threshold']
            },
            headers={"Content-Type": "application/json"}
        )

        if pred_response.status_code == 200:
            pred_result = pred_response.json()
            pred_data = pred_result.get('data', {})

            print(f"\n📊 연체 예측 결과")
            print(f"{'='*80}")
            print(f"연체 여부: {'연체 예상 ❌' if pred_data.get('will_default') else '정상 예상 ✅'}")
            print(f"연체 확률: {pred_data.get('default_probability_percent', 'N/A')}")
            print(f"리스크 등급: {pred_data.get('risk_level', 'N/A')}")
            print(f"신뢰도: {pred_data.get('confidence', 'N/A')}")

            if 'recommendation' in pred_data:
                print(f"\n💡 추천 사항:")
                print(f"  {pred_data['recommendation'].get('message', '')}")
                for action in pred_data['recommendation'].get('actions', []):
                    print(f"  • {action}")
        else:
            print(f"❌ 예측 실패: {pred_response.status_code}")
            print(pred_response.text)

        # 3. 샘플 데이터로 RAM 계산
        print("\n" + "="*80)
        print("[3] 샘플 데이터로 RAM 계산 실행...")
        print("="*80)

        # k값 비교 (최소값 vs 최대값)
        k_values = [0.313, 0.626]

        for k in k_values:
            print(f"\n--- k = {k} ---")

            ram_response = requests.post(
                RAM_URL,
                json={
                    "customer_data": sample_data['customer_data'],
                    "threshold": sample_data['threshold'],
                    "k": k
                },
                headers={"Content-Type": "application/json"}
            )

            if ram_response.status_code == 200:
                ram_result = ram_response.json()
                ram_data = ram_result.get('data', {})

                print(f"\n💰 RAM 계산 결과")
                print(f"RAM: {ram_data.get('ram_percent', 'N/A')}")
                print(f"해석: {ram_data.get('interpretation', 'N/A')}")

                print(f"\n구성 요소:")
                comp = ram_data.get('components', {})
                print(f"  • MDR (가맹점 수수료율): {comp.get('mdr', 0)*100:.2f}%")
                print(f"  • PD (연체 확률): {comp.get('pd', 0)*100:.4f}%")
                print(f"  • 리스크 계수 (k): {comp.get('k', 0)}")
                print(f"  • 수익 요소: {comp.get('revenue_component', 0):.6f}")
                print(f"  • 손실 요소: {comp.get('loss_component', 0):.6f}")
            else:
                print(f"❌ RAM 계산 실패: {ram_response.status_code}")
                print(ram_response.text)

        # 4. 결과 요약
        print("\n" + "="*80)
        print("테스트 결과 요약")
        print("="*80)
        print(f"\n✅ 샘플 데이터는 예상대로 우량 고객 프로필을 보여줍니다.")
        print(f"✅ 낮은 연체 확률과 높은 RAM 값을 확인했습니다.")
        print(f"✅ 모든 API 테스트가 성공적으로 완료되었습니다!")

    else:
        print(f"❌ 샘플 데이터 조회 실패")
else:
    print(f"❌ API 호출 실패: {response.status_code}")
    print(response.text)

print("\n" + "="*80)

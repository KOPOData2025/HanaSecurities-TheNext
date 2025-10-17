/**
 * RAM (Risk-Adjusted Margin) API 서비스
 * 고객 신용평가 및 후불결제 한도 산정
 */

const RAM_BASE_URL = 'http://localhost:8000/api/v1';

/**
 * 고객 데이터 인터페이스
 */
export interface CustomerData {
  credit_score: number;
  income: number;
  debt_ratio: number;
  employment_years: number;
  payment_history_score: number;
  existing_loans: number;
  age: number;
  delinquency_history: number;
}

/**
 * RAM 계산 요청 인터페이스
 */
export interface RamRequest {
  customer_data: CustomerData;
  threshold: number;
  k: number;
}

/**
 * RAM 계산 응답 인터페이스
 */
export interface RamResponse {
  success: boolean;
  message: string;
  data: {
    ram: number;
    ram_percent: string;
    interpretation: string;
    components: {
      mdr: number;
      pd: number;
      k: number;
      revenue_component: number;
      loss_component: number;
    };
  };
}

/**
 * 샘플 데이터 응답 인터페이스
 */
export interface SampleDataResponse {
  success: boolean;
  data: {
    customer_data: CustomerData;
    threshold: number;
    description: {
      profile: string;
      characteristics: string[];
      expected_result: {
        will_default: boolean;
        risk_level: string;
        default_probability_range: string;
      };
    };
  };
}

/**
 * RAM API 서비스
 */
export const ramApi = {
  /**
   * 샘플 고객 데이터 조회
   */
  getSampleData: async (): Promise<SampleDataResponse> => {
    const response = await fetch(`${RAM_BASE_URL}/sample-data/`);
    if (!response.ok) {
      throw new Error('Failed to fetch sample data');
    }
    return response.json();
  },

  /**
   * RAM 계산
   * @param request RAM 계산 요청 데이터
   */
  calculateRam: async (request: RamRequest): Promise<RamResponse> => {
    const response = await fetch(`${RAM_BASE_URL}/ram/`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(request),
    });

    if (!response.ok) {
      throw new Error('Failed to calculate RAM');
    }
    return response.json();
  },

  /**
   * 신용 한도 평가
   * RAM 값이 2% 이상이면 300,000원, 미만이면 0원
   * @param customerData 고객 데이터
   */
  evaluateCreditLimit: async (customerData: CustomerData): Promise<{ approved: boolean; creditLimit: number; ram: number }> => {
    try {
      // RAM 계산 (기본값: threshold=0.5, k=0.313)
      const ramResponse = await ramApi.calculateRam({
        customer_data: customerData,
        threshold: 0.5,
        k: 0.313,
      });

      const ram = ramResponse.data.ram;
      const approved = ram >= 0.02; // 2% 기준
      const creditLimit = approved ? 300000 : 0;

      return {
        approved,
        creditLimit,
        ram,
      };
    } catch (error) {
      console.error('RAM 평가 오류:', error);
      throw error;
    }
  },
};

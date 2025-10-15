import type {
  BnplApplicationRequest,
  BnplApplicationResponse,
  BnplUsageHistoryResponse,
  BnplInfoResponse
} from '../types/bnpl.types';

const BASE_URL = '/api';

export const bnplApi = {
  /**
   * 후불결제 신청
   * @param request 신청 정보 (userId, paymentDay, paymentAccount)
   */
  applyBnpl: async (request: BnplApplicationRequest): Promise<BnplApplicationResponse> => {
    const response = await fetch(`${BASE_URL}/bnpl/apply`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(request),
    });
    if (!response.ok) {
      throw new Error('Failed to apply BNPL');
    }
    return response.json();
  },

  /**
   * 후불결제 이용내역 조회
   * @param userId 사용자 ID
   */
  getUsageHistory: async (userId: string): Promise<BnplUsageHistoryResponse> => {
    const response = await fetch(
      `${BASE_URL}/bnpl/usage-history?userId=${encodeURIComponent(userId)}`
    );
    if (!response.ok) {
      throw new Error('Failed to fetch usage history');
    }
    return response.json();
  },

  /**
   * 후불결제 정보 조회
   * @param userId 사용자 ID
   */
  getBnplInfo: async (userId: string): Promise<BnplInfoResponse> => {
    const response = await fetch(
      `${BASE_URL}/bnpl/info?userId=${encodeURIComponent(userId)}`
    );
    if (!response.ok) {
      throw new Error('Failed to fetch BNPL info');
    }
    return response.json();
  }
};

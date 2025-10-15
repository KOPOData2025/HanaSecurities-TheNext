/**
 * BNPL(후불결제) 신청 요청 데이터
 */
export interface BnplApplicationRequest {
  /** 사용자 ID */
  userId: string;
  /** 결제일 (1~31) */
  paymentDay: number;
  /** 결제 계좌 */
  paymentAccount: string;
}

/**
 * BNPL 신청 응답 데이터
 */
export interface BnplApplicationResponse {
  /** 성공 여부 */
  success: boolean;
  /** 응답 메시지 */
  message: string;
  /** 신용 한도 (선택) */
  creditLimit?: number;
  /** 승인 상태 (선택) */
  approvalStatus?: string;
}

/**
 * BNPL 이용 내역 응답 데이터
 */
export interface BnplUsageHistoryResponse {
  /** 성공 여부 */
  success: boolean;
  /** 응답 메시지 */
  message: string;
  /** 이용 내역 목록 */
  usageHistory: UsageItem[];
}

/**
 * 이용 내역 항목
 */
export interface UsageItem {
  /** 이용 날짜 */
  usageDate: string;
  /** 가맹점명 */
  merchantName: string;
  /** 이용 금액 */
  amount: number;
}

/**
 * BNPL 정보 응답 데이터
 */
export interface BnplInfoResponse {
  /** 성공 여부 */
  success: boolean;
  /** 응답 메시지 */
  message: string;
  /** BNPL 정보 데이터 (선택) */
  data?: BnplInfoData;
}

/**
 * BNPL 정보 데이터
 */
export interface BnplInfoData {
  /** 결제일 */
  paymentDay: number;
  /** 결제 계좌 */
  paymentAccount: string;
  /** 이용 금액 */
  usageAmount: number;
  /** 신용 한도 */
  creditLimit: number;
  /** 신청일 */
  applicationDate: string;
  /** 승인 상태 */
  approvalStatus: string;
}

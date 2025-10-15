/**
 * 메뉴 항목
 */
export interface MenuItem {
  /** 메뉴 라벨 */
  label: string;
  /** 하위 메뉴 항목들 (선택) */
  items?: string[];
}

/**
 * 탭 콘텐츠 구조
 */
export interface TabContent {
  /** 좌측 메뉴 목록 */
  leftMenu: MenuItem[];
  /** 우측 메뉴 목록 (선택) */
  rightMenu?: MenuItem[];
}

/**
 * 메뉴 데이터 구조
 */
export interface MenuData {
  /** 탭별 콘텐츠 (key: 탭 이름) */
  [key: string]: TabContent;
}

/**
 * 전체 메뉴 데이터
 * 국내, 해외, 후불결제, 자산·뱅킹 탭의 메뉴 구조를 정의
 */
export const menuData: MenuData = {
  '국내': {
    leftMenu: [
      { label: '주식' },
      { label: '선물옵션' },
      { label: '채권' }
    ],
    rightMenu: {
      '주식': [
        { label: '관심종목' },
        { label: '현재가' },
        { label: '차트' },
        { label: '주문' },
        { label: '잔고' }
      ],
      '선물옵션': [
        { label: '현재가' },
        { label: '차트' },
        { label: '주문' },
        { label: '잔고' }
      ],
      '채권': [
        { label: '현재가' },
        { label: '주문' }
      ]
    }
  },
  '해외': {
    leftMenu: [
      { label: '주식' },
      { label: '선물옵션' },
      { label: '채권' }
    ],
    rightMenu: {
      '주식': [
        { label: '관심종목' },
        { label: '현재가' },
        { label: '차트' },
        { label: '주문' },
        { label: '잔고' }
      ],
      '선물옵션': [
        { label: '현재가' },
        { label: '차트' },
        { label: '주문' },
        { label: '잔고' }
      ],
      '채권': [
        { label: '현재가' },
        { label: '주문' }
      ]
    }
  },
  '후불결제': {
    leftMenu: [
      { label: '약정/신청' },
      { label: '조회' },
      { label: '결제' },
      { label: '알림/관리' }
    ],
    rightMenu: {
      '약정/신청': [
        { label: '후불결제 서비스 안내' },
        { label: '후불결제 신청/변경' }
      ],
      '조회': [
        { label: '이용내역 조회' },
        { label: '이자/수수료 조회' },
        { label: '한도/약정 조회' }
      ],
      '결제': [
        { label: '즉시결제/상환' },
        { label: '자동결제 관리' }
      ],
      '알림/관리': [
        { label: '알림 설정' },
        { label: '이용 가이드 & FAQ' }
      ]
    }
  },
  '자산·뱅킹': {
    leftMenu: [
      { label: '자산/내역' }
    ],
    rightMenu: {
      '자산/내역': [
        { label: '총 자산현황' },
        { label: '거래내역' }
      ]
    }
  }
};

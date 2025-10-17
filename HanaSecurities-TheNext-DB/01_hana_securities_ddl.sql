-- ============================================================================
-- 하나증권 통합 데이터베이스 스키마 DDL (Oracle)
-- ============================================================================
-- 생성일: 2025-10-13
-- 데이터베이스: Oracle Database
-- 설명: 하나증권 전체 시스템 테이블 구조 정의
-- 포함: 사용자 인증, 주식 정보, 쇼핑, 후불결제
-- ============================================================================

-- ============================================================================
-- 목차
-- ============================================================================
-- 1. 시퀀스 (Sequences)
-- 2. 사용자 인증 관련 테이블 (Users & WebAuthn)
-- 3. 주식 정보 관련 테이블 (Stock Data)
-- 4. 쇼핑 관련 테이블 (Products)
-- 5. 후불결제 관련 테이블 (BNPL)
-- 6. ERD 관계도 요약
-- ============================================================================


-- ============================================================================
-- 1. 시퀀스 (Sequences)
-- ============================================================================

-- 1-1. 사용자 ID 시퀀스
CREATE SEQUENCE USERS_SEQ
    START WITH 1
    INCREMENT BY 1
    NOCACHE
    NOCYCLE;

-- 1-2. 재무정보 ID 시퀀스
CREATE SEQUENCE SEQ_STOCK_FINANCIAL
    START WITH 1
    INCREMENT BY 1
    NOCACHE
    NOCYCLE;

-- 1-3. 투자의견 ID 시퀀스
CREATE SEQUENCE SEQ_STOCK_OPINION
    START WITH 1
    INCREMENT BY 1
    NOCACHE
    NOCYCLE;

-- 1-4. 상품 ID 시퀀스
CREATE SEQUENCE PRODUCTS_SEQ
    START WITH 1
    INCREMENT BY 1
    NOCACHE
    NOCYCLE;

-- 1-5. 금융상품 거래내역 ID 시퀀스
CREATE SEQUENCE FINANCIAL_TRANSACTIONS_SEQ
    START WITH 1
    INCREMENT BY 1
    NOCACHE
    NOCYCLE;

-- 1-6. 입출금 거래내역 ID 시퀀스
CREATE SEQUENCE ACCOUNT_TRANSACTIONS_SEQ
    START WITH 1
    INCREMENT BY 1
    NOCACHE
    NOCYCLE;


-- ============================================================================
-- 2. 사용자 인증 관련 테이블
-- ============================================================================

-- ============================================================================
-- 2-1. USERS (사용자 기본 정보 테이블)
-- ============================================================================
CREATE TABLE USERS (
    user_id NUMBER(19, 0) NOT NULL,
    user_name VARCHAR2(100) NOT NULL,
    mobile_no VARCHAR2(20) NOT NULL,
    gender VARCHAR2(10) NOT NULL,
    birth DATE NOT NULL,
    email VARCHAR2(255) NOT NULL,
    address VARCHAR2(500),
    secondary_password_hash VARCHAR2(128) NOT NULL,
    balance NUMBER(15, 0) DEFAULT 0 NOT NULL,
    CONSTRAINT PK_USERS PRIMARY KEY (user_id),
    CONSTRAINT UK_USERS_MOBILE_NO UNIQUE (mobile_no),
    CONSTRAINT UK_USERS_BIRTH UNIQUE (birth)
);

-- 인덱스
CREATE INDEX IDX_USERS_MOBILE_NO ON USERS(mobile_no);
CREATE INDEX IDX_USERS_EMAIL ON USERS(email);

-- 코멘트
COMMENT ON TABLE USERS IS '사용자 기본 정보 테이블 - 회원가입 및 로그인';
COMMENT ON COLUMN USERS.user_id IS '사용자 ID (PK, 자동 증가)';
COMMENT ON COLUMN USERS.user_name IS '사용자 이름';
COMMENT ON COLUMN USERS.mobile_no IS '휴대폰 번호 (로그인 ID, 중복 불가)';
COMMENT ON COLUMN USERS.gender IS '성별 (M: 남성, F: 여성)';
COMMENT ON COLUMN USERS.birth IS '생년월일 (중복 불가)';
COMMENT ON COLUMN USERS.email IS '이메일';
COMMENT ON COLUMN USERS.address IS '주소';
COMMENT ON COLUMN USERS.secondary_password_hash IS '2차 비밀번호 해시 (SHA-256)';
COMMENT ON COLUMN USERS.balance IS '예수금 (원 단위, 가상계좌 잔액)';


-- ============================================================================
-- 2-2. WEBAUTHN_CREDENTIALS (WebAuthn 지문 인증 정보 테이블)
-- ============================================================================
CREATE TABLE WEBAUTHN_CREDENTIALS (
    credential_id VARCHAR2(200) NOT NULL,
    user_id NUMBER(19, 0) NOT NULL,
    public_key CLOB NOT NULL,
    created_at TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL,
    last_used_at TIMESTAMP,
    CONSTRAINT PK_WEBAUTHN_CREDENTIALS PRIMARY KEY (credential_id),
    CONSTRAINT FK_USERS_TO_WEBAUTHN FOREIGN KEY (user_id)
        REFERENCES USERS(user_id) ON DELETE CASCADE
);

-- 인덱스
CREATE INDEX IDX_WEBAUTHN_USER ON WEBAUTHN_CREDENTIALS(user_id);

-- 코멘트
COMMENT ON TABLE WEBAUTHN_CREDENTIALS IS 'WebAuthn 지문 인증 정보';
COMMENT ON COLUMN WEBAUTHN_CREDENTIALS.credential_id IS '인증기 식별자 (디바이스별 고유 ID)';
COMMENT ON COLUMN WEBAUTHN_CREDENTIALS.user_id IS '사용자 아이디 (FK)';
COMMENT ON COLUMN WEBAUTHN_CREDENTIALS.public_key IS '공개키 (서명 검증용, COSE 형식)';
COMMENT ON COLUMN WEBAUTHN_CREDENTIALS.created_at IS '등록 일시';
COMMENT ON COLUMN WEBAUTHN_CREDENTIALS.last_used_at IS '마지막 사용 일시';


-- ============================================================================
-- 3. 주식 정보 관련 테이블
-- ============================================================================

-- ============================================================================
-- 3-1. STOCK (주식 종목 마스터 테이블)
-- ============================================================================
CREATE TABLE STOCK (
    stock_code VARCHAR2(20) NOT NULL,
    stock_name VARCHAR2(200) NOT NULL,
    market_type VARCHAR2(10),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT PK_STOCK PRIMARY KEY (stock_code)
);

-- 인덱스
CREATE INDEX IDX_STOCK_NAME ON STOCK(stock_name);
CREATE INDEX IDX_STOCK_MARKET ON STOCK(market_type);

-- 코멘트
COMMENT ON TABLE STOCK IS '주식 종목 마스터 테이블 (검색용)';
COMMENT ON COLUMN STOCK.stock_code IS '종목코드 (단축코드 6자리, 예: 005930)';
COMMENT ON COLUMN STOCK.stock_name IS '종목명 (예: 삼성전자)';
COMMENT ON COLUMN STOCK.market_type IS '시장구분 (KOSPI/KOSDAQ)';


-- ============================================================================
-- 3-2. STOCK_OVERVIEW (주식 개요 정보 테이블)
-- ============================================================================
CREATE TABLE STOCK_OVERVIEW (
    stock_code VARCHAR2(20) NOT NULL,
    mket_id_cd VARCHAR2(10),
    scty_grp_id_cd VARCHAR2(10),
    excg_dvsn_cd VARCHAR2(10),
    setl_mmdd VARCHAR2(4),
    lstg_stqt VARCHAR2(30),
    lstg_cptl_amt VARCHAR2(30),
    cpta VARCHAR2(30),
    papr VARCHAR2(30),
    issu_pric VARCHAR2(30),
    kospi200_item_yn CHAR(1),
    scts_mket_lstg_dt VARCHAR2(8),
    stck_kind_cd VARCHAR2(10),
    std_idst_clsf_cd VARCHAR2(10),
    nxt_tr_stop_yn CHAR(1),
    last_synced_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT PK_STOCK_OVERVIEW PRIMARY KEY (stock_code),
    CONSTRAINT FK_OVERVIEW_STOCK FOREIGN KEY (stock_code)
        REFERENCES STOCK(stock_code) ON DELETE CASCADE
);

-- 인덱스
CREATE INDEX IDX_OVERVIEW_MARKET ON STOCK_OVERVIEW(mket_id_cd);
CREATE INDEX IDX_OVERVIEW_INDUSTRY ON STOCK_OVERVIEW(std_idst_clsf_cd);
CREATE INDEX IDX_OVERVIEW_SCTY_GRP ON STOCK_OVERVIEW(scty_grp_id_cd);

-- 코멘트
COMMENT ON TABLE STOCK_OVERVIEW IS '주식 종목 개요 정보 (프론트엔드 필수 필드)';
COMMENT ON COLUMN STOCK_OVERVIEW.stock_code IS '종목코드 (PK, FK)';
COMMENT ON COLUMN STOCK_OVERVIEW.mket_id_cd IS '시장 (시장ID코드)';
COMMENT ON COLUMN STOCK_OVERVIEW.scty_grp_id_cd IS '증권그룹 (증권그룹ID코드)';
COMMENT ON COLUMN STOCK_OVERVIEW.excg_dvsn_cd IS '거래소구분';
COMMENT ON COLUMN STOCK_OVERVIEW.setl_mmdd IS '결산월일 (MMDD)';
COMMENT ON COLUMN STOCK_OVERVIEW.lstg_stqt IS '상장주수';
COMMENT ON COLUMN STOCK_OVERVIEW.lstg_cptl_amt IS '상장자본금액';
COMMENT ON COLUMN STOCK_OVERVIEW.cpta IS '자본금';
COMMENT ON COLUMN STOCK_OVERVIEW.papr IS '액면가';
COMMENT ON COLUMN STOCK_OVERVIEW.issu_pric IS '발행가';
COMMENT ON COLUMN STOCK_OVERVIEW.kospi200_item_yn IS '코스피200종목여부';
COMMENT ON COLUMN STOCK_OVERVIEW.scts_mket_lstg_dt IS '유가증권시장상장일자';
COMMENT ON COLUMN STOCK_OVERVIEW.stck_kind_cd IS '주식종류';
COMMENT ON COLUMN STOCK_OVERVIEW.std_idst_clsf_cd IS '산업분류코드';
COMMENT ON COLUMN STOCK_OVERVIEW.nxt_tr_stop_yn IS 'NXT거래정지여부';


-- ============================================================================
-- 3-3. STOCK_FINANCIAL_INFO (주식 재무정보 테이블)
-- ============================================================================
CREATE TABLE STOCK_FINANCIAL_INFO (
    financial_id NUMBER(19,0) DEFAULT SEQ_STOCK_FINANCIAL.NEXTVAL NOT NULL,
    stock_code VARCHAR2(20) NOT NULL,
    stac_yymm VARCHAR2(8) NOT NULL,
    division_code VARCHAR2(1),
    -- 재무비율 (KisFinancialRatioApiResponse)
    grs VARCHAR2(20),
    bsop_prfi_inrt VARCHAR2(20),
    ntin_inrt VARCHAR2(20),
    roe_val VARCHAR2(20),
    eps VARCHAR2(20),
    sps VARCHAR2(20),
    bps VARCHAR2(20),
    rsrv_rate VARCHAR2(20),
    lblt_rate VARCHAR2(20),
    -- 손익계산서 (KisIncomeStatementApiResponse)
    sale_account VARCHAR2(30),
    sale_cost VARCHAR2(30),
    sale_totl_prfi VARCHAR2(30),
    depr_cost VARCHAR2(30),
    sell_mang VARCHAR2(30),
    bsop_prti VARCHAR2(30),
    bsop_non_ernn VARCHAR2(30),
    bsop_non_expn VARCHAR2(30),
    op_prfi VARCHAR2(30),
    spec_prfi VARCHAR2(30),
    spec_loss VARCHAR2(30),
    thtr_ntin VARCHAR2(30),
    -- 대차대조표 (KisBalanceSheetApiResponse)
    cras VARCHAR2(30),
    fxas VARCHAR2(30),
    total_aset VARCHAR2(30),
    flow_lblt VARCHAR2(30),
    fix_lblt VARCHAR2(30),
    total_lblt VARCHAR2(30),
    cpfn VARCHAR2(30),
    cfp_surp VARCHAR2(30),
    prfi_surp VARCHAR2(30),
    total_cptl VARCHAR2(30),
    -- 캐시 최적화 필드
    last_synced_date DATE,
    -- 메타 정보
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT PK_STOCK_FINANCIAL PRIMARY KEY (financial_id),
    CONSTRAINT FK_FINANCIAL_STOCK FOREIGN KEY (stock_code)
        REFERENCES STOCK(stock_code) ON DELETE CASCADE,
    CONSTRAINT UQ_FINANCIAL_PERIOD UNIQUE (stock_code, stac_yymm),
    CONSTRAINT CK_DIVISION_CODE CHECK (division_code IN ('0', '1'))
);

-- 인덱스
CREATE INDEX IDX_FINANCIAL_STOCK_CODE ON STOCK_FINANCIAL_INFO(stock_code);
CREATE INDEX IDX_FINANCIAL_PERIOD ON STOCK_FINANCIAL_INFO(stac_yymm DESC);
CREATE INDEX IDX_FINANCIAL_STOCK_PERIOD ON STOCK_FINANCIAL_INFO(stock_code, stac_yymm DESC);

-- 코멘트
COMMENT ON TABLE STOCK_FINANCIAL_INFO IS '주식 재무정보 (재무비율+손익계산서+대차대조표 통합)';
COMMENT ON COLUMN STOCK_FINANCIAL_INFO.stock_code IS '종목코드 (FK)';
COMMENT ON COLUMN STOCK_FINANCIAL_INFO.stac_yymm IS '결산년월 (YYYYMM)';
COMMENT ON COLUMN STOCK_FINANCIAL_INFO.division_code IS '분류구분 (0: 년, 1: 분기)';


-- ============================================================================
-- 3-4. STOCK_INVEST_OPINION (종목 투자의견 테이블)
-- ============================================================================
CREATE TABLE STOCK_INVEST_OPINION (
    opinion_id NUMBER(19,0) DEFAULT SEQ_STOCK_OPINION.NEXTVAL NOT NULL,
    stock_code VARCHAR2(20) NOT NULL,
    stck_bsop_date VARCHAR2(8) NOT NULL,
    invt_opnn VARCHAR2(50),
    rgbf_invt_opnn VARCHAR2(50),
    hts_goal_prc VARCHAR2(30),
    mbcr_name VARCHAR2(100),
    last_synced_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT PK_STOCK_OPINION PRIMARY KEY (opinion_id),
    CONSTRAINT FK_OPINION_STOCK FOREIGN KEY (stock_code)
        REFERENCES STOCK(stock_code) ON DELETE CASCADE
);

-- 인덱스
CREATE INDEX IDX_OPINION_STOCK_CODE ON STOCK_INVEST_OPINION(stock_code);
CREATE INDEX IDX_OPINION_DATE ON STOCK_INVEST_OPINION(stck_bsop_date DESC);
CREATE INDEX IDX_OPINION_STOCK_DATE ON STOCK_INVEST_OPINION(stock_code, stck_bsop_date DESC);
CREATE INDEX IDX_OPINION_BROKERAGE ON STOCK_INVEST_OPINION(mbcr_name);

-- 코멘트
COMMENT ON TABLE STOCK_INVEST_OPINION IS '종목 투자의견 (프론트엔드 필수 필드)';
COMMENT ON COLUMN STOCK_INVEST_OPINION.stock_code IS '종목코드 (FK)';
COMMENT ON COLUMN STOCK_INVEST_OPINION.stck_bsop_date IS '발표일 (YYYYMMDD 형식)';
COMMENT ON COLUMN STOCK_INVEST_OPINION.invt_opnn IS '현재 의견 (매수/중립/매도 등)';
COMMENT ON COLUMN STOCK_INVEST_OPINION.rgbf_invt_opnn IS '직전 의견';
COMMENT ON COLUMN STOCK_INVEST_OPINION.hts_goal_prc IS '목표가 (원)';
COMMENT ON COLUMN STOCK_INVEST_OPINION.mbcr_name IS '증권사명';


-- ============================================================================
-- 3-5. STOCK_CHART_DATA (주식 차트 데이터 테이블)
-- ============================================================================
CREATE TABLE STOCK_CHART_DATA (
    stock_code VARCHAR2(20) NOT NULL,
    period_type VARCHAR2(1) NOT NULL,
    trade_date VARCHAR2(8) NOT NULL,
    open_price VARCHAR2(20),
    high_price VARCHAR2(20),
    low_price VARCHAR2(20),
    close_price VARCHAR2(20),
    volume VARCHAR2(30),
    trading_value VARCHAR2(30),
    change_sign VARCHAR2(1),
    change_price VARCHAR2(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT pk_stock_chart_data PRIMARY KEY (stock_code, period_type, trade_date),
    CONSTRAINT fk_stock_chart_stock FOREIGN KEY (stock_code)
        REFERENCES STOCK(stock_code) ON DELETE CASCADE,
    CONSTRAINT chk_period_type CHECK (period_type IN ('D', 'W', 'M', 'Y'))
);

-- 인덱스
CREATE INDEX idx_stock_chart_latest ON STOCK_CHART_DATA(stock_code, period_type, trade_date DESC);
CREATE INDEX idx_stock_chart_period ON STOCK_CHART_DATA(stock_code, period_type);

-- 코멘트
COMMENT ON TABLE STOCK_CHART_DATA IS '주식 차트 데이터 (일봉/주봉/월봉/년봉 통합)';
COMMENT ON COLUMN STOCK_CHART_DATA.stock_code IS '종목코드';
COMMENT ON COLUMN STOCK_CHART_DATA.period_type IS '기간구분 (D:일봉, W:주봉, M:월봉, Y:년봉)';
COMMENT ON COLUMN STOCK_CHART_DATA.trade_date IS '거래일자 (YYYYMMDD)';
COMMENT ON COLUMN STOCK_CHART_DATA.open_price IS '시가';
COMMENT ON COLUMN STOCK_CHART_DATA.high_price IS '고가';
COMMENT ON COLUMN STOCK_CHART_DATA.low_price IS '저가';
COMMENT ON COLUMN STOCK_CHART_DATA.close_price IS '종가';
COMMENT ON COLUMN STOCK_CHART_DATA.volume IS '거래량';
COMMENT ON COLUMN STOCK_CHART_DATA.trading_value IS '거래대금';
COMMENT ON COLUMN STOCK_CHART_DATA.change_sign IS '전일 대비 부호';
COMMENT ON COLUMN STOCK_CHART_DATA.change_price IS '전일 대비 가격';


-- ============================================================================
-- 3-6. USER_WATCHLISTS (사용자 관심 종목 테이블)
-- ============================================================================
CREATE TABLE USER_WATCHLISTS (
    stock_code VARCHAR2(255) NOT NULL,
    user_id NUMBER(19, 0) NOT NULL,
    CONSTRAINT PK_USER_WATCHLISTS PRIMARY KEY (stock_code, user_id),
    CONSTRAINT FK_STOCKS_TO_WATCHLISTS FOREIGN KEY (stock_code)
        REFERENCES STOCK(stock_code) ON DELETE CASCADE,
    CONSTRAINT FK_USERS_TO_WATCHLISTS FOREIGN KEY (user_id)
        REFERENCES USERS(user_id) ON DELETE CASCADE
);

-- 인덱스
CREATE INDEX IDX_WATCHLIST_USER_ID ON USER_WATCHLISTS(user_id);
CREATE INDEX IDX_WATCHLIST_STOCK_CODE ON USER_WATCHLISTS(stock_code);

-- 코멘트
COMMENT ON TABLE USER_WATCHLISTS IS '사용자 관심 종목 테이블';
COMMENT ON COLUMN USER_WATCHLISTS.stock_code IS '종목코드 (PK, FK)';
COMMENT ON COLUMN USER_WATCHLISTS.user_id IS '사용자 ID (PK, FK)';


-- ============================================================================
-- 3-7. FINANCIAL_TRANSACTIONS (금융상품 거래내역 테이블)
-- ============================================================================
CREATE TABLE FINANCIAL_TRANSACTIONS (
    transaction_id NUMBER(19, 0) DEFAULT FINANCIAL_TRANSACTIONS_SEQ.NEXTVAL NOT NULL,
    mobile_no VARCHAR2(20) NOT NULL,
    product_type VARCHAR2(20) NOT NULL,
    product_code VARCHAR2(20) NOT NULL,
    product_name VARCHAR2(200),
    transaction_type VARCHAR2(10) NOT NULL,
    quantity NUMBER(15, 0) NOT NULL,
    price NUMBER(15, 2) NOT NULL,
    total_amount NUMBER(18, 2) NOT NULL,
    fee NUMBER(12, 2) DEFAULT 0,
    tax NUMBER(12, 2) DEFAULT 0,
    net_amount NUMBER(18, 2) NOT NULL,
    transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    settlement_date DATE,
    status VARCHAR2(20) DEFAULT 'COMPLETED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT PK_FINANCIAL_TRANSACTIONS PRIMARY KEY (transaction_id),
    CONSTRAINT FK_FINANCIAL_USER FOREIGN KEY (mobile_no)
        REFERENCES USERS(mobile_no) ON DELETE CASCADE,
    CONSTRAINT CK_PRODUCT_TYPE CHECK (product_type IN ('STOCK', 'BOND', 'FUND', 'ETF', 'DERIVATIVE')),
    CONSTRAINT CK_TRANSACTION_TYPE CHECK (transaction_type IN ('BUY', 'SELL')),
    CONSTRAINT CK_TRANSACTION_STATUS CHECK (status IN ('PENDING', 'COMPLETED', 'CANCELLED', 'FAILED'))
);

-- 인덱스
CREATE INDEX IDX_FINANCIAL_MOBILE ON FINANCIAL_TRANSACTIONS(mobile_no);
CREATE INDEX IDX_FINANCIAL_DATE ON FINANCIAL_TRANSACTIONS(transaction_date DESC);
CREATE INDEX IDX_FINANCIAL_MOBILE_DATE ON FINANCIAL_TRANSACTIONS(mobile_no, transaction_date DESC);
CREATE INDEX IDX_FINANCIAL_PRODUCT ON FINANCIAL_TRANSACTIONS(product_type, product_code);
CREATE INDEX IDX_FINANCIAL_STATUS ON FINANCIAL_TRANSACTIONS(status);

-- 코멘트
COMMENT ON TABLE FINANCIAL_TRANSACTIONS IS '금융상품 거래내역 (주식/채권/펀드 등 통합)';
COMMENT ON COLUMN FINANCIAL_TRANSACTIONS.transaction_id IS '거래내역 ID (PK, 자동 증가)';
COMMENT ON COLUMN FINANCIAL_TRANSACTIONS.mobile_no IS '가상계좌번호 (FK, 사용자 전화번호)';
COMMENT ON COLUMN FINANCIAL_TRANSACTIONS.product_type IS '상품 유형 (STOCK/BOND/FUND/ETF/DERIVATIVE)';
COMMENT ON COLUMN FINANCIAL_TRANSACTIONS.product_code IS '상품 코드 (종목코드 등)';
COMMENT ON COLUMN FINANCIAL_TRANSACTIONS.product_name IS '상품명';
COMMENT ON COLUMN FINANCIAL_TRANSACTIONS.transaction_type IS '거래 유형 (BUY: 매수, SELL: 매도)';
COMMENT ON COLUMN FINANCIAL_TRANSACTIONS.quantity IS '거래 수량';
COMMENT ON COLUMN FINANCIAL_TRANSACTIONS.price IS '거래 단가';
COMMENT ON COLUMN FINANCIAL_TRANSACTIONS.total_amount IS '거래 총액 (수량 × 단가)';
COMMENT ON COLUMN FINANCIAL_TRANSACTIONS.fee IS '수수료';
COMMENT ON COLUMN FINANCIAL_TRANSACTIONS.tax IS '세금';
COMMENT ON COLUMN FINANCIAL_TRANSACTIONS.net_amount IS '실거래금액 (총액 ± 수수료 ± 세금)';
COMMENT ON COLUMN FINANCIAL_TRANSACTIONS.transaction_date IS '거래 일시';
COMMENT ON COLUMN FINANCIAL_TRANSACTIONS.settlement_date IS '결제 예정일';
COMMENT ON COLUMN FINANCIAL_TRANSACTIONS.status IS '거래 상태 (PENDING/COMPLETED/CANCELLED/FAILED)';


-- ============================================================================
-- 3-8. ACCOUNT_TRANSACTIONS (입출금 거래내역 테이블)
-- ============================================================================
CREATE TABLE ACCOUNT_TRANSACTIONS (
    transaction_id NUMBER(19, 0) DEFAULT ACCOUNT_TRANSACTIONS_SEQ.NEXTVAL NOT NULL,
    mobile_no VARCHAR2(20) NOT NULL,
    transaction_type VARCHAR2(20) NOT NULL,
    amount NUMBER(15, 0) NOT NULL,
    balance_after NUMBER(15, 0) NOT NULL,
    description VARCHAR2(500),
    related_account VARCHAR2(50),
    transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    status VARCHAR2(20) DEFAULT 'COMPLETED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT PK_ACCOUNT_TRANSACTIONS PRIMARY KEY (transaction_id),
    CONSTRAINT FK_ACCOUNT_USER FOREIGN KEY (mobile_no)
        REFERENCES USERS(mobile_no) ON DELETE CASCADE,
    CONSTRAINT CK_ACCOUNT_TYPE CHECK (transaction_type IN ('DEPOSIT', 'WITHDRAW', 'TRANSFER_IN', 'TRANSFER_OUT')),
    CONSTRAINT CK_ACCOUNT_STATUS CHECK (status IN ('PENDING', 'COMPLETED', 'CANCELLED', 'FAILED'))
);

-- 인덱스
CREATE INDEX IDX_ACCOUNT_MOBILE ON ACCOUNT_TRANSACTIONS(mobile_no);
CREATE INDEX IDX_ACCOUNT_DATE ON ACCOUNT_TRANSACTIONS(transaction_date DESC);
CREATE INDEX IDX_ACCOUNT_MOBILE_DATE ON ACCOUNT_TRANSACTIONS(mobile_no, transaction_date DESC);
CREATE INDEX IDX_ACCOUNT_TYPE ON ACCOUNT_TRANSACTIONS(transaction_type);
CREATE INDEX IDX_ACCOUNT_STATUS ON ACCOUNT_TRANSACTIONS(status);

-- 코멘트
COMMENT ON TABLE ACCOUNT_TRANSACTIONS IS '계좌 입출금 거래내역';
COMMENT ON COLUMN ACCOUNT_TRANSACTIONS.transaction_id IS '거래내역 ID (PK, 자동 증가)';
COMMENT ON COLUMN ACCOUNT_TRANSACTIONS.mobile_no IS '가상계좌번호 (FK, 사용자 전화번호)';
COMMENT ON COLUMN ACCOUNT_TRANSACTIONS.transaction_type IS '거래 유형 (DEPOSIT/WITHDRAW/TRANSFER_IN/TRANSFER_OUT)';
COMMENT ON COLUMN ACCOUNT_TRANSACTIONS.amount IS '거래 금액';
COMMENT ON COLUMN ACCOUNT_TRANSACTIONS.balance_after IS '거래 후 잔액';
COMMENT ON COLUMN ACCOUNT_TRANSACTIONS.description IS '거래 설명';
COMMENT ON COLUMN ACCOUNT_TRANSACTIONS.related_account IS '상대 계좌 (이체 시)';
COMMENT ON COLUMN ACCOUNT_TRANSACTIONS.transaction_date IS '거래 일시';
COMMENT ON COLUMN ACCOUNT_TRANSACTIONS.status IS '거래 상태 (PENDING/COMPLETED/CANCELLED/FAILED)';


-- ============================================================================
-- 3-9. USER_HOLDINGS (사용자 보유 금융상품 현황 테이블)
-- ============================================================================
CREATE TABLE USER_HOLDINGS (
    holding_id NUMBER(19, 0) GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    mobile_no VARCHAR2(20) NOT NULL,
    product_type VARCHAR2(20) NOT NULL,
    product_code VARCHAR2(20) NOT NULL,
    product_name VARCHAR2(200),
    quantity NUMBER(15, 0) NOT NULL,
    avg_buy_price NUMBER(15, 2) NOT NULL,
    total_buy_amount NUMBER(18, 2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT FK_HOLDINGS_USER FOREIGN KEY (mobile_no)
        REFERENCES USERS(mobile_no) ON DELETE CASCADE,
    CONSTRAINT UK_HOLDINGS_USER_PRODUCT UNIQUE (mobile_no, product_type, product_code),
    CONSTRAINT CK_HOLDINGS_TYPE CHECK (product_type IN ('STOCK', 'BOND', 'FUND', 'ETF', 'DERIVATIVE')),
    CONSTRAINT CK_HOLDINGS_QUANTITY CHECK (quantity >= 0)
);

-- 인덱스
CREATE INDEX IDX_HOLDINGS_MOBILE ON USER_HOLDINGS(mobile_no);
CREATE INDEX IDX_HOLDINGS_PRODUCT ON USER_HOLDINGS(product_type, product_code);
CREATE INDEX IDX_HOLDINGS_MOBILE_TYPE ON USER_HOLDINGS(mobile_no, product_type);

-- 코멘트
COMMENT ON TABLE USER_HOLDINGS IS '사용자 보유 금융상품 현황';
COMMENT ON COLUMN USER_HOLDINGS.holding_id IS '보유내역 ID (PK, 자동 증가)';
COMMENT ON COLUMN USER_HOLDINGS.mobile_no IS '가상계좌번호 (FK, 사용자 전화번호)';
COMMENT ON COLUMN USER_HOLDINGS.product_type IS '상품 유형 (STOCK/BOND/FUND/ETF/DERIVATIVE)';
COMMENT ON COLUMN USER_HOLDINGS.product_code IS '상품 코드';
COMMENT ON COLUMN USER_HOLDINGS.product_name IS '상품명';
COMMENT ON COLUMN USER_HOLDINGS.quantity IS '보유 수량';
COMMENT ON COLUMN USER_HOLDINGS.avg_buy_price IS '평균 매수 단가';
COMMENT ON COLUMN USER_HOLDINGS.total_buy_amount IS '총 매수 금액';


-- ============================================================================
-- 4. 쇼핑 관련 테이블
-- ============================================================================

-- ============================================================================
-- 4-1. PRODUCTS (쇼핑 상품 테이블)
-- ============================================================================
CREATE TABLE PRODUCTS (
    product_id NUMBER(19, 0) NOT NULL,
    product_name VARCHAR2(200) NOT NULL,
    product_image_url VARCHAR2(500),
    price NUMBER(12, 0) NOT NULL,
    original_price NUMBER(12, 0),
    discount_rate NUMBER(5, 2) DEFAULT 0,
    seller VARCHAR2(100),
    rating NUMBER(3, 2) DEFAULT 0,
    review_count NUMBER(10, 0) DEFAULT 0,
    created_at TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL,
    CONSTRAINT PK_PRODUCTS PRIMARY KEY (product_id)
);

-- 인덱스
CREATE INDEX IDX_PRODUCTS_NAME ON PRODUCTS(product_name);
CREATE INDEX IDX_PRODUCTS_SELLER ON PRODUCTS(seller);
CREATE INDEX IDX_PRODUCTS_PRICE ON PRODUCTS(price);
CREATE INDEX IDX_PRODUCTS_RATING ON PRODUCTS(rating DESC);

-- 코멘트
COMMENT ON TABLE PRODUCTS IS '쇼핑 상품 정보 테이블';
COMMENT ON COLUMN PRODUCTS.product_id IS '상품 ID (PK, 자동 증가)';
COMMENT ON COLUMN PRODUCTS.product_name IS '상품명';
COMMENT ON COLUMN PRODUCTS.product_image_url IS '상품 이미지 URL (CDN/S3 경로)';
COMMENT ON COLUMN PRODUCTS.price IS '판매가 (원 단위)';
COMMENT ON COLUMN PRODUCTS.original_price IS '정가 (원 단위)';
COMMENT ON COLUMN PRODUCTS.discount_rate IS '할인율 (%, 0~100)';
COMMENT ON COLUMN PRODUCTS.seller IS '판매처 (삼성전자 등)';
COMMENT ON COLUMN PRODUCTS.rating IS '평점 (0.00 ~ 5.00)';
COMMENT ON COLUMN PRODUCTS.review_count IS '리뷰 개수';
COMMENT ON COLUMN PRODUCTS.created_at IS '등록일시';
COMMENT ON COLUMN PRODUCTS.updated_at IS '수정일시';


-- ============================================================================
-- 5. 후불결제 관련 테이블
-- ============================================================================

-- ============================================================================
-- 5-1. BNPL_INFO (후불결제 정보 테이블)
-- ============================================================================
CREATE TABLE BNPL_INFO (
    user_id VARCHAR2(50) PRIMARY KEY,
    payment_day NUMBER(2) NOT NULL,
    payment_account VARCHAR2(50) NOT NULL,
    usage_amount NUMBER(10) DEFAULT 0,
    credit_limit NUMBER(10) DEFAULT 300000,
    application_date DATE DEFAULT SYSDATE,
    approval_status VARCHAR2(20) DEFAULT 'APPROVED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_payment_day CHECK (payment_day IN (5, 15, 25))
);

-- 코멘트
COMMENT ON TABLE BNPL_INFO IS '후불결제 사용자 정보';
COMMENT ON COLUMN BNPL_INFO.user_id IS '사용자 ID';
COMMENT ON COLUMN BNPL_INFO.payment_day IS '납부일 (5, 15, 25)';
COMMENT ON COLUMN BNPL_INFO.payment_account IS '납부계좌';
COMMENT ON COLUMN BNPL_INFO.usage_amount IS '현재 이용금액';
COMMENT ON COLUMN BNPL_INFO.credit_limit IS '이용한도 (고정 300,000원)';
COMMENT ON COLUMN BNPL_INFO.application_date IS '신청일';
COMMENT ON COLUMN BNPL_INFO.approval_status IS '승인여부 (APPROVED/PENDING/REJECTED)';
COMMENT ON COLUMN BNPL_INFO.created_at IS '생성일시';
COMMENT ON COLUMN BNPL_INFO.updated_at IS '수정일시';


-- ============================================================================
-- 5-2. BNPL_USAGE_HISTORY (후불결제 이용내역 테이블)
-- ============================================================================
CREATE TABLE BNPL_USAGE_HISTORY (
    usage_id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id VARCHAR2(50) NOT NULL,
    usage_date DATE NOT NULL,
    merchant_name VARCHAR2(100) NOT NULL,
    amount NUMBER(10) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_bnpl_usage_user FOREIGN KEY (user_id)
        REFERENCES BNPL_INFO(user_id) ON DELETE CASCADE
);

-- 인덱스
CREATE INDEX idx_bnpl_usage_user_date ON BNPL_USAGE_HISTORY(user_id, usage_date DESC);

-- 코멘트
COMMENT ON TABLE BNPL_USAGE_HISTORY IS '후불결제 이용내역';
COMMENT ON COLUMN BNPL_USAGE_HISTORY.usage_id IS '이용내역 ID';
COMMENT ON COLUMN BNPL_USAGE_HISTORY.user_id IS '사용자 ID';
COMMENT ON COLUMN BNPL_USAGE_HISTORY.usage_date IS '이용날짜';
COMMENT ON COLUMN BNPL_USAGE_HISTORY.merchant_name IS '사용처';
COMMENT ON COLUMN BNPL_USAGE_HISTORY.amount IS '금액';


-- ============================================================================
-- 6. ERD 관계도 요약
-- ============================================================================
--
-- [USERS] (1) ----< (N) [WEBAUTHN_CREDENTIALS]
--    |
--    |---- (1) ----< (N) [USER_WATCHLISTS] >---- (N) ----< (1) [STOCK]
--    |
--    |---- (mobile_no) ----< (N) [FINANCIAL_TRANSACTIONS]
--    |
--    |---- (mobile_no) ----< (N) [ACCOUNT_TRANSACTIONS]
--    |
--    |---- (mobile_no) ----< (N) [USER_HOLDINGS]
--
-- [STOCK] (1) ----< (N) [STOCK_OVERVIEW]
--    |
--    |---- (1) ----< (N) [STOCK_FINANCIAL_INFO]
--    |
--    |---- (1) ----< (N) [STOCK_INVEST_OPINION]
--    |
--    |---- (1) ----< (N) [STOCK_CHART_DATA]
--
-- [BNPL_INFO] (1) ----< (N) [BNPL_USAGE_HISTORY]
--
-- [PRODUCTS] (독립 테이블, FK 없음)
--
-- ============================================================================


-- ============================================================================
-- 7. 제약조건 요약
-- ============================================================================
--
-- ■ Primary Keys:
--   - USERS: user_id
--   - WEBAUTHN_CREDENTIALS: credential_id
--   - STOCK: stock_code
--   - STOCK_OVERVIEW: stock_code
--   - STOCK_FINANCIAL_INFO: financial_id
--   - STOCK_INVEST_OPINION: opinion_id
--   - STOCK_CHART_DATA: (stock_code, period_type, trade_date)
--   - USER_WATCHLISTS: (stock_code, user_id)
--   - FINANCIAL_TRANSACTIONS: transaction_id
--   - ACCOUNT_TRANSACTIONS: transaction_id
--   - USER_HOLDINGS: holding_id
--   - PRODUCTS: product_id
--   - BNPL_INFO: user_id
--   - BNPL_USAGE_HISTORY: usage_id
--
-- ■ Foreign Keys:
--   - WEBAUTHN_CREDENTIALS.user_id → USERS.user_id (CASCADE DELETE)
--   - STOCK_OVERVIEW.stock_code → STOCK.stock_code (CASCADE DELETE)
--   - STOCK_FINANCIAL_INFO.stock_code → STOCK.stock_code (CASCADE DELETE)
--   - STOCK_INVEST_OPINION.stock_code → STOCK.stock_code (CASCADE DELETE)
--   - STOCK_CHART_DATA.stock_code → STOCK.stock_code (CASCADE DELETE)
--   - USER_WATCHLISTS.stock_code → STOCK.stock_code (CASCADE DELETE)
--   - USER_WATCHLISTS.user_id → USERS.user_id (CASCADE DELETE)
--   - FINANCIAL_TRANSACTIONS.mobile_no → USERS.mobile_no (CASCADE DELETE)
--   - ACCOUNT_TRANSACTIONS.mobile_no → USERS.mobile_no (CASCADE DELETE)
--   - USER_HOLDINGS.mobile_no → USERS.mobile_no (CASCADE DELETE)
--   - BNPL_USAGE_HISTORY.user_id → BNPL_INFO.user_id (CASCADE DELETE)
--
-- ■ Unique Constraints:
--   - USERS: mobile_no, birth
--   - STOCK_FINANCIAL_INFO: (stock_code, stac_yymm)
--   - USER_HOLDINGS: (mobile_no, product_type, product_code)
--
-- ■ Check Constraints:
--   - STOCK_FINANCIAL_INFO.division_code: IN ('0', '1')
--   - STOCK_CHART_DATA.period_type: IN ('D', 'W', 'M', 'Y')
--   - FINANCIAL_TRANSACTIONS.product_type: IN ('STOCK', 'BOND', 'FUND', 'ETF', 'DERIVATIVE')
--   - FINANCIAL_TRANSACTIONS.transaction_type: IN ('BUY', 'SELL')
--   - FINANCIAL_TRANSACTIONS.status: IN ('PENDING', 'COMPLETED', 'CANCELLED', 'FAILED')
--   - ACCOUNT_TRANSACTIONS.transaction_type: IN ('DEPOSIT', 'WITHDRAW', 'TRANSFER_IN', 'TRANSFER_OUT')
--   - ACCOUNT_TRANSACTIONS.status: IN ('PENDING', 'COMPLETED', 'CANCELLED', 'FAILED')
--   - USER_HOLDINGS.product_type: IN ('STOCK', 'BOND', 'FUND', 'ETF', 'DERIVATIVE')
--   - USER_HOLDINGS.quantity: >= 0
--   - BNPL_INFO.payment_day: IN (5, 15, 25)
--
-- ============================================================================


-- ============================================================================
-- 8. 테이블 총계
-- ============================================================================
--
-- 총 14개 테이블:
--   1. USERS (사용자)
--   2. WEBAUTHN_CREDENTIALS (WebAuthn 인증)
--   3. STOCK (주식 마스터)
--   4. STOCK_OVERVIEW (주식 개요)
--   5. STOCK_FINANCIAL_INFO (재무정보)
--   6. STOCK_INVEST_OPINION (투자의견)
--   7. STOCK_CHART_DATA (차트 데이터)
--   8. USER_WATCHLISTS (관심종목)
--   9. FINANCIAL_TRANSACTIONS (금융상품 거래내역)
--  10. ACCOUNT_TRANSACTIONS (입출금 거래내역)
--  11. USER_HOLDINGS (보유 금융상품)
--  12. PRODUCTS (쇼핑상품)
--  13. BNPL_INFO (후불결제 정보)
--  14. BNPL_USAGE_HISTORY (후불결제 이용내역)
--
-- 총 6개 시퀀스:
--   1. USERS_SEQ
--   2. SEQ_STOCK_FINANCIAL
--   3. SEQ_STOCK_OPINION
--   4. PRODUCTS_SEQ
--   5. FINANCIAL_TRANSACTIONS_SEQ
--   6. ACCOUNT_TRANSACTIONS_SEQ
--
-- ============================================================================
-- 끝
-- ============================================================================

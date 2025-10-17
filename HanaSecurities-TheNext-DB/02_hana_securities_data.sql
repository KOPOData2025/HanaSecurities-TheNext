-- ============================================================================
-- 하나증권 통합 데이터 삽입 스크립트 (Oracle)
-- ============================================================================
-- 생성일: 2025-10-13
-- 데이터베이스: Oracle Database
-- 설명: 하나증권 데이터베이스 초기 데이터 삽입
-- 포함: 주식 종목 마스터 데이터, 상품 샘플 데이터, 후불결제 더미 데이터
-- ============================================================================

-- ============================================================================
-- 목차
-- ============================================================================
-- 1. 주식 종목 마스터 데이터 (KOSPI/KOSDAQ)
-- 2. 쇼핑 상품 샘플 데이터
-- 3. 후불결제 더미 데이터
-- ============================================================================


-- ============================================================================
-- 1. 주식 종목 마스터 데이터 삽입
-- ============================================================================
-- 설명: KOSPI/KOSDAQ 전체 종목 코드 및 종목명 데이터
-- 파일: insert_stock_data.sql (약 3,628개 종목)
-- ============================================================================

@insert_stock_data.sql


-- ============================================================================
-- 2. 쇼핑 상품 샘플 데이터 삽입
-- ============================================================================

-- 샘플 데이터 1: 갤럭시 버즈3 프로
INSERT INTO PRODUCTS (
    product_id, product_name, product_image_url, price, original_price,
    discount_rate, seller, rating, review_count
) VALUES (
    PRODUCTS_SEQ.NEXTVAL,
    '갤럭시 버즈3 프로',
    '/productImage/galaxy_buds_3_pro.png',
    200900,
    319000,
    37,
    '삼성전자',
    4.6,
    8214
);

-- 샘플 데이터 2: 갤럭시 버즈 FE
INSERT INTO PRODUCTS (
    product_id, product_name, product_image_url, price, original_price,
    discount_rate, seller, rating, review_count
) VALUES (
    PRODUCTS_SEQ.NEXTVAL,
    '갤럭시 버즈 FE',
    '/productImage/galaxy_buds_fe.png',
    151000,
    159000,
    5,
    '삼성전자',
    4.8,
    2894
);

-- 샘플 데이터 3: 갤럭시 핏3
INSERT INTO PRODUCTS (
    product_id, product_name, product_image_url, price, original_price,
    discount_rate, seller, rating, review_count
) VALUES (
    PRODUCTS_SEQ.NEXTVAL,
    '갤럭시 핏3',
    '/productImage/galaxy_fit_3.png',
    87000,
    89000,
    2,
    '삼성전자',
    4.8,
    8605
);

-- 샘플 데이터 4: 갤럭시 워치7
INSERT INTO PRODUCTS (
    product_id, product_name, product_image_url, price, original_price,
    discount_rate, seller, rating, review_count
) VALUES (
    PRODUCTS_SEQ.NEXTVAL,
    '갤럭시 워치7',
    '/productImage/galaxy_watch_7.png',
    225000,
    349000,
    35,
    '삼성전자',
    4.8,
    3125
);

COMMIT;


-- ============================================================================
-- 3. 후불결제 더미 데이터 삽입
-- ============================================================================

-- 테스트 사용자 후불결제 정보 (이미 신청 완료된 상태)
INSERT INTO BNPL_INFO (user_id, payment_day, payment_account, usage_amount, credit_limit, application_date, approval_status)
VALUES ('test_user', 5, '110-123-456789', 15200, 300000, TO_DATE('2025-09-01', 'YYYY-MM-DD'), 'APPROVED');

-- 후불결제 이용내역 더미 데이터 (최근 내역부터)
INSERT INTO BNPL_USAGE_HISTORY (user_id, usage_date, merchant_name, amount)
VALUES ('test_user', TO_DATE('2025-10-12', 'YYYY-MM-DD'), '스타벅스 강남점', 8500);

INSERT INTO BNPL_USAGE_HISTORY (user_id, usage_date, merchant_name, amount)
VALUES ('test_user', TO_DATE('2025-10-11', 'YYYY-MM-DD'), 'CU편의점', 3200);

INSERT INTO BNPL_USAGE_HISTORY (user_id, usage_date, merchant_name, amount)
VALUES ('test_user', TO_DATE('2025-10-10', 'YYYY-MM-DD'), '올리브영', 25000);

INSERT INTO BNPL_USAGE_HISTORY (user_id, usage_date, merchant_name, amount)
VALUES ('test_user', TO_DATE('2025-10-08', 'YYYY-MM-DD'), '쿠팡', 45000);

INSERT INTO BNPL_USAGE_HISTORY (user_id, usage_date, merchant_name, amount)
VALUES ('test_user', TO_DATE('2025-10-05', 'YYYY-MM-DD'), '네이버페이', 12000);

INSERT INTO BNPL_USAGE_HISTORY (user_id, usage_date, merchant_name, amount)
VALUES ('test_user', TO_DATE('2025-10-03', 'YYYY-MM-DD'), 'GS25', 5600);

INSERT INTO BNPL_USAGE_HISTORY (user_id, usage_date, merchant_name, amount)
VALUES ('test_user', TO_DATE('2025-10-01', 'YYYY-MM-DD'), '이마트24', 8900);

INSERT INTO BNPL_USAGE_HISTORY (user_id, usage_date, merchant_name, amount)
VALUES ('test_user', TO_DATE('2025-09-28', 'YYYY-MM-DD'), '맥도날드', 11000);

INSERT INTO BNPL_USAGE_HISTORY (user_id, usage_date, merchant_name, amount)
VALUES ('test_user', TO_DATE('2025-09-25', 'YYYY-MM-DD'), '다이소', 15000);

INSERT INTO BNPL_USAGE_HISTORY (user_id, usage_date, merchant_name, amount)
VALUES ('test_user', TO_DATE('2025-09-22', 'YYYY-MM-DD'), '교보문고', 28000);

INSERT INTO BNPL_USAGE_HISTORY (user_id, usage_date, merchant_name, amount)
VALUES ('test_user', TO_DATE('2025-09-20', 'YYYY-MM-DD'), '쿠키 10개', 1200);

INSERT INTO BNPL_USAGE_HISTORY (user_id, usage_date, merchant_name, amount)
VALUES ('test_user', TO_DATE('2025-09-19', 'YYYY-MM-DD'), '카페베네', 6500);

INSERT INTO BNPL_USAGE_HISTORY (user_id, usage_date, merchant_name, amount)
VALUES ('test_user', TO_DATE('2025-09-15', 'YYYY-MM-DD'), '이디야커피', 4500);

COMMIT;


-- ============================================================================
-- 4. 데이터 삽입 확인 쿼리
-- ============================================================================

-- 주식 종목 데이터 통계
SELECT market_type, COUNT(*) as count
FROM STOCK
GROUP BY market_type
ORDER BY market_type;

-- 상품 데이터 확인
SELECT product_id, product_name, price, discount_rate
FROM PRODUCTS
ORDER BY product_id;

-- 후불결제 정보 확인
SELECT * FROM BNPL_INFO;

-- 후불결제 이용내역 확인
SELECT * FROM BNPL_USAGE_HISTORY
ORDER BY usage_date DESC;


-- ============================================================================
-- 끝
-- ============================================================================

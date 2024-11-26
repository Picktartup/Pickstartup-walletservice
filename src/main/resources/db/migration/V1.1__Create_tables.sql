CREATE TABLE public.wallet
(
    wallet_id          BIGINT       PRIMARY KEY DEFAULT nextval('wallet_seq'),
    user_id            BIGINT       NOT NULL,
    address            VARCHAR(255) NOT NULL UNIQUE,
    keystore_filename  VARCHAR(255),
    balance            NUMERIC(36, 18) NOT NULL,
    status             VARCHAR(50)  NOT NULL,
    created_at         TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    updated_at         TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT wallet_status_check CHECK (
        status IN ('ACTIVE', 'INACTIVE', 'SUSPENDED', 'DELETED')
    )
);

CREATE TABLE public.tokenTransaction
(
    id                 BIGINT PRIMARY KEY DEFAULT nextval('token_transactions_seq'),  -- 거래 ID는 BIGINT로 설정하고 token_transactions_seq 시퀀스를 사용
    user_id            BIGINT NOT NULL,                                               -- 사용자 ID
    order_id           VARCHAR(255) NOT NULL,                                         -- 주문 ID (필수로 설정)
    wallet_address     VARCHAR(255) NOT NULL,                                         -- 지갑 주소
    amount             NUMERIC(20, 8) NOT NULL,                                       -- 결제 금액
    token_amount       NUMERIC(20, 8) NOT NULL,                                       -- 토큰 수량
    transaction_hash   VARCHAR(255),                                                  -- 트랜잭션 해시
    status             VARCHAR(50) NOT NULL,                                          -- 거래 상태
    type               VARCHAR(50) NOT NULL,                                          -- 거래 유형
    completed_at       TIMESTAMP,                                                     -- 완료 시간
    failure_reason     VARCHAR(1000),                                                 -- 실패 사유
    created_at         TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,                  -- 생성 시간

    CONSTRAINT token_transactions_status_check CHECK (                                -- 상태 값 제한
        status IN ('PENDING', 'COMPLETED', 'FAILED')
    ),
    CONSTRAINT token_transactions_type_check CHECK (                                  -- 유형 값 제한
        type IN ('MINT', 'TRANSFER', 'BURN')
    )
);

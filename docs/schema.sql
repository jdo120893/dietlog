-- 사용자
CREATE TABLE users (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    email       VARCHAR(255) NOT NULL,
    password    VARCHAR(255) NOT NULL,            -- BCrypt 해시
    nickname    VARCHAR(50)  NOT NULL,
    created_at  DATETIME     NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_users_email (email)             -- 로그인 ID 중복 방지
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 카테고리 (User 1:N Category)
CREATE TABLE categories (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    user_id     BIGINT       NOT NULL,
    name        VARCHAR(50)  NOT NULL,
    type        ENUM('INCOME','EXPENSE') NOT NULL,
    created_at  DATETIME     NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_categories_user
        FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 거래내역 (User 1:N Transaction, Category 1:N Transaction)
CREATE TABLE transactions (
    id                BIGINT       NOT NULL AUTO_INCREMENT,
    user_id           BIGINT       NOT NULL,
    category_id       BIGINT       NOT NULL,
    type              ENUM('INCOME','EXPENSE') NOT NULL,
    amount            BIGINT       NOT NULL,      -- 원 단위 long, 항상 > 0
    description       VARCHAR(255) NULL,
    transaction_date  DATE         NOT NULL,      -- LocalDate
    created_at        DATETIME     NOT NULL,
    updated_at        DATETIME     NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_transactions_user
        FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_transactions_category
        FOREIGN KEY (category_id) REFERENCES categories(id),
    -- 자주 쓰는 조회(사용자별 + 월별)를 위한 인덱스
    KEY idx_tx_user_date (user_id, transaction_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE budgets (
    id            BIGINT      NOT NULL AUTO_INCREMENT,
    user_id       BIGINT      NOT NULL,
    category_id   BIGINT      NULL,            -- NULL = 전체 예산
    year_month    VARCHAR(7)  NOT NULL,        -- "2026-07"
    limit_amount  BIGINT      NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_budgets_user
        FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_budgets_category
        FOREIGN KEY (category_id) REFERENCES categories(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
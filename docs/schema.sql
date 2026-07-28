-- 사용자
CREATE TABLE users (
                       id          BIGINT       NOT NULL AUTO_INCREMENT,
                       email       VARCHAR(255) NOT NULL,
                       password    VARCHAR(255) NOT NULL,
                       nickname    VARCHAR(50)  NOT NULL,
                       created_at  DATETIME     NOT NULL,
                       PRIMARY KEY (id),
                       UNIQUE KEY uk_users_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 카테고리
CREATE TABLE categories (
                            id          BIGINT       NOT NULL AUTO_INCREMENT,
                            user_id     BIGINT       NOT NULL,
                            name        VARCHAR(50)  NOT NULL,
                            meal_type   ENUM('BREAKFAST','LUNCH','DINNER','SNACK') NOT NULL,
                            food_group  VARCHAR(50)  NOT NULL,
                            created_at  DATETIME     NOT NULL,
                            PRIMARY KEY (id),
                            CONSTRAINT fk_categories_user
                                FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 식단 기록
CREATE TABLE food_logs (
                           id           BIGINT       NOT NULL AUTO_INCREMENT,
                           user_id      BIGINT       NOT NULL,
                           category_id  BIGINT       NOT NULL,
                           meal_type    ENUM('BREAKFAST','LUNCH','DINNER','SNACK') NOT NULL,
                           calorie      BIGINT       NOT NULL,
                           memo         VARCHAR(255) NULL,
                           log_date     DATE         NOT NULL,
                           created_at   DATETIME     NOT NULL,
                           updated_at   DATETIME     NOT NULL,
                           PRIMARY KEY (id),
                           CONSTRAINT fk_foodlogs_user
                               FOREIGN KEY (user_id) REFERENCES users(id),
                           CONSTRAINT fk_foodlogs_category
                               FOREIGN KEY (category_id) REFERENCES categories(id),
                           KEY idx_foodlog_user_date (user_id, log_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 월 목표 칼로리
CREATE TABLE daily_goals (
                             id              BIGINT      NOT NULL AUTO_INCREMENT,
                             user_id         BIGINT      NOT NULL,
                             category_id     BIGINT      NULL,
                             `year_month`      VARCHAR(7)  NOT NULL,
                             target_calorie  BIGINT      NOT NULL,
                             PRIMARY KEY (id),
                             CONSTRAINT fk_dailygoals_user
                                 FOREIGN KEY (user_id) REFERENCES users(id),
                             CONSTRAINT fk_dailygoals_category
                                 FOREIGN KEY (category_id) REFERENCES categories(id),
                             UNIQUE KEY uk_dailygoals_user_month_category (user_id, `year_month`, category_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE refresh_tokens (
                                id          BIGINT       NOT NULL AUTO_INCREMENT,
                                user_id     BIGINT       NOT NULL,
                                token       VARCHAR(500) NOT NULL,
                                expires_at  DATETIME     NOT NULL,
                                PRIMARY KEY (id),
                                UNIQUE KEY uk_refresh_tokens_token (token),
                                CONSTRAINT fk_refreshtokens_user
                                    FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
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
                            meal_type   ENUM('BREAKFAST','LUNCH','DINNER','SNACK') NOT NULL,
                            food_group  VARCHAR(50)  NOT NULL,             -- 한식/양식/분식/디저트 등
                            created_at  DATETIME     NOT NULL,
                            PRIMARY KEY (id),
                            CONSTRAINT fk_categories_user
                                FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 식단 기록 (User 1:N FoodLog, Category 1:N FoodLog)
CREATE TABLE food_logs (
                           id           BIGINT       NOT NULL AUTO_INCREMENT,
                           user_id      BIGINT       NOT NULL,
                           category_id  BIGINT       NOT NULL,
                           meal_type    ENUM('BREAKFAST','LUNCH','DINNER','SNACK') NOT NULL,
                           calorie      BIGINT       NOT NULL,      -- kcal 단위, 항상 > 0
                           memo         VARCHAR(255) NULL,          -- 음식명/메모
                           log_date     DATE         NOT NULL,      -- LocalDate
                           created_at   DATETIME     NOT NULL,
                           updated_at   DATETIME     NOT NULL,
                           PRIMARY KEY (id),
                           CONSTRAINT fk_foodlogs_user
                               FOREIGN KEY (user_id) REFERENCES users(id),
                           CONSTRAINT fk_foodlogs_category
                               FOREIGN KEY (category_id) REFERENCES categories(id),
    -- 자주 쓰는 조회(사용자별 + 월별)를 위한 인덱스
                           KEY idx_foodlog_user_date (user_id, log_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 월 목표 칼로리 (User 1:N DailyGoal, Category 1:N DailyGoal)
CREATE TABLE daily_goals (
                             id              BIGINT      NOT NULL AUTO_INCREMENT,
                             user_id         BIGINT      NOT NULL,
                             category_id     BIGINT      NULL,            -- NULL = 월 전체 목표
                             year_month      VARCHAR(7)  NOT NULL,         -- "2026-07"
                             target_calorie  BIGINT      NOT NULL,
                             PRIMARY KEY (id),
                             CONSTRAINT fk_dailygoals_user
                                 FOREIGN KEY (user_id) REFERENCES users(id),
                             CONSTRAINT fk_dailygoals_category
                                 FOREIGN KEY (category_id) REFERENCES categories(id),
    -- 같은 달·같은 카테고리 목표 중복 방지 (전체 목표(NULL)는 애플리케이션 레벨에서 별도 검증 필요)
                             UNIQUE KEY uk_dailygoals_user_month_category (user_id, year_month, category_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
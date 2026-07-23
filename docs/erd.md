┌─────────────────┐
│      USER       │
│─────────────────│
│ PK id           │
│    email (UQ)   │
│    password     │
│    nickname     │
│    created_at   │
└───────┬─────────┘
   1    │    1               1
   ┌────┼───────────┐        └───────────┐
   │ N  │ N          │ N                  │ N (도전)
┌──▼────────────┐  ┌─▼────────────────┐ ┌─▼──────────────────────┐
│   CATEGORY    │  │     FOODLOG       │ │      DAILYGOAL          │
│───────────────│  │───────────────────│ │──────────────────────────│
│ PK id         │1 │ PK id             │ │ PK id                    │
│ FK user_id    │──┼<FK user_id        │ │ FK user_id                │
│    name       │N │ FK category_id ───┼─┼<FK category_id (NULL)     │
│    meal_type  │  │    meal_type      │ │    year_month              │
│    food_group │  │    calorie        │ │    target_calorie          │
│    created_at │  │    memo           │ └──────────────────────────┘
└───────────────┘  │    log_date       │
                    │    created_at     │
                    │    updated_at     │
                    └───────────────────┘
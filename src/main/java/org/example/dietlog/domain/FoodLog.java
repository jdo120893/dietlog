package org.example.dietlog.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "food_logs")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FoodLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Enumerated(EnumType.STRING)
    @Column(name = "meal_type", nullable = false)
    private MealType mealType;

    @Column(nullable = false)
    private Long calorie;

    @Column(length = 255)
    private String memo;

    @Column(name = "log_date", nullable = false)
    private LocalDate logDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public FoodLog(User user, Category category, MealType mealType, Long calorie, String memo, LocalDate logDate) {
        this.user = user;
        this.category = category;
        this.mealType = mealType;
        this.calorie = calorie;
        this.memo = memo;
        this.logDate = logDate;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void update(Category category, MealType mealType, Long calorie,
                       String memo, LocalDate logDate) {

        this.category = category;
        this.mealType = mealType;
        this.calorie = calorie;
        this.memo = memo;
        this.logDate = logDate;
    }
}

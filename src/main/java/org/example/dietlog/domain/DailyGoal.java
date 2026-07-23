package org.example.dietlog.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "daily_goals",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"user_id", "year_month", "category_id"}
        )
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DailyGoal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = true)
    private Category category;

    @Column(name = "year_month", nullable = false, length = 7)
    private String yearMonth;

    @Column(name = "target_calorie", nullable = false)
    private Long targetCalorie;

    @Builder
    public DailyGoal(User user, Category category, String yearMonth, Long targetCalorie) {
        this.user = user;
        this.category = category;
        this.yearMonth = yearMonth;
        this.targetCalorie = targetCalorie;
    }

    public void updateTargetCalorie(Long targetCalorie) {
        this.targetCalorie = targetCalorie;
    }
}

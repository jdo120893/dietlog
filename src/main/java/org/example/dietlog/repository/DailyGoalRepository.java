package org.example.dietlog.repository;

import org.example.dietlog.domain.Category;
import org.example.dietlog.domain.DailyGoal;
import org.example.dietlog.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DailyGoalRepository extends JpaRepository<DailyGoal, Long> {

    List<DailyGoal> findByUserAndYearMonth(User user, String yearMonth);

    Optional<DailyGoal> findByUserAndYearMonthAndCategory(User user, String yearMont, Category category);

    Optional<DailyGoal> findByIdAndUser(Long id, User user);
}

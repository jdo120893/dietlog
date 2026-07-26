package org.example.dietlog.service;

import lombok.RequiredArgsConstructor;
import org.example.dietlog.domain.Category;
import org.example.dietlog.domain.DailyGoal;
import org.example.dietlog.domain.User;
import org.example.dietlog.repository.DailyGoalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DailyGoalService {

    private final DailyGoalRepository dailyGoalRepository;

    public List<DailyGoal> getMyGoals(User user, String yearMonth) {
        return dailyGoalRepository.findByUserAndYearMonth(user, yearMonth);
    }

    @Transactional
    public DailyGoal createGoal(User user, Category category, String yearMonth, Long targetCalorie) {
        // 같은 달·같은 카테고리(또는 전체) 목표 중복 방지 — DB 유니크 제약과 짝을 이루는 애플리케이션 레벨 검증
        dailyGoalRepository.findByUserAndYearMonthAndCategory(user, yearMonth, category)
                .ifPresent(existing -> {
                    throw new IllegalStateException("GOAL_ALREADY_EXISTS");
                });

        DailyGoal goal = DailyGoal.builder()
                .user(user)
                .category(category)
                .yearMonth(yearMonth)
                .targetCalorie(targetCalorie)
                .build();
        return dailyGoalRepository.save(goal);
    }

    @Transactional
    public DailyGoal updateGoal(User user, Long goalId, Long targetCalorie) {
        DailyGoal goal = getMyGoalOrThrow(user, goalId);
        goal.updateTargetCalorie(targetCalorie);
        return goal;
    }

    @Transactional
    public void deleteGoal(User user, Long goalId) {
        DailyGoal goal = getMyGoalOrThrow(user, goalId);
        dailyGoalRepository.delete(goal);
    }

    private DailyGoal getMyGoalOrThrow(User user, Long goalId) {
        return dailyGoalRepository.findById(goalId)
                .filter(g -> g.getUser().equals(user))
                .orElseThrow(() -> new IllegalArgumentException("GOAL_NOT_FOUND"));
    }
}
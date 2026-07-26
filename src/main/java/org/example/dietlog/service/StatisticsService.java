package org.example.dietlog.service;

import lombok.RequiredArgsConstructor;
import org.example.dietlog.domain.User;
import org.example.dietlog.repository.FoodLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatisticsService {

    private final FoodLogRepository foodLogRepository;

    public MonthlyStatistics getMonthlyStatistics(User user, String yearMonth) {
        YearMonth ym = YearMonth.parse(yearMonth);
        LocalDate from = ym.atDay(1);
        LocalDate to = ym.atEndOfMonth();

        Long totalCalorie = foodLogRepository.sumCalorieByUserAndDateRange(user, from, to);
        List<FoodLogRepository.MealTypeCalorieSum> byMealType =
                foodLogRepository.sumCalorieByMealType(user, from, to);
        List<FoodLogRepository.FoodGroupCalorieSum> byFoodGroup =
                foodLogRepository.sumCalorieByFoodGroup(user, from, to);

        return new MonthlyStatistics(
                totalCalorie != null ? totalCalorie : 0L,
                byMealType,
                byFoodGroup
        );
    }

    public record MonthlyStatistics(
            Long totalCalorie,
            List<FoodLogRepository.MealTypeCalorieSum> byMealType,
            List<FoodLogRepository.FoodGroupCalorieSum> byFoodGroup
    ) {}
}
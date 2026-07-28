package org.example.dietlog.service;

import lombok.RequiredArgsConstructor;
import org.example.dietlog.domain.Category;
import org.example.dietlog.domain.FoodLog;
import org.example.dietlog.domain.MealType;
import org.example.dietlog.domain.User;
import org.example.dietlog.repository.FoodLogRepository;
import org.springframework.cglib.core.Local;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FoodLogService {

    private final FoodLogRepository foodLogRepository;

    public Page<FoodLog> search(User user, String yearMonth, MealType mealType, Category category, Pageable pageable) {
        YearMonth ym = YearMonth.parse(yearMonth);
        LocalDate from = ym.atDay(1);
        LocalDate to = ym.atEndOfMonth();
        return foodLogRepository.search(user, from, to, mealType, category, pageable);
    }

    public Page<FoodLog> searchByKeyword(User user, String keyword, LocalDate from, LocalDate to,
                                         Long minCalorie, Long maxCalorie, Pageable pageable) {
        return foodLogRepository.searchByKeyword(user, keyword, from, to, minCalorie, maxCalorie, pageable);
    }

    public FoodLog getMyFoodLogOrThrow(User user, Long id) {
        return foodLogRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("NOT_FOUND_FOODLOG"));
    }

    @Transactional
    public FoodLog createFoodLog(User user, Category category, MealType mealType,
                                 Long calorie, String memo, LocalDate logDate) {
        FoodLog foodLog = FoodLog.builder()
                .user(user)
                .category(category)
                .mealType(mealType)
                .calorie(calorie)
                .memo(memo)
                .logDate(logDate)
                .build();
        return foodLogRepository.save(foodLog);
    }

    @Transactional
    public FoodLog updateFoodLog(User user, Long id, Category category, MealType mealType,
                                 Long calorie, String memo, LocalDate logDate) {
        FoodLog foodLog = getMyFoodLogOrThrow(user, id);
        foodLog.update(category, mealType, calorie, memo, logDate);
        return foodLog;
    }

    @Transactional
    public void deleteFoodLog(User user, Long id) {
        FoodLog foodLog = getMyFoodLogOrThrow(user, id);
        foodLogRepository.delete(foodLog);
    }
}

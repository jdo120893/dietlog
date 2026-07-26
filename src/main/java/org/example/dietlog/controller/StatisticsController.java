package org.example.dietlog.controller;

import lombok.RequiredArgsConstructor;
import org.example.dietlog.domain.User;
import org.example.dietlog.repository.FoodLogRepository;
import org.example.dietlog.repository.UserRepository;
import org.example.dietlog.service.StatisticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;
    private final UserRepository userRepository;

    @GetMapping("/monthly")
    public ResponseEntity<MonthlyStatisticsResponse> getMonthlyStatistics(
            Authentication authentication,
            @RequestParam String yearMonth
    ) {
        User user = getCurrentUser(authentication);
        StatisticsService.MonthlyStatistics stats = statisticsService.getMonthlyStatistics(user, yearMonth);
        return ResponseEntity.ok(MonthlyStatisticsResponse.from(stats));
    }

    private User getCurrentUser(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("USER_NOT_FOUND"));
    }

    record MonthlyStatisticsResponse(
            Long totalCalorie,
            List<MealTypeStat> byMealType,
            List<FoodGroupStat> byFoodGroup
    ) {
        static MonthlyStatisticsResponse from(StatisticsService.MonthlyStatistics stats) {
            return new MonthlyStatisticsResponse(
                    stats.totalCalorie(),
                    stats.byMealType().stream()
                            .map(m -> new MealTypeStat(m.getMealType(), m.getTotal()))
                            .toList(),
                    stats.byFoodGroup().stream()
                            .map(f -> new FoodGroupStat(f.getFoodGroup(), f.getTotal()))
                            .toList()
            );
        }
    }

    record MealTypeStat(org.example.dietlog.domain.MealType mealType, Long total) {}
    record FoodGroupStat(String foodGroup, Long total) {}
}
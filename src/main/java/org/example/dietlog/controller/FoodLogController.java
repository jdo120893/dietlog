package org.example.dietlog.controller;

import lombok.RequiredArgsConstructor;
import org.example.dietlog.domain.*;
import org.example.dietlog.repository.CategoryRepository;
import org.example.dietlog.repository.UserRepository;
import org.example.dietlog.service.FoodLogService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/foodlogs")
@RequiredArgsConstructor
public class FoodLogController {

    private final FoodLogService foodLogService;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    @GetMapping
    public ResponseEntity<Page<FoodLogResponse>> getFoodLogs(
            Authentication authentication,
            @RequestParam String yearMonth,
            @RequestParam(required = false) MealType mealType,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        User user = getCurrentUser(authentication);
        Category category = categoryId != null ? categoryRepository.findById(categoryId).orElse(null) : null;

        Page<FoodLog> result = foodLogService.search(user, yearMonth, mealType, category, PageRequest.of(page, size));
        return ResponseEntity.ok(result.map(FoodLogResponse::from));
    }

    @PostMapping
    public ResponseEntity<FoodLogResponse> createFoodLog(Authentication authentication,
                                                         @RequestBody FoodLogRequest request) {
        User user = getCurrentUser(authentication);
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new IllegalArgumentException("CATEGORY_NOT_FOUND"));

        FoodLog foodLog = foodLogService.createFoodLog(
                user, category, request.mealType(), request.calorie(), request.memo(), request.logDate());
        return ResponseEntity.status(HttpStatus.CREATED).body(FoodLogResponse.from(foodLog));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FoodLogResponse> getFoodLog(Authentication authentication, @PathVariable Long id) {
        User user = getCurrentUser(authentication);
        FoodLog foodLog = foodLogService.getMyFoodLogOrThrow(user, id);
        return ResponseEntity.ok(FoodLogResponse.from(foodLog));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FoodLogResponse> updateFoodLog(Authentication authentication,
                                                         @PathVariable Long id,
                                                         @RequestBody FoodLogRequest request) {
        User user = getCurrentUser(authentication);
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new IllegalArgumentException("CATEGORY_NOT_FOUND"));

        FoodLog updated = foodLogService.updateFoodLog(
                user, id, category, request.mealType(), request.calorie(), request.memo(), request.logDate());
        return ResponseEntity.ok(FoodLogResponse.from(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFoodLog(Authentication authentication, @PathVariable Long id) {
        User user = getCurrentUser(authentication);
        foodLogService.deleteFoodLog(user, id);
        return ResponseEntity.noContent().build();
    }

    private User getCurrentUser(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("USER_NOT_FOUND"));
    }

    record FoodLogRequest(MealType mealType, Long calorie, Long categoryId, String memo, LocalDate logDate) {}

    record FoodLogResponse(Long id, MealType mealType, Long calorie, Long categoryId,
                           String categoryName, String memo, LocalDate logDate) {
        static FoodLogResponse from(FoodLog foodLog) {
            return new FoodLogResponse(
                    foodLog.getId(),
                    foodLog.getMealType(),
                    foodLog.getCalorie(),
                    foodLog.getCategory().getId(),
                    foodLog.getCategory().getName(),
                    foodLog.getMemo(),
                    foodLog.getLogDate()
            );
        }
    }
}
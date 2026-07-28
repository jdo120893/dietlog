package org.example.dietlog.controller;

import lombok.RequiredArgsConstructor;
import org.example.dietlog.domain.Category;
import org.example.dietlog.domain.DailyGoal;
import org.example.dietlog.domain.User;
import org.example.dietlog.repository.CategoryRepository;
import org.example.dietlog.repository.UserRepository;
import org.example.dietlog.service.DailyGoalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/goals")
@RequiredArgsConstructor
public class DailyGoalController {

    private final DailyGoalService dailyGoalService;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    @GetMapping
    public ResponseEntity<List<DailyGoalResponse>> getGoals(
            Authentication authentication,
            @RequestParam String yearMonth
    ) {
        User user = getCurrentUser(authentication);
        List<DailyGoalResponse> response = dailyGoalService.getMyGoals(user, yearMonth).stream()
                .map(DailyGoalResponse::from)
                .toList();
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<DailyGoalResponse> createGoal(
            Authentication authentication,
            @RequestBody DailyGoalRequest request
    ) {
        User user = getCurrentUser(authentication);
        Category category = request.categoryId() != null
                ? categoryRepository.findById(request.categoryId())
                  .orElseThrow(() -> new IllegalArgumentException("CATEGORY_NOT_FOUND"))
                : null;

        DailyGoal goal = dailyGoalService.createGoal(user, category, request.yearMonth(), request.targetCalorie());
        return ResponseEntity.status(HttpStatus.CREATED).body(DailyGoalResponse.from(goal));
    }

    private User getCurrentUser(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("USER_NOT_FOUND"));
    }

    record DailyGoalRequest(Long categoryId, String yearMonth, Long targetCalorie) {}

    record DailyGoalResponse(Long id, Long categoryId, String categoryName, String yearMonth, Long targetCalorie) {
        static DailyGoalResponse from(DailyGoal goal) {
            return new DailyGoalResponse(
                    goal.getId(),
                    goal.getCategory() != null ? goal.getCategory().getId() : null,
                    goal.getCategory() != null ? goal.getCategory().getName() : null,
                    goal.getYearMonth(),
                    goal.getTargetCalorie()
            );
        }
    }
}
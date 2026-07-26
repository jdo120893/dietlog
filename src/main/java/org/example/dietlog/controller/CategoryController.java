package org.example.dietlog.controller;

import lombok.RequiredArgsConstructor;
import org.example.dietlog.domain.Category;
import org.example.dietlog.domain.MealType;
import org.example.dietlog.domain.User;
import org.example.dietlog.repository.UserRepository;
import org.example.dietlog.service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getCategories(Authentication authentication) {
        User user = getCurrentUser(authentication);
        List<CategoryResponse> response = categoryService.getMyCategories(user).stream()
                .map(CategoryResponse::from)
                .toList();
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(Authentication authentication,
                                                           @RequestBody CategoryRequest request) {
        User user = getCurrentUser(authentication);
        Category category = categoryService.createCategory(user, request.name(), request.mealType(), request.foodGroup());
        return ResponseEntity.status(HttpStatus.CREATED).body(CategoryResponse.from(category));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> updateCategory(Authentication authentication,
                                                   @PathVariable Long id,
                                                   @RequestBody CategoryRequest request) {
        User user = getCurrentUser(authentication);
        Category updated = categoryService.updateCategory(user, id, request.name(), request.mealType(), request.foodGroup());
        return ResponseEntity.ok(CategoryResponse.from(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(Authentication authentication, @PathVariable Long id) {
        User user = getCurrentUser(authentication);
        categoryService.deleteCategory(user, id);
        return ResponseEntity.noContent().build();
    }

    private User getCurrentUser(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("USER_NOT_FOUND"));
    }

    record CategoryRequest(String name, MealType mealType, String foodGroup) {
    }

    record CategoryResponse(Long id, String name, MealType mealType, String foodGroup) {
        static CategoryResponse from(Category category) {
            return new CategoryResponse(
                    category.getId(),
                    category.getName(),
                    category.getMealType(),
                    category.getFoodGroup()
            );
        }
    }
}
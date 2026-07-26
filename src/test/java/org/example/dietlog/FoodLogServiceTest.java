package org.example.dietlog;

import org.example.dietlog.domain.Category;
import org.example.dietlog.domain.FoodLog;
import org.example.dietlog.domain.MealType;
import org.example.dietlog.domain.User;
import org.example.dietlog.repository.CategoryRepository;
import org.example.dietlog.repository.UserRepository;
import org.example.dietlog.service.FoodLogService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class FoodLogServiceTest {

    @Autowired
    private FoodLogService foodLogService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void 식단기록_CRUD_동작_확인() {
        // given
        User user = userRepository.save(
                User.builder()
                        .email("test2@dietlog.com")
                        .password("encoded-password")
                        .nickname("테스터2")
                        .build()
        );

        Category category = categoryRepository.save(
                Category.builder()
                        .user(user)
                        .name("점심 한식")
                        .mealType(MealType.LUNCH)
                        .foodGroup("한식")
                        .build()
        );

        // when: 식단 기록 등록
        FoodLog foodLog = foodLogService.createFoodLog(
                user, category, MealType.LUNCH, 650L, "제육볶음 정식", LocalDate.of(2026, 7, 8)
        );

        // then: 등록 확인
        assertThat(foodLog.getId()).isNotNull();
        assertThat(foodLog.getCalorie()).isEqualTo(650L);

        // when: 월별 검색
        Page<FoodLog> page = foodLogService.search(user, "2026-07", null, null, PageRequest.of(0, 20));

        // then: 검색 확인
        assertThat(page.getTotalElements()).isEqualTo(1);

        // when: 수정
        FoodLog updated = foodLogService.updateFoodLog(
                user, foodLog.getId(), category, MealType.LUNCH, 700L, "제육볶음 곱빼기", LocalDate.of(2026, 7, 8)
        );

        // then: 수정 확인
        assertThat(updated.getCalorie()).isEqualTo(700L);

        // when: 삭제
        foodLogService.deleteFoodLog(user, foodLog.getId());

        // then: 삭제 확인
        Page<FoodLog> afterDelete = foodLogService.search(user, "2026-07", null, null, PageRequest.of(0, 20));
        assertThat(afterDelete.getTotalElements()).isEqualTo(0);
    }
}
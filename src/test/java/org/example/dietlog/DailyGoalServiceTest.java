package org.example.dietlog;

import org.example.dietlog.domain.Category;
import org.example.dietlog.domain.DailyGoal;
import org.example.dietlog.domain.MealType;
import org.example.dietlog.domain.User;
import org.example.dietlog.repository.CategoryRepository;
import org.example.dietlog.repository.UserRepository;
import org.example.dietlog.service.DailyGoalService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class DailyGoalServiceTest {

    @Autowired
    private DailyGoalService dailyGoalService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void 목표칼로리_CRUD_동작_확인() {
        // given
        User user = userRepository.save(
                User.builder()
                        .email("test3@dietlog.com")
                        .password("encoded-password")
                        .nickname("테스터3")
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

        // when: 전체 목표 등록 (category = null)
        DailyGoal wholeGoal = dailyGoalService.createGoal(user, null, "2026-07", 2000L);

        // then
        assertThat(wholeGoal.getId()).isNotNull();
        assertThat(wholeGoal.getCategory()).isNull();

        // when: 카테고리별 목표 등록
        DailyGoal categoryGoal = dailyGoalService.createGoal(user, category, "2026-07", 300L);

        // then
        assertThat(categoryGoal.getCategory()).isEqualTo(category);

        // when: 목록 조회
        List<DailyGoal> goals = dailyGoalService.getMyGoals(user, "2026-07");

        // then: 전체 목표 + 카테고리별 목표 = 2건
        assertThat(goals).hasSize(2);

        // when & then: 같은 조건으로 다시 등록하면 예외 발생 (중복 방지 확인)
        assertThatThrownBy(() ->
                dailyGoalService.createGoal(user, null, "2026-07", 1800L)
        ).isInstanceOf(IllegalStateException.class);

        // when: 목표 칼로리 수정
        DailyGoal updated = dailyGoalService.updateGoal(user, wholeGoal.getId(), 2200L);

        // then
        assertThat(updated.getTargetCalorie()).isEqualTo(2200L);

        // when: 삭제
        dailyGoalService.deleteGoal(user, wholeGoal.getId());

        // then: 1건만 남아야 함
        assertThat(dailyGoalService.getMyGoals(user, "2026-07")).hasSize(1);
    }
}
package jshop.core.category.entity;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import jshop.core.domain.category.dto.CreateCategoryRequest;
import jshop.core.domain.category.entity.Category;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("[단위 테스트] Category")
class CategoryTest {

    @Test
    @DisplayName("CreateCategoryRequest 로 Category 생성 테스트")
    public void of_success() {
        // given
        CreateCategoryRequest createCategoryRequest = CreateCategoryRequest
            .builder().name("전자제품").build();

        // when
        Category category = Category.of(createCategoryRequest);
        // then
        assertThat(category.getName()).isEqualTo(createCategoryRequest.getName());
    }
}
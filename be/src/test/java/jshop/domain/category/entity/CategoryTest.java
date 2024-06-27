package jshop.domain.category.entity;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import jshop.domain.category.dto.CreateCategoryRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Category 엔티티 테스트")
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
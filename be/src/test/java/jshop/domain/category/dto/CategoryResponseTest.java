package jshop.domain.category.dto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import jshop.domain.category.entity.Category;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("[단위 테스트] CategoryResponse")
class CategoryResponseTest {

    @Test
    @DisplayName("Category 로 CategoryResponse 생성 검증")
    public void of() {
        // given
        Category category = Category
            .builder().id(1L).name("전자기기").build();
        // when
        CategoryResponse categoryResponse = CategoryResponse.of(category);

        // then
        assertAll("카테고리 응답 DTO 검증", () -> assertThat(categoryResponse.getName()).isEqualTo(category.getName()),
            () -> assertThat(categoryResponse.getId()).isEqualTo(category.getId()));
    }
}
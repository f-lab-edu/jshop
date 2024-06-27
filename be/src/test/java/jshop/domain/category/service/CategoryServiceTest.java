package jshop.domain.category.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jshop.domain.category.dto.CreateCategoryRequest;
import jshop.domain.category.entity.Category;
import jshop.domain.category.repository.CategoryRepository;
import jshop.global.common.ErrorCode;
import jshop.global.exception.JshopException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("CategoryService Service 테스트")
class CategoryServiceTest {

    @InjectMocks
    private CategoryService categoryService;

    @Mock
    private CategoryRepository categoryRepository;

    @Captor
    private ArgumentCaptor<Category> categoryCaptor;


    @Nested
    class CreateCategory {

        @Test
        @DisplayName("중복 없는 카테고리는 정상적으로 추가됨")
        public void createCategory_success() {
            // given
            CreateCategoryRequest categoryRequest = CreateCategoryRequest
                .builder().name("전자제품").build();

            // when
            categoryService.createCategory(categoryRequest, "ROLE_USER");

            // then
            verify(categoryRepository, times(1)).save(categoryCaptor.capture());
            assertThat(categoryCaptor.getValue().getName()).isEqualTo(categoryRequest.getName());
        }

        @Test
        @DisplayName("중복된 카테고리를 추가하면 ALREADY_EXISTS_CATEGORY 예외가 터짐")
        public void createCategory_dup() {
            // given
            CreateCategoryRequest categoryRequest1 = CreateCategoryRequest
                .builder().name("전자제품").build();

            CreateCategoryRequest categoryRequest2 = CreateCategoryRequest
                .builder().name("전자제품").build();

            // when
            categoryService.createCategory(categoryRequest1, "ROLE_USER");
            when(categoryRepository.existsByName(categoryRequest2.getName())).thenReturn(true);

            // then
            JshopException jshopException = assertThrows(JshopException.class,
                () -> categoryService.createCategory(categoryRequest2, "ROLE_USER"));
            assertThat(jshopException.getErrorCode()).isEqualTo(ErrorCode.ALREADY_EXISTS_CATEGORY);
        }
    }


}
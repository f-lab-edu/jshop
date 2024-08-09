package jshop.core.category.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jshop.core.domain.category.dto.CreateCategoryRequest;
import jshop.core.domain.category.entity.Category;
import jshop.core.domain.category.repository.CategoryRepository;
import jshop.core.domain.category.service.CategoryService;
import jshop.common.exception.ErrorCode;
import jshop.common.exception.JshopException;
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
@DisplayName("[단위 테스트] CategoryService")
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

            // whenㅁ
            categoryService.createCategory(categoryRequest);

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
            categoryService.createCategory(categoryRequest1);
            when(categoryRepository.existsByName(categoryRequest2.getName())).thenReturn(true);

            // then
            JshopException jshopException = assertThrows(JshopException.class,
                () -> categoryService.createCategory(categoryRequest2));
            assertThat(jshopException.getErrorCode()).isEqualTo(ErrorCode.ALREADY_EXISTS_CATEGORY);
        }
    }
}
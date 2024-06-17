package jshop.domain.category.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jshop.domain.category.dto.CreateCategoryRequest;
import jshop.domain.category.entity.Category;
import jshop.domain.category.repository.CategoryRepository;
import jshop.global.exception.category.AlreadyExistsNameCategory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @InjectMocks
    private CategoryService categoryService;

    @Mock
    private CategoryRepository categoryRepository;

    @Captor
    private ArgumentCaptor<Category> categoryCaptor;

    @Test
    public void 카테고리추가() {
        // given
        CreateCategoryRequest categoryRequest = CreateCategoryRequest
            .builder().name("전자제품").build();

        // when
        categoryService.createCategory(categoryRequest);

        // then
        verify(categoryRepository, times(1)).save(categoryCaptor.capture());
        assertThat(categoryCaptor.getValue().getName()).isEqualTo(categoryRequest.getName());
    }

    @Test
    public void 중복카테고리추가() {
        // given
        CreateCategoryRequest categoryRequest1 = CreateCategoryRequest
            .builder().name("전자제품").build();

        CreateCategoryRequest categoryRequest2 = CreateCategoryRequest
            .builder().name("전자제품").build();

        // when
        categoryService.createCategory(categoryRequest1);
        when(categoryRepository.existsByName(categoryRequest2.getName())).thenReturn(true);

        // then
        assertThrows(AlreadyExistsNameCategory.class, () -> categoryService.createCategory(categoryRequest2));
    }

}
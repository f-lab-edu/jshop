package jshop.domain.category.service;

import java.util.List;
import jshop.domain.category.controller.CategoryController;
import jshop.domain.category.dto.CategoryResponse;
import jshop.domain.category.dto.CreateCategoryRequest;
import jshop.domain.category.entity.Category;
import jshop.domain.category.repository.CategoryRepository;
import jshop.global.common.ErrorCode;
import jshop.global.exception.JshopException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream().map(CategoryResponse::ofCategory).toList();
    }

    @Transactional
    public void createCategory(CreateCategoryRequest createCategoryRequest) {
        if (categoryRepository.existsByName(createCategoryRequest.getName())) {
            log.error(ErrorCode.ALREADY_EXISTS_CATEGORY.getLogMessage(), createCategoryRequest.getName());
            throw JshopException.ofErrorCode(ErrorCode.ALREADY_EXISTS_CATEGORY);
        }

        Category category = Category
            .builder().name(createCategoryRequest.getName()).build();

        categoryRepository.save(category);
    }
}

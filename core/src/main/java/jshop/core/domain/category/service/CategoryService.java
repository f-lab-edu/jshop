package jshop.core.domain.category.service;

import java.util.List;
import jshop.core.domain.category.repository.CategoryRepository;
import jshop.core.domain.category.dto.CategoryResponse;
import jshop.core.domain.category.dto.CreateCategoryRequest;
import jshop.core.domain.category.entity.Category;
import jshop.common.exception.ErrorCode;
import jshop.common.exception.JshopException;
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
        return categoryRepository.findAll().stream().map(CategoryResponse::of).toList();
    }

    @Transactional
    public Long createCategory(CreateCategoryRequest createCategoryRequest) {

        if (categoryRepository.existsByName(createCategoryRequest.getName())) {
            log.error(ErrorCode.ALREADY_EXISTS_CATEGORY.getLogMessage(), createCategoryRequest.getName());
            throw JshopException.of(ErrorCode.ALREADY_EXISTS_CATEGORY);
        }

        Category category = Category.of(createCategoryRequest);
        categoryRepository.save(category);
        return category.getId();
    }

    public Category getCategory(Long categoryId) {
        return categoryRepository.findById(categoryId).orElseThrow(() -> {
            log.error(ErrorCode.CATEGORYID_NOT_FOUND.getLogMessage(), categoryId);
            throw JshopException.of(ErrorCode.CATEGORYID_NOT_FOUND);
        });

    }
}

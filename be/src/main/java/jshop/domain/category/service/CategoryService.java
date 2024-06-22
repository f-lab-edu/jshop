package jshop.domain.category.service;

import java.util.List;
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
        return categoryRepository.findAll().stream().map(CategoryResponse::of).toList();
    }

    @Transactional
    public void createCategory(CreateCategoryRequest createCategoryRequest, String userRole) {
        if (userRole == null) {
            /**
             * TODO 권한에 따라 카테고리 생성이 가능함.
             */
            log.error(ErrorCode.UNAUTHORIZED.getLogMessage(), "role", userRole);
            throw JshopException.of(ErrorCode.UNAUTHORIZED);
        }
        if (categoryRepository.existsByName(createCategoryRequest.getName())) {
            log.error(ErrorCode.ALREADY_EXISTS_CATEGORY.getLogMessage(), createCategoryRequest.getName());
            throw JshopException.of(ErrorCode.ALREADY_EXISTS_CATEGORY);
        }

        Category category = Category.of(createCategoryRequest);

        categoryRepository.save(category);
    }
}

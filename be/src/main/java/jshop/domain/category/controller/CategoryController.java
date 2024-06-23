package jshop.domain.category.controller;

import jakarta.validation.Valid;
import java.util.List;
import jshop.domain.category.dto.CategoryResponse;
import jshop.domain.category.dto.CreateCategoryRequest;
import jshop.domain.category.service.CategoryService;
import jshop.global.annotation.CurrentUserId;
import jshop.global.annotation.CurrentUserRole;
import jshop.global.dto.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public Response<List<CategoryResponse>> getAllCategories() {
        return Response
            .<List<CategoryResponse>>builder().data(categoryService.getAllCategories()).build();
    }

    @PostMapping
    public void createCategory(@RequestBody @Valid CreateCategoryRequest createCategoryRequest,
        @CurrentUserRole String userRole) {
        categoryService.createCategory(createCategoryRequest, userRole);
    }
}

package jshop.web.controller;

import jakarta.validation.Valid;
import java.util.List;
import jshop.core.domain.category.dto.CategoryResponse;
import jshop.core.domain.category.dto.CreateCategoryRequest;
import jshop.core.domain.category.service.CategoryService;
import jshop.web.dto.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
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

    @Secured("ROLE_ADMIN")
    @PostMapping
    public void createCategory(@RequestBody @Valid CreateCategoryRequest createCategoryRequest) {
        categoryService.createCategory(createCategoryRequest);
    }
}

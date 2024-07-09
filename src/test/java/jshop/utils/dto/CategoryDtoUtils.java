package jshop.utils.dto;

import jshop.domain.category.dto.CreateCategoryRequest;

public class CategoryDtoUtils {

    public static CreateCategoryRequest getCreateCategoryRequest(String name) {
        return CreateCategoryRequest
            .builder().name(name).build();
    }

    public static CreateCategoryRequest getCreateCategoryRequest() {
        return CreateCategoryRequest
            .builder().name("category").build();
    }
}

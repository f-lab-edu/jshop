package jshop.core.domain.category.dto;

import jshop.core.domain.category.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class CategoryResponse {

    private Long id;
    private String name;

    public static CategoryResponse of(Category category) {
        return CategoryResponse
            .builder().id(category.getId()).name(category.getName()).build();
    }
}

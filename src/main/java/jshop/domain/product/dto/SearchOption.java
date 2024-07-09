package jshop.domain.product.dto;

import java.util.List;
import java.util.Map;
import jshop.domain.category.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class SearchOption {

    private Order order;
    private Filter filter;

    static class Order {

        private boolean date;
        private boolean name;
        private boolean price;
    }

    static class Filter {

        private String manufacturer;
        private Long categoryId;
        private List<Map<String, String>> attributeFilters;
    }
}


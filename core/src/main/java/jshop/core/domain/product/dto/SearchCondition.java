package jshop.core.domain.product.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Setter
@ToString
public class SearchCondition {

    private String query;
    private String manufacturer;
    private Long categoryId;
    @Builder.Default
    private List<Map<String, String>> attributeFilters = new ArrayList<>();
}


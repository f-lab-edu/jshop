package jshop.core.domain.product.dto;

import com.querydsl.core.annotations.QueryProjection;
import java.util.Map;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@Builder
@ToString
public class SearchProductDetailQueryResult {

    private Long id;
    private String name;
    private String category;
    private String manufacturer;
    private String description;
    private Long price;
    private Map<String, String> attribute;

    @QueryProjection
    public SearchProductDetailQueryResult(Long id, String name, String category, String manufacturer,
        String description, Long price, Map<String, String> attribute) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.manufacturer = manufacturer;
        this.description = description;
        this.price = price;
        this.attribute = attribute;
    }
}

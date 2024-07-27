package jshop.domain.product.dto;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@Builder
@AllArgsConstructor
@ToString
public class ProductDetailResponse {

    private Long id;
    private String name;
    private String category;
    private String manufacturer;
    private String description;
    private Long price;
    private Map<String, String> attribute;

    public static ProductDetailResponse of(SearchProductDetailQueryResult searchProductDetailQueryResult) {
        return ProductDetailResponse
            .builder()
            .id(searchProductDetailQueryResult.getId())
            .category(searchProductDetailQueryResult.getCategory())
            .name(searchProductDetailQueryResult.getName())
            .manufacturer(searchProductDetailQueryResult.getManufacturer())
            .description(searchProductDetailQueryResult.getDescription())
            .price(searchProductDetailQueryResult.getPrice())
            .attribute(searchProductDetailQueryResult.getAttribute())
            .build();
    }
}

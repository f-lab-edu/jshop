package jshop.domain.product.dto;

import java.util.List;
import java.util.Map;
import jshop.domain.product.entity.Product;
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
public class ProductResponse {

    private Long id;
    private String name;
    private String manufacturer;
    private String description;
    private Map<String, List<String>> attributes;

    public static ProductResponse of(Product product) {
        return ProductResponse
            .builder()
            .id(product.getId())
            .name(product.getName())
            .manufacturer(product.getManufacturer())
            .description(product.getDescription())
            .attributes(product.getAttributes())
            .build();
    }
}

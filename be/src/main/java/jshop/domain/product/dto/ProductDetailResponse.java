package jshop.domain.product.dto;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@ToString
public class ProductDetailResponse {

    private Long id;
    private String name;
    private String manufacturer;
    private String description;
    private Long price;
    private Map<String, String> attribute;

    public ProductDetailResponse(Long id, String name, String manufacturer, String description,
        Long price, Map<String, String> attribute) {
        this.id = id;
        this.name = name;
        this.manufacturer = manufacturer;
        this.description = description;
        this.price = price;
        this.attribute = attribute;
    }
}

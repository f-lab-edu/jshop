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
@Builder
@AllArgsConstructor
@ToString
public class ProductDetailResponse {

    private Long id;
    private String name;
    private String manufacturer;
    private String description;
    private Long price;
    private Map<String, String> attribute;
}

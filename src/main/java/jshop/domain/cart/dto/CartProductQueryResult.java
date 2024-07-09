package jshop.domain.cart.dto;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
@Builder
@AllArgsConstructor
public class CartProductQueryResult {

    private Long id;
    private Long productDetailId;
    private String productName;
    private String manufacturer;
    private Long price;
    private Integer quantity;
    private Map<String, String> attribute;
}

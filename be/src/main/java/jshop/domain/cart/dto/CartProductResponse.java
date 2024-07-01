package jshop.domain.cart.dto;

import java.util.Map;
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
public class CartProductResponse {

    private Long id;
    private Long productDetailId;
    private String productName;
    private String manufacturer;
    private Long price;
    private Integer quantity;
    private Map<String, String> attribute;

    public static CartProductResponse of(CartProductQueryResult cartProductQueryResult) {
        return CartProductResponse
            .builder()
            .id(cartProductQueryResult.getId())
            .productDetailId(cartProductQueryResult.getProductDetailId())
            .productName(cartProductQueryResult.getProductName())
            .manufacturer(cartProductQueryResult.getManufacturer())
            .price(cartProductQueryResult.getPrice())
            .quantity(cartProductQueryResult.getQuantity())
            .attribute(cartProductQueryResult.getAttribute())
            .build();
    }
}

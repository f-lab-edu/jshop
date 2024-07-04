package jshop.domain.cart.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import jshop.domain.product.entity.ProductDetail;
import jshop.global.common.ErrorCode;
import jshop.global.entity.BaseEntity;
import jshop.global.exception.JshopException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "cart")
public class Cart extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "cart_id")
    private Long id;

    /**
     * 카트물품들은 카트가 삭제되면 같이 삭제
     */
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL)
    @Builder.Default
    private final List<CartProductDetail> cartProductDetails = new ArrayList<>();

    public static Cart create() {
        return Cart
            .builder().build();
    }

    public void addCart(ProductDetail productDetail, int quantity) {
        if (quantity <= 0) {
            log.error(ErrorCode.ILLEGAL_CART_QUANTITY_REQUEST_EXCEPTION.getLogMessage(), quantity);
            throw JshopException.of(ErrorCode.ILLEGAL_CART_QUANTITY_REQUEST_EXCEPTION);
        }

        for (CartProductDetail cartProductDetail : cartProductDetails) {
            if (productDetail.getId().equals(cartProductDetail.getProductDetail().getId())) {
                cartProductDetail.changeQuantity(quantity);
                return;
            }
        }

        CartProductDetail cartProductDetail = CartProductDetail
            .builder().cart(this).productDetail(productDetail).quantity(quantity).build();
        cartProductDetails.add(cartProductDetail);
    }
}

package jshop.core.domain.cart.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jshop.core.domain.product.entity.ProductDetail;
import jshop.common.exception.ErrorCode;
import jshop.core.common.entity.BaseEntity;
import jshop.common.exception.JshopException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "cart_product_detail")
@ToString
public class CartProductDetail extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "cart_product_detail")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_detail_id")
    private ProductDetail productDetail;

    private Integer quantity;

    public void changeQuantity(int quantity) {
        if (this.quantity + quantity <= 0) {
            log.error(ErrorCode.ILLEGAL_CART_QUANTITY_REQUEST_EXCEPTION.getLogMessage(), this.quantity + quantity);
            throw JshopException.of(ErrorCode.ILLEGAL_CART_QUANTITY_REQUEST_EXCEPTION);
        }
        this.quantity += quantity;
    }
}

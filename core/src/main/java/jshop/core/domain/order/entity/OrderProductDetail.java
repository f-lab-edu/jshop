package jshop.core.domain.order.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jshop.common.exception.ErrorCode;
import jshop.common.exception.JshopException;
import jshop.core.domain.order.dto.OrderItemRequest;
import jshop.core.domain.product.entity.ProductDetail;
import jshop.core.common.entity.BaseEntity;
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
@Table(name = "order_product")
public class OrderProductDetail extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "order_product_id")
    private Long id;

    /**
     * 주문하나당 여러개의 주문-상품 테이블이 있다.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    /**
     * 상품 하나당 여러개의 주문-상품 테이블이 있다.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_detail_id")
    private ProductDetail productDetail;

    private Integer orderQuantity;
    private Long orderPrice;

    public static OrderProductDetail of (OrderItemRequest orderItemRequest, ProductDetail productDetail) {
        if (orderItemRequest.getQuantity() == null || orderItemRequest.getPrice() == null) {
            log.error(ErrorCode.INVALID_ORDER_ITEM.getLogMessage(), orderItemRequest.getQuantity(), orderItemRequest.getPrice());
            throw JshopException.of(ErrorCode.INVALID_ORDER_ITEM);
        }

        int quantity = orderItemRequest.getQuantity();
        long price = orderItemRequest.getPrice();

        if (price != productDetail.getPrice()) {
            log.error(ErrorCode.PRODUCT_PRICE_MISMATCH.getLogMessage(), productDetail.getId(), price,
                productDetail.getPrice());
            throw JshopException.of(ErrorCode.PRODUCT_PRICE_MISMATCH);
        }

        productDetail.getInventory().purchase(quantity);

        return OrderProductDetail
            .builder().orderQuantity(quantity).orderPrice(price).productDetail(productDetail).build();
    }


    public void cancel() {
        productDetail.getInventory().refund(orderQuantity);
    }
}

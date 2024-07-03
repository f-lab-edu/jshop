package jshop.domain.order.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jshop.domain.order.dto.OrderItemRequest;
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

    public static OrderProductDetail of(Order order, OrderItemRequest orderItem, ProductDetail productDetail) {
        if (orderItem.getQuantity() == null || orderItem.getPrice() == null) {
            log.error(ErrorCode.INVALID_ORDER_ITEM.getLogMessage(), orderItem.getQuantity(), orderItem.getPrice());
            throw JshopException.of(ErrorCode.INVALID_ORDER_ITEM);
        }
        int quantity = orderItem.getQuantity();
        long price = orderItem.getPrice();

        productDetail.getInventory().purchase(quantity);

        return OrderProductDetail
            .builder().order(order).orderQuantity(quantity).orderPrice(price).productDetail(productDetail).build();
    }


    public void cancel() {
        productDetail.getInventory().refund(orderQuantity);
    }
}

package jshop.domain.order.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import jshop.domain.delivery.entity.Delivery;
import jshop.domain.delivery.entity.DeliveryState;
import jshop.domain.order.entity.Order;
import jshop.domain.order.entity.OrderProductDetail;
import jshop.domain.product.entity.ProductDetail;
import jshop.global.utils.TimeUtils;
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
public class OrderListResponse {

    private Long nextTimestamp;

    @Builder.Default
    private List<OrderResponse> orders = new ArrayList<>();

    public void addOrders(OrderResponse orderResponse) {
        orders.add(orderResponse);
    }

    @Builder
    @ToString
    @Getter
    public static class OrderResponse {

        private Long id;
        private Long orderTimestamp;
        private Long price;
        private Integer quantity;
        private DeliveryState deliveryState;
        private Long deliveredTimestamp;

        @Builder.Default
        private List<OrderProductResponse> products = new ArrayList<>();

        public static OrderResponse of(Order order) {
            Delivery delivery = order.getDelivery();
            Long deliveredTimestamp = delivery.getDeliveredDate() == null ? null
                : TimeUtils.localDateTimeToTimestamp(delivery.getDeliveredDate());
            return OrderResponse
                .builder()
                .id(order.getId())
                .orderTimestamp(TimeUtils.localDateTimeToTimestamp(order.getCreatedAt()))
                .price(order.getTotalPrice())
                .quantity(order.getTotalQuantity())
                .deliveryState(delivery.getDeliveryState())
                .deliveredTimestamp(deliveredTimestamp)
                .build();
        }

        public void addProduct(OrderProductDetail orderItem) {
            ProductDetail productDetail = orderItem.getProductDetail();
            products.add(OrderProductResponse
                .builder()
                .detailId(productDetail.getId())
                .price(productDetail.getPrice())
                .quantity(orderItem.getOrderQuantity())
                .name(productDetail.getProduct().getName())
                .attribute(productDetail.getAttribute())
                .build());
        }
    }

    @Builder
    @ToString
    @Getter
    static class OrderProductResponse {

        private Long detailId;
        private Long price;
        private Integer quantity;
        private String name;
        private Map<String, String> attribute;
    }
}

package jshop.domain.order.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import jshop.domain.delivery.entity.Delivery;
import jshop.domain.delivery.entity.DeliveryState;
import jshop.domain.order.dto.CreateOrderRequest;
import jshop.domain.user.entity.User;
import jshop.domain.wallet.entity.Wallet;
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
@Table(name = "orders")
public class Order extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    /**
     * 유저 하나당 여러개의 주문을 가질 수 있다.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    /**
     * 주문 하나당 하나의 배송을 갖는다.
     * fk는 배송측에서 보관한다.
     */
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;

    /**
     * 주문 하나당 여러개의 아이템을 가지고 있다.
     * 주문이 삭제되면 이 아이템 리스트도 삭제된다.
     * cascade
     */
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    @Builder.Default
    private final List<OrderProductDetail> productDetails = new ArrayList<>();

    @Column(name = "total_price")
    private Long totalPrice;

    @Column(name = "total_quantity")
    private Integer totalQuantity;

    public static Order createOrder(User user, Delivery delivery, CreateOrderRequest createOrderRequest) {
        user.getWallet().purchase(createOrderRequest.getTotalPrice());

        return Order
            .builder()
            .user(user)
            .delivery(delivery)
            .totalPrice(createOrderRequest.getTotalPrice())
            .totalQuantity(createOrderRequest.getTotalQuantity())
            .build();
    }

    public void cancel() {
        if (!delivery.getDeliveryState().equals(DeliveryState.PREPARING)) {
            log.error(ErrorCode.ALREADY_SHIPPING_ORDER.getLogMessage(), id);
            throw JshopException.of(ErrorCode.ALREADY_SHIPPING_ORDER);
        }
        delivery.cancel();

        user.getWallet().refund(totalPrice);

        for (OrderProductDetail orderProductDetail : productDetails) {
            orderProductDetail.cancel();
        }
    }
}

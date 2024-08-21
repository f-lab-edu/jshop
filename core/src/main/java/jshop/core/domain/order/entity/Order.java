package jshop.core.domain.order.entity;

import static jakarta.persistence.ConstraintMode.NO_CONSTRAINT;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
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
import jshop.common.exception.ErrorCode;
import jshop.common.exception.JshopException;
import jshop.core.common.entity.BaseEntity;
import jshop.core.domain.address.entity.Address;
import jshop.core.domain.coupon.entity.Coupon;
import jshop.core.domain.coupon.entity.UserCoupon;
import jshop.core.domain.delivery.entity.Delivery;
import jshop.core.domain.delivery.entity.DeliveryState;
import jshop.core.domain.order.dto.CreateOrderRequest;
import jshop.core.domain.user.entity.User;
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


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_coupon_id", foreignKey = @ForeignKey(NO_CONSTRAINT))
    private UserCoupon userCoupon;

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

    @Column(name = "payment_price")
    private Long paymentPrice;

    @Column(name = "total_quantity")
    private Integer totalQuantity;


    public static Order createOrder(User user, Address deliveryAddress,
        List<OrderProductDetail> orderProducts, UserCoupon userCoupon,
        CreateOrderRequest createOrderRequest) {

        Delivery delivery = Delivery.of(deliveryAddress);
        Long totalPrice = createOrderRequest.getTotalPrice();

        long totalProductPrice = 0L;
        int totalProductQuantity = 0;

        for (OrderProductDetail orderProductDetail : orderProducts) {
            totalProductPrice += orderProductDetail.getOrderPrice() * orderProductDetail.getOrderQuantity();
            totalProductQuantity += orderProductDetail.getOrderQuantity();
        }

        if (totalProductPrice != totalPrice) {
            log.error(ErrorCode.ORDER_PRICE_MISMATCH.getLogMessage(), totalPrice, totalProductPrice);
            throw JshopException.of(ErrorCode.ORDER_PRICE_MISMATCH);
        }

        if (createOrderRequest.getTotalQuantity() != totalProductQuantity) {
            log.error(ErrorCode.ORDER_QUANTITY_MISMATCH.getLogMessage(), createOrderRequest.getTotalQuantity(),
                totalProductQuantity);
            throw JshopException.of(ErrorCode.ORDER_QUANTITY_MISMATCH);
        }

        long paymentPrice = createOrderRequest.getTotalPrice();


        if (userCoupon != null) {
            Coupon coupon = userCoupon.getCoupon();
            paymentPrice = coupon.discount(totalPrice);
            userCoupon.use();
        }

        user.getWallet().withdraw(paymentPrice);

        Order order = Order
            .builder()
            .user(user)
            .delivery(delivery)
            .userCoupon(userCoupon)
            .productDetails(orderProducts)
            .totalPrice(createOrderRequest.getTotalPrice())
            .totalQuantity(createOrderRequest.getTotalQuantity())
            .paymentPrice(paymentPrice)
            .build();

        return order;
    }

    public void cancel() {
        if (!delivery.getDeliveryState().equals(DeliveryState.PREPARING)) {
            log.error(ErrorCode.ALREADY_SHIPPING_ORDER.getLogMessage(), id);
            throw JshopException.of(ErrorCode.ALREADY_SHIPPING_ORDER);
        }
        delivery.cancel();

        user.getWallet().refund(paymentPrice);

        if (userCoupon != null) {
            userCoupon.cancelUse();
        }

        for (OrderProductDetail orderProductDetail : productDetails) {
            orderProductDetail.cancel();
        }
    }
}

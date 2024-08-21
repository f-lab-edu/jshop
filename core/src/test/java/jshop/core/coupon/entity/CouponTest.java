package jshop.core.coupon.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import jshop.core.domain.address.entity.Address;
import jshop.core.domain.coupon.entity.Coupon;
import jshop.core.domain.coupon.entity.FixedPriceCoupon;
import jshop.core.domain.coupon.entity.FixedRateCoupon;
import jshop.core.domain.coupon.entity.UserCoupon;
import jshop.core.domain.inventory.entity.Inventory;
import jshop.core.domain.order.dto.CreateOrderRequest;
import jshop.core.domain.order.dto.OrderItemRequest;
import jshop.core.domain.order.entity.Order;
import jshop.core.domain.order.entity.OrderProductDetail;
import jshop.core.domain.product.entity.ProductDetail;
import jshop.core.domain.user.entity.User;
import jshop.common.exception.ErrorCode;
import jshop.common.exception.JshopException;
import jshop.core.domain.wallet.entity.Wallet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


@DisplayName("[단위 테스트] CouponTest")
class CouponTest {

    @Test
    @DisplayName("정액제 할인 검증")
    public void fix_discount_success() {
        // given
        Coupon coupon = FixedPriceCoupon
            .builder()
            .minOriginPrice(1000L)
            .totalQuantity(10L)
            .remainingQuantity(10L)
            .discountPrice(1000L).build();

        Inventory inventory = Inventory.create();
        inventory.addStock(10);

        ProductDetail productDetail = ProductDetail
            .builder().id(1L).price(1000L).inventory(inventory).build();
        Wallet wallet = Wallet.create();
        wallet.deposit(10000L);

        User user = User
            .builder().wallet(wallet).build();

        UserCoupon userCoupon = coupon.issueCoupon(user);

        Address address = Address
            .builder().build();

        OrderItemRequest orderItemRequest = OrderItemRequest
            .builder().quantity(3).price(1000L).productDetailId(1L).build();

        CreateOrderRequest createOrderRequest = CreateOrderRequest
            .builder()
            .addressId(1L)
            .totalPrice(3000L)
            .totalQuantity(3)
            .orderItems(List.of(orderItemRequest))
            .build();

        List<OrderProductDetail> orderProductDetails = new ArrayList<>();
        orderProductDetails.add(OrderProductDetail.of(orderItemRequest, productDetail));

        // when
        Order order = Order.createOrder(user, address, orderProductDetails, userCoupon, createOrderRequest);

        // then
        assertThat(order.getPaymentPrice()).isEqualTo(2000L);
    }

    @Test
    @DisplayName("정률제 할인 검증")
    public void rate_discount_success() {
        // given
        Coupon coupon = FixedRateCoupon
            .builder()
            .minOriginPrice(1000L)
            .totalQuantity(10L)
            .remainingQuantity(10L)
            .discountRate(0.5).build();
        Inventory inventory = Inventory.create();
        inventory.addStock(10);

        ProductDetail productDetail = ProductDetail
            .builder().id(1L).price(1000L).inventory(inventory).build();
        Wallet wallet = Wallet.create();
        wallet.deposit(10000L);

        User user = User
            .builder().wallet(wallet).build();

        UserCoupon userCoupon = coupon.issueCoupon(user);

        Address address = Address
            .builder().build();

        OrderItemRequest orderItemRequest = OrderItemRequest
            .builder().quantity(3).price(1000L).productDetailId(1L).build();

        CreateOrderRequest createOrderRequest = CreateOrderRequest
            .builder()
            .addressId(1L)
            .totalPrice(3000L)
            .totalQuantity(3)
            .orderItems(List.of(orderItemRequest))
            .build();

        List<OrderProductDetail> orderProductDetails = new ArrayList<>();
        orderProductDetails.add(OrderProductDetail.of(orderItemRequest, productDetail));

        // when
        Order order = Order.createOrder(user, address, orderProductDetails, userCoupon, createOrderRequest);

        // then
        assertThat(order.getPaymentPrice()).isEqualTo(1500L);
    }

    @Test
    @DisplayName("쿠폰은 발급 기간이 아니라면 발급받을 수 없다.")
    public void not_issue_period() {
        // given
        Coupon coupon = FixedRateCoupon
            .builder()
            .totalQuantity(10L)
            .remainingQuantity(10L)
            .issueStartDate(LocalDateTime.of(2022, 1, 12, 0, 0))
            .issueEndDate(LocalDateTime.of(2022, 12, 31, 23, 59))
            .build();

        User user = User
            .builder().build();

        // when

        // then
        JshopException jshopException = assertThrows(JshopException.class, () -> coupon.issueCoupon(user));
        assertThat(jshopException.getErrorCode()).isEqualTo(ErrorCode.COUPON_ISSUE_PERIOD_EXCEPTION);
    }

    @Test
    @DisplayName("쿠폰은 사용 기간이 아니라면 사용할 수 없다.")
    public void not_use_period() {
        // given
        Coupon coupon = FixedRateCoupon
            .builder()
            .totalQuantity(10L)
            .remainingQuantity(10L)
            .useStartDate(LocalDateTime.of(2022, 1, 12, 0, 0))
            .useEndDate(LocalDateTime.of(2022, 12, 31, 23, 59))
            .build();

        // then
        JshopException jshopException = assertThrows(JshopException.class, () -> coupon.discount(1000L));
        assertThat(jshopException.getErrorCode()).isEqualTo(ErrorCode.COUPON_USAGE_PERIOD_EXCEPTION);
    }

    @Test
    @DisplayName("쿠폰의 최소 사용 금액보다 금액이 낮다면 사용할 수 없다.")
    public void under_min_price() {
        // given
        Coupon coupon = FixedRateCoupon
            .builder()
            .totalQuantity(10L)
            .remainingQuantity(10L)
            .minOriginPrice(10000L)
            .build();

        // then
        JshopException jshopException = assertThrows(JshopException.class, () -> coupon.discount(5000L));
        assertThat(jshopException.getErrorCode()).isEqualTo(ErrorCode.COUPON_MIN_PRICE_EXCEPTION);
    }
}
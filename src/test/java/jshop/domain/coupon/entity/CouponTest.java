package jshop.domain.coupon.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import jshop.domain.order.entity.Order;
import jshop.domain.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


@DisplayName("[단위 테스트] CouponTest")
class CouponTest {

    @Test
    @DisplayName("정액제 할인 검증")
    public void fix_discount_success() {
        // given
        Order order = Order
            .builder().totalPrice(10000L).build();

        Coupon coupon = FixedPriceCoupon
            .builder()
            .minOriginPrice(1000L)
            .totalQuantity(10L)
            .remainingQuantity(10L)
            .discountPrice(1000L).build();

        User user = User
            .builder().build();

        UserCoupon userCoupon = coupon.issueCoupon(user);

        // when
        order.applyCoupon(userCoupon);

        // then
        assertThat(order.getPaymentPrice()).isEqualTo(9000L);
    }

    @Test
    @DisplayName("정액제 할인 검증")
    public void rate_discount_success() {
        // given
        Order order = Order
            .builder().totalPrice(10000L).build();

        Coupon coupon = FixedRateCoupon
            .builder()
            .minOriginPrice(1000L)
            .totalQuantity(10L)
            .remainingQuantity(10L)
            .discountRate(0.1).build();

        User user = User
            .builder().build();

        UserCoupon userCoupon = coupon.issueCoupon(user);

        // when
        order.applyCoupon(userCoupon);

        // then
        assertThat(order.getPaymentPrice()).isEqualTo(9000L);
    }
}
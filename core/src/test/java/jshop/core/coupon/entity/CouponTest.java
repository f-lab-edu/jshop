package jshop.core.coupon.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import jshop.core.domain.coupon.entity.Coupon;
import jshop.core.domain.coupon.entity.FixedPriceCoupon;
import jshop.core.domain.coupon.entity.FixedRateCoupon;
import jshop.core.domain.coupon.entity.UserCoupon;
import jshop.core.domain.order.entity.Order;
import jshop.core.domain.user.entity.User;
import jshop.common.exception.ErrorCode;
import jshop.common.exception.JshopException;
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
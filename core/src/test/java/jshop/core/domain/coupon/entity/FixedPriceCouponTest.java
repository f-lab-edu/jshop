package jshop.core.domain.coupon.entity;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import jshop.common.exception.ErrorCode;
import jshop.common.exception.JshopException;
import jshop.core.domain.coupon.dto.CreateCouponRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("[단위 테스트] FixedPriceCoupon")
class FixedPriceCouponTest {



    @ParameterizedTest
    @ValueSource(longs={5000L, 20000L})
    @DisplayName("쿠폰 할인 최소 가격보다 가격이 높다면 할인이 된다. 만약 작으면 예외가 터진다.")
    public void success_discount(long price) {
        // given
        Coupon coupon = FixedPriceCoupon
            .builder()
            .minOriginPrice(10000L)
            .discountPrice(1000L)
            .build();
        // then

        if (price < 10000L) {
            JshopException jshopException = assertThrows(JshopException.class, () -> coupon.discount(price));
            assertEquals(jshopException.getErrorCode(), ErrorCode.COUPON_MIN_PRICE_EXCEPTION);
        } else {
            assertEquals(coupon.discount(price), 19000L);
        }
    }

    @Test
    @DisplayName("쿠폰 생성 검증")
    public void createCoupon() {
        // given
        LocalDateTime startDate = LocalDateTime.of(2022, 1, 1, 0, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(9999, 1, 1, 0, 0, 0);
        LocalDateTime issueStartDate = LocalDateTime.of(2022, 1, 1, 0, 0, 1);
        LocalDateTime issueEndDate = LocalDateTime.of(9999, 1, 1, 0, 0, 1);

        CreateCouponRequest createCouponRequest = CreateCouponRequest
            .builder()
            .couponType(CouponType.FIXED_PRICE)
            .name("test")
            .quantity(10L)
            .value1(1000L)
            .value2(10000L)
            .issueStartDate(issueStartDate)
            .useStartDate(startDate)
            .issueEndDate(issueEndDate)
            .useEndDate(endDate)
            .build();
        // when

        FixedPriceCoupon coupon = FixedPriceCoupon.of(createCouponRequest);

        // then
        assertEquals(coupon.getDiscountPrice(), 1000L);
        assertEquals(coupon.getMinOriginPrice(), 10000L);
        assertEquals(coupon.getIssueStartDate(), issueStartDate);
        assertEquals(coupon.getIssueEndDate(), issueEndDate);
        assertEquals(coupon.getUseStartDate(), startDate);
        assertEquals(coupon.getUseEndDate(), endDate);
    }
}
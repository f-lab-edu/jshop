package jshop.domain.coupon.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import jshop.domain.coupon.dto.CreateCouponRequest;
import jshop.domain.coupon.entity.Coupon;
import jshop.domain.coupon.entity.CouponType;
import jshop.domain.coupon.entity.FixedPriceCoupon;
import jshop.domain.coupon.repository.CouponRepository;
import jshop.domain.user.entity.User;
import jshop.global.common.ErrorCode;
import jshop.global.exception.JshopException;
import jshop.global.utils.UUIDUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("[단위 테스트] CouponService")
class CouponServiceTest {

    @InjectMocks
    CouponService couponService;

    @Mock
    private CouponRepository couponRepository;

    private Coupon coupon;
    private User user;

    @BeforeEach
    public void init() {
        coupon = FixedPriceCoupon
            .builder()
            .name("쿠폰")
            .discountPrice(100L)
            .totalQuantity(10L)
            .remainingQuantity(10L)
            .build();

        user = User
            .builder()
            .build();
    }

    @DisplayName("쿠폰 생성 검증")
    @Nested
    public class CreateCoupon {

        @Captor
        private ArgumentCaptor<Coupon> couponArgumentCaptor;

        @Test
        @DisplayName("쿠폰 생성은 두가지 타입으로 가능하다.")
        public void createCoupon_success() {
            // given
            String uuid = UUIDUtils.generateB64UUID();
            CreateCouponRequest createCouponRequest = CreateCouponRequest
                .builder()
                .id(uuid)
                .name("test")
                .quantity(10L)
                .coupontType(CouponType.FIXED_RATE)
                .value1(10L)
                .build();

            // when
            couponService.createCoupon(createCouponRequest);

            // then
            verify(couponRepository, times(1)).save(couponArgumentCaptor.capture());
            Coupon savedCoupon = couponArgumentCaptor.getValue();
            assertThat(savedCoupon.getId()).isEqualTo(uuid);
            assertThat(savedCoupon.getName()).isEqualTo("test");
            assertThat(savedCoupon.getRemainingQuantity()).isEqualTo(10L);
        }

        @Test
        @DisplayName("쿠폰 생성시 타입이 없으면 예외를 발생시킴")
        public void createCoupon_undefined_type() {
            // given
            String uuid = UUIDUtils.generateB64UUID();
            CreateCouponRequest createCouponRequest = CreateCouponRequest
                .builder()
                .id(uuid)
                .name("test")
                .quantity(10L)
                .value1(10L)
                .build();

            // then
            JshopException jshopException = assertThrows(JshopException.class,
                () -> couponService.createCoupon(createCouponRequest));
            assertThat(jshopException.getErrorCode()).isEqualTo(ErrorCode.COUPON_TYPE_NOT_DEFINED);

        }
    }
}
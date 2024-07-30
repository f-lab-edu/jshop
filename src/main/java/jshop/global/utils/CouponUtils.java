package jshop.global.utils;

import java.util.Optional;
import java.util.UUID;
import jshop.domain.coupon.entity.Coupon;
import jshop.domain.coupon.entity.UserCoupon;
import jshop.global.common.ErrorCode;
import jshop.global.exception.JshopException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CouponUtils {

    public static Coupon getCouponOrThrow(Optional<Coupon> optionalCoupon, UUID couponId) {
        return optionalCoupon.orElseThrow(() -> {
            log.error(ErrorCode.COUPON_ID_NOT_FOUND.getLogMessage(), couponId);
            throw JshopException.of(ErrorCode.COUPON_ID_NOT_FOUND);
        });
    }

    public static UserCoupon getUserCouponOrThrow(Optional<UserCoupon> optionalUserCoupon, Long userCouponId) {
        return optionalUserCoupon.orElseThrow(() -> {
            log.error(ErrorCode.USER_COUPON_ID_NOT_FOUND.getLogMessage(), userCouponId);
            throw JshopException.of(ErrorCode.USER_COUPON_ID_NOT_FOUND);
        });
    }
}

package jshop.domain.coupon.service;

import java.util.Optional;
import java.util.UUID;
import jshop.domain.coupon.entity.Coupon;
import jshop.domain.coupon.entity.UserCoupon;
import jshop.domain.coupon.repository.CouponRepository;
import jshop.domain.coupon.repository.UserCouponRepository;
import jshop.global.utils.CouponUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;
    private final UserCouponRepository userCouponRepository;


    public Coupon getCoupon(UUID couponId) {
        Optional<Coupon> optionalCoupon = couponRepository.findById(couponId);
        return CouponUtils.getCouponOrThrow(optionalCoupon, couponId);
    }

    public UserCoupon getUserCoupon(Long userCouponId) {
        Optional<UserCoupon> optionalUserCoupon = userCouponRepository.findById(userCouponId);
        return CouponUtils.getUserCouponOrThrow(optionalUserCoupon, userCouponId);
    }
}

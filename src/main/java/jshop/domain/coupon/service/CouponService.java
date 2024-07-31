package jshop.domain.coupon.service;

import java.util.Optional;
import jshop.domain.coupon.dto.CreateCouponRequest;
import jshop.domain.coupon.entity.Coupon;
import jshop.domain.coupon.entity.FixedPriceCoupon;
import jshop.domain.coupon.entity.FixedRateCoupon;
import jshop.domain.coupon.entity.UserCoupon;
import jshop.domain.coupon.repository.CouponRepository;
import jshop.domain.coupon.repository.UserCouponRepository;
import jshop.domain.user.entity.User;
import jshop.domain.user.repository.UserRepository;
import jshop.global.annotation.RedisLock;
import jshop.global.common.ErrorCode;
import jshop.global.exception.JshopException;
import jshop.global.utils.CouponUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CouponService {

    private final CouponRepository couponRepository;
    private final UserCouponRepository userCouponRepository;
    private final UserRepository userRepository;
    private final RedissonClient redissonClient;

    @Transactional
    public String createCoupon(CreateCouponRequest createCouponRequest) {
        Coupon coupon = null;
        switch (createCouponRequest.getCoupontType()) {
            case FIXED_PRICE:
                coupon = FixedPriceCoupon.of(createCouponRequest);
                break;
            case FIXED_RATE:
                coupon = FixedRateCoupon.of(createCouponRequest);
                break;
            default:
                log.error(ErrorCode.COUPON_TYPE_NOT_DEFINED.getLogMessage(), createCouponRequest.getId(),
                    createCouponRequest.getCoupontType());
                throw JshopException.of(ErrorCode.COUPON_TYPE_NOT_DEFINED);
        }

        if (coupon == null) {
            log.error(ErrorCode.COUPON_CREATE_EXCEPTION.getLogMessage(), createCouponRequest.getId());
            throw JshopException.of(ErrorCode.COUPON_CREATE_EXCEPTION);
        }

        couponRepository.save(coupon);
        return coupon.getId();
    }

    @Transactional
    @RedisLock("coupon")
    public Long issueCoupon(String couponId, Long userId) {
        Coupon coupon = getCoupon(couponId);
        User user = userRepository.getReferenceById(userId);
        UserCoupon userCoupon = coupon.issueCoupon(user);
        userCouponRepository.save(userCoupon);
        return userCoupon.getId();
    }


    public Coupon getCoupon(String couponId) {
        Optional<Coupon> optionalCoupon = couponRepository.findById(couponId);
        return CouponUtils.getCouponOrThrow(optionalCoupon, couponId);
    }

    public UserCoupon getUserCoupon(Long userCouponId) {
        Optional<UserCoupon> optionalUserCoupon = userCouponRepository.findById(userCouponId);
        return CouponUtils.getUserCouponOrThrow(optionalUserCoupon, userCouponId);
    }
}

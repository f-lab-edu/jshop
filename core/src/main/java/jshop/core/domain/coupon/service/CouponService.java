package jshop.core.domain.coupon.service;

import java.util.Optional;
import jshop.core.domain.coupon.repository.CouponRepository;
import jshop.core.domain.coupon.repository.UserCouponRepository;
import jshop.core.domain.coupon.dto.CreateCouponRequest;
import jshop.core.domain.coupon.entity.Coupon;
import jshop.core.domain.coupon.entity.FixedPriceCoupon;
import jshop.core.domain.coupon.entity.FixedRateCoupon;
import jshop.core.domain.coupon.entity.UserCoupon;
import jshop.core.domain.user.entity.User;
import jshop.core.domain.user.repository.UserRepository;
import jshop.infra.redis.annotation.RedisLock;
import jshop.common.exception.ErrorCode;
import jshop.common.exception.JshopException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
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

    @Transactional
    public String createCoupon(CreateCouponRequest createCouponRequest) {
        Coupon coupon = null;
        if (createCouponRequest.getCouponType() == null) {
            MDC.put("error_code", String.valueOf(ErrorCode.COUPON_TYPE_NOT_DEFINED.getCode()));
            log.error(ErrorCode.COUPON_TYPE_NOT_DEFINED.getLogMessage(), createCouponRequest.getId(),
                createCouponRequest.getCouponType());
            MDC.clear();
            throw JshopException.of(ErrorCode.COUPON_TYPE_NOT_DEFINED);
        }

        switch (createCouponRequest.getCouponType()) {
            case FIXED_PRICE:
                coupon = FixedPriceCoupon.of(createCouponRequest);
                break;
            case FIXED_RATE:
                coupon = FixedRateCoupon.of(createCouponRequest);
                break;
            default:
                MDC.put("error_code", String.valueOf(ErrorCode.COUPON_TYPE_NOT_DEFINED.getCode()));
                log.error(ErrorCode.COUPON_TYPE_NOT_DEFINED.getLogMessage(), createCouponRequest.getId(),
                    createCouponRequest.getCouponType());
                MDC.clear();
                throw JshopException.of(ErrorCode.COUPON_TYPE_NOT_DEFINED);
        }

        if (coupon == null) {
            MDC.put("error_code", String.valueOf(ErrorCode.COUPON_CREATE_EXCEPTION.getCode()));
            log.error(ErrorCode.COUPON_CREATE_EXCEPTION.getLogMessage(), createCouponRequest.getId());
            MDC.clear();
            throw JshopException.of(ErrorCode.COUPON_CREATE_EXCEPTION);
        }

        couponRepository.save(coupon);
        return coupon.getId();
    }

    @Transactional
    @RedisLock(key = "coupon")
    public Long issueCoupon(String couponId, Long userId) {
        Coupon coupon = getCoupon(couponId);
        User user = userRepository.getReferenceById(userId);
        UserCoupon userCoupon = coupon.issueCoupon(user);
        userCouponRepository.save(userCoupon);
        return userCoupon.getId();
    }


    public Coupon getCoupon(String couponId) {
        Optional<Coupon> optionalCoupon = couponRepository.findById(couponId);
        return optionalCoupon.orElseThrow(() -> {
            MDC.put("error_code", String.valueOf(ErrorCode.COUPON_ID_NOT_FOUND.getCode()));
            log.error(ErrorCode.COUPON_ID_NOT_FOUND.getLogMessage(), couponId);
            MDC.clear();
            throw JshopException.of(ErrorCode.COUPON_ID_NOT_FOUND);
        });
    }

    public UserCoupon getUserCoupon(Long userCouponId) {
        Optional<UserCoupon> optionalUserCoupon = userCouponRepository.findById(userCouponId);
        return optionalUserCoupon.orElseThrow(() -> {
            MDC.put("error_code", String.valueOf(ErrorCode.USER_COUPON_ID_NOT_FOUND.getCode()));
            log.error(ErrorCode.USER_COUPON_ID_NOT_FOUND.getLogMessage(), userCouponId);
            MDC.clear();
            throw JshopException.of(ErrorCode.USER_COUPON_ID_NOT_FOUND);
        });
    }
}

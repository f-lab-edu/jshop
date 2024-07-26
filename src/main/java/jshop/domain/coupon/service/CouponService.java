package jshop.domain.coupon.service;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import jshop.domain.coupon.entity.Coupon;
import jshop.domain.coupon.entity.FixedDiscountCoupon;
import jshop.domain.coupon.entity.UserCoupon;
import jshop.domain.coupon.repository.CouponRepository;
import jshop.domain.coupon.repository.UserCouponRepository;
import jshop.domain.user.entity.User;
import jshop.domain.user.repository.UserRepository;
import jshop.global.annotation.RedisLock;
import jshop.global.utils.CouponUtils;
import lombok.RequiredArgsConstructor;
import lombok.Synchronized;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
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
    @RedisLock("coupon")
    public Long issueCoupon(String couponId, Long userId) {
        RLock lock = redissonClient.getLock("coupon");
        try {
            lock.lock();
            Coupon coupon = getCoupon(couponId);
            User user = userRepository.getReferenceById(userId);
            UserCoupon userCoupon = coupon.issueCoupon(user);
            userCouponRepository.save(userCoupon);
            return userCoupon.getId();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
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

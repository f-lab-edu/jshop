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
                coupon = FixedPriceCoupon
                    .builder()
                    .id(createCouponRequest.getId())
                    .name(createCouponRequest.getName())
                    .totalQuantity(createCouponRequest.getAmount())
                    .remainingQuantity(createCouponRequest.getAmount())
                    .issueStartDate(createCouponRequest.getIssueStartDate())
                    .issueEndDate(createCouponRequest.getIssueEndDate())
                    .useStartDate(createCouponRequest.getUseStartDate())
                    .useEndDate(createCouponRequest.getUseEndDate())
                    .discountPrice(createCouponRequest.getValue1())
                    .minOriginPrice(createCouponRequest.getValue2())
                    .build();
                break;
            case FIXED_RATE:
                coupon = FixedRateCoupon
                    .builder()
                    .id(createCouponRequest.getId())
                    .name(createCouponRequest.getName())
                    .totalQuantity(createCouponRequest.getAmount())
                    .remainingQuantity(createCouponRequest.getAmount())
                    .issueStartDate(createCouponRequest.getIssueStartDate())
                    .issueEndDate(createCouponRequest.getIssueEndDate())
                    .useStartDate(createCouponRequest.getUseStartDate())
                    .useEndDate(createCouponRequest.getUseEndDate())
                    .discountRate(createCouponRequest.getValue1() / 100)
                    .minOriginPrice(createCouponRequest.getValue2())
                    .build();
                break;
        }

        if (coupon != null) {
            couponRepository.save(coupon);
        }

        return coupon.getId();
    }

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

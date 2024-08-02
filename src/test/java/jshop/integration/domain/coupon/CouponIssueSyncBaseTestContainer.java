package jshop.integration.domain.coupon;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import jshop.domain.coupon.entity.Coupon;
import jshop.domain.coupon.entity.FixedPriceCoupon;
import jshop.domain.coupon.repository.CouponRepository;
import jshop.domain.coupon.repository.UserCouponRepository;
import jshop.domain.coupon.service.CouponService;
import jshop.domain.user.entity.User;
import jshop.domain.user.repository.UserRepository;
import jshop.utils.config.BaseTestContainers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@DisplayName("[통합 테스트] CouponController - sync")
public class CouponIssueSyncBaseTestContainer extends BaseTestContainers {

    @Autowired
    CouponService couponService;
    @Autowired
    private CouponRepository couponRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    RedissonClient redissonClient;

    private Coupon coupon;
    private User user;
    @Autowired
    private UserCouponRepository userCouponRepository;

    @BeforeEach
    public void beforeEach() {
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

        couponRepository.save(coupon);
        userRepository.save(user);
    }

    @AfterEach
    public void afterEach() {
        userCouponRepository.deleteAll();
        couponRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("쿠폰 발급을 여러명이 요청하더라도, 제한된 수량만큼만 발급되어야 한다.")
    public void coupon_issue_sync_test() throws InterruptedException {
        // given
        Coupon coupon = FixedPriceCoupon
            .builder()
            .name("쿠폰")
            .discountPrice(100L)
            .totalQuantity(10L)
            .remainingQuantity(10L)
            .build();

        User user = User
            .builder()
            .build();

        couponRepository.save(coupon);
        userRepository.save(user);

        AtomicInteger counter = new AtomicInteger(0);

        // when
        ExecutorService executors = Executors.newFixedThreadPool(100);

        for (int i = 0; i < 100; i++) {
            executors.submit(() -> {
                try {
                    couponService.issueCoupon(coupon.getId(), user.getId());
                    counter.getAndIncrement();
                } catch (Exception e) {
                    System.err.println(e);
                }
            });
        }

        executors.shutdown();
        executors.awaitTermination(1, TimeUnit.MINUTES);

        assertThat(counter.get()).isEqualTo(10);
    }
}

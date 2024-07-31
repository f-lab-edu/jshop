package jshop.integration.domain.coupon;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import jshop.domain.coupon.entity.Coupon;
import jshop.domain.coupon.entity.FixedPriceCoupon;
import jshop.domain.coupon.repository.CouponRepository;
import jshop.domain.coupon.service.CouponService;
import jshop.domain.user.entity.User;
import jshop.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CouponIssueSyncTest {

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

        couponRepository.save(coupon);
        userRepository.save(user);
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

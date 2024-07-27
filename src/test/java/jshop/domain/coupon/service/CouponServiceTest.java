package jshop.domain.coupon.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import jshop.domain.coupon.entity.Coupon;
import jshop.domain.coupon.entity.FixedPriceCoupon;
import jshop.domain.coupon.repository.CouponRepository;
import jshop.domain.user.entity.User;
import jshop.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CouponServiceTest {

    @Autowired
    CouponService couponService;
    @Autowired
    private CouponRepository couponRepository;
    @Autowired
    private UserRepository userRepository;
    @PersistenceContext
    EntityManager em;

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
    public void test() {
        // when
        couponService.issueCoupon(coupon.getId(), user.getId());

        // then
        Coupon foundCoupon = couponService.getCoupon(coupon.getId());
        System.out.println(foundCoupon);

    }

    @Test
    public void test2() throws InterruptedException {
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
        ExecutorService executors = Executors.newFixedThreadPool(10);

        for (int i = 0; i < 100; i++) {
            executors.submit(() -> {
                try {
                    couponService.issueCoupon(coupon.getId(), user.getId());
                    counter.getAndIncrement();
                } catch (Exception e) {
                    System.err.println(e);
                }
            });
//            executors.submit(() -> {
//                RLock lock = redissonClient.getLock("coupon");
//                try {
//                    lock.lock();
//                    couponService.issueCoupon(coupon.getId(), user.getId());
//                    counter.getAndIncrement();
//                } catch (Exception e) {
//                    System.err.println(e + " " + e.getMessage());
//                } finally {
//                    lock.unlock();
//                }
//            });
        }

        executors.shutdown();
        executors.awaitTermination(1, TimeUnit.MINUTES);

        assertThat(counter.get()).isEqualTo(10);
    }
}
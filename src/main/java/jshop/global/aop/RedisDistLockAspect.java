package jshop.global.aop;

import java.util.concurrent.TimeUnit;
import jshop.domain.coupon.entity.Coupon;
import jshop.domain.coupon.entity.UserCoupon;
import jshop.domain.user.entity.User;
import jshop.global.annotation.RedisLock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.hibernate.type.OrderedSetType;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Aspect
@Slf4j
@Component
@RequiredArgsConstructor
@Order(1)
public class RedisDistLockAspect {

    private final RedissonClient redissonClient;

    @Around("@annotation(redisLock)")
    public Object lock(ProceedingJoinPoint joinPoint, RedisLock redisLock) throws Throwable {

        RLock lock = redissonClient.getLock(redisLock.value());
        Throwable throwable = null;
        try {
            lock.lock();
            return joinPoint.proceed();
        } catch (Exception e) {
            throwable = e;
            return null;
        } finally {
            lock.unlock();
            if (throwable != null) {
                throw throwable;
            }
        }
    }
}

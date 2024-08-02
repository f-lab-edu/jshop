package jshop.global.aop;

import java.util.concurrent.TimeUnit;
import jshop.global.annotation.RedisLock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Slf4j
@Component
@RequiredArgsConstructor
@Order(1)
public class RedisDistLockAspect {

    private final RedissonClient redissonClient;

    @Around("@annotation(redisLock)")
    public Object lock(ProceedingJoinPoint joinPoint, RedisLock redisLock) throws Throwable {

        RLock lock = redissonClient.getLock(redisLock.key());
        Throwable throwable = null;
        try {
            lock.lock(redisLock.timeout(), TimeUnit.SECONDS);
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

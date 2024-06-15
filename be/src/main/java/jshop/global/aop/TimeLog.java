package jshop.global.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@Aspect
public class TimeLog {

    private long start;

    @Pointcut("within(jshop..*)")
    private void annotationServicePointcut() {
    }

    @Before("annotationServicePointcut()")
    public void start() {
        start = System.currentTimeMillis();
    }

    @After("annotationServicePointcut()")
    public void end(JoinPoint joinPoint) {
        log.info("{} {}ms", joinPoint.getSignature().getName(), System.currentTimeMillis() - start);
    }
}

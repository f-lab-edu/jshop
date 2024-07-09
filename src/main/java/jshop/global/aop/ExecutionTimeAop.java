package jshop.global.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
@Slf4j
//@Component
public class ExecutionTimeAop {

    private Long startTime;

    @Pointcut("execution(* jshop..*(..))")
    public void allMethods() {
    }

    @Before("allMethods()")
    public void start() {
        startTime = System.currentTimeMillis();
    }

    @After("allMethods()")
    public void end(JoinPoint joinPoint) {
        String className = joinPoint.getSignature().getDeclaringType().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        sb.append(className);
        sb.append(".");
        sb.append(methodName);
        sb.append("] ");
        sb.append("time : ");
        sb.append(System.currentTimeMillis() - startTime);
        sb.append("ms");
        log.info(sb.toString());
    }
}

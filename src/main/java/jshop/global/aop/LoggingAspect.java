package jshop.global.aop;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
//@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void controller() {
    }

    @Before("controller()")
    public void logBefore(JoinPoint joinPoint) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        logger.info("Request URL: " + request.getRequestURL().toString());
        logger.info("HTTP Method: " + request.getMethod());
        logger.info("Class Method: " + joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint
            .getSignature()
            .getName());
        logger.info("IP: " + request.getRemoteAddr());
        logger.info("Request Args: " + joinPoint.getArgs()[0]);
    }

//    @After("within(@org.springframework.web.bind.annotation.RestController *)")
//    public void logResponse(JoinPoint joinPoint) {
//        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
//        if (attrs != null) {
//            HttpServletRequest request = attrs.getRequest();
//
//            String method = request.getMethod();
//            String requestURI = request.getRequestURI();
//            String queryString = request.getQueryString();
//            String remoteAddr = request.getRemoteAddr();
//
//            // 요청 정보 로그로 출력
//            System.out.println("Method: " + method);
//            System.out.println("Request URI: " + requestURI);
//            System.out.println("Query String: " + queryString);
//            System.out.println("Remote Address: " + remoteAddr);
//        }
//    }

    @AfterReturning(pointcut = "controller()", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        logger.info("Response: " + result);
    }
}
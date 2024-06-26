package jshop.global.aop;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Slf4j
@RequiredArgsConstructor
//@Component
public class MvcLoggingAop {

    private String id;
    private final ObjectMapper objectMapper;
    private Long requestTime;
    private Long responseTime;

    @Pointcut("bean(*Controller) || @annotation(org.springframework.web.bind.annotation.ExceptionHandler)")
    public void controllerMethods() {
    }


    @Before("controllerMethods()")
    public void logRequest(JoinPoint joinPoint) {
        requestTime = System.currentTimeMillis();
        Map<String, Object> requestLog = new HashMap<>();
        id = UUID.randomUUID().toString();
        requestLog.put("id", id);
        requestLog.put("request_time", requestTime);
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        requestLog.put("url", request.getRequestURL());
        requestLog.put("method", request.getMethod());
        requestLog.put("client", request.getRemoteAddr());
        requestLog.put("params", request.getParameterMap());
        requestLog.put("User-Agent", request.getHeader("User-Agent"));
        if (joinPoint.getArgs().length != 0) {
            requestLog.put("body", joinPoint.getArgs()[0]);
        }

        try {
            log.info(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(requestLog));
        } catch (Exception ex) {
            log.info(requestLog.toString());
        }
    }

    @AfterReturning(pointcut = "controllerMethods()", returning = "response")
    public void afterMethods(JoinPoint joinPoint, Object response) {
        responseTime = System.currentTimeMillis();
        Map<String, Object> responseLog = new HashMap<>();
        responseLog.put("id", id);
        responseLog.put("response_time", responseTime);
        responseLog.put("execution_time", responseTime - requestTime);
        Optional<ServletRequestAttributes> optionalAttrivutes = Optional.of(
            (ServletRequestAttributes) RequestContextHolder.getRequestAttributes());

        optionalAttrivutes.ifPresent((attributes) -> {
            responseLog.put("status", attributes.getResponse().getStatus());
            Map<String, Object> headers = new HashMap<>();

            for (String header : attributes.getResponse().getHeaderNames().stream().toList()) {
                headers.putIfAbsent(header, attributes.getResponse().getHeaders(header));
                headers.putIfAbsent(header, attributes.getResponse().getHeader(header));
            }
            responseLog.put("headers", headers);

            Optional.ofNullable(response).ifPresent(r -> {
                responseLog.put("data", r);
            });
        });

        try {
            log.info(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(responseLog));
        } catch (Exception ex) {
            log.info(responseLog.toString());
        }
    }
}

package jshop.web.logging;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import jshop.common.exception.ErrorCode;
import jshop.web.dto.RequestLog;
import jshop.web.dto.ResponseLog;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

@Slf4j
@RequiredArgsConstructor
public class LoggingFilter implements Filter {

    private final ObjectMapper objectMapper;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {
        Long startTime = System.currentTimeMillis();

        String uuid = UUID.randomUUID().toString();

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        LoggingHttpServletRequestWrapper requestWrapper = new LoggingHttpServletRequestWrapper(httpRequest);
        LoggingHttpServletResponseWrapper responseWrapper = new LoggingHttpServletResponseWrapper(httpResponse);

        Enumeration<String> requestHeaderNames = httpRequest.getHeaderNames();
        Map<String, String> requestHeaders = new HashMap<>();


        String requestData = new String(requestWrapper.getInputStream().readAllBytes(), Charset.defaultCharset());

        if (httpRequest.getRequestURI().equals("/api/login")) {
            requestData = "Secure Info";
        }

        while (requestHeaderNames.hasMoreElements()) {
            String header = requestHeaderNames.nextElement();
            if (requestHeaders.get(header) == null) {
                requestHeaders.put(header,
                    Collections.list(httpRequest.getHeaders(header)).stream().collect(Collectors.joining(",")));
            }
        }

        MDC.put("request_id", uuid);
        MDC.put("uri", httpRequest.getRequestURI());
        MDC.put("method", httpRequest.getMethod());
        MDC.put("client", httpRequest.getRemoteHost());
        MDC.put("protocol", httpRequest.getProtocol());
        MDC.put("headers", requestHeaders.toString());
        MDC.put("queries", httpRequest.getQueryString());
        MDC.put("request_body", requestData);
        MDC.put("jwt", httpRequest.getHeader("Authorization"));
        log.info("Request Log");
        MDC.clear();
        MDC.put("request_id", uuid);

        chain.doFilter(requestWrapper, responseWrapper);

        String responseData = new String(responseWrapper.getResponseData(), Charset.defaultCharset());

        MDC.put("uri", httpRequest.getRequestURI());
        MDC.put("status", "" + httpResponse.getStatus());
        MDC.put("execution_time", String.valueOf(System.currentTimeMillis() - startTime));
        MDC.put("headers", httpResponse
            .getHeaderNames()
            .stream()
            .distinct()
            .collect(Collectors.toMap((header) -> header,
                (header) -> httpResponse.getHeaders(header).stream().collect(Collectors.joining(",")))).toString());
        MDC.put("response_body", responseData);
        log.info("Response Log");
        MDC.clear();
    }

    @Override
    public void destroy() {
        // Clean-up code if needed
    }
}
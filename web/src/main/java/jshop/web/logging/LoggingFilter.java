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
import jshop.web.dto.RequestLog;
import jshop.web.dto.ResponseLog;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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

        String requestData = new String(requestWrapper.getRequestData(), Charset.defaultCharset());

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

        RequestLog requestLog = RequestLog
            .builder()
            .id(uuid)
            .uri(httpRequest.getRequestURI())
            .method(httpRequest.getMethod())
            .client(httpRequest.getRemoteHost())
            .protocol(httpRequest.getProtocol())
            .headers(requestHeaders)
            .queries(httpRequest.getQueryString())
            .body(requestData)
            .build();

        log.info("Request Log\n{}", objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(requestLog));

        chain.doFilter(requestWrapper, responseWrapper);

        String responseData = new String(responseWrapper.getResponseData(), Charset.defaultCharset());

        ResponseLog responseLog = ResponseLog
            .builder()
            .id(uuid)
            .status(httpResponse.getStatus())
            .executeTime(System.currentTimeMillis() - startTime)
            .headers(httpResponse
                .getHeaderNames()
                .stream()
                .distinct()
                .collect(Collectors.toMap((header) -> header,
                    (header) -> httpResponse.getHeaders(header).stream().collect(Collectors.joining(",")))))
            .body(responseData)
            .build();

        log.info("Response Log\n{}", objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(responseLog));
    }

    @Override
    public void destroy() {
        // Clean-up code if needed
    }
}
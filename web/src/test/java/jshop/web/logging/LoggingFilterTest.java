package jshop.web.logging;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import jshop.web.dto.RequestLog;
import jshop.web.dto.ResponseLog;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.DelegatingServletInputStream;
import sun.misc.Unsafe;

@ExtendWith(MockitoExtension.class)
@DisplayName("[단위 테스트] LogginFilter")
class LoggingFilterTest {

    @InjectMocks
    private LoggingFilter loggingFilter;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain chain;

    @Mock
    private org.slf4j.Logger log;

    @Captor
    private ArgumentCaptor<String> loggingArgumentCaptor;

    @Captor
    private ArgumentCaptor<String> loggingArgumentCaptor2;

    @BeforeEach
    public void init() throws Exception {
        /**
         * @Slf4j private final 필드 목으로 변경
         */
        log = Mockito.mock(org.slf4j.Logger.class);

        Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
        theUnsafe.setAccessible(true);
        Unsafe unsafe = (Unsafe) theUnsafe.get(null);

        // MyClass의 log 필드를 가져옴
        Field logField = LoggingFilter.class.getDeclaredField("log");

        // static final 필드를 변경할 수 있도록 설정
        unsafe.putObject(unsafe.staticFieldBase(logField), unsafe.staticFieldOffset(logField), log);
    }

    @Test
    @DisplayName("LoggingFilter는 Request와 Response에서 데이터를 추출해 로그로 남김")
    @Disabled
    public void log_success_json() throws Exception {
        // given
        String requestBody = "{\"a\" : 123}";

        // 문자열을 바이트 배열로 변환
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(requestBody.getBytes());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        when(request.getInputStream()).thenReturn(new DelegatingServletInputStream(byteArrayInputStream));
        when(request.getRequestURI()).thenReturn("/api/test");
        when(request.getMethod()).thenReturn("GET");
        when(request.getRemoteHost()).thenReturn("localhost");
        when(request.getProtocol()).thenReturn("HTTP/1.1");
        when(request.getHeaderNames()).thenReturn(new Enumeration<String>() {
            private final String[] headers = {"Header1", "Header2"};
            private int index = 0;

            @Override
            public boolean hasMoreElements() {
                return index < headers.length;
            }

            @Override
            public String nextElement() {
                return headers[index++];
            }
        });
        when(request.getHeaders("Header1")).thenReturn(Collections.enumeration(List.of("req1")));
        when(request.getHeaders("Header2")).thenReturn(Collections.enumeration(List.of("req2")));
        when(request.getQueryString()).thenReturn("param1=value1&param2=value2");

        doAnswer(invocation -> {
            HttpServletRequest request = invocation.getArgument(0);
            HttpServletResponse response = invocation.getArgument(1);
            request.getInputStream().readAllBytes();
            return null;
        }).when(chain).doFilter(any(HttpServletRequest.class), any(HttpServletResponse.class));

        // Mock response
        when(response.getStatus()).thenReturn(200);
        when(response.getHeaderNames()).thenReturn(new HashSet<>(Arrays.asList("Header1", "Header2")));
        when(response.getHeaders("Header1")).thenReturn(List.of("res1"));
        when(response.getHeaders("Header2")).thenReturn(List.of("res2"));
        // when
        loggingFilter.doFilter(request, response, chain);

        // then
        verify(log, times(2)).info(loggingArgumentCaptor.capture(), loggingArgumentCaptor2.capture());
        RequestLog requestLog = objectMapper.readValue(loggingArgumentCaptor2.getAllValues().get(0), RequestLog.class);
        ResponseLog responseLog = objectMapper.readValue(loggingArgumentCaptor2.getAllValues().get(1),
            ResponseLog.class);

        assertAll("RequestLog 검증", () -> assertThat(requestLog.getProtocol()).isEqualTo("HTTP/1.1"),
            () -> assertThat(requestLog.getUri()).isEqualTo("/api/test"),
            () -> assertThat(requestLog.getMethod()).isEqualTo("GET"),
            () -> assertThat(requestLog.getQueries()).isEqualTo("param1=value1&param2=value2"),
            () -> assertThat(requestLog.getBody()).isEqualTo(requestBody));

        assertAll("ResponseLog 검증", () -> assertThat(responseLog.getStatus()).isEqualTo(200));

        System.out.println(loggingArgumentCaptor2.getAllValues());
    }
}
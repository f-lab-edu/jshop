package jshop.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.Filter;
import java.util.List;
import jshop.global.annotation.CurrentUserIdArgumentResolver;
import jshop.global.filter.LoggingFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import jshop.global.annotation.CurrentUserRoleArgumentResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final ObjectMapper objectMapper;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new CurrentUserIdArgumentResolver());
        resolvers.add(new CurrentUserRoleArgumentResolver());
    }

    @Bean
    public FilterRegistrationBean loggingFilter() {

        FilterRegistrationBean<Filter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new LoggingFilter(objectMapper));
        filterRegistrationBean.addUrlPatterns("/*");
        filterRegistrationBean.setOrder(-101);
        return filterRegistrationBean;
    }
}

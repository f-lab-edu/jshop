package jshop.utils.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * @EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
 * 가 없는 권한 없이 테스트하기 위한 설정
 */
@EnableWebSecurity
@TestConfiguration
public class TestSecurityConfigWithoutMethodSecurity {

    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(auth -> auth.disable());
        return http.build();
    }
}

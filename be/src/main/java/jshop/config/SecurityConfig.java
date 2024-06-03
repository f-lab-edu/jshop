package jshop.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import jshop.jwt.JWTUtil;
import jshop.jwt.JwtFilter;
import jshop.jwt.LoginFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final JWTUtil jwtUtil;

    public SecurityConfig(AuthenticationConfiguration authenticationConfiguration, JWTUtil jwtUtil) {
        this.authenticationConfiguration = authenticationConfiguration;
        this.jwtUtil = jwtUtil;
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf((auth) -> auth.disable());
        http.formLogin(auth -> auth.disable());
        http.httpBasic(auth -> auth.disable());
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/login", "/", "/join")
                .permitAll()
                .requestMatchers("/admin")
                .hasRole("ADMIN")
                .anyRequest()
                .authenticated());

        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.addFilterAt(new LoginFilter(new ObjectMapper(), authenticationManager(authenticationConfiguration), jwtUtil), UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(new JwtFilter(jwtUtil), LoginFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

}

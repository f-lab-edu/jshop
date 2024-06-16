package jshop.global.jwt.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import jshop.global.jwt.dto.CustomUserDetails;
import jshop.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {
        Optional<String> optionalAuthorization = Optional.ofNullable(
            request.getHeader("Authorization"));

        // 토큰이 없거나, Bearer로 시작하지 않을때
        if (!optionalAuthorization.isPresent() || !optionalAuthorization.map(
                auth -> auth.startsWith("Bearer "))
            .orElse(false)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = optionalAuthorization.map(authorization -> authorization.substring(7))
            .get();

        if (jwtUtil.isExpired(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        String email = jwtUtil.getEmail(token);

        User user = User.builder()
            .email(email)
            .build();
        CustomUserDetails customUserDetails = new CustomUserDetails(user);
        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null,
            customUserDetails.getAuthorities());
        SecurityContextHolder.getContext()
            .setAuthentication(authToken);
        filterChain.doFilter(request, response);
    }
}

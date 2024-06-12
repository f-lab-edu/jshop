package jshop.global.jwt.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import jshop.domain.user.entity.User;
import jshop.global.common.ErrorCode;
import jshop.global.dto.Response;
import jshop.global.jwt.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {
        Optional<String> optionalAuthorization = Optional.ofNullable(
            request.getHeader("Authorization"));

        // 토큰이 없을때
        if (!optionalAuthorization.isPresent()) {
            filterChain.doFilter(request, response);
            return;
        }

        // Bearer로 시작하지 않을때
        if (!optionalAuthorization.map(auth -> auth.startsWith("Bearer ")).orElse(false)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            Response responseDto = Response
                .builder()
                .message("Authorizaation 헤더가 잘못되었습니다")
                .error(ErrorCode.BAD_TOKEN)
                .build();
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(responseDto));
            return;
        }

        String token = optionalAuthorization.map(authorization -> authorization.substring(7)).get();

        if (jwtUtil.isExpired(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            Response responseDto = Response
                .builder()
                .message("토큰이 만료되었습니다.")
                .error(ErrorCode.TOKEN_EXPIRED)
                .build();
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(responseDto));
            return;
        }

        String email = jwtUtil.getEmail(token);
        Long id = jwtUtil.getId(token);

        User user = User.builder().id(id).email(email).build();
        CustomUserDetails customUserDetails = new CustomUserDetails(user);
        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null,
            customUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);
        filterChain.doFilter(request, response);
    }
}

package jshop.web.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import jshop.common.exception.ErrorCode;
import jshop.web.dto.Response;
import jshop.web.security.dto.CustomUserDetails;
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
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        Optional<String> optionalAuthorization = Optional.ofNullable(request.getHeader("Authorization"));

        // 토큰이 없을때
        if (!optionalAuthorization.isPresent()) {
            filterChain.doFilter(request, response);
            return;
        }

        // Bearer로 시작하지 않을때
        if (!optionalAuthorization.map(auth -> auth.startsWith("Bearer ")).orElse(false)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            Response responseDto = Response.of(ErrorCode.BAD_TOKEN);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            String responseDtoStr = objectMapper.writeValueAsString(responseDto);
            byte[] utf8JsonString = responseDtoStr.getBytes(StandardCharsets.UTF_8);
            response.getOutputStream().write(utf8JsonString);
            return;
        }

        String token = optionalAuthorization.map(authorization -> authorization.substring(7)).get();

        if (jwtUtil.isExpired(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            Response responseDto = Response.of(ErrorCode.TOKEN_EXPIRED);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            String responseDtoStr = objectMapper.writeValueAsString(responseDto);
            byte[] utf8JsonString = responseDtoStr.getBytes(StandardCharsets.UTF_8);
            response.getOutputStream().write(utf8JsonString);
            return;
        }

        CustomUserDetails customUserDetails = CustomUserDetails
            .builder().id(jwtUtil.getId(token)).username(jwtUtil.getEmail(token)).role(jwtUtil.getRole(token)).build();

        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null,
            customUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);
        filterChain.doFilter(request, response);
    }
}

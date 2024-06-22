package jshop.utils;


import jshop.domain.user.entity.User;
import jshop.global.jwt.dto.CustomUserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

public class SecurityContextUtil {

    public static RequestPostProcessor userSecurityContext() {
        return request -> {

            User u = User
                .builder()
                .id(1L)
                .username("user")
                .email("email@email.com")
                .password("password")
                .role("ROLE_USER")
                .build();
            CustomUserDetails customUserDetails = CustomUserDetails.ofUser(u);
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(customUserDetails,
                null, customUserDetails.getAuthorities());
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authToken);
            context.setAuthentication(authToken);
            SecurityContextHolder.setContext(context);

            return SecurityMockMvcRequestPostProcessors.securityContext(context).postProcessRequest(request);
        };
    }
}

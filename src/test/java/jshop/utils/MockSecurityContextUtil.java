package jshop.utils;


import jshop.domain.user.entity.User;
import jshop.global.jwt.dto.CustomUserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

public class MockSecurityContextUtil {

    private static Long userId = 1L;
    private static String username = "username";
    private static String password = "password";
    private static String role = "ROLE_USER";
    private static String email = "email@email.com";

    public static RequestPostProcessor mockUserSecurityContext() {
        return request -> {

            User u = User
                .builder().id(userId).username(username).email(email).password(password).role(role).build();
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

    public static Long getSecurityContextMockUserId() {
        return userId;
    }
}

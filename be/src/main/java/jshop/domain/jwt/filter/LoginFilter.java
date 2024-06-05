package jshop.domain.jwt.filter;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import jshop.domain.jwt.dto.CustomUserDetails;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class LoginFilter extends UsernamePasswordAuthenticationFilter {

  private final AuthenticationManager authenticationManager;
  private final JwtUtil jwtUtil;
  private final ObjectMapper objectMapper;

  public LoginFilter(ObjectMapper objectMapper, AuthenticationManager authenticationManager,
      JwtUtil jwtUtil) {
    super.setFilterProcessesUrl("/api/login");
    this.authenticationManager = authenticationManager;
    this.jwtUtil = jwtUtil;
    this.objectMapper = objectMapper;
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request,
      HttpServletResponse response) throws AuthenticationException {
    String email = null;
    String password = null;
    try {
      Map<String, String> parameter = objectMapper.readValue(request.getInputStream(), Map.class);
      email = parameter.get("username");
      password = parameter.get("password");
    } catch (IOException exception) {
      throw new BadCredentialsException(exception.getMessage() + " | Request Body(JSON) is Empty");
    }

    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email,
        password, null);
    return authenticationManager.authenticate(authToken);
  }

  @Override
  protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
      FilterChain chain, Authentication authentication) throws IOException, ServletException {
    CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
    Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
    Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
    GrantedAuthority auth = iterator.next();
    Long id = customUserDetails.getId();
    String email = customUserDetails.getUsername();
    String role = auth.getAuthority();

    String token = jwtUtil.createJwt(id, email, role, 60 * 60 * 1000L);

    response.setStatus(HttpServletResponse.SC_NO_CONTENT);
    response.addHeader("Authorization", "Bearer " + token);
  }

  @Override
  protected void unsuccessfulAuthentication(HttpServletRequest request,
      HttpServletResponse response, AuthenticationException failed)
      throws IOException, ServletException {
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
  }
}

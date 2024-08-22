package jshop.web.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.AdditionalMatchers.not;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import jshop.common.aop.ExecutionTimeAspect;
import jshop.web.controller.AccountController;
import jshop.core.domain.user.entity.User;
import jshop.core.domain.user.service.UserService;
import jshop.web.config.SecurityConfig;
import jshop.web.security.dto.CustomUserDetails;
import jshop.web.security.filter.JwtUtil;
import jshop.common.test.BaseTestContainers;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcPrint;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@SpringBootTest(classes = {SecurityConfig.class, JwtUtil.class, ObjectMapper.class, AccountController.class,
    BCryptPasswordEncoder.class, ExecutionTimeAspect.class})
@AutoConfigureMockMvc(print = MockMvcPrint.NONE)
@DisplayName("[통합 테스트] SpringSecurity")
public class LoginBaseTest extends BaseTestContainers {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private UserService userService;

    @BeforeEach
    public void init() {
        BCryptPasswordEncoder bpe = new BCryptPasswordEncoder();
        User u = User
            .builder().id(1L).username("user").email("user").password(bpe.encode("password")).role("ROLE_USER").build();

        System.out.println(userDetailsService);
        UserDetails userDetails = CustomUserDetails.ofUser(u);
        when(userDetailsService.loadUserByUsername("user")).thenReturn(userDetails);
        when(userDetailsService.loadUserByUsername(not(ArgumentMatchers.eq("user")))).thenThrow(
            UsernameNotFoundException.class);

    }

    @Test
    @DisplayName("회원가입한 유저는, 자신의 이메일과 비밀번호로 로그인을 할 수 있다.")
    public void login_success() throws Exception {
        // given
        JSONObject requestBody = new JSONObject();
        requestBody.put("email", "user");
        requestBody.put("password", "password");

        // when
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders
            .post("/api/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody.toString()));

        // then
        perform.andExpect(status().isOk());
    }

    @Test
    @DisplayName("가입되지 않은 계정으로 로그인 시도시 Unauth 를 떨군다.")
    public void login_noAuth() throws Exception {
        // given
        JSONObject requestBody = new JSONObject();
        requestBody.put("email", "unknown_user");
        requestBody.put("password", "password");
        // when
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders
            .post("/api/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody.toString()));
        // then
        perform.andExpect(status().isUnauthorized());
    }


    @Test
    @DisplayName("로그인에 성공하면, 헤더로 JWT를 내려준다.")
    public void login_jwt() throws Exception {
        // given
        BCryptPasswordEncoder bpe = new BCryptPasswordEncoder();
        User u = User
            .builder().id(1L).username("user").email("user").password(bpe.encode("password")).role("ROLE_USER").build();

        UserDetails userDetails = CustomUserDetails.ofUser(u);
        when(userDetailsService.loadUserByUsername("user")).thenReturn(userDetails);

        JSONObject requestBody = new JSONObject();
        requestBody.put("email", "user");
        requestBody.put("password", "password");

        // when
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders
            .post("/api/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody.toString()));

        // then
        perform.andExpect(result -> {
            String authorization = result.getResponse().getHeader("Authorization");
            assertThat(authorization).contains("Bearer");
            String token = authorization.split(" ")[1];
            assertThat(jwtUtil.validJwt(token)).isTrue();
        });
    }

    @Test
    @DisplayName("로그인 헤더에 잘못된 형식의 jwt를 보내면 예외가 발생한다.")
    public void invalid_jwt() throws Exception {
        // given

        // invalid jwt
        String token = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
        // when
        ResultActions perform = mockMvc.perform(
            MockMvcRequestBuilders.post("/api/test").header("Authorization", token));
        // then
        perform.andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("로그인 헤더에 잘못된 형식의 jwt를 보내면 예외가 발생한다. (이상한 문자열)")
    public void invalid_jwt_string() throws Exception {
        // given

        // invalid jwt
        String token = "asdf";
        // when
        ResultActions perform = mockMvc.perform(
            MockMvcRequestBuilders.post("/api/test").header("Authorization", token));
        // then
        perform.andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("로그인 헤더에 jwt를 보낼때 Bearer로 시작하더라도, 잘못된 토큰이면 예외가 발생한다.")
    public void invalid_token_start_bearer() throws Exception {
        // given

        // invalid jwt
        String token = "Bearer invalidtoken";
        // when
        ResultActions perform = mockMvc.perform(
            MockMvcRequestBuilders.post("/api/test").header("Authorization", token));
        // then
        perform.andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("로그인 헤더에 토큰을 보내지 않으면 예외가 발생한다.")
    public void noToken() throws Exception {
        // given
        // when
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.post("/api/test"));
        // then
        perform.andExpect(status().isForbidden());
    }


    @Test
    @DisplayName("정상 로그인으로 토큰을 얻은 유저가 토큰을 헤더에 포함하면, 인가를 받을 수 있다.")
    public void login_success_jwt() throws Exception {
        // given
        JSONObject requestBody = new JSONObject();
        requestBody.put("email", "user");
        requestBody.put("password", "password");

        ResultActions tokenPerform = mockMvc.perform(MockMvcRequestBuilders
            .post("/api/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody.toString()));

        String token = tokenPerform.andReturn().getResponse().getHeader("Authorization");

        System.out.println(token);
        // when
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.get("/api/test").header("Authorization", token));

        // then
        perform
            .andExpect(status().isOk())
            .andExpect(result -> assertThat(result.getResponse().getContentAsString()).isEqualTo("test"));
    }

    @Test
    @DisplayName("정상적이지 않은 토큰으로 인가를 시도하면 실패한다.")
    public void invalid_token() throws Exception {
        // given
        // invalid token
        String token = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
        // when
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.get("/api/test").header("Authorization", token));

        // then
        perform.andExpect(status().isUnauthorized());
    }
}
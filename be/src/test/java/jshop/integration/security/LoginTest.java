package jshop.integration.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.AdditionalMatchers.not;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import jshop.domain.user.controller.AccountController;
import jshop.domain.user.entity.User;
import jshop.domain.user.service.UserService;
import jshop.global.config.SecurityConfig;
import jshop.global.jwt.dto.CustomUserDetails;
import jshop.global.jwt.filter.JwtUtil;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@SpringBootTest(classes = {SecurityConfig.class, JwtUtil.class, ObjectMapper.class, AccountController.class})
@AutoConfigureMockMvc
public class LoginTest {

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
    public void 정상로그인테스트() throws Exception {
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
    public void 비정상로그인테스트() throws Exception {
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
    public void 로그인토큰테스트() throws Exception {
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
    public void 잘못된토큰1_jwt형식() throws Exception {
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
    public void 잘못된토큰2_이상한문자열() throws Exception {
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
    public void 잘못된토큰3_Bearer로시작() throws Exception {
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
    public void 잘못된토큰4_없음() throws Exception {
        // given
        // when
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.post("/api/test"));
        // then
        perform.andExpect(status().isForbidden());
    }


    @Test
    public void 인가받은유저_페이지접속() throws Exception {
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
    public void 인가받지못한유저_페이지접속실패() throws Exception {
        // given
        // invalid token
        String token = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
        // when
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.get("/api/test").header("Authorization", token));

        // then
        perform.andExpect(status().isUnauthorized());
    }
}
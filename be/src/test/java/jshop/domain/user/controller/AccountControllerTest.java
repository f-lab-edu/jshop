package jshop.domain.user.controller;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jshop.domain.user.dto.JoinUserRequest;
import jshop.domain.user.dto.UserType;
import jshop.domain.user.service.UserService;
import jshop.utils.DtoBuilder;
import jshop.global.controller.GlobalExceptionHandler;
import jshop.utils.TestSecurityConfig;
import org.hamcrest.Matchers;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.assertj.core.api.Assertions.assertThat;

@WebMvcTest(AccountController.class)
@Import({TestSecurityConfig.class, GlobalExceptionHandler.class})
class AccountControllerTest {

    @MockBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @MockBean
    private UserService userService;

    @Captor
    private ArgumentCaptor<JoinUserRequest> joinDtoCaptor;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void 정상회원가입() throws Exception {
        // given
        String username = "test";
        String email = "email@email.com";
        String password = "testtest";
        UserType userType = UserType.USER;

        JSONObject requestBody = new JSONObject();
        requestBody.put("username", username);
        requestBody.put("email", email);
        requestBody.put("password", password);
        requestBody.put("userType", "USER");

        JoinUserRequest joinUserRequest = DtoBuilder.getJoinDto(username, email, password, userType);
        // when
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders
            .post("/api/join")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody.toString()));

        // then
        verify(userService, times(1)).joinUser(joinDtoCaptor.capture());
        JoinUserRequest capturedJoinUserRequest = joinDtoCaptor.getValue();
        perform.andExpect(MockMvcResultMatchers.status().isOk());
        assertThat(capturedJoinUserRequest).isEqualTo(joinUserRequest);
    }

    @Test
    public void 잘못된회원가입_이메일형식오류() throws Exception {
        // given
        String username = "test";
        String email = "email";
        String password = "test123123123";
        UserType userType = UserType.USER;

        JSONObject requestBody = new JSONObject();
        requestBody.put("username", username);
        requestBody.put("email", email);
        requestBody.put("password", password);
        requestBody.put("userType", "USER");

        JoinUserRequest joinUserRequest = DtoBuilder.getJoinDto(username, email, password, userType);
        // when
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders
            .post("/api/join")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody.toString()));

        // then
        perform
            .andExpect((result) -> assertThat(result.getResolvedException()).isInstanceOf(MethodArgumentNotValidException.class))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("이메일 형식에 맞지않습니다."));
    }

    @Test
    public void 잘못된회원가입_빈이메일() throws Exception {
        // given
        String username = "test";
        String email = "";
        String password = "test123123123";
        UserType userType = UserType.USER;

        JSONObject requestBody = new JSONObject();
        requestBody.put("username", username);
        requestBody.put("email", email);
        requestBody.put("password", password);
        requestBody.put("userType", "USER");

        JoinUserRequest joinUserRequest = DtoBuilder.getJoinDto(username, email, password, userType);
        // when
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders
            .post("/api/join")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody.toString()));

        // then
        perform
            .andExpect((result) -> assertThat(result.getResolvedException()).isInstanceOf(MethodArgumentNotValidException.class))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("이메일은 공백일 수 없습니다."));
    }

    @Test
    public void 잘못된회원가입_긴유저이름() throws Exception {
        // given
        String username = "123456789912345667";
        String email = "email@email.com";
        String password = "test123123123";
        UserType userType = UserType.USER;

        JSONObject requestBody = new JSONObject();
        requestBody.put("username", username);
        requestBody.put("email", email);
        requestBody.put("password", password);
        requestBody.put("userType", "USER");

        JoinUserRequest joinUserRequest = DtoBuilder.getJoinDto(username, email, password, userType);
        // when
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders
            .post("/api/join")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody.toString()));

        // then
        perform
            .andExpect((result) -> assertThat(result.getResolvedException()).isInstanceOf(MethodArgumentNotValidException.class))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("사용자 이름은 2 ~ 10 자리 이내여야 합니다."));
    }

    @Test
    public void 잘못된회원가입_짧은유저이름() throws Exception {
        // given
        String username = "김";
        String email = "email@email.com";
        String password = "test123123123";
        UserType userType = UserType.USER;

        JSONObject requestBody = new JSONObject();
        requestBody.put("username", username);
        requestBody.put("email", email);
        requestBody.put("password", password);
        requestBody.put("userType", "USER");

        // when
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders
            .post("/api/join")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody.toString()));

        // then
        perform
            .andExpect((result) -> assertThat(result.getResolvedException()).isInstanceOf(MethodArgumentNotValidException.class))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("사용자 이름은 2 ~ 10 자리 이내여야 합니다."));
    }

    @Test
    public void 잘못된회원가입_짧은비밀번호() throws Exception {
        // given
        String username = "jhkim";
        String email = "email@email.com";
        String password = "test";
        UserType userType = UserType.USER;

        JSONObject requestBody = new JSONObject();
        requestBody.put("username", username);
        requestBody.put("email", email);
        requestBody.put("password", password);
        requestBody.put("userType", "USER");

        JoinUserRequest joinUserRequest = DtoBuilder.getJoinDto(username, email, password, userType);
        // when
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders
            .post("/api/join")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody.toString()));

        // then
        perform
            .andExpect((result) -> assertThat(result.getResolvedException()).isInstanceOf(MethodArgumentNotValidException.class))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("비밀번호는 8 ~16 자리 이내여야 합니다."));
    }

    @Test
    public void 잘못된회원가입_긴비밀번호() throws Exception {
        // given
        String username = "jhkim";
        String email = "email@email.com";
        String password = "testtesttesttesttesttesttesttesttesttest";
        UserType userType = UserType.USER;

        JSONObject requestBody = new JSONObject();
        requestBody.put("username", username);
        requestBody.put("email", email);
        requestBody.put("password", password);
        requestBody.put("userType", "USER");

        JoinUserRequest joinUserRequest = DtoBuilder.getJoinDto(username, email, password, userType);
        // when
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders
            .post("/api/join")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody.toString()));

        // then
        perform
            .andExpect((result) -> assertThat(result.getResolvedException()).isInstanceOf(MethodArgumentNotValidException.class))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("비밀번호는 8 ~16 자리 이내여야 합니다."));
    }

    @Test
    public void 잘못된회원가입_공백비밀번호() throws Exception {
        // given
        String username = "jhkim";
        String email = "email@email.com";
        String password = "";
        UserType userType = UserType.USER;

        JSONObject requestBody = new JSONObject();
        requestBody.put("username", username);
        requestBody.put("email", email);
        requestBody.put("password", password);
        requestBody.put("userType", "USER");

        JoinUserRequest joinUserRequest = DtoBuilder.getJoinDto(username, email, password, userType);
        // when
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders
            .post("/api/join")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody.toString()));

        // then
        perform
            .andExpect((result) -> assertThat(result.getResolvedException()).isInstanceOf(MethodArgumentNotValidException.class))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", Matchers.anyOf(Matchers.is("비밀번호는 공백일 수 없습니다."), Matchers.is("비밀번호는 8 ~16 자리 이내여야 합니다."))));

    }

    @Test
    public void 잘못된회원가입_빈요청() throws Exception {
        // given

        // when
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.post("/api/join"));

        // then
        perform
            .andExpect((result) -> assertThat(result.getResolvedException()).isInstanceOf(HttpMessageNotReadableException.class))
            .andExpect(status().isBadRequest());
    }
}
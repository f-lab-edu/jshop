package jshop.domain.user.controller;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jshop.domain.user.dto.JoinDto;
import jshop.domain.user.dto.UserType;
import jshop.domain.user.service.UserService;
import jshop.domain.utils.DtoBuilder;
import jshop.global.controller.GlobalExceptionHandler;
import org.hamcrest.Matchers;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    @Captor
    private ArgumentCaptor<JoinDto> joinDtoCaptor;

    private MockMvc mockMvc;

    @BeforeEach
    public void beforeEach() {
        mockMvc = MockMvcBuilders
            .standaloneSetup(userController)
            .setControllerAdvice(GlobalExceptionHandler.class)
            .build();
    }

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

        JoinDto joinDto = DtoBuilder.getJoinDto(username, email, password, userType);
        // when
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders
            .post("/api/join")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody.toString()));

        // then
        verify(userService, times(1)).joinUser(joinDtoCaptor.capture());
        JoinDto capturedJoinDto = joinDtoCaptor.getValue();
        perform.andExpect(MockMvcResultMatchers
            .status()
            .isOk());
        assertThat(capturedJoinDto).isEqualTo(joinDto);
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

        JoinDto joinDto = DtoBuilder.getJoinDto(username, email, password, userType);
        // when
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders
            .post("/api/join")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody.toString()));

        // then
        perform
            .andExpect((result) -> assertThat(result.getResolvedException()).isInstanceOf(
                MethodArgumentNotValidException.class))
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

        JoinDto joinDto = DtoBuilder.getJoinDto(username, email, password, userType);
        // when
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders
            .post("/api/join")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody.toString()));

        // then
        perform
            .andExpect((result) -> assertThat(result.getResolvedException()).isInstanceOf(
                MethodArgumentNotValidException.class))
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

        JoinDto joinDto = DtoBuilder.getJoinDto(username, email, password, userType);
        // when
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders
            .post("/api/join")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody.toString()));

        // then
        perform
            .andExpect((result) -> assertThat(result.getResolvedException()).isInstanceOf(
                MethodArgumentNotValidException.class))
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
            .andExpect((result) -> assertThat(result.getResolvedException()).isInstanceOf(
                MethodArgumentNotValidException.class))
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

        JoinDto joinDto = DtoBuilder.getJoinDto(username, email, password, userType);
        // when
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders
            .post("/api/join")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody.toString()));

        // then
        perform
            .andExpect((result) -> assertThat(result.getResolvedException()).isInstanceOf(
                MethodArgumentNotValidException.class))
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

        JoinDto joinDto = DtoBuilder.getJoinDto(username, email, password, userType);
        // when
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders
            .post("/api/join")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody.toString()));

        // then
        perform
            .andExpect((result) -> assertThat(result.getResolvedException()).isInstanceOf(
                MethodArgumentNotValidException.class))
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

        JoinDto joinDto = DtoBuilder.getJoinDto(username, email, password, userType);
        // when
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders
            .post("/api/join")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody.toString()));

        // then
        perform
            .andExpect((result) -> assertThat(result.getResolvedException()).isInstanceOf(
                MethodArgumentNotValidException.class))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", Matchers.anyOf(Matchers.is("비밀번호는 공백일 수 없습니다."),
                Matchers.is("비밀번호는 8 ~16 자리 이내여야 합니다."))));

    }

    @Test
    public void 잘못된회원가입_빈요청() throws Exception {
        // given

        // when
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.post("/api/join"));

        // then
        perform
            .andExpect((result) -> assertThat(result.getResolvedException()).isInstanceOf(
                HttpMessageNotReadableException.class))
            .andExpect(status().isBadRequest());
    }
}
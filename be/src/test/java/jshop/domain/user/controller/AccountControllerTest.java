package jshop.domain.user.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jshop.domain.user.dto.JoinUserRequest;
import jshop.domain.user.dto.UserType;
import jshop.domain.user.service.UserService;
import jshop.global.controller.GlobalExceptionHandler;
import jshop.utils.DtoBuilder;
import jshop.utils.config.TestSecurityConfig;
import org.hamcrest.Matchers;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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

@WebMvcTest(AccountController.class)
@Import({TestSecurityConfig.class, GlobalExceptionHandler.class})
@DisplayName("[단위 테스트] AccountController")
class AccountControllerTest {

    @MockBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @MockBean
    private UserService userService;

    @Captor
    private ArgumentCaptor<JoinUserRequest> joinDtoCaptor;

    @Autowired
    private MockMvc mockMvc;

    @Nested
    @DisplayName("회원 가입 검증")
    class Join {

        private String username = "test";
        private String email = "email@email.com";
        private String password = "testtest";
        private UserType userType = UserType.USER;
        private JSONObject requestBody = new JSONObject();

        @BeforeEach
        public void init() throws Exception {
            requestBody.put("username", username);
            requestBody.put("email", email);
            requestBody.put("password", password);
            requestBody.put("userType", "USER");
        }

        @Test
        @DisplayName("회원가입 요청으로 유저이름, 이메일, 비밀번호, 유저타입이 들어오면 회원가입")
        public void join_success() throws Exception {
            // given
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
        @DisplayName("이메일 형식이 잘못되면 예외를 던져줌")
        public void join_invalidEmail() throws Exception {
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
                .andExpect((result) -> assertThat(result.getResolvedException()).isInstanceOf(
                    MethodArgumentNotValidException.class))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("이메일 형식에 맞지않습니다."));
        }

        @Test
        @DisplayName("이메일이 없다면 예외를 던져줌")
        public void join_emptyEmail() throws Exception {
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
                .andExpect((result) -> assertThat(result.getResolvedException()).isInstanceOf(
                    MethodArgumentNotValidException.class))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("이메일은 공백일 수 없습니다."));
        }

        @Test
        @DisplayName("회원 이름이 길다면 예외를 던져줌")
        public void join_longName() throws Exception {
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
                .andExpect((result) -> assertThat(result.getResolvedException()).isInstanceOf(
                    MethodArgumentNotValidException.class))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("사용자 이름은 2 ~ 10 자리 이내여야 합니다."));
        }

        @Test
        @DisplayName("회원 이름이 짧다면 예외를 던져줌")
        public void join_shortName() throws Exception {
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
        @DisplayName("비밀번호가 짧다면 예외를 던져줌")
        public void join_shortPassword() throws Exception {
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
                .andExpect((result) -> assertThat(result.getResolvedException()).isInstanceOf(
                    MethodArgumentNotValidException.class))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("비밀번호는 8 ~16 자리 이내여야 합니다."));
        }

        @Test
        @DisplayName("비밀번호가 길다면 예외를 던져줌")
        public void join_longPassword() throws Exception {
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
                .andExpect((result) -> assertThat(result.getResolvedException()).isInstanceOf(
                    MethodArgumentNotValidException.class))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("비밀번호는 8 ~16 자리 이내여야 합니다."));
        }

        @Test
        @DisplayName("비밀번호가 없다면 예외를 던져줌")
        public void join_emptyPassword() throws Exception {
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
                .andExpect((result) -> assertThat(result.getResolvedException()).isInstanceOf(
                    MethodArgumentNotValidException.class))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message",
                    Matchers.anyOf(Matchers.is("비밀번호는 공백일 수 없습니다."), Matchers.is("비밀번호는 8 ~16 자리 이내여야 합니다."))));

        }

        @Test
        @DisplayName("회원가입 요청으로 body가 없다면 예외를 던져줌")
        public void join_emptyRequest() throws Exception {
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
}
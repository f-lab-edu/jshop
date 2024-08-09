package jshop.web.controller;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.stream.Stream;
import jshop.core.domain.user.dto.UpdateUserRequest;
import jshop.core.domain.user.dto.UpdateWalletBalanceRequest;
import jshop.core.domain.user.service.UserService;
import jshop.core.domain.wallet.entity.WalletChangeType;
import jshop.common.exception.ErrorCode;
import jshop.web.config.MockSecurityContextUtil;
import jshop.web.config.TestSecurityConfig;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest(controllers = {UserController.class, GlobalExceptionHandler.class})
@Import(TestSecurityConfig.class)
@DisplayName("[단위 테스트] UserController")
class UserControllerTest {

    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @Nested
    @DisplayName("현재 인증된 유저 정보 가져오기 검증")
    class GetUserInfo {

        @Test
        @DisplayName("토큰에 인증정보가 있다면 회원 정보를 가져올 수 있음")
        public void getUserInfo_success() throws Exception {
            // when
            ResultActions perform = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/users").with(MockSecurityContextUtil.mockUserSecurityContext()));

            // then
            verify(userService, times(1)).getUserInfo(MockSecurityContextUtil.getSecurityContextMockUserId());
            perform.andExpect(status().isOk());
        }

        @Test
        @DisplayName("토큰에 인증정보가 없다면 회원 정보를 가져올 수 없음")
        public void getUserInfo_noAuth() throws Exception {
            // when
            ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.get("/api/users"));
            // then
            perform.andExpect(status().isUnauthorized());

        }
    }


    @Nested
    @DisplayName("현재 인증된 유저의 정보를 갱신 검증")
    class UpdateUser {

        @Test
        @DisplayName("현재 인증된 유저의 이름을 갱신할 수 있다.")
        public void updateUser_success() throws Exception {
            // given
            JSONObject requestBody = new JSONObject();
            requestBody.put("username", "김재현");

            UpdateUserRequest updateUserRequest = UpdateUserRequest
                .builder().username("김재현").build();

            // when
            ResultActions perform = mockMvc.perform(MockMvcRequestBuilders
                .patch("/api/users")
                .with(MockSecurityContextUtil.mockUserSecurityContext())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody.toString()));

            // then
            perform.andExpect(status().isOk());
        }

        @Test
        @DisplayName("현재 인증되지 않은 유저의 이름은 갱신할 수 없다.")
        public void updateUser_noAuth() throws Exception {
            // given
            JSONObject requestBody = new JSONObject();
            requestBody.put("username", "김재현");

            UpdateUserRequest updateUserRequest = UpdateUserRequest
                .builder().username("김재현").build();

            // when
            ResultActions perform = mockMvc.perform(MockMvcRequestBuilders
                .patch("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody.toString()));

            // then
            perform.andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("현재 유저의 잔고 변경 검증")
    class UpdateWallet {

        @Captor
        ArgumentCaptor<Long> userIdCaptor;

        @Captor
        ArgumentCaptor<UpdateWalletBalanceRequest> updateWalletBalanceRequestArgumentCaptor;

        private static Stream<Arguments> provideValidArgs() {
            String depositRequestStr = """
                { "amount" : 100, "type" : "DEPOSIT"}
                """;

            String withdrawRequestStr = """
                { "amount" : 100, "type" : "WITHDRAW"}
                """;
            return Stream.of(Arguments.of(depositRequestStr, WalletChangeType.DEPOSIT),
                Arguments.of(withdrawRequestStr, WalletChangeType.WITHDRAW));
        }

        private static Stream<Arguments> provideInValidArgs() {
            String depositRequestStr = """
                { "amount" : -100, "type" : "DEPOSIT"}
                """;

            String withdrawRequestStr = """
                { "amount" : 0, "type" : "WITHDRAW"}
                """;
            return Stream.of(Arguments.of(depositRequestStr, WalletChangeType.DEPOSIT),
                Arguments.of(withdrawRequestStr, WalletChangeType.WITHDRAW));
        }

        @ParameterizedTest
        @DisplayName("잔고 변화량이 0 이상이고, 변경 타입이 DEPOSIT, WITHDRAW라면 잔고를 갱신할 수 있다.")
        @MethodSource("provideValidArgs")
        public void updateWalletBalance_success(String request, WalletChangeType type) throws Exception {
            // when
            mockMvc.perform(MockMvcRequestBuilders
                .patch("/api/users/balance")
                .with(MockSecurityContextUtil.mockUserSecurityContext())
                .contentType(MediaType.APPLICATION_JSON)
                .content(request));

            // then
            verify(userService, times(1)).updateWalletBalance(userIdCaptor.capture(),
                updateWalletBalanceRequestArgumentCaptor.capture());

            assertThat(updateWalletBalanceRequestArgumentCaptor.getValue().getAmount()).isEqualTo(100);
            assertThat(updateWalletBalanceRequestArgumentCaptor.getValue().getType()).isEqualTo(type);
        }

        @ParameterizedTest
        @DisplayName("잔고 변화량이 0 이하면 BAD_REQUEST 발생")
        @MethodSource("provideInValidArgs")
        public void updateWalletBalance_illegal_amount(String request, WalletChangeType type) throws Exception {
            // when
            ResultActions perform = mockMvc.perform(MockMvcRequestBuilders
                .patch("/api/users/balance")
                .with(MockSecurityContextUtil.mockUserSecurityContext())
                .contentType(MediaType.APPLICATION_JSON)
                .content(request));

            // then
            perform
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(ErrorCode.BAD_REQUEST.getCode()));
        }
    }
}
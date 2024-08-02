package jshop.integration.domain.user;


import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.stream.Stream;
import jshop.domain.user.entity.User;
import jshop.domain.user.service.UserService;
import jshop.global.common.ErrorCode;
import jshop.utils.config.BaseTestContainers;
import jshop.utils.dto.UserDtoUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcPrint;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@SpringBootTest
@AutoConfigureMockMvc(print = MockMvcPrint.NONE)
@DisplayName("[통합 테스트] UserController")
@Transactional
public class UserControllerIntegrationBaseTest extends BaseTestContainers {

    private Long userId;
    private String userToken;

    @Autowired
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @PersistenceContext
    private EntityManager em;


    @BeforeEach
    public void init() throws Exception {
        userId = userService.joinUser(UserDtoUtils.getJoinUserRequestDto());
        ResultActions login = mockMvc.perform(
            post("/api/login").contentType(MediaType.APPLICATION_JSON).content(UserDtoUtils.getLoginJsonStr()));
        userToken = login.andReturn().getResponse().getHeader("Authorization");
    }

    @Nested
    @DisplayName("사용자 잔고 변화 검증")
    class UpdateUserWallet {

        @BeforeEach
        public void init() {
            User user = userService.getUser(userId);
            user.getWallet().deposit(1000L);
        }

        private static Stream<Arguments> provideValidRequest() {
            String depositRequest = """
                { "amount" : 100, "type" : "DEPOSIT"}
                """;

            String withdrawRequest = """
                { "amount" : 100, "type" : "WITHDRAW"}
                """;
            return Stream.of(Arguments.of(depositRequest, 1100L), Arguments.of(withdrawRequest, 900L));
        }

        private static Stream<Arguments> provideInValidAmount() {
            String negatigeRequest = """
                { "amount" : -100, "type" : "DEPOSIT"}
                """;

            String zeroRequest = """
                { "amount" : 0, "type" : "WITHDRAW"}
                """;
            return Stream.of(Arguments.of(negatigeRequest, 1000L), Arguments.of(zeroRequest, 1000L));
        }

        private static Stream<Arguments> provideInValidType() {
            String request1 = """
                { "amount" : 100, "type" : "REFUND"}
                """;

            String request2 = """
                { "amount" : 100, "type" : "PURCHASE"}
                """;
            return Stream.of(Arguments.of(request1, 1000L), Arguments.of(request2, 1000L));
        }


        @ParameterizedTest
        @DisplayName("로그인한 사용자는 자신의 잔고를 변경할 수 있다")
        @MethodSource("provideValidRequest")
        public void updateBalance_success(String request, Long result) throws Exception {
            // when
            mockMvc.perform(patch("/api/users/balance")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request)
                .header("Authorization", userToken));

            em.flush();
            em.clear();
            // then

            User user = userService.getUser(userId);
            assertThat(user.getWallet().getBalance()).isEqualTo(result);
        }

        @Test
        @DisplayName("로그인하지 않은 사용자는 잔고를 변경할 수 없다.")
        public void updateBalance_noAuth() throws Exception {
            // given
            String request = """
                { "amount" : 100, "type" : "WITHDRAW"}
                """;
            // when
            ResultActions perform = mockMvc.perform(
                patch("/api/users/balance").contentType(MediaType.APPLICATION_JSON).content(request));
            // then
            perform.andExpect(status().isForbidden());
        }

        @ParameterizedTest
        @DisplayName("잔고 변화량이 0 이하라면, 잔고를 변경할 수 없다.")
        @MethodSource("provideInValidAmount")
        public void updateBalance_illegal_amount(String request, Long result) throws Exception {
            // given
            mockMvc.perform(patch("/api/users/balance")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request)
                .header("Authorization", userToken));

            em.flush();
            em.clear();

            // then
            User user = userService.getUser(userId);
            assertThat(user.getWallet().getBalance()).isEqualTo(result);
        }

        @ParameterizedTest
        @DisplayName("변경 타입이 DEPOSIT, WITHDRAW가 아니라면 변경할 수 없다.")
        @MethodSource("provideInValidType")
        public void updateBalance_illegal_type(String request, Long result) throws Exception {
            // given
            mockMvc.perform(patch("/api/users/balance")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request)
                .header("Authorization", userToken));

            em.flush();
            em.clear();

            // then
            User user = userService.getUser(userId);
            assertThat(user.getWallet().getBalance()).isEqualTo(result);
        }

        @Test
        @DisplayName("변경 이후 잔액이 0이하라면 변경할 수 없다")
        public void updateBalance_WALLET_BALANCE_EXCEPTION() throws Exception {
            // given
            String request = """
                { "amount" : 1100, "type" : "WITHDRAW" }
                """;

            // when
            ResultActions perform = mockMvc.perform(patch("/api/users/balance")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request)
                .header("Authorization", userToken));

            em.flush();
            em.clear();

            // then

            perform
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(ErrorCode.WALLET_BALANCE_EXCEPTION.getCode()));
            User user = userService.getUser(userId);
            assertThat(user.getWallet().getBalance()).isEqualTo(1000L);
        }
    }
}

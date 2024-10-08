package jshop.web.acceptance;


import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import jshop.core.domain.cart.repository.CartRepository;
import jshop.core.domain.user.dto.JoinUserRequest;
import jshop.core.domain.user.dto.UserType;
import jshop.core.domain.user.entity.User;
import jshop.core.domain.user.repository.UserRepository;
import jshop.core.domain.user.service.UserService;
import jshop.core.domain.wallet.entity.Wallet;
import jshop.core.domain.wallet.repository.WalletHistoryRepository;
import jshop.core.domain.wallet.repository.WalletRepository;
import jshop.common.exception.ErrorCode;
import jshop.common.test.BaseTestContainers;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcPrint;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.history.Revision;
import org.springframework.data.history.Revisions;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Slf4j
@EnableWebMvc
@SpringBootTest
@AutoConfigureMockMvc(print = MockMvcPrint.NONE)
@DisplayName("[통합 테스트] UserController - Sync")
public class UserControllerSyncBaseTest extends BaseTestContainers {

    private Long userId;
    private String userToken;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private MockMvc mockMvc;

    @PersistenceContext
    private EntityManager em;


    @BeforeEach
    public void init() throws Exception {
        String userJoinStr = """
            { "email" : "email@email.com", "password" : "password"}
            """;

        JoinUserRequest joinUserRequest = JoinUserRequest
            .builder()
            .email("email@email.com")
            .username("username")
            .password("password")
            .userType(UserType.SELLER)
            .build();

        userId = userService.joinUser(joinUserRequest);
        ResultActions login = mockMvc.perform(
            post("/api/login").contentType(MediaType.APPLICATION_JSON).content(userJoinStr));
        userToken = login.andReturn().getResponse().getHeader("Authorization");
    }

    @AfterEach
    public void destroy() {
        userRepository.deleteAll();
    }

    @Nested
    @DisplayName("사용자 잔고 변경 낙관적락 동시성 테스트")
    class UpdateUserWallet {

        @BeforeEach
        public void init() {
            User user = userService.getUser(userId);
            Wallet wallet = user.getWallet();
            wallet.deposit(1000L);
            walletRepository.save(wallet);
        }

        @Test
        @DisplayName("잔고 변경 요청이 동시에 들어와도 재시도를 통해 동시성 문제 해결")
        public void updateBalance_success() throws Exception {
            ExecutorService executors = Executors.newFixedThreadPool(10);

            for (int i = 0; i < 3; i++) {
                executors.submit(() -> {
                    String request = """
                        { "amount" : 100, "type" : "DEPOSIT"}
                        """;
                    try {
                        mockMvc.perform(patch("/api/users/balance")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(request)
                            .header("Authorization", userToken));
                    } catch (Exception e) {

                    }
                });
            }

            executors.shutdown();
            executors.awaitTermination(1, TimeUnit.MINUTES);

            User user = userService.getUser(userId);
            Wallet wallet = user.getWallet();
            assertThat(wallet.getBalance()).isEqualTo(1300L);
        }

        @Test
        @DisplayName("너무 많은 재시도를 시도 하면 예외를 발생시킴")
        public void updateBalance_many_retry() throws Exception {
            ExecutorService executors = Executors.newFixedThreadPool(8);

            List<ResultActions> performs = new ArrayList<>();

            for (int i = 0; i < 20; i++) {
                executors.submit(() -> {
                    String request = """
                        { "amount" : 100, "type" : "DEPOSIT"}
                        """;
                    try {
                        ResultActions perform = mockMvc.perform(patch("/api/users/balance")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(request)
                            .header("Authorization", userToken));

                        performs.add(perform);
                    } catch (Exception e) {

                    }
                });
            }

            executors.shutdown();
            executors.awaitTermination(1, TimeUnit.MINUTES);

            User user = userService.getUser(userId);
            Wallet wallet = user.getWallet();
            assertThat(wallet.getBalance()).isNotEqualTo(3000L);

            for (ResultActions perform : performs) {
                if (perform.andReturn().getResponse().getStatus() != HttpStatus.OK.value()) {
                    perform
                        .andExpect(status().isInternalServerError())
                        .andExpect(jsonPath("$.errorCode").value(ErrorCode.INTERNAL_SERVER_ERROR.getCode()));
                }
            }
        }
    }
}

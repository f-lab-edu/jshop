package jshop.core.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import jshop.core.domain.address.dto.AddressInfoResponse;
import jshop.core.domain.address.entity.Address;
import jshop.core.domain.address.repository.AddressRepository;
import jshop.core.domain.user.dto.JoinUserRequest;
import jshop.core.domain.user.dto.UpdateUserRequest;
import jshop.core.domain.user.dto.UpdateWalletBalanceRequest;
import jshop.core.domain.user.dto.UserInfoResponse;
import jshop.core.domain.user.dto.UserType;
import jshop.core.domain.user.entity.User;
import jshop.core.domain.user.repository.UserRepository;
import jshop.core.domain.user.service.UserService;
import jshop.core.domain.wallet.entity.Wallet;
import jshop.core.domain.wallet.entity.WalletChangeType;
import jshop.core.domain.wallet.repository.WalletRepository;
import jshop.common.exception.ErrorCode;
import jshop.common.exception.JshopException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@ExtendWith(MockitoExtension.class)
@DisplayName("[단위 테스트] UserService")
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Spy
    private BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    @Mock
    private UserRepository userRepository;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private WalletRepository walletRepository;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    @Nested
    @DisplayName("회원 가입 검증")
    class JoinUser {

        private final String username = "test";
        private final String email = "email@email.com";
        private final String password = "test";
        private final UserType userType = UserType.USER;
        private final String role = "ROLE_USER";

        @Test
        @DisplayName("중복된 이메일이 없다면 회원가입이 가능")
        public void joinUser_success() {
            // given
            JoinUserRequest joinUserRequest = getJoinUserRequestDto(username, email, password, userType);
            User user = getJoinUser(username, email, password, userType, role);

            // when
            userService.joinUser(joinUserRequest);

            // then
            verify(userRepository, times(1)).save(userCaptor.capture());
            User capturedUser = userCaptor.getValue();

            assertThat(capturedUser.getUsername()).isEqualTo(user.getUsername());
            assertThat(bCryptPasswordEncoder.matches(password, capturedUser.getPassword())).isTrue();
            assertThat(capturedUser.getEmail()).isEqualTo(user.getEmail());
            assertThat(capturedUser.getUserType()).isEqualTo(user.getUserType());
            assertThat(capturedUser.getWallet().getBalance()).isEqualTo(0);
            assertThat(capturedUser.getCart()).isNotNull();
            // 여기서 선언한 `user` 객체는 영속성 컨텍스트에서 가져온 엔티티가 아니기 때문에 둘은 다른 엔티티임
        }

        @Test
        @DisplayName("중복된 이메일이 있다면 회원 가입이 불가능")
        public void joinUser_dupEmail() {
            // given
            JoinUserRequest joinUserRequest = getJoinUserRequestDto(username, email, password, userType);

            // when
            userService.joinUser(joinUserRequest);
            when(userRepository.existsByEmail(email)).thenReturn(true);

            // then
            JshopException jshopException = assertThrows(JshopException.class,
                () -> userService.joinUser(joinUserRequest));
            assertThat(jshopException.getErrorCode()).isEqualTo(ErrorCode.ALREADY_REGISTERED_EMAIL);
        }
    }

    @Nested
    @DisplayName("회원 정보 가져오기 검증")
    class GetUser {

        private User user;
        private Address address;

        @BeforeEach
        public void init() {
            user = User
                .builder()
                .id(1L)
                .username("user")
                .password("password")
                .email("email@email.com")
                .role("ROLE_USER")
                .wallet(Wallet
                    .builder().balance(0L).build())
                .build();

            address = Address
                .builder()
                .receiverName("김재현")
                .receiverNumber("010-1234-5678")
                .province("경기도")
                .city("광주시")
                .district("송정동")
                .street("경안천로")
                .detailAddress1("123-1234")
                .detailAddress2(null)
                .message("문앞에 놔주세요")
                .user(user)
                .build();
        }

        @Test
        @DisplayName("회원 정보가 있다면 회원 정보를 가져올 수 있음")
        public void getUser_success() {
            // when
            when(userRepository.findUserWithWalletAndAddressById(1L)).thenReturn(Optional.of(user));
            when(addressRepository.findByUser(user)).thenReturn(List.of(address));
            UserInfoResponse userInfoResponse = userService.getUserInfo(1L);

            // then
            assertThat(userInfoResponse).isNotNull();
            assertThat(userInfoResponse.getAddresses()).isEqualTo(List.of(AddressInfoResponse.of(address)));
        }

        @Test
        @DisplayName("현재 세션에서 인증된 회원 정보가 없다면 회원 정보를 가져올 수 없다")
        public void getUser_noSuchUser() {
            JshopException jshopException = assertThrows(JshopException.class, () -> userService.getUserInfo(1L));
            assertThat(jshopException.getErrorCode()).isEqualTo(ErrorCode.USERID_NOT_FOUND);
        }
    }


    @Nested
    @DisplayName("회원 정보 수정 검증")
    class UpdateUser {

        private User user;

        @BeforeEach
        public void init() {
            user = User
                .builder()
                .id(1L)
                .username("user")
                .password("password")
                .email("email@email.com")
                .role("ROLE_USER")
                .wallet(Wallet
                    .builder().balance(0L).build())
                .build();
        }

        @Test
        @DisplayName("회원 정보가 있다면 수정할 수 있음")
        public void updateUser_success() {
            // given
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));

            // when
            userService.updateUser(1L, UpdateUserRequest
                .builder().username("new_user").build());

            // then
            verify(userRepository, times(1)).findById(1L);
            assertThat(user.getUsername()).isEqualTo("new_user");
        }


        @Test
        @DisplayName("일치하는 회원 정보가 없다면 수정할 수 없음")
        public void updateUser_noSuchUser() {
            // then
            JshopException jshopException = assertThrows(JshopException.class, () -> userService.updateUser(1L,
                UpdateUserRequest
                    .builder().build()));

            assertThat(jshopException.getErrorCode()).isEqualTo(ErrorCode.USERID_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("회원 잔고 변경 검증")
    class UpdateWallet {

        private static Stream<Arguments> provideValidArgs() {
            UpdateWalletBalanceRequest deposit = UpdateWalletBalanceRequest
                .builder().amount(100L).type(WalletChangeType.DEPOSIT).build();

            UpdateWalletBalanceRequest withdraw = UpdateWalletBalanceRequest
                .builder().amount(100L).type(WalletChangeType.WITHDRAW).build();

            return Stream.of(Arguments.of(deposit, 400L), Arguments.of(withdraw, 200L));
        }

        private static Stream<Arguments> provideInValidBalance() {
            UpdateWalletBalanceRequest deposit = UpdateWalletBalanceRequest
                .builder().amount(-100L).type(WalletChangeType.DEPOSIT).build();

            UpdateWalletBalanceRequest withdraw = UpdateWalletBalanceRequest
                .builder().amount(0L).type(WalletChangeType.WITHDRAW).build();

            return Stream.of(Arguments.of(deposit, 300L), Arguments.of(withdraw, 300L));
        }

        private static Stream<Arguments> provideInValidType() {
            UpdateWalletBalanceRequest deposit = UpdateWalletBalanceRequest
                .builder().amount(100L).type(WalletChangeType.PURCHASE).build();

            UpdateWalletBalanceRequest withdraw = UpdateWalletBalanceRequest
                .builder().amount(100L).type(WalletChangeType.REFUND).build();

            return Stream.of(Arguments.of(deposit, 300L), Arguments.of(withdraw, 300L));
        }


        @ParameterizedTest
        @DisplayName("잔고 변경 금액이 0보다 크고, 타입이 DEPOSIT, WITHDRAW 중 하나라면 잔고 변경 가능")
        @MethodSource("provideValidArgs")
        public void updateWalletBalance_success(UpdateWalletBalanceRequest request, Long result) {
            // given
            Wallet wallet = Wallet.create(300L);

            // when
            when(walletRepository.findWalletByUserId(1L)).thenReturn(Optional.of(wallet));
            userService.updateWalletBalance(1L, request);

            // then
            assertThat(wallet.getBalance()).isEqualTo(result);
        }

        @ParameterizedTest
        @DisplayName("잔고 변경 금액이 0 이하라면 ILLEGAL_BALANCE_REQUEST 발생")
        @MethodSource("provideInValidBalance")
        public void updateWalletBalance_illegal_balance(UpdateWalletBalanceRequest request, Long result) {
            // given
            Wallet wallet = Wallet.create(300L);

            // when
            when(walletRepository.findWalletByUserId(1L)).thenReturn(Optional.of(wallet));

            // then

            JshopException jshopException = assertThrows(JshopException.class,
                () -> userService.updateWalletBalance(1L, request));
            assertThat(jshopException.getErrorCode()).isEqualTo(ErrorCode.ILLEGAL_BALANCE_REQUEST);
            assertThat(wallet.getBalance()).isEqualTo(result);
        }

        @ParameterizedTest
        @DisplayName("잔고 변경 타입이 DEPOSIT, WITHDRAW가 아니라면 아무런 작업도 이루어지지 않음.")
        @MethodSource("provideInValidType")
        public void updateWalletBalance_illegal_type(UpdateWalletBalanceRequest request, Long result) {
            // given
            Wallet wallet = Wallet.create(300L);

            // when
            when(walletRepository.findWalletByUserId(1L)).thenReturn(Optional.of(wallet));
            userService.updateWalletBalance(1L, request);
            // then

            assertThat(wallet.getBalance()).isEqualTo(result);
        }
    }

    private JoinUserRequest getJoinUserRequestDto(String username, String email, String password, UserType userType) {
        JoinUserRequest joinUserRequest = JoinUserRequest
            .builder().password(password).email(email).username(username).userType(userType).build();
        return joinUserRequest;
    }

    private User getJoinUser(String username, String email, String password, UserType userType, String role) {
        User testUser = User
            .builder()
            .password(bCryptPasswordEncoder.encode(password))
            .email(email)
            .username(username)
            .userType(userType)
            .role(role)
            .build();
        return testUser;
    }
}
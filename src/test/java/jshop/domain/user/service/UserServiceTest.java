package jshop.domain.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import jshop.domain.address.dto.AddressInfoResponse;
import jshop.domain.address.entity.Address;
import jshop.domain.address.repository.AddressRepository;
import jshop.domain.user.dto.JoinUserRequest;
import jshop.domain.user.dto.UpdateUserRequest;
import jshop.domain.user.dto.UserInfoResponse;
import jshop.domain.user.dto.UserType;
import jshop.domain.user.entity.User;
import jshop.domain.user.repository.UserRepository;
import jshop.domain.wallet.entity.Wallet;
import jshop.global.common.ErrorCode;
import jshop.global.exception.JshopException;
import jshop.utils.EntityBuilder;
import jshop.utils.dto.UserDtoUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

    @Captor
    private ArgumentCaptor<User> userCaptor;

    @Nested
    @DisplayName("회원 가입 검증")
    class JoinUser {

        private String username = "test";
        private String email = "email@email.com";
        private String password = "test";
        private UserType userType = UserType.USER;
        private String role = "ROLE_USER";

        @Test
        @DisplayName("중복된 이메일이 없다면 회원가입이 가능")
        public void joinUser_success() {
            // given
            JoinUserRequest joinUserRequest = UserDtoUtils.getJoinUserRequestDto(username, email, password, userType);
            User user = EntityBuilder.getJoinUser(username, email, password, userType, role);

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
            JoinUserRequest joinUserRequest = UserDtoUtils.getJoinUserRequestDto(username, email, password, userType);

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
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
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
}
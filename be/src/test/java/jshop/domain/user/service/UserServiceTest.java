package jshop.domain.user.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import javax.swing.text.html.Option;
import jshop.domain.address.dto.AddressInfoResponse;
import jshop.domain.address.entity.Address;
import jshop.domain.address.repository.AddressRepository;
import jshop.domain.user.dto.JoinDto;
import jshop.domain.user.dto.UserInfoResponse;
import jshop.domain.user.dto.UserType;
import jshop.domain.user.entity.User;
import jshop.domain.user.repository.UserRepository;
import jshop.domain.wallet.entity.Wallet;
import jshop.global.exception.user.UserIdNotFoundException;
import jshop.utils.DtoBuilder;
import jshop.utils.EntityBuilder;
import jshop.global.exception.user.AlreadyRegisteredEmailException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
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

    @Test
    public void 회원가입() {
        // given
        String username = "test";
        String email = "email@email.com";
        String password = "test";
        UserType userType = UserType.USER;
        String role = "ROLE_USER";

        JoinDto joinDto = DtoBuilder.getJoinDto(username, email, password, userType);
        User user = EntityBuilder.getJoinUser(username, email, password, userType, role);

        // when
        userService.joinUser(joinDto);

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
    public void 중복회원가입() {
        // given
        String username = "test";
        String email = "email@email.com";
        String password = "test";
        UserType userType = UserType.USER;
        String role = "ROLE_USER";

        JoinDto joinDto = DtoBuilder.getJoinDto(username, email, password, userType);

        // when
        userService.joinUser(joinDto);
        when(userRepository.existsByEmail(email)).thenReturn(true);

        // then
        assertThrows(AlreadyRegisteredEmailException.class, () -> userService.joinUser(joinDto));
    }

    @Test
    public void 회원정보가져오기() {
        // given
        User user = createUser();
        Address address = createAddress(user);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(addressRepository.findByUser(user)).thenReturn(List.of(address));

        // when
        UserInfoResponse userInfoResponse = userService.getUser(1L);

        // then
        assertThat(userInfoResponse).isNotNull();
        assertThat(userInfoResponse.getAddresses()).isEqualTo(List.of(AddressInfoResponse.ofAddress(address)));
    }

    @Test
    public void 회원정보가져오기_없는ID() {
        assertThrows(UserIdNotFoundException.class, () -> userService.getUser(1L));
    }

    private User createUser() {
        return User
            .builder()
            .id(1L)
            .username("user")
            .password("password")
            .email("email@email.com")
            .role("ROLE_USER")
            .wallet(Wallet
                .builder().balance(0).build())
            .build();
    }

    private Address createAddress(User user) {
        return Address
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
}
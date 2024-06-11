package jshop.domain.user.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jshop.domain.user.dto.JoinDto;
import jshop.domain.user.dto.UserType;
import jshop.domain.user.entity.User;
import jshop.domain.user.repository.UserRepository;
import jshop.domain.utils.DtoBuilder;
import jshop.domain.utils.EntityBuilder;
import jshop.global.exception.AlreadyRegisteredEmailException;
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

    @Spy
    private BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

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


}
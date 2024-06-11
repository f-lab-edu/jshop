package jshop.domain.user.repository;

import static org.assertj.core.api.Assertions.assertThat;

import jshop.domain.user.dto.UserType;
import jshop.domain.user.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void 회원가입() {
        // given
        User user = User.builder().email("test").userType(UserType.USER).username("kim")
            .password("kim").build();

        // when
        userRepository.save(user);

        // then
        User findUser = userRepository.findByEmail("test");
        assertThat(findUser).isEqualTo(user);
    }
}
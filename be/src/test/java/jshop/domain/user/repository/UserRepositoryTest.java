package jshop.domain.user.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jshop.domain.user.dto.UserType;
import jshop.domain.user.entity.User;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class UserRepositoryTest {

    @PersistenceContext
    EntityManager em;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void 회원가입() {
        // given
        User user = User
            .builder()
            .email("test")
            .userType(UserType.USER)
            .username("kim")
            .password("kim")
            .build();

        // when
        userRepository.save(user);

        // then
        User findUser = userRepository.findByEmail("test");
        assertThat(findUser).isEqualTo(user);
    }

    @Test
    public void 중복이메일_제약조건() {
        // given
        User user1 = User
            .builder()
            .email("test")
            .userType(UserType.USER)
            .username("kim")
            .password("kim")
            .build();

        User user2 = User
            .builder()
            .email("test")
            .userType(UserType.USER)
            .username("kim1")
            .password("kim2")
            .build();

        // when
        userRepository.save(user1);
        em.flush();

        // then
        userRepository.save(user2);
        assertThrows(ConstraintViolationException.class, () -> em.flush());

    }
}
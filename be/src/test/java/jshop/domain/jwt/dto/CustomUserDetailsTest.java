package jshop.domain.jwt.dto;

import static org.assertj.core.api.Assertions.assertThat;

import jshop.domain.user.entity.User;
import jshop.global.jwt.dto.CustomUserDetails;
import org.junit.jupiter.api.Test;

class CustomUserDetailsTest {

    @Test
    public void username_이메일리턴() {
        // given
        User user = User
            .builder().username("kim").email("email").password("password").role("ROLE_USER").build();

        // when
        CustomUserDetails cud = CustomUserDetails.ofUser(user);

        // then
        assertThat(cud.getUsername()).isEqualTo(user.getEmail());
        assertThat(cud.getPassword()).isEqualTo(user.getPassword());
    }

}
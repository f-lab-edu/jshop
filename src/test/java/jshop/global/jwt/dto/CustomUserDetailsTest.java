package jshop.global.jwt.dto;

import static org.assertj.core.api.Assertions.assertThat;

import jshop.domain.user.entity.User;
import jshop.global.jwt.dto.CustomUserDetails;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("[단위 테스트] CustomUserDetails")
class CustomUserDetailsTest {

    @Test
    @DisplayName("getUsrename을 하면 email을 리턴해야함.")
    public void getUsername() {
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
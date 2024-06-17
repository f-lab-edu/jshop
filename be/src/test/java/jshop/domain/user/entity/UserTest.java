package jshop.domain.user.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import jshop.domain.user.dto.UpdateUserRequest;
import org.junit.jupiter.api.Test;

class UserTest {

    @Test
    public void updateUserInfo() {
        // given
        User user = User
            .builder().username("kim").build();
        UpdateUserRequest updateUserRequest = UpdateUserRequest
            .builder().username("new_username").build();
        // when
        user.updateUserInfo(updateUserRequest);

        // then
        assertThat(user.getUsername()).isEqualTo("new_username");
    }

}
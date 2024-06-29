package jshop.domain.user.entity;

import static org.assertj.core.api.Assertions.assertThat;

import jshop.domain.user.dto.UpdateUserRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("[단위 테스트] User")
class UserTest {

    @Nested
    class UpdateUserName {

        @Test
        @DisplayName("변경하려는 이름이 null이 아니라면 유저의 이름을 변경함")
        public void updateUserName_success() {
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

        @Test
        @DisplayName("변경하려는 이름이 null이라면 유저의 이름을 변경할 수 없음")
        public void updateUserName_null() {
            // given
            User user = User
                .builder().username("user").build();
            UpdateUserRequest updateUserRequest = UpdateUserRequest
                .builder().username(null).build();

            // when
            user.updateUserInfo(updateUserRequest);

            // then
            assertThat(user.getUsername()).isEqualTo("user");
        }
    }


}
package jshop.utils.dto;

import jshop.domain.user.dto.JoinUserRequest;
import jshop.domain.user.dto.UserType;

public class UserDtoUtils {

    public static JoinUserRequest getJoinUserRequestDto(String username, String email, String password,
        UserType userType) {
        JoinUserRequest joinUserRequest = JoinUserRequest
            .builder().password(password).email(email).username(username).userType(userType).build();
        return joinUserRequest;
    }

    public static JoinUserRequest getJoinUserRequestDto() {
        return JoinUserRequest
            .builder()
            .email("email@email.com")
            .username("username")
            .password("password")
            .userType(UserType.SELLER)
            .build();
    }

    public static String getLoginJsonStr() {
        return """
            { "email" : "email@email.com", "password" : "password"}
            """;
    }
}

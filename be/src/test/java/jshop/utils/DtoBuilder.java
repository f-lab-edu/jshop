package jshop.utils;

import jshop.domain.user.dto.JoinUserRequest;
import jshop.domain.user.dto.UserType;

public class DtoBuilder {

    static public JoinUserRequest getJoinDto(String username, String email, String password, UserType userType) {

        JoinUserRequest joinUserRequest = JoinUserRequest
            .builder().password(password).email(email).username(username).userType(userType).build();
        return joinUserRequest;
    }

}

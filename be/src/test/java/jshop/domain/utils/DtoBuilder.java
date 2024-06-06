package jshop.domain.utils;

import jshop.domain.user.dto.JoinDto;
import jshop.domain.user.dto.UserType;

public class DtoBuilder {

    static public JoinDto getJoinDto(String username, String email, String password,
        UserType userType) {

        JoinDto joinDto = JoinDto.builder().password(password).email(email).username(username)
            .userType(userType).build();
        return joinDto;
    }

}

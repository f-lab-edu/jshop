package jshop.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JoinDto {

    private String username;
    private String password;
    private String email;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private UserType userType;
}

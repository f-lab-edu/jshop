package jshop.domain.user.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jshop.domain.user.dto.UserType;
import lombok.Getter;

@Getter
public class JoinDto {

  private String username;
  private String password;
  private String email;

  @JsonFormat(shape = JsonFormat.Shape.STRING)
  private UserType userType;
}

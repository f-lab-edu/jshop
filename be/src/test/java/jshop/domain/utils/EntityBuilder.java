package jshop.domain.utils;

import jshop.domain.user.dto.UserType;
import jshop.domain.user.entity.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class EntityBuilder {

  private static BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

  public static User getJoinUser(String username, String email, String password, UserType userType,
      String role) {

    User testUser = User.builder()
        .password(bCryptPasswordEncoder.encode(password))
        .email(email)
        .username(username)
        .userType(userType)
        .role(role)
        .build();
    return testUser;
  }
}

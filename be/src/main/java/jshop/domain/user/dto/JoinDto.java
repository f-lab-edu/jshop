package jshop.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.Objects;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@Builder
@EqualsAndHashCode
public class JoinDto {

  @NotEmpty
  private String username;

  @NotEmpty
  private String password;

  @NotEmpty
  @Email
  private String email;

  @JsonFormat(shape = JsonFormat.Shape.STRING)
  @NotNull
  private UserType userType;
}

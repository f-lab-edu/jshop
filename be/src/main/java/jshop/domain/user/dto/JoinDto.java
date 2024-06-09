package jshop.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class JoinDto {

    @NotBlank(message = "사용자 이름은 공백일 수 없습니다.")
    @Size(min = 2, max = 10, message = "사용자 이름은 2 ~ 10 자리 이내여야 합니다.")
    private String username;

    @NotBlank(message = "비밀번호는 공백일 수 없습니다.")
    @Size(min = 8, max = 16, message = "비밀번호는 8 ~16 자리 이내여야 합니다.")
    private String password;

    @NotBlank(message = "이메일은 공백일 수 없습니다.")
    @Email(message = "이메일 형식에 맞지않습니다.")
    private String email;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @NotNull(message = "유저 타입은 공백일 수 없습니다.")
    private UserType userType;
}

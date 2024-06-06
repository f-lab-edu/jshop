package jshop.domain.user.service;

import jshop.domain.user.dto.JoinDto;
import jshop.domain.user.dto.UserType;
import jshop.domain.user.entity.User;
import jshop.domain.user.repository.UserRepository;
import jshop.global.exception.AlreadyRegisteredEmailException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserRepository userRepository;

    public void joinUser(JoinDto joinDto) {
        String username = joinDto.getUsername();
        String password = joinDto.getPassword();
        String email = joinDto.getEmail();
        UserType userType = joinDto.getUserType();

        Boolean isExist = userRepository.existsByEmail(email);

        if (isExist) {
            log.info(String.format("%s 는 이미 가입된 이메일입니다.", email));
            throw new AlreadyRegisteredEmailException("이미 가입된 이메일입니다.");
        } else {
            User user = User.builder().username(username)
                .password(bCryptPasswordEncoder.encode(password)).email(email).userType(userType)
                .role("ROLE_USER").build();

            userRepository.save(user);
        }
    }
}

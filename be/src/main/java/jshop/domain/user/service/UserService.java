package jshop.domain.user.service;

import jshop.domain.user.dto.UserType;
import jshop.domain.user.dto.JoinDto;
import jshop.domain.user.entity.User;
import jshop.domain.user.repository.UserRepository;
import jshop.global.exception.AlreadyRegisteredEmailException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class UserService {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private UserRepository userRepository;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public Long joinProcess(JoinDto joinDto){
        String username = joinDto.getUsername();
        String password = joinDto.getPassword();
        String email = joinDto.getEmail();
        UserType userType = joinDto.getUserType();

        Boolean isExist = userRepository.existsByEmail(email);

        if (isExist) {
            throw new AlreadyRegisteredEmailException("이미 가입된 이메일입니다.", String.format("%s 는 이미 가입된 이메일 입니다.", email));
        } else {
            User user = new User();
            user.setUsername(username);
            user.setPassword(bCryptPasswordEncoder.encode(password));
            user.setEmail(email);
            user.setUserType(userType);
            user.setRole("ROLE_USER");

            userRepository.save(user);
            return user.getId();
        }
    }
}

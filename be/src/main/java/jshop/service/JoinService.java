package jshop.service;

import jshop.dto.JoinDto;
import jshop.entity.User;
import jshop.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class JoinService {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private UserRepository userRepository;

    public JoinService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public void joinProcess(JoinDto joinDto){
        String username = joinDto.getUsername();
        String password = joinDto.getPassword();

        Boolean isExist = userRepository.existsByUsername(username);
        System.out.println(isExist);
        if (isExist) {
            return ;
        } else {
            User user = new User();
            user.setUsername(username);
            user.setPassword(bCryptPasswordEncoder.encode(password));
            user.setRole("ROLE_ADMIN");

            userRepository.save(user);
        }
    }
}

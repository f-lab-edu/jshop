package jshop.global.command;

import jshop.domain.user.dto.JoinUserRequest;
import jshop.domain.user.dto.UserType;
import jshop.domain.user.entity.User;
import jshop.domain.user.repository.UserRepository;
import jshop.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Profile("admin")
public class AdminInit implements CommandLineRunner {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public void run(String... args) throws Exception {
        User admin = User
            .builder()
            .username("admin")
            .password(bCryptPasswordEncoder.encode("admin"))
            .email("admin@admin.com")
            .role("ROLE_ADMIN")
            .build();

        userRepository.save(admin);
    }
}

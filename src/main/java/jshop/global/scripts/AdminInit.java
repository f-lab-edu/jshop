package jshop.global.scripts;

import jshop.core.domain.user.entity.User;
import jshop.core.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Profile("admin")
@RequiredArgsConstructor
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

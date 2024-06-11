package jshop.domain.jwt.service;

import java.util.Optional;
import jshop.domain.jwt.dto.CustomUserDetails;
import jshop.domain.user.entity.User;
import jshop.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> optionalUser = Optional.of(userRepository.findByEmail(email));

        User user = optionalUser.orElseThrow(
            () -> new UsernameNotFoundException("User not found: " + email));

        return new CustomUserDetails(user);
    }
}

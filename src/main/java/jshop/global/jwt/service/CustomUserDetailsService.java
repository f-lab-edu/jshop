package jshop.global.jwt.service;

import java.util.Optional;
import jshop.domain.user.entity.User;
import jshop.domain.user.repository.UserRepository;
import jshop.global.jwt.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = Optional
            .ofNullable(userRepository.findByEmail(email))
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        return CustomUserDetails
            .builder()
            .id(user.getId())
            .username(user.getEmail())
            .password(user.getPassword())
            .role(user.getRole())
            .build();
    }
}

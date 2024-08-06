package jshop.web.security.dto;

import java.util.ArrayList;
import java.util.Collection;
import jshop.core.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@RequiredArgsConstructor
@Builder
@AllArgsConstructor
public class CustomUserDetails implements UserDetails {

    private Long id;
    private final String username;
    private final String password;
    private final String role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();

        collection.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return role;
            }
        });

        return collection;
    }

    public static CustomUserDetails ofUser(User user) {
        return CustomUserDetails
            .builder()
            .id(user.getId())
            .username(user.getEmail())
            .password(user.getPassword())
            .role(user.getRole())
            .build();
    }

    public Long getId() {
        return id;
    }

    public String getRole() {
        return role;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}

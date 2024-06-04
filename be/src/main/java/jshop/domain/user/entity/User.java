package jshop.domain.user.entity;

import jakarta.persistence.*;

import jshop.domain.user.dto.UserType;
import jshop.global.entity.CreatedAt;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
public class User extends CreatedAt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    private String username;

    @Setter
    private String password;

    @Setter
    private String email;

    @Setter
    @Enumerated(EnumType.STRING)
    private UserType userType;

    @Setter
    private String role;
}

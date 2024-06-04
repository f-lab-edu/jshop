package jshop.domain.user.repository;


import jshop.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    Boolean existsByEmail(String email);
    Boolean existsByUsername(String username);
    User findByUsername(String username);
    User findByEmail(String email);
}
package jshop.core.domain.user.repository;


import java.util.List;
import java.util.Optional;
import jshop.core.domain.user.dto.UserInfoResponse;
import jshop.core.domain.user.dto.UserType;
import jshop.core.domain.user.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {

    Boolean existsByEmail(String email);

    User findByEmail(String email);

    /**
     * 여기서 User와 관련된 Wallet의 fetch join
     */
    @EntityGraph(attributePaths = "wallet")
    Optional<User> findById(Long id);

    @EntityGraph(attributePaths = {"wallet", "addresses"})
    Optional<User> findUserWithWalletAndAddressById(@Param("id") Long id);

    List<User> findUsersByUserType(UserType userType);
}
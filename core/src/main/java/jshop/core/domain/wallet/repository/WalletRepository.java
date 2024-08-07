package jshop.core.domain.wallet.repository;

import java.util.Optional;
import jshop.core.domain.wallet.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface WalletRepository extends JpaRepository<Wallet, Long> {

    @Query("select u.wallet from User u where u.id = :userId")
    Optional<Wallet> findWalletByUserId(@Param("userId") Long userId);
}

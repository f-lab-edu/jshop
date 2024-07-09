package jshop.domain.cart.repository;

import java.util.Optional;
import jshop.domain.cart.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CartRepository extends JpaRepository<Cart, Long> {

    @Query("select u.cart from User u where u.id = :userId")
    Optional<Cart> findCartByUserId(@Param("userId") Long userId);
}

package jshop.domain.cart.repository;

import java.util.Optional;
import jshop.domain.cart.entity.CartProductDetail;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartProductDetailRepository extends JpaRepository<CartProductDetail, Long> {

    @EntityGraph(attributePaths = {"cart", "productDetail"})
    Optional<CartProductDetail> findById(Long id);
}

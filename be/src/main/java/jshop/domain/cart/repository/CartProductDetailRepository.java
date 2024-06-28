package jshop.domain.cart.repository;

import java.util.Optional;
import jshop.domain.cart.entity.Cart;
import jshop.domain.cart.entity.CartProductDetail;
import jshop.domain.product.entity.ProductDetail;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartProductDetailRepository extends JpaRepository<CartProductDetail, Long> {

    @EntityGraph(attributePaths = {"cart", "productDetail"})
    Optional<CartProductDetail> findById(Long id);

    Optional<CartProductDetail> findByCartAndProductDetail(Cart cart, ProductDetail productDetail);
}

package jshop.domain.cart.repository;

import java.util.Optional;
import jshop.domain.cart.dto.CartProductQueryResult;
import jshop.domain.cart.entity.Cart;
import jshop.domain.cart.entity.CartProductDetail;
import jshop.domain.product.entity.ProductDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CartProductDetailRepository extends JpaRepository<CartProductDetail, Long> {

    @EntityGraph(attributePaths = {"cart", "productDetail"})
    Optional<CartProductDetail> findById(Long id);

    Optional<CartProductDetail> findByCartAndProductDetail(Cart cart, ProductDetail productDetail);

    @Query("select new jshop.domain.cart.dto.CartProductQueryResult(cpd.id, pd.id, p.name, p.manufacturer, pd.price,"
        + " cpd.quantity, pd.attribute) from CartProductDetail cpd join cpd"
        + ".productDetail pd join pd.product p where cpd.cart = :cart")
    Page<CartProductQueryResult> findCartProductInfoByQuery(@Param("cart") Cart cart, Pageable pageable);
}

package jshop.core.domain.product.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import jshop.core.domain.product.entity.Product;
import jshop.core.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Override
    @EntityGraph(attributePaths = "owner")
    Optional<Product> findById(Long productId);

    Optional<Product> findByAttributes(Map<String, List<String>> attributes);

    Page<Product> findByOwner(User owner, Pageable pageable);
}

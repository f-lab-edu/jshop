package jshop.domain.product.repository;

import java.util.Map;
import jshop.domain.product.entity.ProductDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductDetailRepository extends JpaRepository<ProductDetail, Long> {

    boolean existsByAttribute(Map<String, String> attribute);
}

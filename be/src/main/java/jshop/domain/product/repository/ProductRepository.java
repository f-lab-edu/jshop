package jshop.domain.product.repository;

import java.util.List;
import java.util.Map;
import jshop.domain.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Product findByAttributes(Map<String, List<String>> attributes);
}

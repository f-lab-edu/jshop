package jshop.domain.product.repository;

import java.util.Map;
import java.util.Optional;
import jshop.domain.product.dto.SearchProductDetailQueryResult;
import jshop.domain.product.entity.Product;
import jshop.domain.product.entity.ProductDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductDetailRepository extends JpaRepository<ProductDetail, Long> {


    boolean existsByAttributeAndProduct(Map<String, String> attribute, Product product);

    @Query("select new jshop.domain.product.dto.SearchProductDetailQueryResult(pd.id, p.name, p.manufacturer "
        + ",p.description, pd.price, pd.attribute) from ProductDetail pd "
        + "join pd.product p where pd.id < :lastProductId and p.name like %:name%")
    Page<SearchProductDetailQueryResult> searchProductDetailsByQuery(@Param("lastProductId") Long lastProductId,
        @Param("name") String name, Pageable pageable);

    @EntityGraph(attributePaths = {"inventory", "product"})
    Optional<ProductDetail> findById(Long id);


}

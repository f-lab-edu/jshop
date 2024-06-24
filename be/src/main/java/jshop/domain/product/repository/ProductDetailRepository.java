package jshop.domain.product.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import jshop.domain.product.dto.ProductDetailResponse;
import jshop.domain.product.entity.ProductDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductDetailRepository extends JpaRepository<ProductDetail, Long> {

    boolean existsByAttribute(Map<String, String> attribute);

    @Query("select new jshop.domain.product.dto.ProductDetailResponse(pd.id, p.name, p.manufacturer "
        + ",p.description, pd.price, pd.attribute) from ProductDetail pd "
        + "join pd.product p where pd.id < :lastProductId and p.name like %:name%")
    Page<ProductDetailResponse> searchProductDetailsByQuery(@Param("lastProductId") Long lastProductId,
        @Param("name") String name, Pageable pageable);

    @EntityGraph(attributePaths = {"inventory", "product"})
    Optional<ProductDetail> findById(Long id);


}

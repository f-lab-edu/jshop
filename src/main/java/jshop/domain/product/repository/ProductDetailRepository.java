package jshop.domain.product.repository;

import jakarta.persistence.LockModeType;
import java.util.Map;
import java.util.Optional;
import jshop.domain.inventory.entity.Inventory;
import jshop.domain.product.dto.SearchProductDetailQueryResult;
import jshop.domain.product.entity.Product;
import jshop.domain.product.entity.ProductDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductDetailRepository extends JpaRepository<ProductDetail, Long> {


    boolean existsByAttributeAndProduct(Map<String, String> attribute, Product product);


    Optional<ProductDetail> findById(@Param("id") Long id);

    boolean existsByIdAndIsDeletedFalse(Long productDetailId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select pd.inventory from ProductDetail pd where pd.id = :detailId")
    Optional<Inventory> findInventoryByProductDetailId(@Param("detailId") Long detailId);
}

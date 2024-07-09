package jshop.domain.product.repository;

import jshop.domain.product.dto.SearchProductDetailQueryResult;
import jshop.domain.product.entity.ProductDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SearchRepository extends JpaRepository<ProductDetail, Long>, QDSLSearchRepository {

    @Query("select new jshop.domain.product.dto.SearchProductDetailQueryResult(pd.id, p.name, p.manufacturer "
        + ",p.description, pd.price, pd.attribute) from ProductDetail pd "
        + "join pd.product p where p.name like %:name% and pd.isDeleted = false")
    Page<SearchProductDetailQueryResult> searchProductDetailsByQuery(@Param("name") String name, Pageable pageable);
}

package jshop.domain.product.repository;

import static jshop.domain.product.entity.QProductDetail.productDetail;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.util.List;
import jshop.domain.product.entity.ProductDetail;
import jshop.domain.product.entity.QProductDetail;

public class QDSLSearchRepositoryImpl implements QDSLSearchRepository {

    private final JPAQueryFactory queryFactory;

    public QDSLSearchRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }


    @Override
    public List<ProductDetail> search() {
        return queryFactory.selectFrom(productDetail).fetch();
    }
}

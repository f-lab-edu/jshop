package jshop.domain.product.repository;

import java.util.List;
import jshop.domain.product.entity.ProductDetail;

public interface QDSLSearchRepository {

    public List<ProductDetail> search();
}

package jshop.core.domain.product.repository;

import jshop.core.domain.product.dto.SearchCondition;
import jshop.core.domain.product.dto.SearchProductDetailQueryResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SearchRepository {

    Page<SearchProductDetailQueryResult> search(SearchCondition condition, Pageable pageable);
}

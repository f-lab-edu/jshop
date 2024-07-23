package jshop.domain.product.repository;

import jshop.domain.product.dto.SearchCondition;
import jshop.domain.product.dto.SearchProductDetailQueryResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

public interface SearchRepository {

    Page<SearchProductDetailQueryResult> search(SearchCondition condition, Pageable pageable);
}
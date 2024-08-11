package jshop.core.domain.product.repository;

import jshop.core.domain.product.dto.SearchCondition;
import org.springframework.data.domain.Pageable;

public interface SearchCounterQueryRepository {

    public Long getTotalCount(SearchCondition condition);
}

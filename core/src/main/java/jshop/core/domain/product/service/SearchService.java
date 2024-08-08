package jshop.core.domain.product.service;

import java.util.List;
import jshop.core.domain.product.dto.SearchCondition;
import jshop.core.domain.product.repository.SearchRepository;
import jshop.core.domain.product.dto.ProductDetailResponse;
import jshop.core.domain.product.dto.SearchProductDetailQueryResult;
import jshop.core.domain.product.dto.SearchProductDetailsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class SearchService {

    private final SearchRepository searchRepository;

    public SearchProductDetailsResponse search(SearchCondition condition, Pageable pageable) {
        log.info("TX : {}", TransactionSynchronizationManager.isSynchronizationActive());
        log.info("TX : {}", TransactionSynchronizationManager.isCurrentTransactionReadOnly());
        log.info("TX : {}", TransactionSynchronizationManager.isActualTransactionActive());
        Page<SearchProductDetailQueryResult> page = searchRepository.search(condition, pageable);

        List<ProductDetailResponse> contents = page.getContent().stream().map(ProductDetailResponse::of).toList();

        return SearchProductDetailsResponse
            .builder()
            .totalCount(page.getTotalElements())
            .totalPages(page.getTotalPages())
            .currentPage(pageable.getPageNumber())
            .products(contents)
            .build();
    }
}

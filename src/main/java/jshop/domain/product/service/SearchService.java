package jshop.domain.product.service;

import java.util.List;
import jshop.domain.product.dto.ProductDetailResponse;
import jshop.domain.product.dto.SearchCondition;
import jshop.domain.product.dto.SearchProductDetailQueryResult;
import jshop.domain.product.dto.SearchProductDetailsResponse;
import jshop.domain.product.repository.SearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SearchService {

    private final SearchRepository searchRepository;

    public SearchProductDetailsResponse search(SearchCondition condition, Pageable pageable) {

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

package jshop.domain.product.service;

import java.util.List;
import jshop.domain.product.dto.ProductDetailResponse;
import jshop.domain.product.dto.SearchOption;
import jshop.domain.product.dto.SearchProductDetailQueryResult;
import jshop.domain.product.dto.SearchProductDetailsResponse;
import jshop.domain.product.repository.SearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SearchService {

    private final SearchRepository searchRepository;

    public SearchProductDetailsResponse searchProductDetail(int pageNumber, int pageSize, String query) {

        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);
        Page<SearchProductDetailQueryResult> page = searchRepository.searchProductDetailsByQuery(query, pageRequest);

        List<ProductDetailResponse> contents = page.getContent().stream().map(ProductDetailResponse::of).toList();

        return SearchProductDetailsResponse
            .builder().products(contents).build();
    }

    public SearchProductDetailsResponse searchProductDetailWithOptions(int pageNumber, int pageSize, String query,
        SearchOption searchOption) {

        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);
        Page<SearchProductDetailQueryResult> page = searchRepository.searchProductDetailsByQuery(query, pageRequest);

        List<ProductDetailResponse> contents = page.getContent().stream().map(ProductDetailResponse::of).toList();

        return SearchProductDetailsResponse
            .builder().products(contents).build();
    }
}

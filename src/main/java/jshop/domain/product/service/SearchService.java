package jshop.domain.product.service;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import jshop.domain.product.dto.ProductDetailResponse;
import jshop.domain.product.dto.SearchProductDetailQueryResult;
import jshop.domain.product.dto.SearchProductDetailsResponse;
import jshop.domain.product.repository.ProductDetailRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SearchService {

    private final ProductDetailRepository productDetailRepository;

    public SearchProductDetailsResponse searchProductDetail(long lastProductId, String query, int size) {

        PageRequest pageRequest = PageRequest.of(0, size, Sort.by(Direction.DESC, "id"));
        Page<SearchProductDetailQueryResult> page = productDetailRepository.searchProductDetailsByQuery(lastProductId,
            query, pageRequest);

        List<ProductDetailResponse> contents = page.getContent().stream().map(ProductDetailResponse::of).toList();

        Long nextCursor = Optional
            .ofNullable(page.getContent())
            .filter(Predicate.not(List::isEmpty))
            .map(list -> list.get(list.size() - 1))
            .map(SearchProductDetailQueryResult::getId)
            .orElse(null);

        return SearchProductDetailsResponse
            .builder().nextCursor(nextCursor).products(contents).build();
    }
}

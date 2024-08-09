package jshop.web.controller;

import jshop.core.domain.product.dto.SearchCondition;
import jshop.core.domain.product.dto.SearchProductDetailsResponse;
import jshop.core.domain.product.service.SearchService;
import jshop.web.dto.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @GetMapping
    public Response<SearchProductDetailsResponse> searchProductDetail(SearchCondition condition,
        @PageableDefault(size = 30) Pageable pageable) {
        SearchProductDetailsResponse searchResult = searchService.search(condition, pageable);

        return Response
            .<SearchProductDetailsResponse>builder()
            .data(searchResult)
            .build();
    }
}

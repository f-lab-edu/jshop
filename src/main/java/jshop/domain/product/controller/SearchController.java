package jshop.domain.product.controller;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import jshop.domain.product.dto.SearchCondition;
import jshop.domain.product.dto.SearchProductDetailsResponse;
import jshop.domain.product.service.SearchService;
import jshop.global.common.ErrorCode;
import jshop.global.dto.Response;
import jshop.global.exception.JshopException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

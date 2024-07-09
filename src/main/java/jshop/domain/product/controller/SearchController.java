package jshop.domain.product.controller;

import jakarta.validation.Valid;
import java.util.Optional;
import jshop.domain.product.dto.SearchOption;
import jshop.domain.product.dto.SearchProductDetailsResponse;
import jshop.domain.product.service.SearchService;
import jshop.global.common.ErrorCode;
import jshop.global.dto.Response;
import jshop.global.exception.JshopException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public Response<SearchProductDetailsResponse> searchProductDetail(
        @RequestParam("page") Optional<Integer> optionalPage, @RequestParam("size") Optional<Integer> optionalSize,
        @RequestParam("query") Optional<String> optionalQuery,
        @RequestBody(required = false) @Valid SearchOption searchOption) {

        int pageNumber = optionalPage.orElse(0);
        int pageSize = optionalSize.orElse(30);
        String query = optionalQuery.orElseThrow(() -> {
            log.error(ErrorCode.NO_SEARCH_QUERY.getLogMessage());
            throw JshopException.of(ErrorCode.NO_SEARCH_QUERY);
        });

        if (searchOption.getOrder() == null && searchOption.getFilter() == null) {
            return Response
                .<SearchProductDetailsResponse>builder()
                .data(searchService.searchProductDetail(pageNumber, pageSize, query))
                .build();
        } else {
            return Response
                .<SearchProductDetailsResponse>builder()
                .data(searchService.searchProductDetailWithOptions(pageNumber, pageSize, query, searchOption))
                .build();
        }
    }
}

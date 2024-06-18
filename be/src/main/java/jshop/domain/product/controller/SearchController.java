package jshop.domain.product.controller;

import jshop.domain.product.dto.SearchProductDetailsResponse;
import jshop.domain.product.service.ProductService;
import jshop.global.dto.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final ProductService productService;

    @GetMapping
    public Response<SearchProductDetailsResponse> searchProductDetail(
        @RequestParam(defaultValue = "9223372036854775807") long cursor,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "") String query) {

        return Response
            .<SearchProductDetailsResponse>builder()
            .data(productService.searchProductDetail(cursor, query, size))
            .build();
    }
}

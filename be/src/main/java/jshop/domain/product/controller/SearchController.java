package jshop.domain.product.controller;

import java.util.Optional;
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
        @RequestParam(defaultValue = "0", name = "cursor") long lastProductId,
        @RequestParam(defaultValue = "10") int size, @RequestParam Optional<String> query) {
        lastProductId = lastProductId == 0L ? Long.MAX_VALUE : lastProductId;

        return Response
            .<SearchProductDetailsResponse>builder()
            .data(productService.searchProductDetail(lastProductId, query, size))
            .build();
    }
}

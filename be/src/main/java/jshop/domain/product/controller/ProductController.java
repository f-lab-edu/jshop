package jshop.domain.product.controller;

import jakarta.validation.Valid;
import jshop.domain.product.dto.CreateProductDetailRequest;
import jshop.domain.product.dto.CreateProductRequest;
import jshop.domain.product.dto.OwnProductsResponse;
import jshop.domain.product.service.ProductService;
import jshop.global.annotation.CurrentUserId;
import jshop.global.dto.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public void createProduct(@RequestBody @Valid CreateProductRequest createProductRequest,
        @CurrentUserId Long userId) {
        productService.createProduct(createProductRequest, userId);
    }

    @GetMapping
    public Response<OwnProductsResponse> getOwnProducts(@CurrentUserId Long userId,
        @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return Response
            .<OwnProductsResponse>builder()
            .data(productService.getOwnProducts(userId, page, size))
            .build();
    }

    @PostMapping("/{product_id}/details")
    public void createProductDetail(
        @PathVariable(value = "product_id", required = true) Long productId,
        @RequestBody @Valid CreateProductDetailRequest createProductDetailRequest,
        @CurrentUserId Long userId) {
        productService.createProductDetail(createProductDetailRequest, userId, productId);
    }
}

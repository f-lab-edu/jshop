package jshop.domain.product.controller;

import jakarta.validation.Valid;
import jshop.domain.product.dto.CreateProductDetailRequest;
import jshop.domain.product.dto.CreateProductRequest;
import jshop.domain.product.dto.OwnProductsResponse;
import jshop.domain.product.dto.UpdateProductDetailRequest;
import jshop.domain.product.dto.UpdateProductDetailStockRequest;
import jshop.domain.product.service.ProductService;
import jshop.global.annotation.CurrentUserId;
import jshop.global.dto.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
            .<OwnProductsResponse>builder().data(productService.getOwnProducts(userId, page, size)).build();
    }

    @PostMapping("/{product_id}/details")
    public void createProductDetail(@PathVariable("product_id") Long productId,
        @RequestBody @Valid CreateProductDetailRequest createProductDetailRequest, @CurrentUserId Long userId) {
        productService.createProductDetail(createProductDetailRequest, userId, productId);
    }

    @PutMapping("/{product_id}/details/{detail_id}")
    public void updateProductDetail(@PathVariable("product_id") Long productId,
        @PathVariable("detail_id") Long detailId, @CurrentUserId Long userId,
        @RequestBody @Valid UpdateProductDetailRequest updateProductDetailRequest) {
        productService.updateProductDetail(productId, detailId, userId, updateProductDetailRequest);
    }

    @PatchMapping("/{product_id}/details/{detail_id}")
    public void updateProductDetailStock(@PathVariable("product_id") Long productId,
        @PathVariable("detail_id") Long detailId, @CurrentUserId Long userId,
        @RequestBody @Valid UpdateProductDetailStockRequest updateProductDetailStockRequest) {
        productService.updateProductDetailStock(detailId, userId, updateProductDetailStockRequest.getQuantity());
    }

    @DeleteMapping("/{product_id}/details/{detail_id}")
    public void deleteProductDetail(@PathVariable("product_id") Long productId,
        @PathVariable("detail_id") Long detailId, @CurrentUserId Long userId) {
        productService.deleteProductDetail(detailId, userId);
    }
}

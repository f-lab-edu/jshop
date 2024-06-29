package jshop.domain.product.controller;

import jakarta.validation.Valid;
import java.util.Optional;
import jshop.domain.product.dto.CreateProductDetailRequest;
import jshop.domain.product.dto.CreateProductRequest;
import jshop.domain.product.dto.OwnProductsResponse;
import jshop.domain.product.dto.UpdateProductDetailRequest;
import jshop.domain.product.dto.UpdateProductDetailStockRequest;
import jshop.domain.product.service.ProductService;
import jshop.global.annotation.CurrentUserId;
import jshop.global.dto.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
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
        @RequestParam("page") Optional<Integer> optionalPage, @RequestParam("size") Optional<Integer> optionalSize) {

        int page = optionalPage.orElse(0);
        int size = optionalSize.orElse(10);

        return Response
            .<OwnProductsResponse>builder().data(productService.getOwnProducts(userId, page, size)).build();
    }

    @PostMapping("/{product_id}/details")
    @PreAuthorize("isAuthenticated() && @productService.checkProductOwnership(authentication.principal, #productId)")
    public void createProductDetail(@PathVariable("product_id") @P("productId") Long productId,
        @RequestBody @Valid CreateProductDetailRequest createProductDetailRequest) {
        productService.createProductDetail(createProductDetailRequest, productId);
    }

    @PutMapping("/{product_id}/details/{detail_id}")
    @PreAuthorize("isAuthenticated() && @productService.checkProductDetailOwnership(authentication.principal, "
        + "#detailId, #productId)")
    public void updateProductDetail(@PathVariable("product_id") @P("productId") Long productId,
        @PathVariable("detail_id") @P("detailId") Long detailId,
        @RequestBody @Valid UpdateProductDetailRequest updateProductDetailRequest) {
        productService.updateProductDetail(detailId, updateProductDetailRequest);
    }

    @PatchMapping("/{product_id}/details/{detail_id}")
    @PreAuthorize("isAuthenticated() && @productService.checkProductDetailOwnership(authentication.principal, "
        + "#detailId, #productId)")
    public void updateProductDetailStock(@PathVariable("product_id") @P("productId") Long productId,
        @PathVariable("detail_id") @P("detailId") Long detailId,
        @RequestBody @Valid UpdateProductDetailStockRequest updateProductDetailStockRequest) {
        productService.updateProductDetailStock(detailId, updateProductDetailStockRequest.getQuantity());
    }

    @DeleteMapping("/{product_id}/details/{detail_id}")
    @PreAuthorize("isAuthenticated() && @productService.checkProductDetailOwnership(authentication.principal, "
        + "#detailId, #productId)")
    public void deleteProductDetail(@PathVariable("product_id") @P("productId") Long productId,
        @PathVariable("detail_id") @P("detailId") Long detailId) {
        productService.deleteProductDetail(detailId);
    }
}

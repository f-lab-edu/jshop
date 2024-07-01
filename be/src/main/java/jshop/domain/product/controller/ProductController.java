package jshop.domain.product.controller;

import jakarta.validation.Valid;
import java.util.Optional;
import jshop.domain.product.dto.CreateProductDetailRequest;
import jshop.domain.product.dto.CreateProductDetailResponse;
import jshop.domain.product.dto.CreateProductRequest;
import jshop.domain.product.dto.CreateProductResponse;
import jshop.domain.product.dto.OwnProductsResponse;
import jshop.domain.product.dto.UpdateProductDetailRequest;
import jshop.domain.product.dto.UpdateProductDetailStockRequest;
import jshop.domain.product.service.ProductService;
import jshop.global.annotation.CurrentUserId;
import jshop.global.common.ErrorCode;
import jshop.global.dto.Response;
import jshop.global.exception.JshopException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.transaction.support.TransactionSynchronizationManager;
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

@Slf4j
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public Response<CreateProductResponse> createProduct(@RequestBody @Valid CreateProductRequest createProductRequest,
        @CurrentUserId Long userId) {
        Long productId = productService.createProduct(createProductRequest, userId);

        return Response
            .<CreateProductResponse>builder()
            .data(CreateProductResponse
                .builder().id(productId).build())
            .build();
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public Response<OwnProductsResponse> getOwnProducts(@CurrentUserId Long userId,
        @RequestParam("page") Optional<Integer> optionalPageNumber,
        @RequestParam("size") Optional<Integer> optionalPageSize) {

        int pageNumber = optionalPageNumber.orElse(0);
        int pageSize = optionalPageSize.orElse(10);

        if (pageSize > 100 || pageSize < 0 || pageNumber < 0) {
            log.error(ErrorCode.ILLEGAL_PAGE_REQUEST.getLogMessage(), pageNumber, pageSize);
            throw JshopException.of(ErrorCode.ILLEGAL_PAGE_REQUEST);
        }

        return Response
            .<OwnProductsResponse>builder().data(productService.getOwnProducts(userId, pageNumber, pageSize)).build();
    }

    @PostMapping("/{product_id}/details")
    @PreAuthorize("isAuthenticated() && @productService.checkProductOwnership(authentication.principal, #productId)")
    public Response<CreateProductDetailResponse> createProductDetail(
        @PathVariable("product_id") @P("productId") Long productId,
        @RequestBody @Valid CreateProductDetailRequest createProductDetailRequest) {
        Long productDetailId = productService.createProductDetail(createProductDetailRequest, productId);

        return Response
            .<CreateProductDetailResponse>builder()
            .data(CreateProductDetailResponse
                .builder().id(productDetailId).build())
            .build();
    }

    @PutMapping("/{product_id}/details/{detail_id}")
    @PreAuthorize("isAuthenticated() && @productService.checkProductDetailOwnership(authentication.principal, "
        + "#detailId, #productId)")
    public void updateProductDetail(@PathVariable("product_id") @P("productId") Long productId,
        @PathVariable("detail_id") @P("detailId") Long detailId,
        @RequestBody @Valid UpdateProductDetailRequest updateProductDetailRequest) {
        productService.updateProductDetail(detailId, updateProductDetailRequest);
    }

    @PatchMapping("/{product_id}/details/{detail_id}/stocks")
    @PreAuthorize("isAuthenticated() && @productService.checkProductDetailOwnership(authentication.principal, "
        + "#detailId, #productId)")
    public void updateProductDetailStock(@PathVariable("product_id") @P("productId") Long productId,
        @PathVariable("detail_id") @P("detailId") Long detailId,
        @RequestBody @Valid UpdateProductDetailStockRequest updateProductDetailStockRequest) {
        Object transactionId = TransactionSynchronizationManager.getCurrentTransactionName();
        log.info("ProductStockSyncTest.init transactionId = {}", transactionId);
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

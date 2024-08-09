package jshop.web.controller;

import jakarta.validation.Valid;
import java.util.Optional;
import jshop.core.domain.product.dto.CreateProductDetailRequest;
import jshop.core.domain.product.dto.CreateProductRequest;
import jshop.core.domain.product.dto.UpdateProductDetailRequest;
import jshop.core.domain.product.dto.UpdateProductDetailStockRequest;
import jshop.core.domain.product.dto.CreateProductDetailResponse;
import jshop.core.domain.product.dto.CreateProductResponse;
import jshop.core.domain.product.dto.OwnProductsResponse;
import jshop.core.domain.product.service.ProductService;
import jshop.web.security.annotation.CurrentUserId;
import jshop.common.exception.ErrorCode;
import jshop.web.dto.Response;
import jshop.common.exception.JshopException;
import jshop.web.security.service.AuthorizationService;
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
    private final AuthorizationService authorizationService;

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
    @PreAuthorize("isAuthenticated() && @authorizationService.checkProductOwnership(authentication.principal, #productId)")
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
    @PreAuthorize("isAuthenticated() && @authorizationService.checkProductDetailOwnership(authentication.principal, "
        + "#detailId, #productId)")
    public void updateProductDetail(@PathVariable("product_id") @P("productId") Long productId,
        @PathVariable("detail_id") @P("detailId") Long detailId,
        @RequestBody @Valid UpdateProductDetailRequest updateProductDetailRequest) {
        productService.updateProductDetail(detailId, updateProductDetailRequest);
    }

    @PatchMapping("/{product_id}/details/{detail_id}/stocks")
    @PreAuthorize("isAuthenticated() && @authorizationService.checkProductDetailOwnership(authentication.principal, "
        + "#detailId, #productId)")
    public void updateProductDetailStock(@PathVariable("product_id") @P("productId") Long productId,
        @PathVariable("detail_id") @P("detailId") Long detailId,
        @RequestBody @Valid UpdateProductDetailStockRequest updateProductDetailStockRequest) {
        Object transactionId = TransactionSynchronizationManager.getCurrentTransactionName();
        log.info("ProductStockSyncTest.init transactionId = {}", transactionId);
        productService.updateProductDetailStock(detailId, updateProductDetailStockRequest.getQuantity());
    }

    @DeleteMapping("/{product_id}/details/{detail_id}")
    @PreAuthorize("isAuthenticated() && @authorizationService.checkProductDetailOwnership(authentication.principal, "
        + "#detailId, #productId)")
    public void deleteProductDetail(@PathVariable("product_id") @P("productId") Long productId,
        @PathVariable("detail_id") @P("detailId") Long detailId) {
        productService.deleteProductDetail(detailId);
    }
}

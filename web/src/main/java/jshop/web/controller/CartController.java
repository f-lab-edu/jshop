package jshop.web.controller;

import jakarta.validation.Valid;
import java.util.Optional;
import jshop.core.domain.cart.dto.AddCartRequest;
import jshop.core.domain.cart.dto.OwnCartInfoResponse;
import jshop.core.domain.cart.dto.UpdateCartRequest;
import jshop.core.domain.cart.service.CartService;
import jshop.web.security.annotation.CurrentUserId;
import jshop.common.exception.ErrorCode;
import jshop.web.dto.Response;
import jshop.common.exception.JshopException;
import jshop.web.security.service.AuthorizationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final AuthorizationService authorizationService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public Response<OwnCartInfoResponse> getCart(@CurrentUserId Long userId,
        @RequestParam("size") Optional<Integer> optionalSize, @RequestParam("page") Optional<Integer> optionalPage) {

        int pageNumber = optionalPage.orElse(0);
        int pageSize = optionalSize.orElse(30);

        if (pageNumber < 0 || pageSize > 30 || pageSize < 0) {
            log.error(ErrorCode.ILLEGAL_PAGE_REQUEST.getLogMessage(), pageNumber, pageSize);
            throw JshopException.of(ErrorCode.ILLEGAL_PAGE_REQUEST);
        }
        OwnCartInfoResponse cartInfo = cartService.getCartPage(userId, pageNumber, pageSize);

        return Response
            .<OwnCartInfoResponse>builder().data(cartInfo).build();
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public void addCart(@RequestBody @Valid AddCartRequest addCartRequest, @CurrentUserId Long userId) {
        cartService.addCart(addCartRequest, userId);
    }

    @DeleteMapping("/{cart_product_detail_id}")
    @PreAuthorize("@authorizationService.checkCartProductOwnership(#productId, authentication.principal)")
    public void deleteCart(@PathVariable("cart_product_detail_id") @P("productId") Long cartProductDetailId,
        @CurrentUserId Long userId) {
        cartService.deleteCart(userId, cartProductDetailId);
    }

    @PutMapping("/{cart_product_detail_id}")
    @PreAuthorize("@authorizationService.checkCartProductOwnership(#productId, authentication.principal)")
    public void updateCart(@PathVariable("cart_product_detail_id") @P("productId") Long cartProductDetailId,
        @RequestBody @Valid UpdateCartRequest updateCartRequest) {
        cartService.updateCart(cartProductDetailId, updateCartRequest);
    }
}

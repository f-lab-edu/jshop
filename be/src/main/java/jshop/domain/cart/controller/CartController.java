package jshop.domain.cart.controller;

import jakarta.validation.Valid;
import java.util.Optional;
import jshop.domain.cart.dto.AddCartRequest;
import jshop.domain.cart.service.CartService;
import jshop.global.annotation.CurrentUserId;
import jshop.global.exception.JshopException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public void addCart(@RequestBody @Valid AddCartRequest addCartRequest, @CurrentUserId Long userId) {
        cartService.addCart(addCartRequest, userId);
    }

    @DeleteMapping("/{cart_product_detail_id}")
    @PreAuthorize("isAuthenticated() && @cartService.checkCartProductOwnership(#productId, authentication.principal)")
    public void deleteCart(@PathVariable("cart_product_detail_id") @P("productId") Optional<Long> optionalId,
        @CurrentUserId Long userId) {

        Long cartProductDetailId = optionalId.orElseThrow(JshopException::new);

        cartService.deleteCart(cartProductDetailId, userId);
    }
}

package jshop.global.utils;

import java.util.Optional;
import jshop.domain.address.entity.Address;
import jshop.domain.cart.entity.Cart;
import jshop.domain.cart.entity.CartProductDetail;
import jshop.global.common.ErrorCode;
import jshop.global.exception.JshopException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CartUtils {

    public static Cart getCartOrThrow(Optional<Cart> optionalCart, Long userId) {
        return optionalCart.orElseThrow(() -> {
            log.error(ErrorCode.CART_NOT_FOUND.getLogMessage(), userId);
            throw JshopException.of(ErrorCode.CART_NOT_FOUND);
        });
    }

    public static CartProductDetail getCarProductDetailtOrThrow(Optional<CartProductDetail> optionalCartProductDetail,
        Long cartProductDetailId) {
        return optionalCartProductDetail.orElseThrow(() -> {
            log.error(ErrorCode.CART_PRODUCTDETAIL_ID_NOT_FOUND.getLogMessage(), cartProductDetailId);
            throw JshopException.of(ErrorCode.CART_PRODUCTDETAIL_ID_NOT_FOUND);
        });
    }

}
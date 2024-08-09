package jshop.web.security.service;

import jshop.common.exception.ErrorCode;
import jshop.common.exception.JshopException;
import jshop.core.domain.address.entity.Address;
import jshop.core.domain.address.service.AddressService;
import jshop.core.domain.cart.entity.Cart;
import jshop.core.domain.cart.entity.CartProductDetail;
import jshop.core.domain.cart.service.CartService;
import jshop.core.domain.order.entity.Order;
import jshop.core.domain.order.service.OrderService;
import jshop.core.domain.product.entity.Product;
import jshop.core.domain.product.entity.ProductDetail;
import jshop.core.domain.product.service.ProductService;
import jshop.web.security.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthorizationService {

    private final CartService cartService;
    private final AddressService addressService;
    private final OrderService orderService;
    private final ProductService productService;

    public boolean checkAddressOwnership(UserDetails userDetails, Long addressId) {
        CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
        Long userId = customUserDetails.getId();

        Address address = addressService.getAddress(addressId);

        if (address.getUser().getId().equals(userId)) {
            return true;
        }

        log.error(ErrorCode.UNAUTHORIZED.getLogMessage(), "Address", addressId, userId);
        throw JshopException.of(ErrorCode.UNAUTHORIZED);
    }

    public boolean checkCartProductOwnership(Long cartProductDetailid, UserDetails userDetails) {
        CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
        Long userId = customUserDetails.getId();

        Cart cart = cartService.getCart(userId);
        CartProductDetail cartProductDetail = cartService.getCartProductDetail(cartProductDetailid);

        return cartProductDetail.getCart().getId().equals(cart.getId());
    }

    public boolean checkOrderOwnership(UserDetails userDetails, Long orderId) {
        CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
        Long userId = customUserDetails.getId();

        Order order = orderService.getOrder(orderId);

        if (order.getUser().getId().equals(userId)) {
            return true;
        }
        log.error(ErrorCode.UNAUTHORIZED.getLogMessage(), "Order", orderId, userId);
        throw JshopException.of(ErrorCode.UNAUTHORIZED);
    }

    public boolean checkProductOwnership(UserDetails userDetails, Long productId) {
        CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
        Long userId = customUserDetails.getId();

        Product product = productService.getProduct(productId);

        if (product.getOwner().getId().equals(userId)) {
            return true;
        }
        log.error(ErrorCode.UNAUTHORIZED.getLogMessage(), "Product", productId, userId);
        throw JshopException.of(ErrorCode.UNAUTHORIZED);
    }


    public boolean checkProductDetailOwnership(UserDetails userDetails, Long detailId, Long productId) {
        Object transactionId = TransactionSynchronizationManager.getCurrentTransactionName();
        log.info("ProductStockSyncTest.init transactionId = {}", transactionId);
        CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
        Long userId = customUserDetails.getId();

        ProductDetail productDetail = productService.getProductDetail(detailId);

        Product product = productDetail.getProduct();

        if (!product.getId().equals(productId)) {
            log.error("상세 상품이 상품에 속하지 않습니다. 상품 ID : [{}], 상세 상품 ID : [{}]", productId, detailId);
            throw JshopException.of(ErrorCode.BAD_REQUEST);
        }

        if (product.getOwner().getId().equals(userId)) {
            return true;
        }
        log.error(ErrorCode.UNAUTHORIZED.getLogMessage(), "ProductDetail", detailId, userId);
        throw JshopException.of(ErrorCode.UNAUTHORIZED);
    }

}

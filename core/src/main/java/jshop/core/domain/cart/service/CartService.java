package jshop.core.domain.cart.service;

import java.util.Optional;
import jshop.core.domain.cart.repository.CartProductDetailRepository;
import jshop.core.domain.cart.repository.CartRepository;
import jshop.core.domain.cart.dto.AddCartRequest;
import jshop.core.domain.cart.dto.CartProductQueryResult;
import jshop.core.domain.cart.dto.OwnCartInfoResponse;
import jshop.core.domain.cart.dto.UpdateCartRequest;
import jshop.core.domain.cart.entity.Cart;
import jshop.core.domain.cart.entity.CartProductDetail;
import jshop.core.domain.product.entity.ProductDetail;
import jshop.core.domain.product.repository.ProductDetailRepository;
import jshop.common.exception.ErrorCode;
import jshop.common.exception.JshopException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartService {

    private final CartRepository cartRepository;
    private final ProductDetailRepository productDetailRepository;
    private final CartProductDetailRepository cartProductDetailRepository;

    public OwnCartInfoResponse getCartPage(Long userId, int pageNumber, int pageSize) {
        Cart cart = getCart(userId);

        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize, Sort.by(Direction.DESC, "createdAt"));
        Page<CartProductQueryResult> page = cartProductDetailRepository.findCartProductInfoByQuery(cart, pageRequest);

        return OwnCartInfoResponse.create(page);
    }

    @Transactional
    public void addCart(AddCartRequest addCartRequest, Long userId) {

        Long detailId = addCartRequest.getProductDetailId();
        Cart cart = getCart(userId);

        if (!productDetailRepository.existsByIdAndIsDeletedFalse(detailId)) {
            log.error(ErrorCode.PRODUCTDETAIL_ID_NOT_FOUND.getLogMessage(), detailId);
            throw JshopException.of(ErrorCode.PRODUCTDETAIL_ID_NOT_FOUND);
        }

        ProductDetail productDetail = productDetailRepository.getReferenceById(detailId);

        cart.addCart(productDetail, addCartRequest.getQuantity());
    }

    @Transactional
    public void deleteCart(Long userId, Long cartProductDetailId) {
        Cart cart = getCart(userId);
        cart.deleteCart(cartProductDetailId);
    }

    @Transactional
    public void updateCart(Long cartProductDetailId, UpdateCartRequest updateCartRequest) {
        CartProductDetail cartProductDetail = getCartProductDetail(cartProductDetailId);
        cartProductDetail.changeQuantity(updateCartRequest.getQuantity());
    }

    public CartProductDetail getCartProductDetail(Long id) {
        Optional<CartProductDetail> optionalCartProductDetail = cartProductDetailRepository.findById(id);
        return optionalCartProductDetail.orElseThrow(() -> {
            log.error(ErrorCode.CART_PRODUCTDETAIL_ID_NOT_FOUND.getLogMessage(), id);
            throw JshopException.of(ErrorCode.CART_PRODUCTDETAIL_ID_NOT_FOUND);
        });
    }

    public Cart getCart(Long userId) {
        Optional<Cart> optionalCart = cartRepository.findCartByUserId(userId);
        return optionalCart.orElseThrow(() -> {
            log.error(ErrorCode.CART_NOT_FOUND.getLogMessage(), userId);
            throw JshopException.of(ErrorCode.CART_NOT_FOUND);
        });
    }
}

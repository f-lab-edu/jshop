package jshop.domain.cart.service;

import java.util.List;
import java.util.Optional;
import jshop.domain.cart.dto.AddCartRequest;
import jshop.domain.cart.dto.CartProductQueryResult;
import jshop.domain.cart.dto.CartProductResponse;
import jshop.domain.cart.dto.OwnCartInfoResponse;
import jshop.domain.cart.dto.UpdateCartRequest;
import jshop.domain.cart.entity.Cart;
import jshop.domain.cart.entity.CartProductDetail;
import jshop.domain.cart.repository.CartProductDetailRepository;
import jshop.domain.cart.repository.CartRepository;
import jshop.domain.product.entity.ProductDetail;
import jshop.domain.product.repository.ProductDetailRepository;
import jshop.global.common.ErrorCode;
import jshop.global.exception.JshopException;
import jshop.global.jwt.dto.CustomUserDetails;
import jshop.global.utils.CartUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.core.userdetails.UserDetails;
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
    public void deleteCart(Long cartProductDetailId) {
        cartProductDetailRepository.deleteById(cartProductDetailId);
    }

    @Transactional
    public void updateCart(Long cartProductDetailId, UpdateCartRequest updateCartRequest) {
        CartProductDetail cartProductDetail = getCartProductDetail(cartProductDetailId);
        cartProductDetail.changeQuantity(updateCartRequest.getQuantity());
    }

    public boolean checkCartProductOwnership(Long cartProductDetailid, UserDetails userDetails) {
        CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
        Long userId = customUserDetails.getId();

        Cart cart = getCart(userId);
        CartProductDetail cartProductDetail = getCartProductDetail(cartProductDetailid);

        return cartProductDetail.getCart().getId().equals(cart.getId());
    }

    private CartProductDetail getCartProductDetail(Long id) {
        Optional<CartProductDetail> optionalCartProductDetail = cartProductDetailRepository.findById(id);
        return CartUtils.getCarProductDetailtOrThrow(optionalCartProductDetail, id);
    }

    private Cart getCart(Long userId) {
        Optional<Cart> optionalCart = cartRepository.findCartByUserId(userId);
        return CartUtils.getCartOrThrow(optionalCart, userId);
    }
}

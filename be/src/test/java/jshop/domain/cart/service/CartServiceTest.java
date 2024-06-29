package jshop.domain.cart.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import jshop.domain.cart.dto.AddCartRequest;
import jshop.domain.cart.entity.Cart;
import jshop.domain.cart.entity.CartProductDetail;
import jshop.domain.cart.repository.CartProductDetailRepository;
import jshop.domain.cart.repository.CartRepository;
import jshop.domain.product.entity.ProductDetail;
import jshop.domain.product.repository.ProductDetailRepository;
import jshop.domain.user.entity.User;
import jshop.global.common.ErrorCode;
import jshop.global.exception.JshopException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("[단위 테스트] CartService")
class CartServiceTest {

    @InjectMocks
    private CartService cartService;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductDetailRepository productDetailRepository;

    @Mock
    private CartProductDetailRepository cartProductDetailRepository;

    @Captor
    private ArgumentCaptor<CartProductDetail> cartProductDetailArgumentCaptor;

    @Nested
    @DisplayName("장바구니 추가 검증")
    class AddCart {

        @Test
        @DisplayName("추가하려는 상품에 문제가 없다면 (존재하면서, 삭제상태가 아니면) 장바구니에 추가할 수 있다")
        public void addCart_success() {
            // given
            Long userId = 1L;
            Long detailId = 1L;

            ProductDetail productDetail = ProductDetail
                .builder().id(detailId).build();
            Cart cart = Cart
                .builder().build();

            AddCartRequest addCartRequest = AddCartRequest
                .builder().productDetailId(detailId).quantity(1).build();

            // when
            when(cartRepository.findCartByUserId(userId)).thenReturn(Optional.of(cart));
            when(productDetailRepository.existsByIdAndIsDeletedFalse(detailId)).thenReturn(true);
            when(productDetailRepository.getReferenceById(detailId)).thenReturn(productDetail);

            cartService.addCart(addCartRequest, userId);
            // then
            verify(cartProductDetailRepository, times(1)).save(cartProductDetailArgumentCaptor.capture());
        }

        @Test
        @DisplayName("추가하려는 상품이 기존 장바구니에 추가되어 있다면, 추가 수량만큼 더한다")
        public void addCart_addQuantity() {
            // given
            Long userId = 1L;
            Long detailId = 1L;

            ProductDetail productDetail = ProductDetail
                .builder().id(detailId).build();
            Cart cart = Cart
                .builder().build();

            AddCartRequest addCartRequest = AddCartRequest
                .builder().productDetailId(detailId).quantity(1).build();

            CartProductDetail cartProductDetail = CartProductDetail
                .builder().cart(cart).productDetail(productDetail).quantity(10).build();

            // when
            when(cartRepository.findCartByUserId(userId)).thenReturn(Optional.of(cart));
            when(productDetailRepository.existsByIdAndIsDeletedFalse(detailId)).thenReturn(true);
            when(productDetailRepository.getReferenceById(detailId)).thenReturn(productDetail);

            cartService.addCart(addCartRequest, userId);

            when(cartProductDetailRepository.findByCartAndProductDetail(cart, productDetail)).thenReturn(
                Optional.of(cartProductDetail));
            cartService.addCart(addCartRequest, userId);
            // then
            assertThat(cartProductDetail.getQuantity()).isEqualTo(11);
        }

        @Test
        @DisplayName("추가하려는 상품에 문제가 있다면 장바구니에 추가할 수 없다")
        public void addCart_noSuchProduct() {
            // given
            Long userId = 1L;
            Long detailId = 1L;

            ProductDetail productDetail = ProductDetail
                .builder().id(detailId).build();
            Cart cart = Cart
                .builder().build();

            AddCartRequest addCartRequest = AddCartRequest
                .builder().productDetailId(detailId).quantity(1).build();

            // when
            when(cartRepository.findCartByUserId(userId)).thenReturn(Optional.of(cart));
            when(productDetailRepository.existsByIdAndIsDeletedFalse(detailId)).thenReturn(false);

            // then
            JshopException jshopException = assertThrows(JshopException.class,
                () -> cartService.addCart(addCartRequest, userId));
            assertThat(jshopException.getErrorCode()).isEqualTo(ErrorCode.PRODUCTDETAIL_ID_NOT_FOUND);
        }

        @Test
        @DisplayName("유저의 카트를 찾을 수 없다면, 예외 발생")
        public void addCart_noSuchCart() {
            // given
            Long userId = 1L;
            Long detailId = 1L;

            AddCartRequest addCartRequest = AddCartRequest
                .builder().productDetailId(detailId).quantity(1).build();

            // when
            when(cartRepository.findCartByUserId(userId)).thenReturn(Optional.empty());

            // then
            JshopException jshopException = assertThrows(JshopException.class,
                () -> cartService.addCart(addCartRequest, userId));
            assertThat(jshopException.getErrorCode()).isEqualTo(ErrorCode.CART_NOT_FOUND);
        }

    }
}
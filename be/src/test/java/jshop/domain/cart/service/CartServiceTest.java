package jshop.domain.cart.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

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
        @DisplayName("추가하려는 상품의 수량은 1보다 커야한다.")
        public void addCart_quantityOver1() {
            // given
            Long userId = 1L;
            Long detailId = 1L;

            ProductDetail productDetail = ProductDetail
                .builder().id(detailId).build();
            Cart cart = Cart
                .builder().build();

            AddCartRequest addCartRequest = AddCartRequest
                .builder().productDetailId(detailId).quantity(0).build();

            // when
            when(cartRepository.findCartByUserId(userId)).thenReturn(Optional.of(cart));
            when(productDetailRepository.existsByIdAndIsDeletedFalse(detailId)).thenReturn(true);

            // then
            JshopException jshopException = assertThrows(JshopException.class,
                () -> cartService.addCart(addCartRequest, userId));
            assertThat(jshopException.getErrorCode()).isEqualTo(ErrorCode.ILLEGAL_CART_QUANTITY_REQUEST_EXCEPTION);
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

    @Nested
    @DisplayName("장바구니 삭제 검증")
    class DeleteCart {

        @Test
        @DisplayName("삭제하려는 상품에 문제가 없다면 장바구니에서 삭제할 수 있다.")
        public void addCart_success() {
            // given
            Long deleteCartProductId = 1L;
            // when
            cartService.deleteCart(deleteCartProductId);
            // then
            verify(cartProductDetailRepository, times(1)).deleteById(deleteCartProductId);
        }
    }

    @Nested
    @DisplayName("장바구니 수량 변경 검증")
    class UpdateCart {

        @Test
        @DisplayName("장바구니 아이템의 변경 이후 수량이 1 이상 이라면 수량을 변경할 수 있다.")
        public void addCart_success() {
            // given
            long userId = 1L;
            long detailId = 1L;
            long cartProductId = 1L;
            int initQuantity = 10;

            ProductDetail productDetail = ProductDetail
                .builder().id(detailId).build();
            Cart cart = Cart
                .builder().build();

            CartProductDetail cartProductDetail = CartProductDetail
                .builder().id(1L).cart(cart).productDetail(productDetail).quantity(initQuantity).build();

            UpdateCartRequest updateCartRequest = UpdateCartRequest
                .builder().productDetailId(cartProductId).quantity(5).build();

            // when
            when(cartProductDetailRepository.findById(cartProductId)).thenReturn(Optional.of(cartProductDetail));

            cartService.updateCart(cartProductId, updateCartRequest);

            // then
            assertThat(cartProductDetail.getQuantity()).isEqualTo(15);
        }

        @Test
        @DisplayName("장바구니 아이템의 변경 이후 수량이 1보다 작다면 변경할 수 없다.")
        public void addCart_less1() {
            // given
            long userId = 1L;
            long detailId = 1L;
            long cartProductId = 1L;
            int initQuantity = 10;

            ProductDetail productDetail = ProductDetail
                .builder().id(detailId).build();
            Cart cart = Cart
                .builder().build();

            CartProductDetail cartProductDetail = CartProductDetail
                .builder().id(1L).cart(cart).productDetail(productDetail).quantity(initQuantity).build();

            UpdateCartRequest updateCartRequest = UpdateCartRequest
                .builder().productDetailId(cartProductId).quantity(-11).build();

            // when
            when(cartProductDetailRepository.findById(cartProductId)).thenReturn(Optional.of(cartProductDetail));

            // then
            JshopException jshopException = assertThrows(JshopException.class,
                () -> cartService.updateCart(cartProductId, updateCartRequest));

            assertThat(cartProductDetail.getQuantity()).isEqualTo(10);
            assertThat(jshopException.getErrorCode()).isEqualTo(ErrorCode.ILLEGAL_CART_QUANTITY_REQUEST_EXCEPTION);
        }
    }

    @Nested
    @DisplayName("장바구니 페이징 가져오기 검증")
    class GetCartPage {

        @Test
        @DisplayName("장바구니에 포함된 상품들을 페이징으로 가져올 수 있다")
        public void getCart_success() throws Exception {
            // given
            Cart cart = Cart
                .builder().build();
            int pageNumber = 0;
            int pageSize = 30;

            PageRequest pageRequest = PageRequest.of(pageNumber, pageSize, Sort.by(Direction.DESC, "createdAt"));

            List<CartProductQueryResult> products = new ArrayList<>();
            for (long i = 0; i < 10; i++) {
                products.add(CartProductQueryResult
                    .builder()
                    .id(i)
                    .productDetailId(i)
                    .productName("test" + i)
                    .manufacturer("제조사")
                    .price(1000L)
                    .quantity(1)
                    .build());
            }

            Page<CartProductQueryResult> page = new PageImpl<>(products);

            // when
            when(cartRepository.findCartByUserId(1L)).thenReturn(Optional.of(cart));
            when(cartProductDetailRepository.findCartProductInfoByQuery(cart, pageRequest)).thenReturn(page);
            OwnCartInfoResponse results = cartService.getCartPage(1L, pageNumber, pageSize);

            // then
            assertThat(results.getPage()).isEqualTo(0);
            assertThat(results.getTotalCount()).isEqualTo(10);
            assertThat(results.getTotalPage()).isEqualTo(1);
            assertThat(results.getProducts().get(0).getProductName()).isEqualTo("test0");
            assertThat(results.getProducts().get(0).getPrice()).isEqualTo(1000L);
            assertThat(results.getProducts().get(0).getQuantity()).isEqualTo(1);
        }
    }
}
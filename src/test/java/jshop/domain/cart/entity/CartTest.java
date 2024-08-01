package jshop.domain.cart.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jshop.domain.cart.repository.CartRepository;
import jshop.domain.product.entity.ProductDetail;
import jshop.domain.product.repository.ProductDetailRepository;
import jshop.global.common.ErrorCode;
import jshop.global.exception.JshopException;
import jshop.utils.config.BaseTestContainers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@DisplayName("[단위 테스트] Cart")
@AutoConfigureTestDatabase(replace = Replace.NONE)
class CartTest extends BaseTestContainers {

    @Autowired
    ProductDetailRepository productDetailRepository;

    @Autowired
    CartRepository cartRepository;

    @PersistenceContext
    EntityManager em;

    @Nested
    @DisplayName("장바구니 추가 검증")
    class AddCart {

        @Test
        @DisplayName("장바구니에 없던 상품이라면 장바구니에 상품을 추가할 수 있다.")
        public void addCart_firstAdd() {
            // given
            ProductDetail productDetail = ProductDetail
                .builder().build();

            Cart cart = Cart.create();

            productDetailRepository.save(productDetail);
            cartRepository.save(cart);

            // when
            cart.addCart(productDetail, 3);

            em.flush();
            em.clear();

            // then
            Cart foundCart = cartRepository.findById(cart.getId()).get();
            assertThat(foundCart.getCartProductDetails().size()).isEqualTo(1);
            assertThat(foundCart.getCartProductDetails().get(0).getProductDetail().getId()).isEqualTo(
                productDetail.getId());
            assertThat(foundCart.getCartProductDetails().get(0).getQuantity()).isEqualTo(3);
        }

        @Test
        @DisplayName("장바구니에 있던 상품이라면 수량이 증가한다")
        public void addCart_secondAdd() {
            // given
            ProductDetail productDetail = ProductDetail
                .builder().build();

            Cart cart = Cart.create();

            productDetailRepository.save(productDetail);
            cartRepository.save(cart);

            // when
            cart.addCart(productDetail, 3);
            cart.addCart(productDetail, 3);
            em.flush();
            em.clear();

            // then
            Cart foundCart = cartRepository.findById(cart.getId()).get();
            assertThat(foundCart.getCartProductDetails().size()).isEqualTo(1);
            assertThat(foundCart.getCartProductDetails().get(0).getProductDetail().getId()).isEqualTo(
                productDetail.getId());
            assertThat(foundCart.getCartProductDetails().get(0).getQuantity()).isEqualTo(6);
        }

        @Test
        @DisplayName("추가 수량이 1보다 작다면 ILLEGAL_CART_QUANTITY_REQUEST_EXCEPTION 발생")
        public void addCart_invalidQuantity() {
            // given
            ProductDetail productDetail = ProductDetail
                .builder().build();

            Cart cart = Cart.create();

            productDetailRepository.save(productDetail);
            cartRepository.save(cart);

            // when
            JshopException jshopException = assertThrows(JshopException.class, () -> cart.addCart(productDetail, 0));

            // then
            assertThat(jshopException.getErrorCode()).isEqualTo(ErrorCode.ILLEGAL_CART_QUANTITY_REQUEST_EXCEPTION);
        }

    }
}
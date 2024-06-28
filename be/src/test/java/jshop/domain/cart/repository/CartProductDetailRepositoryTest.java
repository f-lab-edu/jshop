package jshop.domain.cart.repository;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jshop.domain.cart.entity.Cart;
import jshop.domain.cart.entity.CartProductDetail;
import jshop.domain.inventory.entity.Inventory;
import jshop.domain.product.dto.ProductDetailResponse;
import jshop.domain.product.entity.Product;
import jshop.domain.product.entity.ProductDetail;
import jshop.domain.product.repository.ProductDetailRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

@DataJpaTest
@Transactional
@DisplayName("[단위 테스트] CartProductDetailRepository")
class CartProductDetailRepositoryTest {

    @Autowired
    private CartProductDetailRepository cartProductDetailRepository;

    @Autowired
    private ProductDetailRepository productDetailRepository;

    @Autowired
    private CartRepository cartRepository;

    @PersistenceContext
    private EntityManager em;

    @Test
    @DisplayName("장바구니와 상품으로 장바구니에 추가된 상품 찾기")
    public void findByCartAndProductDetail() {
        // given
        Cart cart = Cart
            .builder().build();

        ProductDetail productDetail = ProductDetail
            .builder().build();

        cartRepository.save(cart);
        productDetailRepository.save(productDetail);

        CartProductDetail cartProductDetail = CartProductDetail
            .builder().cart(cart).productDetail(productDetail).quantity(1).build();

        cartProductDetailRepository.save(cartProductDetail);

        em.flush();
        em.clear();

        // when
        Cart searchCart = cartRepository.getReferenceById(cart.getId());
        ProductDetail searchProductDetail = productDetailRepository.getReferenceById(productDetail.getId());

        CartProductDetail foundCartProductDetail = cartProductDetailRepository
            .findByCartAndProductDetail(searchCart, searchProductDetail)
            .get();

        // then
        assertThat(foundCartProductDetail.getCart().getId()).isEqualTo(cart.getId());
        assertThat(foundCartProductDetail.getProductDetail().getId()).isEqualTo(productDetail.getId());
    }
}
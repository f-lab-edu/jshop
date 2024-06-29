package jshop.domain.cart.repository;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import jshop.domain.cart.dto.CartProductQueryResult;
import jshop.domain.cart.entity.Cart;
import jshop.domain.cart.entity.CartProductDetail;
import jshop.domain.inventory.entity.Inventory;
import jshop.domain.product.dto.ProductDetailResponse;
import jshop.domain.product.entity.Product;
import jshop.domain.product.entity.ProductDetail;
import jshop.domain.product.repository.ProductDetailRepository;
import jshop.domain.product.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
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
    @Autowired
    private ProductRepository productRepository;

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

    @Test
    @DisplayName("특정 장바구니에 포함된 상품 리스트 가져오기")
    public void findCartProductInfoByQuery() {
        // given
        Cart cart = Cart
            .builder().build();
        cartRepository.save(cart);
        List<ProductDetail> productDetails = new ArrayList<>();

        Product product = Product
            .builder().name("상품").description("상품 설명 입니다.").manufacturer("제조사").build();
        productRepository.save(product);

        for (int i = 0; i < 10; i++) {
            ProductDetail pd = ProductDetail
                .builder().price(1000L).product(product).build();
            productDetailRepository.save(pd);
            productDetails.add(pd);
        }

        for (int i = 0; i < 10; i++) {
            CartProductDetail cartProductDetail = CartProductDetail
                .builder().cart(cart).productDetail(productDetails.get(i)).quantity(i).build();
            cartProductDetailRepository.save(cartProductDetail);
        }

        // when
        PageRequest pageRequest = PageRequest.of(0, 5, Sort.by(Direction.DESC, "createdAt"));
        Page<CartProductQueryResult> page = cartProductDetailRepository.findCartProductInfoByQuery(cart, pageRequest);

        // then
        assertThat(page.getSize()).isEqualTo(5);
        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.getNumberOfElements()).isEqualTo(5);
        assertThat(page.getTotalElements()).isEqualTo(10);
        assertThat(page.getNumber()).isEqualTo(0);
        assertThat(page.getContent().get(0).getProductDetailId()).isEqualTo(productDetails.get(9).getId());
    }
}
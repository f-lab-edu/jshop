package jshop.core.cart.repository;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import jshop.core.domain.cart.dto.CartProductQueryResult;
import jshop.core.domain.cart.entity.Cart;
import jshop.core.domain.cart.entity.CartProductDetail;
import jshop.core.domain.cart.repository.CartProductDetailRepository;
import jshop.core.domain.cart.repository.CartRepository;
import jshop.core.domain.product.entity.Product;
import jshop.core.domain.product.entity.ProductDetail;
import jshop.core.domain.product.repository.ProductDetailRepository;
import jshop.core.domain.product.repository.ProductRepository;
import jshop.core.config.P6SpyConfig;
import jshop.common.test.BaseTestContainers;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@DataJpaTest
@EnableJpaAuditing
@DisplayName("[단위 테스트] CartProductDetailRepository")
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Import(P6SpyConfig.class)
class CartProductDetailRepositoryTest extends BaseTestContainers {

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
        log.info("d : {}", page.getContent());
        log.info("d2 : {}", productDetails);
        assertThat(page.getContent().get(0).getProductDetailId()).isEqualTo(productDetails.get(9).getId());
    }
}
package jshop.domain.product.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jshop.domain.product.dto.SearchProductDetailQueryResult;
import jshop.domain.product.entity.Product;
import jshop.domain.product.entity.ProductDetail;
import jshop.global.config.P6SpyConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.transaction.annotation.Transactional;

@DataJpaTest
@Import(P6SpyConfig.class)
@DisplayName("[단위 테스트] ProductDetailRepository")
@Transactional
class ProductDetailRepositoryTest {

    @Autowired
    private ProductDetailRepository productDetailRepository;

    @Autowired
    private SearchRepository searchRepository;

    @Autowired
    private ProductRepository productRepository;

    @Nested
    @DisplayName("검색어로 상품정보 찾기 무한스크롤 JPQL 테스트")
    class SearchProductDetailsByQuery {

        private Map<String, List<String>> attributes;
        private Map<String, String> attribute1;
        private Map<String, String> attribute2;

        @BeforeEach
        public void init() {
            attributes = new HashMap<>();
            attributes.put("storage", List.of("128GB", "256GB"));
            attributes.put("color", List.of("white", "yellow"));

            attribute1 = new HashMap<>();
            attribute1.put("storage", "128GB");
            attribute1.put("color", "yellow");

            attribute2 = new HashMap<>();
            attribute2.put("storage", "128GB");
            attribute2.put("color", "white");
        }


        @Test
        @DisplayName("검색어로 페이징쿼리 요청")
        public void searchProduct() throws Exception {
            // given
            Product iphone = Product
                .builder().name("아이폰").description("아이폰입니다.").manufacturer("애플").attributes(attributes).build();

            ProductDetail iphone_yellow_128 = ProductDetail
                .builder().product(iphone).price(1_000_000L).attribute(attribute1).build();

            ProductDetail iphone_white_128 = ProductDetail
                .builder().product(iphone).price(1_200_000L).attribute(attribute2).build();

            productRepository.save(iphone);
            productDetailRepository.save(iphone_yellow_128);
            productDetailRepository.save(iphone_white_128);

            // when
            Page<SearchProductDetailQueryResult> page = searchRepository.searchProductDetailsByQuery("아이폰",
                PageRequest.of(0, 3, Sort.by(Direction.DESC, "id")));

            // then
            assertThat(page.getTotalElements()).isEqualTo(2);
            assertThat(page.getTotalPages()).isEqualTo(1);
            assertThat(page.getContent().get(0).getName()).isEqualTo("아이폰");
            assertThat(page.getContent().get(0).getManufacturer()).isEqualTo("애플");
            assertThat(page.getContent().get(0).getDescription()).isEqualTo("아이폰입니다.");
            assertThat(page.getContent().get(0).getPrice()).isEqualTo(1_200_000L);
            assertThat(page.getContent().get(0).getAttribute()).isEqualTo(attribute2);
        }

        @Test
        @DisplayName("검색 쿼리가 없을때는 결과가 없다.")
        public void searchProduct_nullQuery() {
            // given
            Product iphone = Product
                .builder().name("아이폰").description("아이폰입니다.").manufacturer("애플").attributes(attributes).build();

            ProductDetail iphone_yellow_128 = ProductDetail
                .builder().product(iphone).price(1_000_000L).attribute(attribute1).build();

            ProductDetail iphone_white_128 = ProductDetail
                .builder().product(iphone).price(1_200_000L).attribute(attribute2).build();

            productRepository.save(iphone);
            productDetailRepository.save(iphone_yellow_128);
            productDetailRepository.save(iphone_white_128);

            // when
            Page<SearchProductDetailQueryResult> page = searchRepository.searchProductDetailsByQuery(null,
                PageRequest.of(0, 3, Sort.by(Direction.DESC, "id")));

            // then
            assertThat(page.getTotalElements()).isEqualTo(0);
        }
    }

    @Nested
    @DisplayName("상세 상품 존재 여부 확인")
    class ExistsProductDetails {

        @Test
        @DisplayName("is_deleted == true 인 상품은 existsByIdAndIsDeletedFalse 의 결과가 false")
        public void existsByIdAndIsDeletedFalse_success() {
            // given
            ProductDetail productDetail = ProductDetail
                .builder().isDeleted(true).build();

            // when
            productDetailRepository.save(productDetail);

            // then
            boolean result = productDetailRepository.existsByIdAndIsDeletedFalse(productDetail.getId());
            assertThat(result).isFalse();
        }
    }

}
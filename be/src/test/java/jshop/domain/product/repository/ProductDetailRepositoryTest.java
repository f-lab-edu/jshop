package jshop.domain.product.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jshop.domain.product.dto.ProductDetailResponse;
import jshop.domain.product.entity.Product;
import jshop.domain.product.entity.ProductDetail;
import jshop.global.config.P6SpyConfig;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

@DataJpaTest
@Import(P6SpyConfig.class)
class ProductDetailRepositoryTest {

    @Autowired
    private ProductDetailRepository productDetailRepository;

    @Autowired
    private ProductRepository productRepository;

    @Test
    public void 상품검색기능() throws Exception {
        // given
        Map<String, List<String>> attributes = new HashMap<>();
        attributes.put("storage", List.of("128GB", "256GB"));
        attributes.put("color", List.of("white", "yellow"));

        Map<String, String> attribute1 = new HashMap<>();
        attribute1.put("storage", "128GB");
        attribute1.put("color", "yellow");

        Map<String, String> attribute2 = new HashMap<>();
        attribute2.put("storage", "128GB");
        attribute2.put("color", "white");

        Product iphone = Product
            .builder()
            .name("아이폰")
            .description("아이폰입니다.")
            .manufacturer("애플")
            .attributes(attributes)
            .build();

        ProductDetail iphone_yellow_128 = ProductDetail
            .builder().product(iphone).price(1_000_000L).attribute(attribute1).build();

        ProductDetail iphone_white_128 = ProductDetail
            .builder().product(iphone).price(1_200_000L).attribute(attribute2).build();

        productRepository.save(iphone);
        productDetailRepository.save(iphone_yellow_128);
        productDetailRepository.save(iphone_white_128);

        // when
        Page<ProductDetailResponse> page = productDetailRepository.searchProductDetailsByQuery(Long.MAX_VALUE, "아이폰", PageRequest.of(0, 3, Sort.by(Direction.DESC, "id")));

        // then
        assertThat(page.getTotalElements()).isEqualTo(2);
        assertThat(page.getTotalPages()).isEqualTo(1);
        assertThat(page.getContent().getFirst().getName()).isEqualTo("아이폰");
        assertThat(page.getContent().getFirst().getManufacturer()).isEqualTo("애플");
        assertThat(page.getContent().getFirst().getDescription()).isEqualTo("아이폰입니다.");
        assertThat(page.getContent().getFirst().getPrice()).isEqualTo(1_200_000L);
        assertThat(page.getContent().getFirst().getAttribute()).isEqualTo(attribute2);
    }
}
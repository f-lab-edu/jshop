package jshop.domain.product.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import jshop.domain.product.entity.Product;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    public void 상품생성() {
        // given
        Map<String, List<String>> attributes = new HashMap<>();
        List<String> colorList = List.of("red", "blue", "yellow");
        attributes.put("color", colorList);
        List<String> sizeList = List.of("95", "100", "105");
        attributes.put("size", sizeList);
        Product p = Product.builder()
            .name("test")
            .attributes(attributes)
            .build();
        // when

        productRepository.save(p);

        // then
        Optional<Product> optionalFindProduct = productRepository.findById(p.getId());
        optionalFindProduct.ifPresentOrElse((product) -> {
            assertThat(product.getName()).isEqualTo("test");
            assertThat(product.getAttributes()
                .get("color")).isEqualTo(colorList);
            assertThat(product.getAttributes()
                .get("size")).isEqualTo(sizeList);

        }, () -> {
            Assertions.fail("fail");
        });

        Product byAttributes = productRepository.findByAttributes(attributes);

        System.out.println(byAttributes);
    }
}
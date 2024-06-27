package jshop.domain.product.dto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jshop.domain.product.entity.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("[단위 테스트] ProductResponse")
class ProductResponseTest {

    @Test
    @DisplayName("Product로 ProductResponse 생성 검증")
    public void of_success() {
        // given
        Map<String, List<String>> attributes = new HashMap<>();
        attributes.put("속성1", List.of("a", "b", "c"));
        attributes.put("속성2", List.of("1", "2", "3"));

        Product product = Product
            .builder().name("이름").manufacturer("제조사").description("설명").attributes(attributes).build();
        // when

        ProductResponse productResponse = ProductResponse.of(product);
        // then
        assertAll("ProductResponse 검증", () -> assertThat(productResponse.getName()).isEqualTo(product.getName()),
            () -> assertThat(productResponse.getManufacturer()).isEqualTo(product.getManufacturer()),
            () -> assertThat(productResponse.getDescription()).isEqualTo(product.getDescription()),
            () -> assertThat(productResponse.getAttributes()).isEqualTo(product.getAttributes()));
    }
}
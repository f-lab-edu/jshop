package jshop.domain.product.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class ProductTest {

    @Test
    public void verifyChildAttribute_올바른속성() {
        // given
        Map<String, List<String>> attributes = new HashMap<>();
        attributes.put("color", List.of("red", "blue"));
        attributes.put("size", List.of("100", "105"));
        Product product = Product
            .builder().attributes(attributes).build();

        Map<String, String> attribute = new HashMap<>();
        attribute.put("color", "blue");
        attribute.put("size", "100");
        // when

        boolean result = product.verifyChildAttribute(attribute);

        // then
        assertThat(result).isTrue();
    }

    @Test
    public void verifyChildAttribute_제품_상세제품_속성없음() {
        // given
        Map<String, List<String>> attributes = new HashMap<>();
        Product product = Product
            .builder().attributes(attributes).build();

        Map<String, String> attribute = new HashMap<>();
        // when

        boolean result = product.verifyChildAttribute(attribute);

        // then
        assertThat(result).isTrue();
    }

    @Test
    public void verifyChildAttribute_상세제품_속성필드부족() {
        // given
        Map<String, List<String>> attributes = new HashMap<>();
        attributes.put("color", List.of("red", "blue"));
        attributes.put("size", List.of("100", "105"));
        Product product = Product
            .builder().attributes(attributes).build();

        Map<String, String> attribute = new HashMap<>();
        attribute.put("color", "blue");
        // when

        boolean result = product.verifyChildAttribute(attribute);

        // then
        assertThat(result).isFalse();
    }

    @Test
    public void verifyChildAttribute_상세제품_정의되지않은속성값() {
        // given
        Map<String, List<String>> attributes = new HashMap<>();
        attributes.put("color", List.of("red", "blue"));
        attributes.put("size", List.of("100", "105"));
        Product product = Product
            .builder().attributes(attributes).build();

        Map<String, String> attribute = new HashMap<>();
        attribute.put("color", "blue");
        attribute.put("size", "95");
        // when

        boolean result = product.verifyChildAttribute(attribute);

        // then
        assertThat(result).isFalse();
    }

    @Test
    public void verifyChildAttribute_상세제품_속성필드없음() {
        // given
        Map<String, List<String>> attributes = new HashMap<>();
        attributes.put("color", List.of("red", "blue"));
        attributes.put("size", List.of("100", "105"));
        Product product = Product
            .builder().attributes(attributes).build();

        Map<String, String> attribute = null;
        // when

        boolean result = product.verifyChildAttribute(attribute);

        // then
        assertThat(result).isFalse();
    }
}
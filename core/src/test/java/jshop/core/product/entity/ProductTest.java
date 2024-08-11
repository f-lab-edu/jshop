package jshop.core.product.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jshop.core.domain.category.entity.Category;
import jshop.core.domain.product.dto.CreateProductRequest;
import jshop.core.domain.product.entity.Product;
import jshop.core.domain.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("[단위 테스트] Product")
class ProductTest {

    @Nested
    @DisplayName("상세상품의 속성이 상품에 정의된 속성인지 검증")
    class VerifyChildAttribute {

        @Test
        @DisplayName("상세상품의 속성이 상품에 정의되어 있을때는 true")
        public void verifyChildAttribute_success() {
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
        @DisplayName("상세상품과 상품에 모두 속성이 정외되어 있지 않다면 true")
        public void verifyChildAttribute_noEachAttribute() {
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
        @DisplayName("상품에 정의된 필드와 상세상품에 필드가 다르면 false")
        public void verifyChildAttribute_diffKey() {
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
        @DisplayName("상세상품의 속성값이 상품에 정의되어있지 않다면 false")
        public void verifyChildAttribute_undefinedValue() {
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
        @DisplayName("상품에 필드가 있지만, 상세상품에 속성필드가 없다면 false")
        public void verifyChildAttribute_noAttr() {
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

    @Test
    @DisplayName("CreateProductRequest 로 Product 생성 검증")
    public void of_success() {
        // given
        Map<String, List<String>> attributes = new HashMap<>();
        attributes.put("속성1", List.of("a", "b", "c"));
        attributes.put("속성2", List.of("1", "2", "3"));

        CreateProductRequest createProductRequest = CreateProductRequest
            .builder().name("이름").manufacturer("제조사").description("설명").attributes(attributes).build();

        Category category = Category
            .builder().name("카테고리").build();
        User user = User
            .builder().id(1L).username("kim").email("test").role("ROLE_USER").build();
        // when
        Product product = Product.of(createProductRequest, category, user);

        // then
        assertAll("Product 생성 검증", () -> assertThat(product.getName()).isEqualTo("이름"),
            () -> assertThat(product.getManufacturer()).isEqualTo("제조사"),
            () -> assertThat(product.getDescription()).isEqualTo("설명"),
            () -> assertThat(product.getCategory()).isEqualTo(category),
            () -> assertThat(product.getOwner()).isEqualTo(user),
            () -> assertThat(product.getAttributes()).isEqualTo(attributes));
    }

}
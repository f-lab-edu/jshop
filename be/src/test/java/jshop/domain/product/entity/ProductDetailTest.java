package jshop.domain.product.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jshop.domain.inventory.entity.Inventory;
import jshop.domain.product.dto.CreateProductDetailRequest;
import jshop.global.exception.JshopException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("[단위 테스트] ProductDetail")
class ProductDetailTest {

    @Test
    @DisplayName("상세 상품의 속성이, 상품에 정의된 속성에 포함된다면 상세 상품을 생성할 수 있다.")
    public void of_success() {
        // given
        Map<String, String> attribute = new HashMap<>();
        attribute.put("attr1", "a");

        Map<String, List<String>> attributes = new HashMap<>();
        attributes.put("attr1", List.of("a", "b"));
        CreateProductDetailRequest createProductDetailRequest = CreateProductDetailRequest
            .builder().price(1000L).attribute(attribute).build();

        Product product = Product
            .builder().name("상품").attributes(attributes).build();

        Inventory inventory = Inventory
            .builder().build();

        // when
        ProductDetail productDetail = ProductDetail.of(createProductDetailRequest, product, inventory);

        // then
        assertAll("ProductDetail 생성 검증", () -> assertThat(productDetail.getProduct()).isEqualTo(product),
            () -> assertThat(productDetail.getPrice()).isEqualTo(1000L),
            () -> assertThat(productDetail.getAttribute()).isEqualTo(attribute),
            () -> assertThat(productDetail.getInventory()).isEqualTo(inventory));
    }

    @Test
    @DisplayName("상세 상품의 속성이, 상품에 정의되어있지 않다면 상세 상품을 생성할 수 없다.")
    public void of_noAttr() {
        // given
        Map<String, String> attribute = new HashMap<>();
        attribute.put("attr1", "a");

        CreateProductDetailRequest createProductDetailRequest = CreateProductDetailRequest
            .builder().price(1000L).attribute(attribute).build();

        Product product = Product
            .builder().name("상품").build();

        Inventory inventory = Inventory
            .builder().build();

        // then
        assertThrows(JshopException.class, () -> ProductDetail.of(createProductDetailRequest, product, inventory));
    }
}
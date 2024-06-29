package jshop.domain.product.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.HashMap;
import java.util.Map;
import jshop.domain.inventory.entity.Inventory;
import jshop.domain.product.dto.CreateProductDetailRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("[단위 테스트] ProductDetail")
class ProductDetailTest {

    @Test
    @DisplayName("CreateProductDetailRequest로 ProductDetail 생성 검증")
    public void of_success() {
        // given
        Map<String, String> attribute = new HashMap<>();
        attribute.put("a", "b");
        CreateProductDetailRequest createProductDetailRequest = CreateProductDetailRequest
            .builder().price(1000L).attribute(attribute).build();

        Product product = Product
            .builder().name("상품").build();

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
}
package jshop.domain.product.dto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jshop.domain.product.entity.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.annotation.MergedAnnotations.Search;

@DisplayName("[단위 테스트] ProductDetailResponse")
class ProductDetailResponseTest {

    @Test
    @DisplayName("SearchProductDetailQueryResult로 ProductDetailResponse 생성 검증")
    public void of_success() {
        // given
        Map<String, String> attribute = new HashMap<>();
        attribute.put("속성1", "a");
        attribute.put("속성2", "b");

        SearchProductDetailQueryResult searchProductDetailQueryResult = SearchProductDetailQueryResult
            .builder().attribute(attribute).name("test").manufacturer("apple").description("asdf").build();

        // when
        ProductDetailResponse productDetailResponse = ProductDetailResponse.of(searchProductDetailQueryResult);

        // then
        assertAll("ProductDetailResponse 검증",
            () -> assertThat(productDetailResponse.getName()).isEqualTo(searchProductDetailQueryResult.getName()),
            () -> assertThat(productDetailResponse.getManufacturer()).isEqualTo(
                searchProductDetailQueryResult.getManufacturer()),
            () -> assertThat(productDetailResponse.getDescription()).isEqualTo(
                searchProductDetailQueryResult.getDescription()),
            () -> assertThat(productDetailResponse.getAttribute()).isEqualTo(
                searchProductDetailQueryResult.getAttribute()));
    }
}
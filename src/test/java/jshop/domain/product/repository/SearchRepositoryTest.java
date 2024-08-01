package jshop.domain.product.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jshop.domain.category.entity.Category;
import jshop.domain.category.repository.CategoryRepository;
import jshop.domain.product.dto.SearchCondition;
import jshop.domain.product.dto.SearchProductDetailQueryResult;
import jshop.domain.product.entity.Product;
import jshop.domain.product.entity.ProductDetail;
import jshop.utils.config.BaseTestContainers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@DisplayName("[단위 테스트] SearchRepository")
class SearchRepositoryTest extends BaseTestContainers {

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ProductDetailRepository productDetailRepository;

    @Autowired
    SearchRepository searchRepository;

    List<Category> categories = new ArrayList<>();
    List<Product> products = new ArrayList<>();
    List<ProductDetail> productDetails = new ArrayList<>();

    int categoryN = 3;
    int productN = 18;
    int pdN = 3;

    @BeforeEach
    public void init() {
        for (int i = 0; i < categoryN; i++) {
            categories.add(categoryRepository.save(
                Category
                    .builder()
                    .name("category" + i)
                    .build()));
        }

        for (int i = 0; i < productN; i++) {
            Map<String, List<String>> attributes = new HashMap<>();
            List<String> attribute1 = List.of("1", "2", "3");
            List<String> attribute2 = List.of("a", "b", "c");
            boolean attr1 = false;
            boolean attr2 = false;

            if (i % 2 == 0) {
                attributes.put("attr1", attribute1);
                attr1 = true;
            }

            if (i % 3 == 0) {
                attributes.put("attr2", attribute2);
                attr2 = true;
            }

            Product product = productRepository.save(Product
                .builder()
                .name("product" + i)
                .attributes(attributes)
                .manufacturer("manufacturer" + i)
                .category(categories.get(i % 3))
                .build());

            products.add(product);

            for (int j = 0; j < pdN; j++) {
                Map<String, String> attribute = new HashMap<>();
                if (attr1) {
                    attribute.put("attr1", attribute1.get(j));
                }

                if (attr2) {
                    attribute.put("attr2", attribute2.get(j));
                }

                productDetails.add(productDetailRepository.save(ProductDetail
                    .builder()
                    .price(i * 1000L + j * 100L)
                    .attribute(attribute)
                    .product(product)
                    .build()));
            }
        }
    }

    @Test
    @DisplayName("정렬, 필터 없이 검색")
    public void search_noCondition() {

        // given
        SearchCondition condition = SearchCondition
            .builder()
            .query("product")
            .build();

        PageRequest pageRequest = PageRequest.of(0, 10);

        // when
        Page<SearchProductDetailQueryResult> page = searchRepository.search(condition, pageRequest);

        // then
        assertThat(page.getTotalElements()).isEqualTo(productN * pdN);
        assertThat(page.getNumberOfElements()).isEqualTo(10);
    }

    @Test
    @DisplayName("특정 제조사로 필터링해 검색")
    public void search_filter_manufacturer() {
        // given
        SearchCondition condition = SearchCondition
            .builder()
            .query("product")
            .manufacturer("manufacturer3")
            .build();

        PageRequest pageRequest = PageRequest.of(0, 10);
        // when
        Page<SearchProductDetailQueryResult> page = searchRepository.search(condition, pageRequest);
        List<SearchProductDetailQueryResult> content = page.getContent();

        // then
        assertThat(page.getTotalElements()).isEqualTo(3);
        assertThat(page.getNumberOfElements()).isEqualTo(3);
        assertThat(content.get(0).getName()).isEqualTo("product3");
        assertThat(content.get(0).getManufacturer()).isEqualTo("manufacturer3");
    }

    @Test
    @DisplayName("특정 카테고리로 필터링해 검색")
    public void search_filter_category() {
        // given
        Category category = categories.get(2);
        SearchCondition condition = SearchCondition
            .builder()
            .query("product")
            .categoryId(category.getId())
            .build();

        PageRequest pageRequest = PageRequest.of(0, 10);
        // when
        Page<SearchProductDetailQueryResult> page = searchRepository.search(condition, pageRequest);
        List<SearchProductDetailQueryResult> content = page.getContent();

        // then
        assertThat(page.getTotalElements()).isEqualTo((productN * pdN) / categoryN);
        assertThat(page.getNumberOfElements()).isEqualTo(10);
        assertThat(content.get(0).getName()).isEqualTo("product2");
        assertThat(content.get(0).getManufacturer()).isEqualTo("manufacturer2");
        assertThat(content.get(0).getCategory()).isEqualTo(category.getName());
    }

    @Test
    @DisplayName("특정 속성으로 필터링해 검색")
    @Disabled("MySQL에서만 동작함.")
    public void search_filter_attribute() {
        // given
        Map<String, String> attribute = new HashMap<>();
        attribute.put("attr1", "1");

        SearchCondition condition = SearchCondition
            .builder()
            .query("product")
            .attributeFilters(List.of(attribute))
            .build();

        PageRequest pageRequest = PageRequest.of(0, 10);
        // when
        Page<SearchProductDetailQueryResult> page = searchRepository.search(condition, pageRequest);
        List<SearchProductDetailQueryResult> content = page.getContent();

        // then
        System.out.println(content.get(0));
        assertThat(page.getTotalElements()).isEqualTo(9);
        assertThat(page.getNumberOfElements()).isEqualTo(9);
        assertThat(content.get(0).getName()).isEqualTo("product0");
        assertThat(content.get(0).getManufacturer()).isEqualTo("manufacturer0");
        assertThat(content.get(0).getAttribute()).containsAllEntriesOf(attribute);
    }

    @Test
    @DisplayName("가격으로 오름차순 정렬")
    public void search_sort_price_asc() {
        // given
        SearchCondition condition = SearchCondition
            .builder()
            .query("product")
            .build();

        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Order.asc("price")));
        // when
        Page<SearchProductDetailQueryResult> page = searchRepository.search(condition, pageRequest);
        List<SearchProductDetailQueryResult> content = page.getContent();

        // then
        assertThat(page.getTotalElements()).isEqualTo(pdN * productN);
        assertThat(page.getNumberOfElements()).isEqualTo(10);
        assertThat(content.get(0).getId()).isEqualTo(productDetails.get(0).getId());
        assertThat(content.get(0).getName()).isEqualTo("product0");
        assertThat(content.get(0).getManufacturer()).isEqualTo("manufacturer0");
        assertThat(content.get(0).getPrice()).isEqualTo(0);
    }

    @Test
    @DisplayName("가격으로 내림차순 정렬")
    public void search_sort_price_desc() {
        // given
        SearchCondition condition = SearchCondition
            .builder()
            .query("product")
            .build();

        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Order.desc("price")));
        // when
        Page<SearchProductDetailQueryResult> page = searchRepository.search(condition, pageRequest);
        List<SearchProductDetailQueryResult> content = page.getContent();

        // then
        ProductDetail productDetail = productDetails.get(productDetails.size() - 1);
        Product product = productDetail.getProduct();
        assertThat(page.getTotalElements()).isEqualTo(pdN * productN);
        assertThat(page.getNumberOfElements()).isEqualTo(10);
        assertThat(content.get(0).getName()).isEqualTo(product.getName());
        assertThat(content.get(0).getManufacturer()).isEqualTo(product.getManufacturer());
        assertThat(content.get(0).getPrice()).isEqualTo(productDetail.getPrice());
    }

    @Test
    @DisplayName("생성시간으로 내림차순 정렬")
    public void search_sort_createdAt_desc() {
        // given
        SearchCondition condition = SearchCondition
            .builder()
            .query("product")
            .build();

        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Order.desc("createdAt")));
        // when
        Page<SearchProductDetailQueryResult> page = searchRepository.search(condition, pageRequest);
        List<SearchProductDetailQueryResult> content = page.getContent();

        // then
        ProductDetail productDetail = productDetails.get(productDetails.size() - 1);
        Product product = productDetail.getProduct();
        assertThat(page.getTotalElements()).isEqualTo(pdN * productN);
        assertThat(page.getNumberOfElements()).isEqualTo(10);
        assertThat(content.get(0).getName()).isEqualTo(product.getName());
        assertThat(content.get(0).getManufacturer()).isEqualTo(product.getManufacturer());
        assertThat(content.get(0).getPrice()).isEqualTo(productDetail.getPrice());
    }

    @Test
    @DisplayName("이름으로 내림차순 정렬")
    public void search_sort_name_desc() {
        // given
        SearchCondition condition = SearchCondition
            .builder()
            .query("product")
            .build();

        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Order.desc("name")));
        // when
        Page<SearchProductDetailQueryResult> page = searchRepository.search(condition, pageRequest);
        List<SearchProductDetailQueryResult> content = page.getContent();

        // then
        assertThat(page.getTotalElements()).isEqualTo(pdN * productN);
        assertThat(page.getNumberOfElements()).isEqualTo(10);
        assertThat(content.get(0).getName()).isEqualTo("product9");
        assertThat(content.get(0).getManufacturer()).isEqualTo("manufacturer9");
    }
}
package jshop.web.acceptance;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jshop.core.config.P6SpyConfig;
import jshop.core.domain.category.entity.Category;
import jshop.core.domain.category.repository.CategoryRepository;
import jshop.core.domain.product.entity.Product;
import jshop.core.domain.product.entity.ProductDetail;
import jshop.core.domain.product.repository.ProductDetailRepository;
import jshop.core.domain.product.repository.ProductRepository;
import jshop.core.domain.product.repository.SearchRepository;
import jshop.common.test.BaseTestContainers;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.processing.SQL;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcPrint;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Slf4j
@EnableWebMvc
@AutoConfigureMockMvc(print = MockMvcPrint.NONE)
@SpringBootTest
@DisplayName("[통합 테스트] SearchController")
@Sql(statements = "ALTER TABLE product ADD FULLTEXT(name)", executionPhase = ExecutionPhase.BEFORE_TEST_CLASS)
class SearchControllerIntegrationBaseTest extends BaseTestContainers {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ProductDetailRepository productDetailRepository;

    @Autowired
    SearchRepository searchRepository;

    @PersistenceContext
    EntityManager em;

    List<Category> categories = new ArrayList<>();
    List<Product> products = new ArrayList<>();
    List<ProductDetail> productDetails = new ArrayList<>();

    int categoryN = 3;
    int productN = 18;
    int pdN = 3;

    @Qualifier("objectMapper")
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void init() {

        for (int i = 0; i < categoryN; i++) {
            categories.add(categoryRepository.save(
                Category
                    .builder()
                    .name("category " + i)
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
                .name("product " + i)
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

    @AfterEach
    public void afterEach() {
        productDetailRepository.deleteAll();
        productRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    @Test
    @DisplayName("정렬, 필터 없이 검색")
    public void search_noCondition() throws Exception {
        // when
        log.info("TX : {}", TransactionSynchronizationManager.isSynchronizationActive());
        log.info("TX : {}", TransactionSynchronizationManager.isCurrentTransactionReadOnly());
        log.info("TX : {}", TransactionSynchronizationManager.isActualTransactionActive());
        ResultActions perform = mockMvc.perform(get("/api/search?query=product"));

        // then
        perform.andExpect(status().isOk())
               .andExpect(jsonPath("$.data.totalCount").value(54))
               .andExpect(jsonPath("$.data.currentPage").value(0))
               .andExpect(jsonPath("$.data.products", hasSize(30)));

    }

    @Test
    @DisplayName("특정 제조사로 필터링해 검색")
    public void search_filter_manufacturer() throws Exception {
        // when
        ResultActions perform = mockMvc.perform(get("/api/search?query=product&manufacturer=manufacturer3"));

        // then
        perform.andExpect(status().isOk())
               .andExpect(jsonPath("$.data.totalCount").value(3))
               .andExpect(jsonPath("$.data.totalPages").value(1))
               .andExpect(jsonPath("$.data.currentPage").value(0))
               .andExpect(jsonPath("$.data.products", hasSize(3)))
               .andExpect(jsonPath("$.data.products[0].name").value("product 3"))
               .andExpect(jsonPath("$.data.products[0].manufacturer").value("manufacturer3"));
    }

    @Test
    @DisplayName("특정 카테고리로 필터링해 검색")
    public void search_filter_category() throws Exception {
        // given
        Category category = categories.get(2);

        // when
        ResultActions perform = mockMvc.perform(get("/api/search?query=product&categoryId={categoryId}",
            category.getId()));

        // then
        perform.andExpect(status().isOk())
               .andExpect(jsonPath("$.data.totalCount").value(18))
               .andExpect(jsonPath("$.data.totalPages").value(1))
               .andExpect(jsonPath("$.data.currentPage").value(0))
               .andExpect(jsonPath("$.data.products", hasSize(18)))
               .andExpect(jsonPath("$.data.products[0].name").value("product 2"))
               .andExpect(jsonPath("$.data.products[0].manufacturer").value("manufacturer2"))
               .andExpect(jsonPath("$.data.products[0].category").value(category.getName()));
    }

    @Test
    @DisplayName("특정 속성으로 필터링해 검색")
    public void search_filter_attribute() throws Exception {
        // given
        Map<String, String> attribute = new HashMap<>();
        attribute.put("attr1", "1");

        // when
        ResultActions perform = mockMvc.perform(
            get("/api/search?query=product&attributeFilters[0][attr1]=1&attributeFilters[1][attr2]=2"));

        // then
        perform.andExpect(status().isOk())
               .andExpect(jsonPath("$.data.totalCount").value(9))
               .andExpect(jsonPath("$.data.totalPages").value(1))
               .andExpect(jsonPath("$.data.currentPage").value(0))
               .andExpect(jsonPath("$.data.products", hasSize(9)))
               .andExpect(jsonPath("$.data.products[0].name").value("product 0"))
               .andExpect(jsonPath("$.data.products[0].manufacturer").value("manufacturer0"));
    }

    @Test
    @DisplayName("가격으로 오름차순 정렬")
    public void search_sort_price_asc() throws Exception {
        // when
        ResultActions perform = mockMvc.perform(get("/api/search?query=product&sort=price"));

        // then
        perform.andExpect(status().isOk())
               .andExpect(jsonPath("$.data.totalCount").value(54))
               .andExpect(jsonPath("$.data.totalPages").value(2))
               .andExpect(jsonPath("$.data.currentPage").value(0))
               .andExpect(jsonPath("$.data.products", hasSize(30)))
               .andExpect(jsonPath("$.data.products[0].name").value("product 0"))
               .andExpect(jsonPath("$.data.products[0].manufacturer").value("manufacturer0"))
               .andExpect(jsonPath("$.data.products[0].price").value(0));
    }

    @Test
    @DisplayName("가격으로 내림차순 정렬")
    public void search_sort_price_desc() throws Exception {
        // when
        ResultActions perform = mockMvc.perform(get("/api/search?query=product&sort=price,desc"));

        // then
        ProductDetail productDetail = productDetails.get(productDetails.size() - 1);
        Product product = productDetail.getProduct();

        perform.andExpect(status().isOk())
               .andExpect(jsonPath("$.data.totalCount").value(54))
               .andExpect(jsonPath("$.data.totalPages").value(2))
               .andExpect(jsonPath("$.data.currentPage").value(0))
               .andExpect(jsonPath("$.data.products", hasSize(30)))
               .andExpect(jsonPath("$.data.products[0].name").value(product.getName()))
               .andExpect(jsonPath("$.data.products[0].manufacturer").value(product.getManufacturer()))
               .andExpect(jsonPath("$.data.products[0].price").value(productDetail.getPrice()));

    }

    @Test
    @DisplayName("생성시간으로 내림차순 정렬")
    public void search_sort_createdAt_desc() throws Exception {
        // when
        ResultActions perform = mockMvc.perform(get("/api/search?query=product&sort=createdAt,desc"));

        // then
        ProductDetail productDetail = productDetails.get(productDetails.size() - 1);
        Product product = productDetail.getProduct();

        perform.andExpect(status().isOk())
               .andExpect(jsonPath("$.data.totalCount").value(54))
               .andExpect(jsonPath("$.data.totalPages").value(2))
               .andExpect(jsonPath("$.data.currentPage").value(0))
               .andExpect(jsonPath("$.data.products", hasSize(30)))
               .andExpect(jsonPath("$.data.products[0].name").value(product.getName()))
               .andExpect(jsonPath("$.data.products[0].manufacturer").value(product.getManufacturer()))
               .andExpect(jsonPath("$.data.products[0].price").value(productDetail.getPrice()));
    }

    @Test
    @DisplayName("이름으로 내림차순 정렬")
    public void search_sort_name_desc() throws Exception {
        // when
        ResultActions perform = mockMvc.perform(get("/api/search?query=product&sort=name,desc"));

        // then
        perform.andExpect(status().isOk())
               .andExpect(jsonPath("$.data.totalCount").value(54))
               .andExpect(jsonPath("$.data.totalPages").value(2))
               .andExpect(jsonPath("$.data.currentPage").value(0))
               .andExpect(jsonPath("$.data.products", hasSize(30)))
               .andExpect(jsonPath("$.data.products[0].name").value("product 9"))
               .andExpect(jsonPath("$.data.products[0].manufacturer").value("manufacturer9"));
    }
}

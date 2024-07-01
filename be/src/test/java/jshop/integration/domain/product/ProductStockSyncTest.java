package jshop.integration.domain.product;

import static org.junit.jupiter.api.Assertions.assertTimeout;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import jshop.domain.category.dto.CreateCategoryRequest;
import jshop.domain.category.service.CategoryService;
import jshop.domain.product.dto.CreateProductDetailRequest;
import jshop.domain.product.dto.CreateProductRequest;
import jshop.domain.product.entity.ProductDetail;
import jshop.domain.product.repository.ProductDetailRepository;
import jshop.domain.product.service.ProductService;
import jshop.domain.user.dto.JoinUserResponse;
import jshop.global.dto.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@AutoConfigureMockMvc
@SpringBootTest
@DisplayName("[통합 테스트] 상품 재고 동기화 테스트")
@Transactional
public class ProductStockSyncTest {

    private static Long sellerUserId;
    private static String sellerUserToken;
    private static Long categoryId;
    private static Long productId;
    private static Long productDetailId;

    @Autowired
    private ProductDetailRepository productDetailRepository;

    @Autowired
    private MockMvc mockMvc;

    @BeforeAll
    public static void init(@Autowired MockMvc mockMvc, @Autowired ObjectMapper objectMapper,
        @Autowired CategoryService categoryService, @Autowired ProductService productService) throws Exception {

        /**
         * 판매 유저 인증 과정
         */
        TypeReference<Response<JoinUserResponse>> typeReference = new TypeReference<Response<JoinUserResponse>>() {};
        String joinUser = """
            { "username" : "sync_test", "email" : "sync_test_user@email.com", "password" : "password2", "userType" : "SELLER"}""";

        String userLoginRequest = """
            { "email" : "sync_test_user@email.com", "password" : "password2" }""";

        ResultActions perform = mockMvc.perform(
            post("/api/join").contentType(MediaType.APPLICATION_JSON).content(joinUser));

        Response<JoinUserResponse> joinUserResponse2 = objectMapper.readValue(
            perform.andReturn().getResponse().getContentAsString(), typeReference);
        sellerUserId = joinUserResponse2.getData().getId();

        ResultActions login = mockMvc.perform(
            post("/api/login").contentType(MediaType.APPLICATION_JSON).content(userLoginRequest));
        sellerUserToken = login.andReturn().getResponse().getHeader("Authorization");

        /**
         * 기초 카테고리 생성
         */

        CreateCategoryRequest createCategoryRequest = CreateCategoryRequest
            .builder().name("전자제품2").build();

        categoryId = categoryService.createCategory(createCategoryRequest);

        Map<String, List<String>> attributes = new HashMap<>();
        attributes.put("attr1", List.of("a", "b", "c"));
        attributes.put("attr2", List.of("1", "2", "3"));

        Map<String, String> attribute = new HashMap<>();
        attribute.put("attr1", "a");
        attribute.put("attr2", "3");

        productId = productService.createProduct(CreateProductRequest
            .builder().name("product").categoryId(categoryId).attributes(attributes).build(), sellerUserId);

        productDetailId = productService.createProductDetail(CreateProductDetailRequest
            .builder().price(1000L).attribute(attribute).build(), productId);
    }

    @Test
    @DisplayName("동시에 여러번의 재고 변화 요청이 있더라도, 모두 반영되어야 함. (동시성 문제)")
    public void changeStock_sync() throws Exception {
        /**
         * InventoryRepository#findByIdWithNoLock 을 사용하면 문제가 생김
         */
        // given
        String updateProductDetailStockRequestSTr = """
            { "quantity" : 10 }
            """;

        ExecutorService executors = Executors.newFixedThreadPool(10);

        // when
        for (int i = 0; i < 10; i++) {
            executors.submit(() -> {
                try {
                    mockMvc.perform(
                        patch("/api/products/{product_id}/details/{detail_id}/stocks", productId, productDetailId)
                            .header("Authorization", sellerUserToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(updateProductDetailStockRequestSTr));
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            });
        }

        executors.shutdown();
        executors.awaitTermination(1L, TimeUnit.MINUTES);

        // then
        ProductDetail productDetail = productDetailRepository.findById(productDetailId).get();
        assertThat(productDetail.getInventory().getQuantity()).isEqualTo(100);
    }
}

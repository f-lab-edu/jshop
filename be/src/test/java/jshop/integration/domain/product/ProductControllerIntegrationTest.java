package jshop.integration.domain.product;

import static org.junit.jupiter.api.Assertions.assertTimeout;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import jshop.domain.category.dto.CreateCategoryRequest;
import jshop.domain.category.service.CategoryService;
import jshop.domain.inventory.service.InventoryService;
import jshop.domain.product.dto.CreateProductDetailRequest;
import jshop.domain.product.dto.CreateProductDetailResponse;
import jshop.domain.product.dto.CreateProductRequest;
import jshop.domain.product.dto.CreateProductResponse;
import jshop.domain.product.dto.OwnProductsResponse;
import jshop.domain.product.entity.Product;
import jshop.domain.product.entity.ProductDetail;
import jshop.domain.product.repository.ProductDetailRepository;
import jshop.domain.product.repository.ProductRepository;
import jshop.domain.product.service.ProductService;
import jshop.domain.user.dto.JoinUserResponse;
import jshop.global.common.ErrorCode;
import jshop.global.dto.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@AutoConfigureMockMvc
@SpringBootTest
@DisplayName("[통합 테스트] ProductController")
@Transactional
public class ProductControllerIntegrationTest {

    private static Long normalUserId;
    private static String normalUserToken;
    private static Long sellerUserId;
    private static String sellerUserToken;
    private static Long anotherSellerUserId;
    private static String anotherSellerUserToken;
    private static Long categoryId;


    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductDetailRepository productDetailRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @BeforeAll
    public static void init(@Autowired MockMvc mockMvc, @Autowired ObjectMapper objectMapper,
        @Autowired CategoryService categoryService) throws Exception {

        /**
         * 일반 유저 인증 과정
         */
        String joinUser1 = """
            { "username" : "username", "email" : "email@email.com", "password" : "password", "userType" : "USER"}""";

        String user1LoginRequest = """
            { "email" : "email@email.com", "password" : "password" }""";

        ResultActions perform = mockMvc.perform(
            post("/api/join").contentType(MediaType.APPLICATION_JSON).content(joinUser1));

        TypeReference<Response<JoinUserResponse>> typeReference = new TypeReference<Response<JoinUserResponse>>() {};
        Response<JoinUserResponse> joinUserResponse = objectMapper.readValue(
            perform.andReturn().getResponse().getContentAsString(), typeReference);
        normalUserId = joinUserResponse.getData().getId();

        ResultActions login = mockMvc.perform(
            post("/api/login").contentType(MediaType.APPLICATION_JSON).content(user1LoginRequest));
        normalUserToken = login.andReturn().getResponse().getHeader("Authorization");

        /**
         * 판매 유저 인증 과정
         */
        String joinUser2 = """
            { "username" : "username2", "email" : "email2@email.com", "password" : "password2", "userType" : "SELLER"}""";

        String user2LoginRequest = """
            { "email" : "email2@email.com", "password" : "password2" }""";

        ResultActions perform2 = mockMvc.perform(
            post("/api/join").contentType(MediaType.APPLICATION_JSON).content(joinUser2));

        Response<JoinUserResponse> joinUserResponse2 = objectMapper.readValue(
            perform2.andReturn().getResponse().getContentAsString(), typeReference);
        sellerUserId = joinUserResponse2.getData().getId();

        ResultActions login2 = mockMvc.perform(
            post("/api/login").contentType(MediaType.APPLICATION_JSON).content(user2LoginRequest));
        sellerUserToken = login2.andReturn().getResponse().getHeader("Authorization");

        /**
         * 테스트 판매 유저 인증 과정
         */
        String joinUser3 = """
            { "username" : "username3", "email" : "email3@email.com", "password" : "password3", "userType" : "SELLER"}""";

        String user3LoginRequest = """
            { "email" : "email3@email.com", "password" : "password3" }""";

        ResultActions perform3 = mockMvc.perform(
            post("/api/join").contentType(MediaType.APPLICATION_JSON).content(joinUser3));

        Response<JoinUserResponse> joinUserResponse3 = objectMapper.readValue(
            perform3.andReturn().getResponse().getContentAsString(), typeReference);
        anotherSellerUserId = joinUserResponse3.getData().getId();

        ResultActions login3 = mockMvc.perform(
            post("/api/login").contentType(MediaType.APPLICATION_JSON).content(user3LoginRequest));
        anotherSellerUserToken = login3.andReturn().getResponse().getHeader("Authorization");

        /**
         * 기초 카테고리 생성
         */

        CreateCategoryRequest createCategoryRequest = CreateCategoryRequest
            .builder().name("전자제품").build();

        categoryId = categoryService.createCategory(createCategoryRequest);
    }

    @Nested
    @DisplayName("상품 생성 검증, DB에 생성되는지까지 ")
    class CreateProduct {

        String createProductRequestFormat = """
            {
                "name" : "product",
                "categoryId" : %d,
                "manufacturer" : "apple",
                "description" : "description",
                "attributes" : {
                    "attr1" : ["a", "b", "c"],
                    "attr2" : ["1", "2", "3"]
                }
            }
            """;

        @Test
        @DisplayName("로그인한 판매 회원이 상품 생성시 DB에 반영되는지 테스트")
        public void createProduct_success() throws Exception {
            // given
            String createProductRequestStr = String.format(createProductRequestFormat, categoryId.longValue());

            // when
            ResultActions perform = mockMvc.perform(post("/api/products")
                .header("Authorization", sellerUserToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(createProductRequestStr));

            // then
            perform.andExpect(status().isOk());
            Long productId = getProductIdFromResultActions(perform);

            productRepository.findById(productId).ifPresentOrElse((product) -> {
                Map<String, List<String>> attributes = new HashMap<>();
                attributes.put("attr1", List.of("a", "b", "c"));
                attributes.put("attr2", List.of("1", "2", "3"));

                assertThat(product.getName()).isEqualTo("product");
                assertThat(product.getManufacturer()).isEqualTo("apple");
                assertThat(product.getCategory().getId()).isEqualTo(categoryId);
                assertThat(product.getAttributes()).isEqualTo(attributes);
            }, () -> {
                Assertions.fail();
            });
        }

        @Test
        @DisplayName("판매자가 아닌 회원이 상품 생성시 USER_NOT_SELLER 예외")
        public void createProduct_noSeller() throws Exception {
            // given
            String createProductRequestStr = String.format(createProductRequestFormat, categoryId.longValue());

            // when
            ResultActions perform = mockMvc.perform(post("/api/products")
                .header("Authorization", normalUserToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(createProductRequestStr));

            // then
            perform
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(ErrorCode.USER_NOT_SELLER.getCode()));
        }

        @Test
        @DisplayName("카테고리가 존재하지 않을시, CATEGORYID_NOT_FOUND")
        public void createProduct_noCategory() throws Exception {
            // given
            String createProductRequestStr = String.format(createProductRequestFormat, 99);

            // when
            ResultActions perform = mockMvc.perform(post("/api/products")
                .header("Authorization", sellerUserToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(createProductRequestStr));

            // then
            perform
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(ErrorCode.CATEGORYID_NOT_FOUND.getCode()));
        }
    }

    @Nested
    @DisplayName("자신이 등록한 상품 가져오기")
    class GetOwnProducts {

        private int totalCount = 30;

        @BeforeEach
        public void init(@Autowired ProductService productService) {
            for (int i = 0; i < totalCount; i++) {
                productService.createProduct(CreateProductRequest
                    .builder().name("product" + i).categoryId(categoryId).build(), sellerUserId);
            }
        }

        @Test
        @DisplayName("페이지 번호, 페이지 크기를 넘기면 데이터베이스에 들어있는 자신의 상품을 가져올 수 있다.")
        public void getOwnProducts_success() throws Exception {
            // given
            int pageNumber = 0;
            int pageSize = 10;
            int totalCount = 30;

            // when
            ResultActions perform = mockMvc.perform(
                get("/api/products?page=" + pageNumber + "&size=" + pageSize).header("Authorization", sellerUserToken));

            OwnProductsResponse ownProductsResponse = getOwnProductsResponseFromResultActions(perform);
            // then

            assertThat(ownProductsResponse.getPage()).isEqualTo(pageNumber);
            assertThat(ownProductsResponse.getTotalCount()).isEqualTo(totalCount);
            assertThat(ownProductsResponse.getTotalPage()).isEqualTo((int) totalCount / pageSize);
        }

        @Test
        @DisplayName("페이지 번호, 페이지 크기를 넘기지 않는다면 기본값이 사용된다. 데이터베이스에 들어있는 자신의 상품을 가져올 수 있다.")
        public void getOwnProducts_useDefault() throws Exception {
            // given
            int pageNumber = 0;
            int pageSize = 10;
            int totalCount = 30;

            // when
            ResultActions perform = mockMvc.perform(get("/api/products").header("Authorization", sellerUserToken));
            OwnProductsResponse ownProductsResponse = getOwnProductsResponseFromResultActions(perform);

            // then
            assertThat(ownProductsResponse.getPage()).isEqualTo(pageNumber);
            assertThat(ownProductsResponse.getTotalCount()).isEqualTo(totalCount);
            assertThat(ownProductsResponse.getTotalPage()).isEqualTo(totalCount / pageSize);
        }

        @Test
        @DisplayName("유저가 가진 상품이 없다면, 빈배열, 기본값(0) 으로 제공")
        public void getOwnProducts_hasNoProduct() throws Exception {
            // when
            ResultActions perform = mockMvc.perform(get("/api/products").header("Authorization", normalUserToken));
            OwnProductsResponse ownProductsResponse = getOwnProductsResponseFromResultActions(perform);

            // then
            assertThat(ownProductsResponse.getPage()).isEqualTo(0);
            assertThat(ownProductsResponse.getTotalCount()).isEqualTo(0);
            assertThat(ownProductsResponse.getTotalPage()).isEqualTo(0);
        }

        @Test
        @DisplayName("페이지 범위가 요청 범위를 벗어나면 ILLEGAL_PAGE_REQUEST 예외 (pageSize > 100 || pageSize < 0 || pageNumber < 0)")
        public void getOwnProducts_illegalRange() throws Exception {
            // when
            ResultActions perform = mockMvc.perform(
                get("/api/products?page=0&size=-1").header("Authorization", sellerUserToken));

            // then
            perform
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(ErrorCode.ILLEGAL_PAGE_REQUEST.getCode()));
        }

        @Test
        @DisplayName("권한 없는 유저가 요청하면 (토큰이 없다면) Forbidden 예외")
        public void getOwnProducts_noAuth() throws Exception {
            // when
            ResultActions perform = mockMvc.perform(get("/api/products?page=0&size=-1"));

            // then
            perform.andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("상세 상품 생성하기")
    class CreateProductDetail {

        private Long productId;

        String createProductDetailRequestStr = """
            { 
                "price" : 1000, 
                "attribute" : { 
                    "attr1" :"a", 
                    "attr2" : "3"
                } 
            }
            """;

        String invalidCreateProductDetailRequestStr = """
            { 
                "price" : 1000, 
                "attribute" : { 
                    "attr1" :"d", 
                    "attr2" : "4"
                } 
            }
            """;

        @BeforeEach
        public void init(@Autowired ProductService productService) {
            Map<String, List<String>> attributes = new HashMap<>();
            attributes.put("attr1", List.of("a", "b", "c"));
            attributes.put("attr2", List.of("1", "2", "3"));

            productId = productService.createProduct(CreateProductRequest
                .builder().name("product").categoryId(categoryId).attributes(attributes).build(), sellerUserId);
        }

        @Test
        @DisplayName("로그인한 회원은 상세 상품을 생성할 수 있다.")
        public void createProductDetail_success() throws Exception {
            // when
            ResultActions perform = mockMvc.perform(post("/api/products/{product_id}/details", productId)
                .header("Authorization", sellerUserToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(createProductDetailRequestStr));

            // then
            perform.andExpect(status().isOk());
            Long productDetailId = getProductDetailIdFromResultActions(perform);

            productDetailRepository.findById(productDetailId).ifPresentOrElse(productDetail -> {
                Map<String, String> attribute = new HashMap<>();
                attribute.put("attr1", "a");
                attribute.put("attr2", "3");

                assertThat(productDetail.getPrice()).isEqualTo(1000);
                assertThat(productDetail.getAttribute()).isEqualTo(attribute);
            }, () -> {
                Assertions.fail();
            });
        }

        @Test
        @DisplayName("인증안된 회원은 상품을 생성할 수 없다.")
        public void createProductDetail_noAuth() throws Exception {
            // when
            ResultActions perform = mockMvc.perform(post("/api/products/{product_id}/details", productId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(createProductDetailRequestStr));

            // then
            perform.andExpect(status().isForbidden());
        }


        @Test
        @DisplayName("로그인한 회원이더라도, 자신의 상품이 아니라면 밑에 상세 상품을 생성할 수 없다.")
        public void createProductDetail_noOwnership() throws Exception {
            // when
            ResultActions perform = mockMvc.perform(post("/api/products/{product_id}/details", productId)
                .header("Authorization", anotherSellerUserToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(createProductDetailRequestStr));

            // then
            perform.andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("상세 상품의 속성이 상품에 정의된 속성에 속하지 않는다면 생성할 수 없다.")
        public void createProductDetail_invalid_attribute() throws Exception {
            // when
            ResultActions perform = mockMvc.perform(post("/api/products/{product_id}/details", productId)
                .header("Authorization", sellerUserToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidCreateProductDetailRequestStr));

            // then
            perform.andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("동일 상품 아래에, 이미 같은 속성의 상세 상품이 있다면 생성할 수 없다.")
        public void createProductDetail_dup_attribute() throws Exception {
            // when
            ResultActions perform = mockMvc.perform(post("/api/products/{product_id}/details", productId)
                .header("Authorization", sellerUserToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(createProductDetailRequestStr));

            ResultActions perform2 = mockMvc.perform(post("/api/products/{product_id}/details", productId)
                .header("Authorization", sellerUserToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(createProductDetailRequestStr));

            // then

            perform.andExpect(status().isOk());
            perform2
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(ErrorCode.ALREADY_EXISTS_PRODUCT_DETAIL.getCode()));
        }
    }

    @Nested
    @DisplayName("자신이 등록한 상세 상품 변경하기")
    class UpdateProductDetail {

        private Long productId;
        private Long productDetailId;

        @BeforeEach
        public void init(@Autowired ProductService productService) {
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
        @DisplayName("로그인한 사용자는 자신의 상세 상품의 가격을 변경할 수 있다.")
        public void updateProductDetail() throws Exception {
            // given
            String updateProductDetailRequestStr = """
                { "price" : 2000 }
                """;

            // when
            ResultActions perform = mockMvc.perform(
                put("/api/products/{product_id}/details/{detail_id}", productId, productDetailId)
                    .header("Authorization", sellerUserToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(updateProductDetailRequestStr));
            // then
            perform.andExpect(status().isOk());
            ProductDetail productDetail = productDetailRepository.findById(productDetailId).get();
            assertThat(productDetail.getPrice()).isEqualTo(2000);
        }

        @Test
        @DisplayName("패스 파라미터의 상품ID에 상세 상품ID의 속하지 않는다면 예외를 던짐")
        public void updateProductDetail_invalidId() throws Exception {
            // given
            String updateProductDetailRequestStr = """
                { "price" : 2000 }
                """;

            // when
            ResultActions perform = mockMvc.perform(
                put("/api/products/{product_id}/details/{detail_id}", 99L, productDetailId)
                    .header("Authorization", sellerUserToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(updateProductDetailRequestStr));
            // then
            perform.andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("상세 상품(상품) 의 소유주가 아니라면 UNAUTHORIZED")
        public void updateProductDetail_noOwnership() throws Exception {
            // given
            String updateProductDetailRequestStr = """
                { "price" : 2000 }
                """;

            // when
            ResultActions perform = mockMvc.perform(
                put("/api/products/{product_id}/details/{detail_id}", productId, productDetailId)
                    .header("Authorization", anotherSellerUserToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(updateProductDetailRequestStr));
            // then
            perform
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorCode").value(ErrorCode.UNAUTHORIZED.getCode()));
        }
    }

    @Nested
    @DisplayName("자신의 상품의 재고수량을 변경할 수 있다.")
    class UpdateProductDetailStock {

        private Long productId;
        private Long productDetailId;

        @BeforeEach
        public void init(@Autowired ProductService productService) {
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
        @DisplayName("로그인한 유저는 자신의 상세 상품의 재고를 추가할 수 있다.")
        public void addStock_success() throws Exception {
            // given
            String updateProductDetailStockRequestSTr = """
                { "quantity" : 10 }
                """;

            // when
            ResultActions perform = mockMvc.perform(
                patch("/api/products/{product_id}/details/{detail_id}/stocks", productId, productDetailId)
                    .header("Authorization", sellerUserToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(updateProductDetailStockRequestSTr));
            // then
            perform.andExpect(status().isOk());
            ProductDetail productDetail = productDetailRepository.findById(productDetailId).get();
            assertThat(productDetail.getInventory().getQuantity()).isEqualTo(10);
        }

        @Test
        @DisplayName("로그인한 유저는 자신의 상세 상품의 재고를 감소할 수 있다. 이때 감소한 재고량이 0보다 커야한다.")
        public void removeStock_success() throws Exception {
            // given
            String updateProductDetailStockRequestSTr = """
                { "quantity" : 10 }
                """;

            mockMvc.perform(patch("/api/products/{product_id}/details/{detail_id}/stocks", productId, productDetailId)
                .header("Authorization", sellerUserToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateProductDetailStockRequestSTr));

            String updateProductDetailStockRequestSTr2 = """
                { "quantity" : -5 }
                """;

            // when
            ResultActions perform2 = mockMvc.perform(
                patch("/api/products/{product_id}/details/{detail_id}/stocks", productId, productDetailId)
                    .header("Authorization", sellerUserToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(updateProductDetailStockRequestSTr2));
            // then
            perform2.andExpect(status().isOk());
            ProductDetail productDetail = productDetailRepository.findById(productDetailId).get();
            assertThat(productDetail.getInventory().getQuantity()).isEqualTo(5);
        }

        @Test
        @DisplayName("감소한 재고량이 0보다 작다면 반영하지 않고 예외를 던진다.")
        public void removeStock_minzero() throws Exception {
            // given
            String updateProductDetailStockRequestSTr = """
                { "quantity" : 10 }
                """;

            mockMvc.perform(patch("/api/products/{product_id}/details/{detail_id}/stocks", productId, productDetailId)
                .header("Authorization", sellerUserToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateProductDetailStockRequestSTr));

            String updateProductDetailStockRequestSTr2 = """
                { "quantity" : -11 }
                """;

            // when
            ResultActions perform2 = mockMvc.perform(
                patch("/api/products/{product_id}/details/{detail_id}/stocks", productId, productDetailId)
                    .header("Authorization", sellerUserToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(updateProductDetailStockRequestSTr2));
            // then
            perform2
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(ErrorCode.NEGATIVE_QUANTITY_EXCEPTION.getCode()));
            ProductDetail productDetail = productDetailRepository.findById(productDetailId).get();
            assertThat(productDetail.getInventory().getQuantity()).isEqualTo(10);
        }
    }

    @Nested
    @DisplayName("자신의 상품을 삭제할 수 있다. (소프트 삭제)")
    class DeleteProductDetail {

        private Long productId;
        private Long productDetailId;

        @BeforeEach
        public void init(@Autowired ProductService productService) {
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
        @DisplayName("로그인한 사용자는 자신의 상세 상품을 삭제할 수 있다.")
        public void deleteProductDetail_success() throws Exception {
            // when
            ResultActions perform = mockMvc.perform(
                delete("/api/products/{product_id}/details/{detail_id}", productId, productDetailId).header(
                    "Authorization", sellerUserToken));

            // then
            perform.andExpect(status().isOk());
            productDetailRepository.findById(productDetailId).ifPresentOrElse(productDetail -> {
                assertThat(productDetail.getIsDeleted()).isTrue();
            }, () -> {
                Assertions.fail();
            });
        }

        @Test
        @DisplayName("로그인한 유저라도 자신의 상세 상품(상품) 이 아니라면 삭제할 수 없다.")
        public void deleteProductDetail_noOwnership() throws Exception {
            // when
            ResultActions perform = mockMvc.perform(
                delete("/api/products/{product_id}/details/{detail_id}", productId, productDetailId).header(
                    "Authorization", anotherSellerUserToken));
            
            // then
            perform.andExpect(status().isUnauthorized());
        }
    }

    private Long getProductIdFromResultActions(ResultActions perform)
        throws JsonProcessingException, UnsupportedEncodingException {
        TypeReference<Response<CreateProductResponse>> typeReference = new TypeReference<Response<CreateProductResponse>>() {};
        Response<CreateProductResponse> createProductResponse = objectMapper.readValue(
            perform.andReturn().getResponse().getContentAsString(), typeReference);
        return createProductResponse.getData().getId();
    }

    private Long getProductDetailIdFromResultActions(ResultActions perform)
        throws JsonProcessingException, UnsupportedEncodingException {
        TypeReference<Response<CreateProductDetailResponse>> typeReference = new TypeReference<Response<CreateProductDetailResponse>>() {};
        Response<CreateProductDetailResponse> createProductDetailResponse = objectMapper.readValue(
            perform.andReturn().getResponse().getContentAsString(), typeReference);
        return createProductDetailResponse.getData().getId();
    }

    private OwnProductsResponse getOwnProductsResponseFromResultActions(ResultActions perform)
        throws JsonProcessingException, UnsupportedEncodingException {
        TypeReference<Response<OwnProductsResponse>> typeReference = new TypeReference<Response<OwnProductsResponse>>() {};
        Response<OwnProductsResponse> joinUserResponse = objectMapper.readValue(
            perform.andReturn().getResponse().getContentAsString(), typeReference);
        return joinUserResponse.getData();
    }
}

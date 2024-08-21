package jshop.web.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import jshop.core.domain.cart.entity.CartProductDetail;
import jshop.core.domain.cart.repository.CartProductDetailRepository;
import jshop.core.domain.cart.repository.CartRepository;
import jshop.core.domain.product.dto.CreateProductDetailRequest;
import jshop.core.domain.product.entity.Product;
import jshop.core.domain.product.repository.ProductRepository;
import jshop.core.domain.product.service.ProductService;
import jshop.core.domain.user.dto.JoinUserResponse;
import jshop.core.domain.user.entity.User;
import jshop.core.domain.user.repository.UserRepository;
import jshop.common.exception.ErrorCode;
import jshop.web.dto.Response;
import jshop.common.test.BaseTestContainers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcPrint;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@AutoConfigureMockMvc(print = MockMvcPrint.NONE)
@SpringBootTest
@DisplayName("[통합 테스트] CartController")
@Transactional
public class CartControllerIntegrationTest extends BaseTestContainers {

    @Autowired
    private CartProductDetailRepository cartProductDetailRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;

    @PersistenceContext
    private EntityManager em;

    private Long userId;
    private String userToken;
    private Long anotherUserId;
    private String anotherUserToken;

    private Long productId;
    private final List<Long> productIds = new ArrayList<>();
    private final List<Long> productDetailIds = new ArrayList<>();
    @Autowired
    private ProductService productService;


    @BeforeEach
    public void init() throws Exception {
        /**
         * 일반 유저 생성
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
        userId = joinUserResponse.getData().getId();

        ResultActions login = mockMvc.perform(
            post("/api/login").contentType(MediaType.APPLICATION_JSON).content(user1LoginRequest));
        userToken = login.andReturn().getResponse().getHeader("Authorization");

        /**
         * 테스트용 유저 생성
         */
        String joinUser3 = """
            { "username" : "username3", "email" : "email3@email.com", "password" : "password3", "userType" : "SELLER"}""";

        String user3LoginRequest = """
            { "email" : "email3@email.com", "password" : "password3" }""";

        ResultActions perform3 = mockMvc.perform(
            post("/api/join").contentType(MediaType.APPLICATION_JSON).content(joinUser3));

        Response<JoinUserResponse> joinUserResponse3 = objectMapper.readValue(
            perform3.andReturn().getResponse().getContentAsString(), typeReference);
        anotherUserId = joinUserResponse3.getData().getId();

        ResultActions login3 = mockMvc.perform(
            post("/api/login").contentType(MediaType.APPLICATION_JSON).content(user3LoginRequest));
        anotherUserToken = login3.andReturn().getResponse().getHeader("Authorization");

        /**
         *  초기 상품 생성
         */

        User owner = userRepository.getReferenceById(userId);

        for (int i = 0; i < 50; i++) {
            Product product = Product
                .builder().name("product" + i).description("상세 정보").manufacturer("제조사").owner(owner).build();
            productRepository.save(product);
            productIds.add(product.getId());
        }

        for (int i = 0; i < 50; i++) {
            CreateProductDetailRequest createProductDetailRequest = CreateProductDetailRequest
                .builder().price(1000L + i).build();

            Long productDetailId = productService.createProductDetail(createProductDetailRequest, productIds.get(i));
            productDetailIds.add(productDetailId);
        }
    }

    @Nested
    @DisplayName("장바구니 정보 가져오기 검증")
    class GetCartList {

        String addCartRequest = """
            { "productDetailId" : %d, "quantity" : 1}
            """;

        Long cartProductDetailId;

        @BeforeEach
        public void init() throws Exception {
            for (Long productDetailId : productDetailIds) {
                mockMvc.perform(post("/api/cart")
                    .header("Authorization", userToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(String.format(addCartRequest, productDetailId)));
            }

            em.flush();
            em.clear();

            User user = userRepository.findById(userId).get();

            List<CartProductDetail> products = user.getCart().getCartProductDetails();
            assertThat(products.size()).isEqualTo(50);

            cartProductDetailId = products.get(0).getId();
        }

        @Test
        @DisplayName("인증된 유저는 자신의 장바구니 정보를 가져올 수 있다")
        public void getCart_success() throws Exception {
            // when
            ResultActions perform = mockMvc.perform(get("/api/cart?page=1&size=5").header("Authorization", userToken));

            // then
            perform
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.products[0].manufacturer").value("제조사"))
                .andExpect(jsonPath("$.data.products[0].price").value(1044))
                .andExpect(jsonPath("$.data.products[0].quantity").value(1))
                .andExpect(jsonPath("$.data.page").value(1))
                .andExpect(jsonPath("$.data.totalPage").value(10))
                .andExpect(jsonPath("$.data.totalCount").value(50));
        }

        @Test
        @DisplayName("페이지 정보를 주지 않는다면 기본 값이 사용된다.")
        public void getCart_default() throws Exception {
            // when
            ResultActions perform = mockMvc.perform(get("/api/cart").header("Authorization", userToken));

            // then
            perform
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.products[0].manufacturer").value("제조사"))
                .andExpect(jsonPath("$.data.products[0].price").value(1049))
                .andExpect(jsonPath("$.data.products[0].quantity").value(1))
                .andExpect(jsonPath("$.data.page").value(0))
                .andExpect(jsonPath("$.data.totalPage").value(2))
                .andExpect(jsonPath("$.data.totalCount").value(50));
        }

        @Test
        @DisplayName("인증이 안된 요청이 오면 Forbidden 을 내린다")
        public void getCart_noAuth() throws Exception {
            // when
            ResultActions perform = mockMvc.perform(get("/api/cart"));

            // then
            perform.andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("장바구니 추가")
    class AddCart {

        String addCartRequest = """
            { "productDetailId" : %d, "quantity" : 1}
            """;

        @Test
        @DisplayName("로그인한 유저는 자신의 장바구니에 상품을 담을 수 있다.")
        public void addCart_success() throws Exception {
            // given
            mockMvc.perform(post("/api/cart")
                .header("Authorization", userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format(addCartRequest, productDetailIds.get(0))));
            em.flush();
            em.clear();

            // when
            User user = userRepository.findById(userId).get();
            List<CartProductDetail> list = cartRepository
                .findById(user.getCart().getId())
                .get()
                .getCartProductDetails();

            // then
            assertThat(list.size()).isEqualTo(1);
            assertThat(list.get(0).getQuantity()).isEqualTo(1);
            assertThat(list.get(0).getProductDetail().getId()).isEqualTo(productDetailIds.get(0));
            assertThat(list.get(0).getCart()).isEqualTo(user.getCart());
        }

        @Test
        @DisplayName("기존에 장바구니에 들어있던 상품을 다시 추가하면, 상품의 수량이 늘어남.")
        public void addCart_addStcok() throws Exception {
            // given
            mockMvc.perform(post("/api/cart")
                .header("Authorization", userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format(addCartRequest, productDetailIds.get(0))));

            em.flush();
            em.clear();

            User user = userRepository.findById(userId).get();
            List<CartProductDetail> list = cartRepository
                .findById(user.getCart().getId())
                .get()
                .getCartProductDetails();

            assertThat(list.size()).isEqualTo(1);
            assertThat(list.get(0).getQuantity()).isEqualTo(1);
            assertThat(list.get(0).getProductDetail().getId()).isEqualTo(productDetailIds.get(0));
            assertThat(list.get(0).getCart()).isEqualTo(user.getCart());

            // when
            mockMvc.perform(post("/api/cart")
                .header("Authorization", userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format(addCartRequest, productDetailIds.get(0))));

            em.flush();
            em.clear();

            // then
            user = userRepository.findById(userId).get();
            list = cartRepository.findById(user.getCart().getId()).get().getCartProductDetails();

            assertThat(list.size()).isEqualTo(1);
            assertThat(list.get(0).getQuantity()).isEqualTo(2);
            assertThat(list.get(0).getProductDetail().getId()).isEqualTo(productDetailIds.get(0));
            assertThat(list.get(0).getCart()).isEqualTo(user.getCart());
        }

        @Test
        @DisplayName("잘못된 상세 상품 ID를 장바구니에 추가하면 PRODUCTDETAIL_ID_NOT_FOUND 발생")
        public void addCart_noSuchProduct() throws Exception {
            // when
            ResultActions perform = mockMvc.perform(post("/api/cart")
                .header("Authorization", userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format(addCartRequest, 99L)));

            // then

            perform
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(ErrorCode.PRODUCTDETAIL_ID_NOT_FOUND.getCode()));
        }

        @Test
        @DisplayName("인증안된 유저가 요청한다면 Forbidden 발생")
        public void addCart_noAuth() throws Exception {
            // when
            ResultActions perform = mockMvc.perform(
                post("/api/cart").contentType(MediaType.APPLICATION_JSON).content(String.format(addCartRequest, 1L)));

            // then
            perform.andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("장바구니 삭제")
    class DeleteCart {

        String addCartRequest = """
            { "productDetailId" : %d, "quantity" : 1}
            """;

        Long cartProductDetailId;

        @BeforeEach
        public void init() throws Exception {
            mockMvc.perform(post("/api/cart")
                .header("Authorization", userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format(addCartRequest, productDetailIds.get(0))));
            em.flush();
            em.clear();

            User user = userRepository.findById(userId).get();

            List<CartProductDetail> products = user.getCart().getCartProductDetails();
            assertThat(products.size()).isEqualTo(1);

            cartProductDetailId = products.get(0).getId();
        }

        @Test
        @DisplayName("로그인한 유저는 자신의 장바구니에서 장바구니_상품_ID로 삭제할 수 있다.")
        public void deleteCart_success() throws Exception {
            // when
            mockMvc.perform(
                delete("/api/cart/{cartProductDetailId}", cartProductDetailId).header("Authorization", userToken));
            em.flush();
            em.clear();

            // then
            User user = userRepository.findById(userId).get();

            List<CartProductDetail> products = user.getCart().getCartProductDetails();
            assertThat(products.size()).isEqualTo(0);
        }

        @Test
        @DisplayName("인증이 안된 요청은 Forbidden을 던진다")
        public void deleteCart_noAuth() throws Exception {
            // when
            ResultActions perform = mockMvc.perform(delete("/api/cart/{cartProductDetailId}", cartProductDetailId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format(addCartRequest, productDetailIds.get(0))));

            // then
            perform.andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("삭제하려는 아이템이 자신의 장바구니에 속한게 아니라면 Forbidden을 던진다")
        public void deleteCart_noOwnership() throws Exception {
            // when
            ResultActions perform = mockMvc.perform(delete("/api/cart/{cartProductDetailId}", cartProductDetailId)
                .header("Authorization", anotherUserToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format(addCartRequest, productDetailIds.get(0))));

            // then
            perform.andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("삭제하려는 아이템이 없다면 Bad Request 예외를 던진다.")
        public void deleteCart_noSuchProducts() throws Exception {
            // when
            ResultActions perform = mockMvc.perform(delete("/api/cart/{cartProductDetailId}", 99L)
                .header("Authorization", userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format(addCartRequest, productDetailIds.get(0))));

            // then
            perform
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(ErrorCode.CART_PRODUCTDETAIL_ID_NOT_FOUND.getCode()));
        }
    }

    @Nested
    @DisplayName("장바구니 수량 변경")
    class UpdateCart {

        String addCartRequest = """
            { "productDetailId" : %d, "quantity" : 1}
            """;

        String updateCartRequest = """
            { "productDetailId" : %d, "quantity" : %d}
            """;

        Long cartProductDetailId;

        @BeforeEach
        public void init() throws Exception {
            mockMvc.perform(post("/api/cart")
                .header("Authorization", userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format(addCartRequest, productDetailIds.get(0))));
            em.flush();
            em.clear();

            User user = userRepository.findById(userId).get();

            List<CartProductDetail> products = user.getCart().getCartProductDetails();
            assertThat(products.size()).isEqualTo(1);

            cartProductDetailId = products.get(0).getId();
        }

        @Test
        @DisplayName("로그인한 사용자는 변경 이후 수량이 1 이상이라면 자신의 장바구니의 수량을 변경할 수 있다.")
        public void updateCart_success() throws Exception {
            // when
            mockMvc.perform(put("/api/cart/{id}", cartProductDetailId)
                .header("Authorization", userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format(updateCartRequest, productDetailIds.get(0), 3)));

            // then
            User user = userRepository.findById(userId).get();

            List<CartProductDetail> products = user.getCart().getCartProductDetails();
            assertThat(products.get(0).getQuantity()).isEqualTo(4);
        }

        @Test
        @DisplayName("로그인하지 않은 요청에는 forbidden 예외를 던진다.")
        public void updateCart_noAuth() throws Exception {
            // when
            ResultActions perform = mockMvc.perform(put("/api/cart/{id}", cartProductDetailId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format(updateCartRequest, productDetailIds.get(0), 3)));

            // then
            perform.andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("변경 이후 수량이 1보다 작다면 ILLEGAL_CART_QUANTITY_REQUEST_EXCEPTION 예외를 던진다.")
        public void updateCart_lessthen1() throws Exception {
            // when
            ResultActions perform = mockMvc.perform(put("/api/cart/{id}", cartProductDetailId)
                .header("Authorization", userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format(updateCartRequest, productDetailIds.get(0), -2)));

            // then
            perform
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(ErrorCode.ILLEGAL_CART_QUANTITY_REQUEST_EXCEPTION.getCode()));
        }
    }
}

package jshop.integration.domain.cart;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import jshop.domain.cart.entity.CartProductDetail;
import jshop.domain.cart.repository.CartProductDetailRepository;
import jshop.domain.cart.repository.CartRepository;
import jshop.domain.product.dto.CreateProductDetailRequest;
import jshop.domain.product.entity.Product;
import jshop.domain.product.repository.ProductDetailRepository;
import jshop.domain.product.repository.ProductRepository;
import jshop.domain.product.service.ProductService;
import jshop.domain.user.dto.JoinUserResponse;
import jshop.domain.user.entity.User;
import jshop.domain.user.repository.UserRepository;
import jshop.global.common.ErrorCode;
import jshop.global.dto.Response;
import org.junit.jupiter.api.BeforeEach;
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
@DisplayName("[통합 테스트] CartController")
@Transactional
public class CartControllerIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductDetailRepository productDetailRepository;

    @Autowired
    private CartProductDetailRepository cartProductDetailRepository;

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
    private List<Long> productDetailIds = new ArrayList<>();
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
        Product product = Product
            .builder().name("product").owner(owner).build();
        productRepository.save(product);
        productId = product.getId();

        for (int i = 0; i < 10; i++) {
            CreateProductDetailRequest createProductDetailRequest = CreateProductDetailRequest
                .builder().price(1000L).build();

            Long productDetailId = productService.createProductDetail(createProductDetailRequest, productId);
            productDetailIds.add(productDetailId);
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
            mockMvc.perform(delete("/api/cart/{cartProductDetailId}", cartProductDetailId)
                .header("Authorization", userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format(addCartRequest, productDetailIds.get(0))));
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
}

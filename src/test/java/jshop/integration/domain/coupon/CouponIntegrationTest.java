package jshop.integration.domain.coupon;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import jshop.domain.cart.repository.CartProductDetailRepository;
import jshop.domain.cart.repository.CartRepository;
import jshop.domain.category.repository.CategoryRepository;
import jshop.domain.coupon.dto.CreateCouponRequest;
import jshop.domain.coupon.entity.Coupon;
import jshop.domain.coupon.entity.CouponType;
import jshop.domain.coupon.entity.UserCoupon;
import jshop.domain.coupon.repository.CouponRepository;
import jshop.domain.coupon.repository.UserCouponRepository;
import jshop.domain.coupon.service.CouponService;
import jshop.domain.product.dto.CreateProductDetailRequest;
import jshop.domain.product.entity.Product;
import jshop.domain.product.repository.ProductDetailRepository;
import jshop.domain.product.repository.ProductRepository;
import jshop.domain.product.service.ProductService;
import jshop.domain.user.dto.JoinUserResponse;
import jshop.domain.user.entity.User;
import jshop.domain.user.repository.UserRepository;
import jshop.global.dto.Response;
import jshop.global.utils.UUIDUtils;
import jshop.utils.command.DeleteDBUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
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
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
@EnableWebMvc
@AutoConfigureMockMvc
@SpringBootTest
@DisplayName("[통합 테스트] CouponController")
public class CouponIntegrationTest {

    @Autowired
    private UserCouponRepository userCouponRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private DeleteDBUtils deleteDBUtils;

    @Autowired
    private CouponService couponService;

    @PersistenceContext
    private EntityManager em;


    private Long userId;
    private String userToken;
    private Long adminId;
    private String adminToken;
    private Long anotherUserId;
    private String anotherUserToken;

    private Long productId;
    private List<Long> productDetailIds = new ArrayList<>();
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductDetailRepository productDetailRepository;
    @Autowired
    private CategoryRepository categoryRepository;


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
         * 어드민 로그인
         */
        String loginStr = """
            {"email" : "admin@admin.com", "password" : "admin"}
            """;
        ResultActions loginAdmin = mockMvc.perform(
            post("/api/login").contentType(MediaType.APPLICATION_JSON).content(loginStr));
        adminToken = loginAdmin.andReturn().getResponse().getHeader("Authorization");

        /**
         *  초기 상품 생성
         */

        User owner = userRepository.getReferenceById(userId);
        Product product = Product
            .builder().name("product").description("상세 정보").manufacturer("제조사").owner(owner).build();
        productRepository.save(product);
        productId = product.getId();

        CreateProductDetailRequest createProductDetailRequest = CreateProductDetailRequest
            .builder().price(1000L).build();

        Long productDetailId = productService.createProductDetail(createProductDetailRequest, productId);
        productDetailIds.add(productDetailId);
    }


    @Nested
    @DisplayName("쿠폰 생성 검증")
    class CreateCoupon {

        @Test
        @DisplayName("admin 은 쿠폰을 생성할 수 있다")
        public void createCoupon_admin() throws Exception {
            // given
            String uuid = UUIDUtils.generateB64UUID();
            CreateCouponRequest createCouponRequest = CreateCouponRequest
                .builder()
                .id(uuid)
                .name("test")
                .quantity(10L)
                .coupontType(CouponType.FIXED_RATE)
                .value1(10L)
                .build();

            String s = objectMapper.writeValueAsString(createCouponRequest);

            // when
            ResultActions perform = mockMvc.perform(post("/api/coupon")
                .header("Authorization", adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(s));

            // then
            perform.andExpect(status().isOk());
        }

        @Test
        @DisplayName("일반 유저는 쿠폰을 생성할 수 없다")
        public void createCoupon_normal_user() throws Exception {
            // given
            String uuid = UUIDUtils.generateB64UUID();
            CreateCouponRequest createCouponRequest = CreateCouponRequest
                .builder()
                .id(uuid)
                .name("test")
                .quantity(10L)
                .coupontType(CouponType.FIXED_RATE)
                .value1(10L)
                .build();

            String s = objectMapper.writeValueAsString(createCouponRequest);

            // when
            ResultActions perform = mockMvc.perform(post("/api/coupon")
                .header("Authorization", userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(s));

            // then
            perform.andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("쿠폰 발급 검증")
    class IssueCoupon {

        private String couponId;

        @BeforeEach
        public void init() throws Exception {
            String uuid = UUIDUtils.generateB64UUID();
            CreateCouponRequest createCouponRequest = CreateCouponRequest
                .builder()
                .id(uuid)
                .name("test")
                .quantity(10L)
                .coupontType(CouponType.FIXED_RATE)
                .value1(10L)
                .build();

            couponId = couponService.createCoupon(createCouponRequest);
        }

        @Test
        @DisplayName("쿠폰 발급은 정해진 수량만큼만 가능하다")
        public void issue_coupon() throws InterruptedException {
            // given
            User user = userRepository.getReferenceById(userId);
            ExecutorService executors = Executors.newFixedThreadPool(100);

            // when
            for (int i = 0; i < 100; i++) {
                executors.submit(() -> {
                    try {
                        mockMvc.perform(post("/api/coupon/{coupon_id}", couponId)
                            .header("Authorization", userToken));
                    } catch (Exception e) {
                        log.error("err : ", e);
                    }
                });
            }

            executors.shutdown();
            executors.awaitTermination(1, TimeUnit.MINUTES);

            // then
            List<UserCoupon> byUser = userCouponRepository.findByUser(user);
            assertThat(byUser.size()).isEqualTo(10);

        }
    }

    @AfterEach
    public void destroy() {
        deleteDBUtils.destroy();
    }
}
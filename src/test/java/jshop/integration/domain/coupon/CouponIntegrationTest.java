package jshop.integration.domain.coupon;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import jshop.core.domain.cart.repository.CartRepository;
import jshop.core.domain.category.repository.CategoryRepository;
import jshop.core.domain.coupon.dto.CreateCouponRequest;
import jshop.core.domain.coupon.entity.CouponType;
import jshop.core.domain.coupon.repository.CouponRepository;
import jshop.core.domain.coupon.repository.UserCouponRepository;
import jshop.core.domain.coupon.service.CouponService;
import jshop.core.domain.product.dto.CreateProductDetailRequest;
import jshop.core.domain.product.entity.Product;
import jshop.core.domain.product.repository.ProductDetailRepository;
import jshop.core.domain.product.repository.ProductRepository;
import jshop.core.domain.product.service.ProductService;
import jshop.core.domain.user.dto.JoinUserResponse;
import jshop.core.domain.user.entity.User;
import jshop.core.domain.user.repository.UserRepository;
import jshop.web.dto.Response;
import jshop.common.utils.UUIDUtils;
import jshop.common.test.BaseTestContainers;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcPrint;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Slf4j
@EnableWebMvc
@AutoConfigureMockMvc(print = MockMvcPrint.NONE)
@SpringBootTest
@DisplayName("[통합 테스트] CouponController")
@Transactional
public class CouponIntegrationTest extends BaseTestContainers {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;


    private Long userId;
    private String userToken;
    private Long adminId;
    private String adminToken;
    private Long anotherUserId;
    private String anotherUserToken;

    private Long productId;
    private final List<Long> productDetailIds = new ArrayList<>();
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductDetailRepository productDetailRepository;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private DataSource dataSource;


    @BeforeEach
    public void init() throws Exception {
        User admin = User
            .builder()
            .username("admin")
            .password(bCryptPasswordEncoder.encode("admin"))
            .email("admin@admin.com")
            .role("ROLE_ADMIN")
            .build();

        userRepository.save(admin);

        log.info("url : {}", dataSource.getConnection().getMetaData().getURL());
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
                .couponType(CouponType.FIXED_RATE)
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
                .couponType(CouponType.FIXED_RATE)
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
}

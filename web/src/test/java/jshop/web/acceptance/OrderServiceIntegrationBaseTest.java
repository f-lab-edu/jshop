package jshop.web.acceptance;


import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jshop.core.domain.address.dto.CreateAddressRequest;
import jshop.core.domain.address.service.AddressService;
import jshop.core.domain.category.dto.CreateCategoryRequest;
import jshop.core.domain.category.service.CategoryService;
import jshop.core.domain.order.dto.CreateOrderRequest;
import jshop.core.domain.order.dto.OrderItemRequest;
import jshop.core.domain.order.dto.OrderListResponse;
import jshop.core.domain.order.entity.Order;
import jshop.core.domain.order.service.OrderService;
import jshop.core.domain.product.dto.CreateProductDetailRequest;
import jshop.core.domain.product.dto.CreateProductRequest;
import jshop.core.domain.product.service.ProductService;
import jshop.core.domain.user.dto.JoinUserRequest;
import jshop.core.domain.user.dto.UserType;
import jshop.core.domain.user.entity.User;
import jshop.core.domain.user.service.UserService;
import jshop.common.utils.TimeUtils;
import jshop.common.test.BaseTestContainers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@SpringBootTest
@Transactional
@DisplayName("[통합 테스트] OrderService")
class OrderServiceIntegrationBaseTest extends BaseTestContainers {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    UserService userService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ProductService productService;
    @Autowired
    private OrderService orderService;

    Long userId;
    Long addressId;
    Long categoryId;
    List<Long> productIds = new ArrayList<>();
    List<Long> productDetailIds = new ArrayList<>();
    @Autowired
    private AddressService addressService;

    @BeforeEach
    public void init() {

        JoinUserRequest joinUserRequest = JoinUserRequest
            .builder()
            .email("email@email.com")
            .username("username")
            .password("password")
            .userType(UserType.SELLER)
            .build();



        List<CreateProductDetailRequest> createProductDetailRequests = new ArrayList<>();
        String[] props = {"a", "b", "c"};

        for (String prop : props) {
            Map<String, String> attribute = new HashMap<>();
            attribute.put("attr1", prop);
            createProductDetailRequests.add(CreateProductDetailRequest
                .builder().price(1000L).attribute(attribute).build());
        }

        userId = userService.joinUser(joinUserRequest);
        CreateCategoryRequest createCategoryRequest = CreateCategoryRequest
            .builder().name("category").build();

        User user = userService.getUser(userId);
        user.getWallet().deposit(100_000_000L);
        addressId = addressService.createAddress(getCreateAddressRequest(), userId);
        categoryId = categoryService.createCategory(createCategoryRequest);

        Map<String, List<String>> attributes = new HashMap<>();
        attributes.put("attr1", List.of("a", "b", "c"));
        CreateProductRequest createProductRequest = CreateProductRequest
            .builder()
            .name("product")
            .categoryId(categoryId)
            .manufacturer("manufacturer")
            .description("description")
            .attributes(attributes)
            .build();

        for (int i = 0; i < 10; i++) {
            Long productId = productService.createProduct(createProductRequest, userId);
            productIds.add(productId);

            for (CreateProductDetailRequest request : createProductDetailRequests) {
                Long productDetailId = productService.createProductDetail(request, productId);
                productDetailIds.add(productDetailId);
                productService.updateProductDetailStock(productDetailId, 100);
            }
        }
    }

    @Nested
    @DisplayName("주문 리스트 가져오기 검증")
    class GetOrderList {

        @Test
        @DisplayName("사용자는 자신의 주문 리스트를 주문 시간 기준, 커서 페이징으로 가져올 수 있다.")
        public void getOrderList_succes() {
            // given
            List<Long> orderIds = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                long totalPrice = 0L;
                int totalQuantity = 0;
                List<OrderItemRequest> list = new ArrayList<>();
                for (int j = 0; j < 3; j++) {
                    list.add(OrderItemRequest
                        .builder().productDetailId(productDetailIds.get(j * 10 + i)).quantity(3).price(1000L).build());
                    totalQuantity += 3;
                    totalPrice += 3 * 1000L;
                }

                CreateOrderRequest createOrderRequest = CreateOrderRequest
                    .builder()
                    .totalPrice(totalPrice)
                    .totalQuantity(totalQuantity)
                    .addressId(addressId)
                    .orderItems(list)
                    .build();

                Long orderId = orderService.createOrder(createOrderRequest, userId);
                orderIds.add(orderId);
            }

            // when
            OrderListResponse orderListResponse = orderService.getOrderList(5, LocalDateTime.now(), userId);

            // then
            Order order = orderService.getOrder(orderIds.get(5));
            assertThat(orderListResponse.getOrders().size()).isEqualTo(5);
            assertThat(orderListResponse.getNextTimestamp()).isEqualTo(
                TimeUtils.localDateTimeToTimestamp(order.getCreatedAt()));
            assertThat(orderListResponse.getOrders().get(0).getId()).isEqualTo(orderIds.get(9));
        }

        @Test
        @DisplayName("주문이 없다면 빈 배열과, nextTimestamp도 null을 리턴한다.")
        public void getOrderList_empty() {
            // when
            OrderListResponse orderListResponse = orderService.getOrderList(5, LocalDateTime.now(), userId);

            // then
            assertThat(orderListResponse.getOrders().size()).isEqualTo(0);
            assertThat(orderListResponse.getNextTimestamp()).isEqualTo(null);
        }
    }

    private CreateAddressRequest getCreateAddressRequest() {
        return CreateAddressRequest
            .builder()
            .receiverName("username")
            .receiverNumber("1234")
            .province("province")
            .city("city")
            .district("district")
            .street("street")
            .detailAddress1("detail address 1")
            .detailAddress2("detail address 2")
            .message("message")
            .build();
    }
}
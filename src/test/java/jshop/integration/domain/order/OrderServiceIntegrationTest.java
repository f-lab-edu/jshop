package jshop.integration.domain.order;


import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import jshop.domain.address.service.AddressService;
import jshop.domain.category.service.CategoryService;
import jshop.domain.order.dto.CreateOrderRequest;
import jshop.domain.order.dto.OrderItemRequest;
import jshop.domain.order.dto.OrderListResponse;
import jshop.domain.order.entity.Order;
import jshop.domain.order.service.OrderService;
import jshop.domain.product.dto.CreateProductDetailRequest;
import jshop.domain.product.service.ProductService;
import jshop.domain.user.entity.User;
import jshop.domain.user.service.UserService;
import jshop.global.utils.TimeUtils;
import jshop.utils.dto.AddressDtoUtils;
import jshop.utils.dto.CategoryDtoUtils;
import jshop.utils.dto.ProductDtoUtils;
import jshop.utils.dto.UserDtoUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@DisplayName("[통합 테스트] OrderService")
class OrderServiceIntegrationTest {

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

        userId = userService.joinUser(UserDtoUtils.getJoinUserRequestDto());
        User user = userService.getUser(userId);
        user.getWallet().deposit(100_000_000L);
        addressId = addressService.createAddress(AddressDtoUtils.getCreateAddressRequest(), userId);
        categoryId = categoryService.createCategory(CategoryDtoUtils.getCreateCategoryRequest());
        for (int i = 0; i < 10; i++) {
            Long productId = productService.createProduct(ProductDtoUtils.getCreateProductRequest(categoryId), userId);
            productIds.add(productId);

            for (CreateProductDetailRequest request : ProductDtoUtils.getCreateProductDetailRequest()) {
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
}
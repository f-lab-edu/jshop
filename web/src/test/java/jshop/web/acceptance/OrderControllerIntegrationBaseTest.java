package jshop.web.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import jshop.core.domain.address.dto.CreateAddressRequest;
import jshop.core.domain.address.repository.AddressRepository;
import jshop.core.domain.address.service.AddressService;
import jshop.core.domain.category.dto.CreateCategoryRequest;
import jshop.core.domain.category.repository.CategoryRepository;
import jshop.core.domain.category.service.CategoryService;
import jshop.core.domain.inventory.entity.Inventory;
import jshop.core.domain.inventory.repository.InventoryRepository;
import jshop.core.domain.order.dto.CreateOrderRequest;
import jshop.core.domain.order.dto.OrderItemRequest;
import jshop.core.domain.order.repository.OrderProductDetailRepository;
import jshop.core.domain.order.repository.OrderRepository;
import jshop.core.domain.product.dto.CreateProductDetailRequest;
import jshop.core.domain.product.dto.CreateProductRequest;
import jshop.core.domain.product.repository.ProductDetailRepository;
import jshop.core.domain.product.repository.ProductRepository;
import jshop.core.domain.product.service.ProductService;
import jshop.core.domain.user.dto.JoinUserResponse;
import jshop.core.domain.user.entity.User;
import jshop.core.domain.user.repository.UserRepository;
import jshop.core.domain.user.service.UserService;
import jshop.common.exception.ErrorCode;
import jshop.web.dto.Response;
import jshop.common.test.BaseTestContainers;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcPrint;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Slf4j
@EnableWebMvc
@SpringBootTest
@AutoConfigureMockMvc(print = MockMvcPrint.NONE)
@DisplayName("[통합 테스트] OrderController")
public class OrderControllerIntegrationBaseTest extends BaseTestContainers {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private UserRepository userRepository;

    @PersistenceContext
    private EntityManager em;

    private Long user1Id;
    private String user1Token;
    private Long categoryId, product1Id, product2Id, addressId, productDetail1Id, productDetail2Id;

    @Autowired
    private UserService userService;
    @Autowired
    private AddressService addressService;
    @Autowired
    private ProductService productService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ProductDetailRepository productDetailRepository;
    @Autowired
    private OrderProductDetailRepository orderProductDetailRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    InventoryRepository inventoryRepository;

    @BeforeEach
    public void beforeEach() throws Exception {

        String joinUser1 = """
            { "username" : "username", "email" : "email@email.com", "password" : "password", "userType" : "SELLER"}""";

        String user1LoginRequest = """
            { "email" : "email@email.com", "password" : "password" }""";

        ResultActions perform = mockMvc.perform(
            post("/api/join").contentType(MediaType.APPLICATION_JSON).content(joinUser1));

        TypeReference<Response<JoinUserResponse>> typeReference = new TypeReference<Response<JoinUserResponse>>() {};
        Response<JoinUserResponse> joinUserResponse = objectMapper.readValue(
            perform.andReturn().getResponse().getContentAsString(), typeReference);
        user1Id = joinUserResponse.getData().getId();

        ResultActions login = mockMvc.perform(
            post("/api/login").contentType(MediaType.APPLICATION_JSON).content(user1LoginRequest));
        user1Token = login.andReturn().getResponse().getHeader("Authorization");

        categoryId = categoryService.createCategory(CreateCategoryRequest
            .builder().name("category").build());
        addressId = addressService.createAddress(CreateAddressRequest
            .builder().build(), user1Id);

        product1Id = productService.createProduct(CreateProductRequest
            .builder().categoryId(categoryId).build(), user1Id);

        product2Id = productService.createProduct(CreateProductRequest
            .builder().categoryId(categoryId).build(), user1Id);

        productDetail1Id = productService.createProductDetail(CreateProductDetailRequest
            .builder().price(1000L).build(), product1Id);

        productDetail2Id = productService.createProductDetail(CreateProductDetailRequest
            .builder().price(2000L).build(), product2Id);

        productService.updateProductDetailStock(productDetail1Id, 10);
        productService.updateProductDetailStock(productDetail2Id, 10);

        User user = userService.getUser(user1Id);
        user.getWallet().deposit(10000L);

        userRepository.save(user);
    }

    @AfterEach
    public void afterEach() {
        addressRepository.deleteAll();
        orderRepository.deleteAll();
        orderProductDetailRepository.deleteAll();
        productDetailRepository.deleteAll();
        productRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Nested
    @DisplayName("주문 생성 검증")
    class CreateOrder {

        @Test
        @DisplayName("주문 생성시 유저의 잔고를 감소시키고, 상품 재고를 감소시키고 주문을 생성한다")
        public void createOrder_success() throws Exception {
            // given
            List<OrderItemRequest> orderItems = new ArrayList<>();

            orderItems.add(OrderItemRequest
                .builder().productDetailId(productDetail1Id).quantity(3).price(1000L).build());
            orderItems.add(OrderItemRequest
                .builder().productDetailId(productDetail2Id).quantity(3).price(2000L).build());

            CreateOrderRequest createOrderRequest = CreateOrderRequest
                .builder().addressId(addressId).orderItems(orderItems).totalQuantity(6).totalPrice(9000L).build();

            // when
            ResultActions perform = mockMvc.perform(post("/api/orders")
                .header("Authorization", user1Token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createOrderRequest)));

            // then
            perform.andExpect(status().isOk());
            assertThat(userService.getUser(user1Id).getWallet().getBalance()).isEqualTo(1000L);
            Inventory inventory1 = productService.getProductDetail(productDetail1Id).getInventory();
            Inventory inventory2 = productService.getProductDetail(productDetail2Id).getInventory();
            assertThat(inventoryRepository.findById(inventory1.getId()).get().getQuantity()).isEqualTo(7);
            assertThat(inventoryRepository.findById(inventory2.getId()).get().getQuantity()).isEqualTo(7);

        }

        @Test
        @DisplayName("주문 생성시 유저의 잔고가 부족하면 WALLET_BALANCE_EXCEPTION 발생")
        public void createOrder_no_balance() throws Exception {
            // given
            User user = userService.getUser(user1Id);
            user.getWallet().withdraw(10000L);
            userRepository.save(user);
            List<OrderItemRequest> orderItems = new ArrayList<>();

            orderItems.add(OrderItemRequest
                .builder().productDetailId(productDetail1Id).quantity(3).price(1000L).build());
            orderItems.add(OrderItemRequest
                .builder().productDetailId(productDetail2Id).quantity(3).price(2000L).build());

            CreateOrderRequest createOrderRequest = CreateOrderRequest
                .builder().addressId(addressId).orderItems(orderItems).totalQuantity(6).totalPrice(9000L).build();

            // when
            ResultActions perform = mockMvc.perform(post("/api/orders")
                .header("Authorization", user1Token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createOrderRequest)));

            // then
            perform
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(ErrorCode.WALLET_BALANCE_EXCEPTION.getCode()));
//
            assertThat(userService.getUser(user1Id).getWallet().getBalance()).isEqualTo(0L);
            Inventory inventory1 = productService.getProductDetail(productDetail1Id).getInventory();
            Inventory inventory2 = productService.getProductDetail(productDetail2Id).getInventory();
            assertThat(inventoryRepository.findById(inventory1.getId()).get().getQuantity()).isEqualTo(10);
            assertThat(inventoryRepository.findById(inventory2.getId()).get().getQuantity()).isEqualTo(10);
        }

        @Test
        @DisplayName("주문 생성시 상품의 수량이 부족하면 NEGATIVE_QUANTITY_EXCEPTION 가 발생한다")
        public void createOrder_ILLEGAL_QUANTITY_REQUEST_EXCEPTION() throws Exception {
            // given
            List<OrderItemRequest> orderItems = new ArrayList<>();

            orderItems.add(OrderItemRequest
                .builder().productDetailId(productDetail1Id).quantity(3).price(1000L).build());
            orderItems.add(OrderItemRequest
                .builder().productDetailId(productDetail2Id).quantity(11).price(2000L).build());

            CreateOrderRequest createOrderRequest = CreateOrderRequest
                .builder().addressId(addressId).orderItems(orderItems).totalQuantity(14).totalPrice(25000L).build();

            // when
            ResultActions perform = mockMvc.perform(post("/api/orders")
                .header("Authorization", user1Token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createOrderRequest)));

            // then
            perform
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(ErrorCode.NEGATIVE_QUANTITY_EXCEPTION.getCode()));

            assertThat(userService.getUser(user1Id).getWallet().getBalance()).isEqualTo(10000L);
            Inventory inventory1 = productService.getProductDetail(productDetail1Id).getInventory();
            Inventory inventory2 = productService.getProductDetail(productDetail2Id).getInventory();
            assertThat(inventoryRepository.findById(inventory1.getId()).get().getQuantity()).isEqualTo(10);
            assertThat(inventoryRepository.findById(inventory2.getId()).get().getQuantity()).isEqualTo(10);
        }

        @Test
        @DisplayName("주문 생성시 주문 가격과, 상품 가격의 합이 다르면 ORDER_PRICE_MISMATCH가 발생")
        public void createOrder_ORDER_PRICE_MISMATCH() throws Exception {
            // given
            List<OrderItemRequest> orderItems = new ArrayList<>();

            orderItems.add(OrderItemRequest
                .builder().productDetailId(productDetail1Id).quantity(3).price(1000L).build());
            orderItems.add(OrderItemRequest
                .builder().productDetailId(productDetail2Id).quantity(3).price(2000L).build());

            CreateOrderRequest createOrderRequest = CreateOrderRequest
                .builder().addressId(addressId).orderItems(orderItems).totalQuantity(6).totalPrice(10000L).build();

            // when
            ResultActions perform = mockMvc.perform(post("/api/orders")
                .header("Authorization", user1Token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createOrderRequest)));

            // then
            perform
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(ErrorCode.ORDER_PRICE_MISMATCH.getCode()));

            assertThat(userService.getUser(user1Id).getWallet().getBalance()).isEqualTo(10000L);
            Inventory inventory1 = productService.getProductDetail(productDetail1Id).getInventory();
            Inventory inventory2 = productService.getProductDetail(productDetail2Id).getInventory();
            assertThat(inventoryRepository.findById(inventory1.getId()).get().getQuantity()).isEqualTo(10);
            assertThat(inventoryRepository.findById(inventory2.getId()).get().getQuantity()).isEqualTo(10);
        }
    }
}

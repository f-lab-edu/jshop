package jshop.domain.order.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import jshop.domain.address.entity.Address;
import jshop.domain.address.service.AddressService;
import jshop.domain.delivery.entity.DeliveryState;
import jshop.domain.inventory.entity.Inventory;
import jshop.domain.order.dto.CreateOrderRequest;
import jshop.domain.order.dto.OrderItemRequest;
import jshop.domain.order.entity.Order;
import jshop.domain.order.repository.OrderProductDetailRepository;
import jshop.domain.order.repository.OrderRepository;
import jshop.domain.product.entity.ProductDetail;
import jshop.domain.product.service.ProductService;
import jshop.domain.user.dto.JoinUserRequest;
import jshop.domain.user.entity.User;
import jshop.domain.user.service.UserService;
import jshop.global.common.ErrorCode;
import jshop.global.exception.JshopException;
import org.aspectj.weaver.ast.Or;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("[단위 테스트] OrderService")
class OrderServiceTest {

    @InjectMocks
    OrderService orderService;

    @Mock
    AddressService addressService;

    @Mock
    OrderRepository orderRepository;

    @Mock
    OrderProductDetailRepository orderProductDetailRepository;

    @Mock
    UserService userService;

    @Mock
    ProductService productService;

    @Nested
    @DisplayName("주문 생성 검증")
    class CreateOrder {

        User user;
        Address address;
        ProductDetail productDetail1, productDetail2;
        Inventory inventory1, inventory2;

        @BeforeEach
        public void init() {
            JoinUserRequest joinUserRequest = JoinUserRequest
                .builder().build();
            user = User.of(joinUserRequest, "password");
            user.getWallet().deposit(10000L);
            address = Address
                .builder().id(1L).build();
            inventory1 = Inventory.create();
            inventory2 = Inventory.create();

            productDetail1 = ProductDetail
                .builder().id(1L).price(1000L).inventory(inventory1).build();

            productDetail2 = ProductDetail
                .builder().id(2L).price(2000L).inventory(inventory2).build();

            inventory1.addStock(10);
            inventory2.addStock(10);
        }

        @Test
        @DisplayName("주문 생성시 유저의 잔고를 감소시키고, 상품 재고를 감소시키고 주문을 생성한다")
        public void createOrder_success() {
            // given
            List<OrderItemRequest> orderItems = new ArrayList<>();

            orderItems.add(OrderItemRequest
                .builder().productDetailId(1L).quantity(3).price(1000L).build());
            orderItems.add(OrderItemRequest
                .builder().productDetailId(2L).quantity(3).price(2000L).build());

            CreateOrderRequest createOrderRequest = CreateOrderRequest
                .builder().addressId(1L).totalPrice(9000L).totalQuantity(6).orderItems(orderItems).build();

            // when
            when(userService.getUser(1L)).thenReturn(user);
            when(addressService.getAddress(1L)).thenReturn(address);
            when(productService.getProductDetail(1L)).thenReturn(productDetail1);
            when(productService.getProductDetail(2L)).thenReturn(productDetail2);
            orderService.createOrder(createOrderRequest, 1L);

            // then
            assertThat(user.getWallet().getBalance()).isEqualTo(1000L);
            assertThat(inventory1.getQuantity()).isEqualTo(7);
            assertThat(inventory2.getQuantity()).isEqualTo(7);
        }

        @Test
        @DisplayName("주문 생성시 유저의 잔고가 부족하면 WALLET_BALANCE_EXCEPTION 발생")
        public void createOrder_WALLET_BALANCE_EXCEPTION() {
            // given
            user.getWallet().withdraw(10000L);
            List<OrderItemRequest> orderItems = new ArrayList<>();

            orderItems.add(OrderItemRequest
                .builder().productDetailId(1L).quantity(3).price(1000L).build());
            orderItems.add(OrderItemRequest
                .builder().productDetailId(2L).quantity(3).price(2000L).build());

            CreateOrderRequest createOrderRequest = CreateOrderRequest
                .builder().addressId(1L).totalPrice(9000L).totalQuantity(6).orderItems(orderItems).build();

            // when
            when(userService.getUser(1L)).thenReturn(user);

            // then
            JshopException jshopException = assertThrows(JshopException.class,
                () -> orderService.createOrder(createOrderRequest, 1L));

            assertThat(jshopException.getErrorCode()).isEqualTo(ErrorCode.WALLET_BALANCE_EXCEPTION);
        }


        @Test
        @DisplayName("주문 생성시 상품의 수량이나 가격 정보가 잘못되면 INVALID_ORDER_ITEM 가 발생한다")
        public void createOrder_INVALID_ORDER_ITEM() {
            // given
            List<OrderItemRequest> orderItems = new ArrayList<>();

            orderItems.add(OrderItemRequest
                .builder().productDetailId(1L).quantity(3).price(1000L).build());
            orderItems.add(OrderItemRequest
                .builder().productDetailId(2L).price(2000L).build());

            CreateOrderRequest createOrderRequest = CreateOrderRequest
                .builder().addressId(1L).totalPrice(9000L).totalQuantity(6).orderItems(orderItems).build();

            // when
            when(userService.getUser(1L)).thenReturn(user);
            when(addressService.getAddress(1L)).thenReturn(address);
            when(productService.getProductDetail(1L)).thenReturn(productDetail1);
            when(productService.getProductDetail(2L)).thenReturn(productDetail2);

            // then
            JshopException jshopException = assertThrows(JshopException.class,
                () -> orderService.createOrder(createOrderRequest, 1L));
            assertThat(jshopException.getErrorCode()).isEqualTo(ErrorCode.INVALID_ORDER_ITEM);
        }

        @Test
        @DisplayName("주문 생성시 상품의 수량이 부족하면 ILLEGAL_QUANTITY_REQUEST_EXCEPTION 가 발생한다")
        public void createOrder_ILLEGAL_QUANTITY_REQUEST_EXCEPTION() {
            // given
            List<OrderItemRequest> orderItems = new ArrayList<>();

            orderItems.add(OrderItemRequest
                .builder().productDetailId(1L).quantity(3).price(1000L).build());
            orderItems.add(OrderItemRequest
                .builder().productDetailId(2L).quantity(11).price(2000L).build());

            CreateOrderRequest createOrderRequest = CreateOrderRequest
                .builder().addressId(1L).totalPrice(9000L).totalQuantity(6).orderItems(orderItems).build();

            // when
            when(userService.getUser(1L)).thenReturn(user);
            when(addressService.getAddress(1L)).thenReturn(address);
            when(productService.getProductDetail(1L)).thenReturn(productDetail1);
            when(productService.getProductDetail(2L)).thenReturn(productDetail2);

            // then
            JshopException jshopException = assertThrows(JshopException.class,
                () -> orderService.createOrder(createOrderRequest, 1L));
            assertThat(jshopException.getErrorCode()).isEqualTo(ErrorCode.NEGATIVE_QUANTITY_EXCEPTION);
        }

        @Test
        @DisplayName("주문 생성시 주문 가격과, 상품 가격의 합이 다르면 ORDER_PRICE_MISMATCH가 발생")
        public void createOrder_ORDER_PRICE_MISMATCH() {
            // given
            List<OrderItemRequest> orderItems = new ArrayList<>();

            orderItems.add(OrderItemRequest
                .builder().productDetailId(1L).quantity(3).price(1000L).build());
            orderItems.add(OrderItemRequest
                .builder().productDetailId(2L).quantity(3).price(2000L).build());

            CreateOrderRequest createOrderRequest = CreateOrderRequest
                .builder().addressId(1L).totalPrice(8000L).totalQuantity(6).orderItems(orderItems).build();

            // when
            when(userService.getUser(1L)).thenReturn(user);
            when(addressService.getAddress(1L)).thenReturn(address);
            when(productService.getProductDetail(1L)).thenReturn(productDetail1);
            when(productService.getProductDetail(2L)).thenReturn(productDetail2);

            // then
            JshopException jshopException = assertThrows(JshopException.class,
                () -> orderService.createOrder(createOrderRequest, 1L));
            assertThat(jshopException.getErrorCode()).isEqualTo(ErrorCode.ORDER_PRICE_MISMATCH);
        }
    }


    @Nested
    @DisplayName("주문 취소 검증")
    class DeleteOrder {

        User user;
        Address address;
        ProductDetail productDetail1, productDetail2;
        Inventory inventory1, inventory2;
        Order order;

        @Captor
        ArgumentCaptor<Order> orderArgumentCaptor;

        @BeforeEach
        public void init() {
            JoinUserRequest joinUserRequest = JoinUserRequest
                .builder().build();
            user = User.of(joinUserRequest, "password");
            user.getWallet().deposit(10000L);
            address = Address
                .builder().id(1L).build();
            inventory1 = Inventory.create();
            inventory2 = Inventory.create();

            productDetail1 = ProductDetail
                .builder().id(1L).price(1000L).inventory(inventory1).build();

            productDetail2 = ProductDetail
                .builder().id(2L).price(2000L).inventory(inventory2).build();

            inventory1.addStock(10);
            inventory2.addStock(10);

            List<OrderItemRequest> orderItems = new ArrayList<>();

            orderItems.add(OrderItemRequest
                .builder().productDetailId(1L).quantity(3).price(1000L).build());
            orderItems.add(OrderItemRequest
                .builder().productDetailId(2L).quantity(3).price(2000L).build());

            CreateOrderRequest createOrderRequest = CreateOrderRequest
                .builder().addressId(1L).totalPrice(9000L).totalQuantity(6).orderItems(orderItems).build();

            // when
            when(userService.getUser(1L)).thenReturn(user);
            when(addressService.getAddress(1L)).thenReturn(address);
            when(productService.getProductDetail(1L)).thenReturn(productDetail1);
            when(productService.getProductDetail(2L)).thenReturn(productDetail2);
            orderService.createOrder(createOrderRequest, 1L);

            // then
            verify(orderRepository, times(1)).save(orderArgumentCaptor.capture());
            assertThat(user.getWallet().getBalance()).isEqualTo(1000L);
            assertThat(inventory1.getQuantity()).isEqualTo(7);
            assertThat(inventory2.getQuantity()).isEqualTo(7);
            order = orderArgumentCaptor.getValue();
        }

        @Test
        @DisplayName("주문 취소시 회원의 잔고를 늘리고, 상품 재고를 늘리고, 배송을 취소한다")
        public void cancelOrder_success() {
            // when
            when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
            orderService.deleteOrder(1L);
            // then
            assertThat(user.getWallet().getBalance()).isEqualTo(10000L);
            assertThat(inventory1.getQuantity()).isEqualTo(10);
            assertThat(inventory2.getQuantity()).isEqualTo(10);
            assertThat(order.getDelivery().getDeliveryState()).isEqualTo(DeliveryState.CANCLED);
        }

        @Test
        @DisplayName("주문 취소시 배송이 진행중이라면 주문을 취소할 수 없다.")
        public void cancelOrder_shipping() {
            // given
            order.getDelivery().nextState();
            // when
            when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

            // then
            JshopException jshopException = assertThrows(JshopException.class, () -> orderService.deleteOrder(1L));
            assertThat(jshopException.getErrorCode()).isEqualTo(ErrorCode.ALREADY_SHIPPING_ORDER);
        }
    }
}
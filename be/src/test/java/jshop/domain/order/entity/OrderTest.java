package jshop.domain.order.entity;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import jshop.domain.address.entity.Address;
import jshop.domain.delivery.entity.Delivery;
import jshop.domain.delivery.entity.DeliveryState;
import jshop.domain.inventory.entity.Inventory;
import jshop.domain.order.dto.CreateOrderRequest;
import jshop.domain.order.dto.OrderItemRequest;
import jshop.domain.order.repository.OrderRepository;
import jshop.domain.product.dto.CreateProductDetailRequest;
import jshop.domain.product.dto.CreateProductRequest;
import jshop.domain.product.entity.Product;
import jshop.domain.product.entity.ProductDetail;
import jshop.domain.product.repository.ProductDetailRepository;
import jshop.domain.product.service.ProductService;
import jshop.domain.user.dto.JoinUserRequest;
import jshop.domain.user.entity.User;
import jshop.domain.user.repository.UserRepository;
import jshop.domain.user.service.UserService;
import jshop.domain.wallet.entity.Wallet;
import jshop.global.common.ErrorCode;
import jshop.global.exception.JshopException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@DisplayName("[단위 테스트]  Order")
class OrderTest {

    @BeforeEach
    public void init() {

    }

    @Nested
    @DisplayName("주문 취소 로직 검증")
    class CreateOrder {

        @Test
        @DisplayName("주문 취소시 주문 금액을 환불하고 각 재고를 되돌리고, 배송을 취소한다. ")
        public void createOrder_success() {
            // given
            Inventory inventory = Inventory.create();
            inventory.addStock(10);

            ProductDetail productDetail = ProductDetail
                .builder().id(1L).price(1000L).inventory(inventory).build();
            Wallet wallet = Wallet.create();
            wallet.deposit(10000L);
            User user = User
                .builder().wallet(wallet).build();

            Delivery delivery = Delivery
                .builder().deliveryState(DeliveryState.PREPARING).build();

            CreateOrderRequest createOrderRequest = CreateOrderRequest
                .builder()
                .addressId(1L)
                .totalPrice(3000L)
                .totalQuantity(3)
                .orderItems(List.of(OrderItemRequest
                    .builder().quantity(3).price(1000L).productDetailId(1L).build()))
                .build();

            Order order = Order.createOrder(user, delivery, createOrderRequest);

            // when
            order.cancel();

            // then
            assertThat(user.getWallet().getBalance()).isEqualTo(13000L);
            assertThat(delivery.getDeliveryState()).isEqualTo(DeliveryState.CANCLED);
            assertThat(productDetail.getInventory().getQuantity()).isEqualTo(13);
        }

        @Test
        @DisplayName("배송중이라면 주문을 취소할 수 없다.")
        public void createOrder_shipping() {
            // given
            Inventory inventory = Inventory.create();
            inventory.addStock(10);

            ProductDetail productDetail = ProductDetail
                .builder().id(1L).price(1000L).inventory(inventory).build();
            Wallet wallet = Wallet.create();
            wallet.deposit(10000L);
            User user = User
                .builder().wallet(wallet).build();

            Delivery delivery = Delivery
                .builder().deliveryState(DeliveryState.PREPARING).build();

            CreateOrderRequest createOrderRequest = CreateOrderRequest
                .builder()
                .addressId(1L)
                .totalPrice(3000L)
                .totalQuantity(3)
                .orderItems(List.of(OrderItemRequest
                    .builder().quantity(3).price(1000L).productDetailId(1L).build()))
                .build();

            Order order = Order.createOrder(user, delivery, createOrderRequest);

            // when
            delivery.startTransit();
            JshopException jshopException = assertThrows(JshopException.class, () -> order.cancel());

            // then
            assertThat(jshopException.getErrorCode()).isEqualTo(ErrorCode.ALREADY_SHIPPING_ORDER);
        }
    }
}
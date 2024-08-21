package jshop.core.order.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import jshop.core.domain.address.entity.Address;
import jshop.core.domain.delivery.entity.Delivery;
import jshop.core.domain.delivery.entity.DeliveryState;
import jshop.core.domain.inventory.entity.Inventory;
import jshop.core.domain.order.dto.CreateOrderRequest;
import jshop.core.domain.order.dto.OrderItemRequest;
import jshop.core.domain.order.entity.Order;
import jshop.core.domain.order.entity.OrderProductDetail;
import jshop.core.domain.product.entity.ProductDetail;
import jshop.core.domain.user.entity.User;
import jshop.core.domain.wallet.entity.Wallet;
import jshop.common.exception.ErrorCode;
import jshop.common.exception.JshopException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;


@DisplayName("[단위 테스트]  Order")
class OrderTest {

    @Nested
    @DisplayName("주문 취소 로직 검증")
    class CreateOrder {

        @Test
        @DisplayName("주문 취소시 주문 금액을 환불하고 각 재고를 되돌리고, 배송을 취소한다.")
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

            Address address = Address
                .builder().build();

            OrderItemRequest orderItemRequest = OrderItemRequest
                .builder().quantity(3).price(1000L).productDetailId(1L).build();

            CreateOrderRequest createOrderRequest = CreateOrderRequest
                .builder()
                .addressId(1L)
                .totalPrice(3000L)
                .totalQuantity(3)
                .orderItems(List.of(orderItemRequest))
                .build();

            List<OrderProductDetail> orderProductDetails = new ArrayList<>();
            orderProductDetails.add(OrderProductDetail.of(orderItemRequest, productDetail));

            Order order = Order.createOrder(user, address, orderProductDetails, null, createOrderRequest);

            // when
            order.cancel();

            // then
            assertThat(user.getWallet().getBalance()).isEqualTo(10000L);
            assertThat(productDetail.getInventory().getQuantity()).isEqualTo(10);
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

            Address address = Address
                .builder().build();

            OrderItemRequest orderItemRequest = OrderItemRequest
                .builder().quantity(3).price(1000L).productDetailId(1L).build();

            CreateOrderRequest createOrderRequest = CreateOrderRequest
                .builder()
                .addressId(1L)
                .totalPrice(3000L)
                .totalQuantity(3)
                .orderItems(List.of(orderItemRequest))
                .build();

            List<OrderProductDetail> orderProductDetails = new ArrayList<>();
            orderProductDetails.add(OrderProductDetail.of(orderItemRequest, productDetail));

            Order order = Order.createOrder(user, address, orderProductDetails, null, createOrderRequest);

            // when
            order.getDelivery().startTransit();
            JshopException jshopException = assertThrows(JshopException.class, () -> order.cancel());

            // then
            assertThat(jshopException.getErrorCode()).isEqualTo(ErrorCode.ALREADY_SHIPPING_ORDER);
        }
    }
}
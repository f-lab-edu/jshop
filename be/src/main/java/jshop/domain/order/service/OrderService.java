package jshop.domain.order.service;

import jshop.domain.address.entity.Address;
import jshop.domain.address.service.AddressService;
import jshop.domain.delivery.entity.Delivery;
import jshop.domain.delivery.service.DeliveryService;
import jshop.domain.inventory.service.InventoryService;
import jshop.domain.order.dto.CreateOrderRequest;
import jshop.domain.order.dto.OrderItemRequest;
import jshop.domain.order.entity.Order;
import jshop.domain.order.entity.OrderProductDetail;
import jshop.domain.order.repository.OrderProductDetailRepository;
import jshop.domain.order.repository.OrderRepository;
import jshop.domain.product.entity.ProductDetail;
import jshop.domain.product.repository.ProductDetailRepository;
import jshop.domain.user.entity.User;
import jshop.domain.user.repository.UserRepository;
import jshop.domain.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private final InventoryService inventoryService;
    private final AddressService addressService;
    private final WalletService walletService;
    private final DeliveryService deliveryService;
    private final UserRepository userRepository;
    private final ProductDetailRepository productDetailRepository;
    private final OrderRepository orderRepository;
    private final OrderProductDetailRepository orderProductDetailRepository;

    @Transactional
    public Long createOrder(CreateOrderRequest createOrderRequest, Long userId) {
        User user = userRepository.getReferenceById(userId);
        walletService.updateBalance(userId, createOrderRequest.getTotalPrice());

        for (OrderItemRequest orderItem : createOrderRequest.getOrderItems()) {
            inventoryService.changeStock(orderItem.getProductDetailId(), orderItem.getQuantity());
        }

        Address deliveryAddress = addressService.getAddress(createOrderRequest.getAddressId());
        Delivery delivery = deliveryService.createDelivery(deliveryAddress);

        Order order = Order
            .builder()
            .user(user)
            .delivery(delivery)
            .totalPrice(createOrderRequest.getTotalPrice())
            .totalQuantity(createOrderRequest.getTotalQuantity())
            .build();

        orderRepository.save(order);

        for (OrderItemRequest orderItem : createOrderRequest.getOrderItems()) {
            ProductDetail productDetail = productDetailRepository.getReferenceById(orderItem.getProductDetailId());
            OrderProductDetail orderProductDetail = OrderProductDetail
                .builder()
                .order(order)
                .productDetail(productDetail)
                .orderQuantity(orderItem.getQuantity())
                .orderPrice(orderItem.getPrice())
                .build();

            orderProductDetailRepository.save(orderProductDetail);
        }

        return order.getId();
    }
}

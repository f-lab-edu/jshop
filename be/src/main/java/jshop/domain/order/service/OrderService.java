package jshop.domain.order.service;

import java.util.Optional;
import jshop.domain.address.entity.Address;
import jshop.domain.address.service.AddressService;
import jshop.domain.delivery.entity.Delivery;
import jshop.domain.order.dto.CreateOrderRequest;
import jshop.domain.order.dto.OrderItemRequest;
import jshop.domain.order.entity.Order;
import jshop.domain.order.entity.OrderProductDetail;
import jshop.domain.order.repository.OrderProductDetailRepository;
import jshop.domain.order.repository.OrderRepository;
import jshop.domain.product.entity.ProductDetail;
import jshop.domain.product.repository.ProductDetailRepository;
import jshop.domain.product.service.ProductService;
import jshop.domain.user.entity.User;
import jshop.domain.user.repository.UserRepository;
import jshop.domain.user.service.UserService;
import jshop.global.common.ErrorCode;
import jshop.global.exception.JshopException;
import jshop.global.jwt.dto.CustomUserDetails;
import jshop.global.utils.OrderUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private final AddressService addressService;
    private final OrderRepository orderRepository;
    private final OrderProductDetailRepository orderProductDetailRepository;
    private final UserService userService;
    private final ProductService productService;

    @Transactional
    public Long createOrder(CreateOrderRequest createOrderRequest, Long userId) {
        User user = userService.getUser(userId);

        Address deliveryAddress = addressService.getAddress(createOrderRequest.getAddressId());
        Delivery delivery = Delivery.of(deliveryAddress);

        Order order = Order.createOrder(user, delivery, createOrderRequest);

        orderRepository.save(order);

        for (OrderItemRequest orderItem : createOrderRequest.getOrderItems()) {
            ProductDetail productDetail = productService.getProductDetail(orderItem.getProductDetailId());
            OrderProductDetail orderProductDetail = OrderProductDetail.createOrderProductDetail(order,
                orderItem.getQuantity(), orderItem.getPrice(), productDetail);
            orderProductDetailRepository.save(orderProductDetail);
        }

        return order.getId();
    }

    @Transactional
    public Long deleteOrder(Long orderId) {
        Order order = getOrder(orderId);
        order.cancel();

        return order.getId();
    }

    public Order getOrder(Long orderId) {
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        return OrderUtils.getOrderOrThrow(optionalOrder, orderId);
    }

    public boolean checkOrderOwnership(UserDetails userDetails, Long orderId) {
        CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
        Long userId = customUserDetails.getId();

        Order order = getOrder(orderId);

        if (order.getUser().getId().equals(userId)) {
            return true;
        }
        log.error(ErrorCode.UNAUTHORIZED.getLogMessage(), "Order", orderId, userId);
        throw JshopException.of(ErrorCode.UNAUTHORIZED);
    }
}

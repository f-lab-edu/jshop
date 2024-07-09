package jshop.domain.order.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import jshop.domain.address.entity.Address;
import jshop.domain.address.service.AddressService;
import jshop.domain.delivery.entity.Delivery;
import jshop.domain.order.dto.CreateOrderRequest;
import jshop.domain.order.dto.OrderItemRequest;
import jshop.domain.order.dto.OrderListResponse;
import jshop.domain.order.dto.OrderListResponse.OrderResponse;
import jshop.domain.order.entity.Order;
import jshop.domain.order.entity.OrderProductDetail;
import jshop.domain.order.repository.OrderProductDetailRepository;
import jshop.domain.order.repository.OrderRepository;
import jshop.domain.product.entity.ProductDetail;
import jshop.domain.product.service.ProductService;
import jshop.domain.user.entity.User;
import jshop.domain.user.repository.UserRepository;
import jshop.domain.user.service.UserService;
import jshop.global.common.ErrorCode;
import jshop.global.exception.JshopException;
import jshop.global.jwt.dto.CustomUserDetails;
import jshop.global.utils.OrderUtils;
import jshop.global.utils.TimeUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.weaver.ast.Or;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

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
    private final UserRepository userRepository;

    public OrderListResponse getOrderList(int pageSize, LocalDateTime lastOrderDate, Long userId) {
        User user = userRepository.getReferenceById(userId);
        PageRequest pageRequest = PageRequest.of(0, pageSize, Sort.by(Direction.DESC, "createdAt"));
//        Page<Order> page = orderRepository.findOrdersByQuery(user, pageRequest, lastOrderDate);
        Page<Order> page = orderRepository.findOrdersByUserAndCreatedAtIsBefore(user, lastOrderDate, pageRequest);

        List<Order> orders = page.getContent();

        OrderListResponse orderListResponse = OrderListResponse
            .builder()
            .nextTimestamp(null)
            .nextTimestamp(orders.isEmpty() ? null
                : TimeUtils.localDateTimeToTimestamp(orders.get(orders.size() - 1).getCreatedAt()))
            .build();

        for (Order order : orders) {
            OrderResponse orderResponse = OrderResponse.of(order);
            for (OrderProductDetail orderItem : order.getProductDetails()) {
                orderResponse.addProduct(orderItem);
            }
            orderListResponse.addOrders(orderResponse);
        }

        return orderListResponse;
    }

    @Transactional
    public Long createOrder(CreateOrderRequest createOrderRequest, Long userId) {
        User user = userService.getUser(userId);
        Long totalPrice = createOrderRequest.getTotalPrice();

        Address deliveryAddress = addressService.getAddress(createOrderRequest.getAddressId());
        Delivery delivery = Delivery.of(deliveryAddress);

        Order order = Order.createOrder(user, delivery, createOrderRequest);

        long totalProductPrice = 0L;
        int totalProductQuantity = 0;
        for (OrderItemRequest orderItem : createOrderRequest.getOrderItems()) {
            ProductDetail productDetail = productService.getProductDetail(orderItem.getProductDetailId());
            order.addProduct(orderItem, productDetail);
            totalProductPrice += orderItem.getPrice() * orderItem.getQuantity();
            totalProductQuantity += orderItem.getQuantity();
        }

        if (totalProductPrice != totalPrice) {
            log.error(ErrorCode.ORDER_PRICE_MISMATCH.getLogMessage(), totalPrice, totalProductPrice);
            throw JshopException.of(ErrorCode.ORDER_PRICE_MISMATCH);
        }

        if (createOrderRequest.getTotalQuantity() != totalProductQuantity) {
            log.error(ErrorCode.ORDER_QUANTITY_MISMATCH.getLogMessage(), createOrderRequest.getTotalQuantity(),
                totalProductQuantity);
            throw JshopException.of(ErrorCode.ORDER_QUANTITY_MISMATCH);
        }

        user.getWallet().purchase(totalPrice);

        /**
         * TODO
         * 쿠폰 가격 할인 적용
         */

        orderRepository.save(order);

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

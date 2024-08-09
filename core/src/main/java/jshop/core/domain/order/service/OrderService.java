package jshop.core.domain.order.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import jshop.core.domain.address.entity.Address;
import jshop.core.domain.address.service.AddressService;
import jshop.core.domain.coupon.entity.UserCoupon;
import jshop.core.domain.coupon.repository.CouponRepository;
import jshop.core.domain.coupon.service.CouponService;
import jshop.core.domain.delivery.entity.Delivery;
import jshop.core.domain.order.dto.CreateOrderRequest;
import jshop.core.domain.order.dto.OrderItemRequest;
import jshop.core.domain.order.dto.OrderListResponse;
import jshop.core.domain.order.dto.OrderListResponse.OrderResponse;
import jshop.core.domain.order.entity.Order;
import jshop.core.domain.order.entity.OrderProductDetail;
import jshop.core.domain.order.repository.OrderProductDetailRepository;
import jshop.core.domain.order.repository.OrderRepository;
import jshop.core.domain.product.entity.ProductDetail;
import jshop.core.domain.product.service.ProductService;
import jshop.core.domain.user.entity.User;
import jshop.core.domain.user.repository.UserRepository;
import jshop.core.domain.user.service.UserService;
import jshop.common.exception.ErrorCode;
import jshop.common.exception.JshopException;
import jshop.common.utils.TimeUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
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
    private final UserRepository userRepository;
    private final CouponRepository couponRepository;
    private final CouponService couponService;

    public OrderListResponse getOrderList(int pageSize, LocalDateTime lastOrderDate, Long userId) {
        User user = userRepository.getReferenceById(userId);
        PageRequest pageRequest = PageRequest.of(0, pageSize, Sort.by(Direction.DESC, "createdAt"));
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

        if (createOrderRequest.getUserCouponId() != null) {
            UserCoupon userCoupon = couponService.getUserCoupon(createOrderRequest.getUserCouponId());
            order.applyCoupon(userCoupon);
        }

        user.getWallet().purchase(order.getPaymentPrice());

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
        return optionalOrder.orElseThrow(() -> {
            log.error(ErrorCode.ORDER_ID_NOT_FOUND.getLogMessage(), orderId);
            throw JshopException.of(ErrorCode.ORDER_ID_NOT_FOUND);
        });
    }
}

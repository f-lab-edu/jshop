package jshop.domain.order.controller;

import jakarta.validation.Valid;
import jshop.domain.order.dto.CreateOrderRequest;
import jshop.domain.order.service.OrderService;
import jshop.global.annotation.CurrentUserId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public void createOrder(@RequestBody @Valid CreateOrderRequest createOrderRequest, @CurrentUserId Long userId) {
        orderService.createOrder(createOrderRequest, userId);
    }

    @DeleteMapping("/{order_id}")
    @PreAuthorize("@orderService.checkOrderOwnership(authentication.principal, #orderId)")
    public void deleteOrder(@PathVariable("order_id") @P("orderId") Long orderId) {
        orderService.deleteOrder(orderId);

    }

}

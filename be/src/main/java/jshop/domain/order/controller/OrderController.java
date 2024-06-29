package jshop.domain.order.controller;

import jakarta.validation.Valid;
import jshop.domain.order.dto.CreateOrderRequest;
import jshop.domain.order.service.OrderService;
import jshop.global.annotation.CurrentUserId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
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
}

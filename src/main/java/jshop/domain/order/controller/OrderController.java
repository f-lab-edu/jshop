package jshop.domain.order.controller;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.Optional;
import jshop.domain.order.dto.CreateOrderRequest;
import jshop.domain.order.dto.OrderListResponse;
import jshop.domain.order.service.OrderService;
import jshop.global.annotation.CurrentUserId;
import jshop.global.common.ErrorCode;
import jshop.global.dto.Response;
import jshop.global.exception.JshopException;
import jshop.global.utils.TimeUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public Response<OrderListResponse> getOrderList(@RequestParam("size") Optional<Integer> optionalSize,
        @RequestParam("last_timestamp") Optional<Long> optionalLastTimestamp, @CurrentUserId Long userId) {
        long lastTimestamp = optionalLastTimestamp.orElse(32503561200000L);
        LocalDateTime lastOrderDate = TimeUtils.timestampToLocalDateTime(lastTimestamp);
        int pageSize = optionalSize.orElse(10);

        if (pageSize > 10 || pageSize < 0) {
            log.error(ErrorCode.ILLEGAL_PAGE_REQUEST.getLogMessage(), 0, pageSize);
            throw JshopException.of(ErrorCode.ILLEGAL_PAGE_REQUEST);
        }

        OrderListResponse orderListResponse = orderService.getOrderList(pageSize, lastOrderDate, userId);

        return Response
            .<OrderListResponse>builder().data(orderListResponse).build();
    }

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

package jshop.web.controller;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.Optional;
import jshop.core.domain.order.dto.CreateOrderRequest;
import jshop.core.domain.order.dto.OrderListResponse;
import jshop.core.domain.order.service.OrderService;
import jshop.web.security.annotation.CurrentUserId;
import jshop.common.exception.ErrorCode;
import jshop.web.dto.Response;
import jshop.common.exception.JshopException;
import jshop.common.utils.TimeUtils;
import jshop.web.security.service.AuthorizationService;
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
    private final AuthorizationService authorizationService;

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
    @PreAuthorize("@authorizationService.checkOrderOwnership(authentication.principal, #orderId)")
    public void deleteOrder(@PathVariable("order_id") @P("orderId") Long orderId) {
        orderService.deleteOrder(orderId);
    }
}

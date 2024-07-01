package jshop.global.utils;

import java.util.Optional;
import jshop.domain.cart.entity.Cart;
import jshop.domain.order.entity.Order;
import jshop.global.common.ErrorCode;
import jshop.global.exception.JshopException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OrderUtils {

    public static Order getOrderOrThrow(Optional<Order> optionalOrder, Long orderId) {
        return optionalOrder.orElseThrow(() -> {
            log.error(ErrorCode.ORDER_ID_NOT_FOUND.getLogMessage(), orderId);
            throw JshopException.of(ErrorCode.ORDER_ID_NOT_FOUND);
        });
    }
}

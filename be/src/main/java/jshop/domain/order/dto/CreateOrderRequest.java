package jshop.domain.order.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class CreateOrderRequest {

    private Long addressId;
    private List<OrderItemRequest> orderItems;
    private Integer totalQuantity;
    private Long totalPrice;
}

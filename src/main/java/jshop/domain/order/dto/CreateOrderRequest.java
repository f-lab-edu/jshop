package jshop.domain.order.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;
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

    @NotNull(message = "주소는 공백일 수 없습니다.")
    private Long addressId;

    @NotEmpty(message = "주문 상품은 공백일 수 없습니다.")
    private List<OrderItemRequest> orderItems;

    @NotNull(message = "주문 수량은 공백일 수 없습니다.")
    private Integer totalQuantity;

    @NotNull(message = "주문 가격은 공백일 수 없습니다.")
    private Long totalPrice;

    private Long userCouponId;
}

package jshop.core.domain.cart.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
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
public class AddCartRequest {

    @NotNull(message = "상품은 공백일 수 없습니다.")
    private Long productDetailId;

    @NotNull(message = "수량은 공백일 수 없습니다.")
    @Min(value = 1, message = "수량은 1 이상이여야 합니다.")
    private Integer quantity;
}

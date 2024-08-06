package jshop.core.domain.coupon.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import jshop.core.domain.coupon.entity.CouponType;
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
public class CreateCouponRequest {

    @NotEmpty(message = "쿠폰 ID는 공백일 수 없습니다.")
    private String id;

    @NotEmpty(message = "쿠폰 ID는 공백일 수 없습니다.")
    private String name;

    @NotNull(message = "쿠폰 수량은 공백일 수 없습니다.")
    private Long quantity;

    @NotNull(message = "쿠폰 타입은 공백일 수 없습니다.")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private CouponType couponType;
    private LocalDateTime issueStartDate;
    private LocalDateTime issueEndDate;
    private LocalDateTime useStartDate;
    private LocalDateTime useEndDate;

    /**
     * 목적에 따라 동적으로 사용합니다.
     */
    private Long value1;
    private Long value2;
    private Long value3;
}

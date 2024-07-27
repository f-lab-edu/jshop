package jshop.domain.coupon.dto;

import java.time.LocalDateTime;
import jshop.domain.coupon.entity.CouponType;
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

    private String id;
    private String name;
    private Long amount;
    private CouponType coupontType;
    private LocalDateTime issueStartDate;
    private LocalDateTime issueEndDate;
    private LocalDateTime useStartDate;
    private LocalDateTime useEndDate;
    private Long value1;
    private Long value2;
    private Long value3;
}

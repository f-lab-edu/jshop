package jshop.core.domain.coupon.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import jshop.core.domain.coupon.dto.CreateCouponRequest;
import jshop.common.exception.ErrorCode;
import jshop.common.exception.JshopException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

@Slf4j
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "fixed_rate_coupon")
public class FixedRateCoupon extends Coupon {

    @Column(name = "discount_rate")
    private double discountRate;

    @Column(name = "min_origin_price")
    private Long minOriginPrice;

    public static FixedRateCoupon of(CreateCouponRequest createCouponRequest) {
        LocalDateTime defaultStartDate = LocalDateTime.now();
        LocalDateTime defaultEndDate = LocalDateTime.of(9999, 12, 31, 23, 59);
        return FixedRateCoupon
            .builder()
            .id(createCouponRequest.getId())
            .name(createCouponRequest.getName())
            .totalQuantity(createCouponRequest.getQuantity())
            .remainingQuantity(createCouponRequest.getQuantity())
            .issueStartDate(createCouponRequest.getIssueStartDate() != null ?
                createCouponRequest.getIssueStartDate() : defaultStartDate)
            .issueEndDate(createCouponRequest.getIssueEndDate() != null ? createCouponRequest.getIssueEndDate() :
                defaultEndDate)
            .useStartDate(createCouponRequest.getUseStartDate() != null ? createCouponRequest.getUseStartDate() :
                defaultStartDate)
            .useEndDate(
                createCouponRequest.getUseEndDate() != null ? createCouponRequest.getUseEndDate() : defaultEndDate)
            .discountRate(createCouponRequest.getValue1() / 100)
            .minOriginPrice(createCouponRequest.getValue2())
            .build();
    }

    @Override
    public long discount(long originPrice) {
        checkCouponUsagePeriod();

        if (originPrice < minOriginPrice) {
            MDC.put("error_code", String.valueOf(ErrorCode.COUPON_MIN_PRICE_EXCEPTION.getCode()));
            log.error(ErrorCode.COUPON_MIN_PRICE_EXCEPTION.getLogMessage(), minOriginPrice, originPrice);
            MDC.clear();
            throw JshopException.of(ErrorCode.COUPON_MIN_PRICE_EXCEPTION);
        }

        return Math.round(originPrice * (1 - discountRate));
    }
}
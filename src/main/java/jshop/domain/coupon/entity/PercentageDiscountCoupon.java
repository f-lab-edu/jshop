package jshop.domain.coupon.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jshop.global.common.ErrorCode;
import jshop.global.exception.JshopException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class PercentageDiscountCoupon extends Coupon {

    @Column(name = "discount_rate")
    private double discountRate;

    @Column(name = "min_origin_price")
    private Long minOriginPrice;

    @Override
    public long discount(long originPrice) {
        checkCouponUsagePeriod();

        if (originPrice < minOriginPrice) {
            log.error(ErrorCode.COUPON_MIN_PRICE_EXCEPTION.getLogMessage(), minOriginPrice, originPrice);
            JshopException.of(ErrorCode.COUPON_MIN_PRICE_EXCEPTION);
        }

        return Math.round(originPrice * (1 - discountRate));
    }
}
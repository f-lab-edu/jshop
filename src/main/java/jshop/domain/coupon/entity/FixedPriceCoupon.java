package jshop.domain.coupon.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jshop.global.common.ErrorCode;
import jshop.global.exception.JshopException;
import lombok.AllArgsConstructor;
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
public class FixedPriceCoupon extends Coupon {

    @Column(name = "discount_price")
    private Long discountPrice;
    @Column(name = "min_origin_price")
    private Long minOriginPrice;

    @Override
    public long discount(long originPrice) {
        checkCouponUsagePeriod();

        if (originPrice < minOriginPrice) {
            log.error(ErrorCode.COUPON_MIN_PRICE_EXCEPTION.getLogMessage(), minOriginPrice, originPrice);
            JshopException.of(ErrorCode.COUPON_MIN_PRICE_EXCEPTION);
        }

        return originPrice - discountPrice;
    }

    @Override
    public String toString() {
        return "FixedDiscountCoupon{" +
            "discountPrice=" + discountPrice +
            ", minOriginPrice=" + minOriginPrice +
            ", totalQuantity=" + totalQuantity +
            ", remainingQuantity=" + remainingQuantity +
            ", issueStartDate=" + issueStartDate +
            ", issueEndDate=" + issueEndDate +
            ", useStartDate=" + useStartDate +
            ", useEndDate=" + useEndDate +
            '}';
    }
}

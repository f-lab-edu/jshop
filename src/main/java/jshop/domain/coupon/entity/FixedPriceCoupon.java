package jshop.domain.coupon.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import jshop.domain.coupon.dto.CreateCouponRequest;
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
@Table(name = "fixed_price_coupon")
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

    public static FixedPriceCoupon of(CreateCouponRequest createCouponRequest) {
        LocalDateTime defaultStartDate = LocalDateTime.now();
        LocalDateTime defaultEndDate = LocalDateTime.of(9999, 12, 31, 23, 59);

        return FixedPriceCoupon
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
            .discountPrice(createCouponRequest.getValue1())
            .minOriginPrice(createCouponRequest.getValue2())
            .build();
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

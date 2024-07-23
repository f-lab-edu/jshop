package jshop.domain.coupon.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.UUID;
import jshop.domain.user.entity.User;
import jshop.global.common.ErrorCode;
import jshop.global.entity.BaseEntity;
import jshop.global.exception.JshopException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

@Slf4j
@Entity
@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "coupon_id")
    private UUID id;

    private String name;

    @Column(name = "total_quantity")
    private Integer totalQuantity;

    @Column(name = "remaining_quantity")
    private Integer remainingQuantity;

    @Column(name = "created_at")
    @CreatedDate
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @LastModifiedDate
    private LocalDateTime updatedAt;

    abstract public long discount(long originPrice);

    @Column(name = "issue_start_date")
    @Default
    protected LocalDateTime issueStartDate = LocalDateTime.now();

    @Column(name = "issue_end_date")
    @Default
    protected LocalDateTime issueEndDate = LocalDateTime.of(9999, 12, 31, 23, 59);

    @Column(name = "use_start_date")
    @Default
    protected LocalDateTime useStartDate = LocalDateTime.now();

    @Column(name = "use_end_date")
    @Default
    protected LocalDateTime useEndDate = LocalDateTime.of(9999, 12, 31, 23, 59);
    ;

    protected boolean checkCouponUsagePeriod() {
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(useStartDate) || now.isAfter(useEndDate)) {
            log.error(ErrorCode.COUPON_USAGE_PERIOD_EXCEPTION.getLogMessage(), useStartDate, useEndDate);
            throw JshopException.of(ErrorCode.COUPON_USAGE_PERIOD_EXCEPTION);
        }

        return true;
    }

    public UserCoupon issueCoupon(User user) {
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(issueStartDate) || now.isAfter(issueEndDate)) {
            log.error(ErrorCode.COUPON_ISSUE_PERIOD_EXCEPTION.getLogMessage(), issueStartDate, issueEndDate);
            throw JshopException.of(ErrorCode.COUPON_ISSUE_PERIOD_EXCEPTION);
        }

        remainingQuantity--;
        return UserCoupon
            .builder()
            .user(user)
            .coupon(this)
            .build();
    }
}

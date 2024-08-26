package jshop.core.domain.coupon.repository;

import jakarta.persistence.LockModeType;
import java.util.Optional;
import jshop.core.domain.coupon.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

public interface CouponRepository extends JpaRepository<Coupon, String> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Coupon> findById(String couponId);
}

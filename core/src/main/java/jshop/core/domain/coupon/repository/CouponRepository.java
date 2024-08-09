package jshop.core.domain.coupon.repository;

import java.util.Optional;
import jshop.core.domain.coupon.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponRepository extends JpaRepository<Coupon, String> {

    Optional<Coupon> findById(String couponId);
}

package jshop.domain.coupon.repository;

import java.util.UUID;
import jshop.domain.coupon.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponRepository extends JpaRepository<Coupon, UUID> {
}

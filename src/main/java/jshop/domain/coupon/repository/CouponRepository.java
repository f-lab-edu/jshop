package jshop.domain.coupon.repository;

import jakarta.persistence.LockModeType;
import java.util.Optional;
import java.util.UUID;
import javax.swing.text.html.Option;
import jshop.domain.coupon.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

public interface CouponRepository extends JpaRepository<Coupon, String> {
    
    public Optional<Coupon> findById(String couponId);
}

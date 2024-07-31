package jshop.domain.coupon.repository;

import java.util.List;
import jshop.domain.coupon.entity.Coupon;
import jshop.domain.coupon.entity.UserCoupon;
import jshop.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserCouponRepository extends JpaRepository<UserCoupon, Long> {

    public List<UserCoupon> findByUser(User user);
}

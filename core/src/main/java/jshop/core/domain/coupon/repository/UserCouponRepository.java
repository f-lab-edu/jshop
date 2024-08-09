package jshop.core.domain.coupon.repository;

import java.util.List;
import jshop.core.domain.coupon.entity.UserCoupon;
import jshop.core.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserCouponRepository extends JpaRepository<UserCoupon, Long> {

    List<UserCoupon> findByUser(User user);
}

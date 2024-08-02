package jshop.domain.coupon.repository;

import static org.assertj.core.api.Assertions.assertThat;

import jshop.domain.coupon.entity.Coupon;
import jshop.domain.coupon.entity.FixedPriceCoupon;
import jshop.utils.config.BaseTestContainers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@DisplayName("[단위 테스트] CouponRepository")
@AutoConfigureTestDatabase(replace = Replace.NONE)
class CouponRepositoryTest extends BaseTestContainers {

    @Autowired
    private CouponRepository couponRepository;

    @Test
    @DisplayName("쿠폰 생성 id 검증")
    public void createId() {
        // given
        Coupon coupon = FixedPriceCoupon
            .builder().build();
        // when
        couponRepository.save(coupon);

        // then
        assertThat(coupon.getId()).isNotNull();
    }
}
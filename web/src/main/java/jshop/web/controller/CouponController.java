package jshop.web.controller;

import jakarta.validation.Valid;
import jshop.core.domain.coupon.dto.CreateCouponRequest;
import jshop.core.domain.coupon.service.CouponService;
import jshop.web.security.annotation.CurrentUserId;
import jshop.web.dto.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/coupon")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    @PostMapping
    @Secured("ROLE_ADMIN")
    public Response<String> createCoupon(@RequestBody @Valid CreateCouponRequest createCouponRequest) {
        String couponId = couponService.createCoupon(createCouponRequest);

        return Response
            .<String>builder().data(couponId).build();
    }

    @PostMapping("/{coupon_id}")
    @PreAuthorize("isAuthenticated()")
    public void issueCoupon(@PathVariable("coupon_id") String couponId, @CurrentUserId Long userId) {
        couponService.issueCoupon(couponId, userId);
    }
}

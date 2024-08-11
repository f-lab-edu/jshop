package jshop.web.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.stream.Stream;
import jshop.core.domain.coupon.dto.CreateCouponRequest;
import jshop.core.domain.coupon.entity.CouponType;
import jshop.core.domain.coupon.service.CouponService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest(controllers = {CouponController.class, GlobalExceptionHandler.class}, excludeAutoConfiguration =
    SecurityAutoConfiguration.class)
//@Import({ GlobalExceptionHandler.class})
@DisplayName("[단위 테스트] CouponController")
class CouponControllerTest {

    @MockBean
    private CouponService couponService;

    @Autowired
    MockMvc mockMvc;

    @Captor
    private ArgumentCaptor<CreateCouponRequest> createCouponRequestArgumentCaptor;

    private static Stream<Arguments> provideCouponTestArgs() {
        return Stream.of(
            Arguments.of("FIXED_RATE", CouponType.FIXED_RATE),
            Arguments.of("FIXED_PRICE", CouponType.FIXED_PRICE)
        );
    }

    @ParameterizedTest
    @MethodSource("provideCouponTestArgs")
    @DisplayName("Admin 유저는 쿠폰을 생성할 수 있다. (테스트에서는 Admin 권한 없음)")
    public void createCoupon_success(String couponTypeStr, CouponType couponType) throws Exception {
        // given
        String id = "821b4692-90c8-48fb-9ba5-ebeda1621cba";
        String name = "test_coupon";
        int quantity = 10000;
        int value1 = 1000;
        int value2 = 1000;
        String createCouponStr = """
            {
                "id" : "%s",
                "name" : "%s",
                "quantity" : %d,
                "couponType" : "%s",
                "value1" : %d,
                "value2" : %d
            }
            """;

        // when
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders
            .post("/api/coupon")
            .contentType(MediaType.APPLICATION_JSON)
            .content(String.format(createCouponStr, id, name, quantity, couponTypeStr, value1, value2)));

        // then
        perform.andExpect(status().isOk());
        verify(couponService, times(1)).createCoupon(createCouponRequestArgumentCaptor.capture());
        assertThat(createCouponRequestArgumentCaptor.getValue().getId()).isEqualTo(id);
        assertThat(createCouponRequestArgumentCaptor.getValue().getCouponType()).isEqualTo(couponType);
    }
}
package jshop.web.controller;

import static jshop.web.config.MockSecurityContextUtil.getSecurityContextMockUserId;
import static jshop.web.config.MockSecurityContextUtil.mockUserSecurityContext;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import jshop.common.utils.TimeUtils;
import jshop.core.domain.order.service.OrderService;
import jshop.web.security.service.AuthorizationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest(controllers = {OrderController.class, GlobalExceptionHandler.class}, excludeAutoConfiguration =
    SecurityAutoConfiguration.class)
@DisplayName("[단위 테스트] OrderController")
class OrderControllerTest {

    @MockBean
    OrderService orderService;

    @MockBean
    AuthorizationService authorizationService;

    @Autowired
    private MockMvc mockMvc;

    @Nested
    @DisplayName("주문 리스트 가져오기 검증 ")
    class GetOrderList {

        @Test
        @DisplayName("별다른 정보가 없다면, 기본값이 사용된다")
        public void default_getOrderList() throws Exception {
            // given
            LocalDateTime defaultLastDate = TimeUtils.timestampToLocalDateTime(3000000000000L);

            // when
            ResultActions perform = mockMvc.perform(
                MockMvcRequestBuilders
                    .get("/api/orders")
                    .contentType(MediaType.APPLICATION_JSON)
                    .with(mockUserSecurityContext()));

            // then
            verify(orderService, times(1)).getOrderList(10, defaultLastDate, getSecurityContextMockUserId());
            perform.andExpect(status().isOk());
        }

        @ParameterizedTest
        @ValueSource(ints = {5, 0, 30})
        @DisplayName("페이지 크기가 제공되면 해당 페이지 크기를 사용한다.")
        public void page_getOrderList(int page) throws Exception {
            // given
            LocalDateTime defaultLastDate = TimeUtils.timestampToLocalDateTime(3000000000000L);

            // when
            ResultActions perform = mockMvc.perform(
                MockMvcRequestBuilders
                    .get("/api/orders?size={}", page)
                    .contentType(MediaType.APPLICATION_JSON)
                    .with(mockUserSecurityContext()));

            // then
            if (page == 5) {
                verify(orderService, times(1)).getOrderList(page, defaultLastDate, getSecurityContextMockUserId());
                perform.andExpect(status().isOk());
            } else {
                perform.andExpect(status().isBadRequest());
            }

        }
    }

}
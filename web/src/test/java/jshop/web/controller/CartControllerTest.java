package jshop.web.controller;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jshop.core.domain.cart.dto.AddCartRequest;
import jshop.core.domain.cart.service.CartService;
import jshop.common.exception.ErrorCode;
import jshop.web.config.MockSecurityContextUtil;
import jshop.web.security.service.AuthorizationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
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

@WebMvcTest(controllers = {CartController.class, GlobalExceptionHandler.class}, excludeAutoConfiguration =
    SecurityAutoConfiguration.class)
@DisplayName("[단위 테스트] CartController")
class CartControllerTest {

    @MockBean
    private CartService cartService;

    @MockBean
    private AuthorizationService authorizationService;

    @Autowired
    private MockMvc mockMvc;

    @Captor
    private ArgumentCaptor<AddCartRequest> addCartRequestArgumentCaptor;

    @Captor
    private ArgumentCaptor<Long> userIdCaptor;

    @Nested
    @DisplayName("장바구니 가져오기 검증")
    class GetCartList {

        @Test
        @DisplayName("로그인한 유저는 페이지 정보를 통해 자신의 장바구니의 상품을 가져올 수 있다.")
        public void getCart_success() throws Exception {
            // when
            mockMvc.perform(MockMvcRequestBuilders.get("/api/cart?page=1&size=10").with(
                MockSecurityContextUtil.mockUserSecurityContext()));

            // then
            verify(cartService, times(1)).getCartPage(MockSecurityContextUtil.getSecurityContextMockUserId(), 1, 10);
        }

        @Test
        @DisplayName("페이지 정보를 넘기지 않는다면 기본 값이 사용된다")
        public void getCart_default() throws Exception {
            // when
            mockMvc.perform(MockMvcRequestBuilders.get("/api/cart").with(MockSecurityContextUtil.mockUserSecurityContext()));

            // then
            verify(cartService, times(1)).getCartPage(MockSecurityContextUtil.getSecurityContextMockUserId(), 0, 30);
        }

        @Test
        @DisplayName("페이지 정보가 잘못되었다면 ILLEGAL_PAGE_REQUEST를 날린다")
        public void addCart_success() throws Exception {
            // when
            ResultActions perform = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/cart?page=1&size=-10").with(
                    MockSecurityContextUtil.mockUserSecurityContext()));

            // then
            perform
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(ErrorCode.ILLEGAL_PAGE_REQUEST.getCode()));
        }
    }

    @Nested
    @DisplayName("장바구니 추가 검증")
    class AddCart {

        @Test
        @DisplayName("로그인한 일반 유저는 장바구니에 상품을 추가할 수 있다")
        public void addCart_success() throws Exception {
            // given
            String addCartRequestStr = """
                { "productDetailId" : 1, "quantity" : 1}
                """;

            // when
            ResultActions perform = mockMvc.perform(MockMvcRequestBuilders
                .post("/api/cart")
                .with(MockSecurityContextUtil.mockUserSecurityContext())
                .contentType(MediaType.APPLICATION_JSON)
                .content(addCartRequestStr));

            // then
            perform.andExpect(status().isOk());
            verify(cartService, times(1)).addCart(addCartRequestArgumentCaptor.capture(), userIdCaptor.capture());

            assertThat(userIdCaptor.getValue()).isEqualTo(MockSecurityContextUtil.getSecurityContextMockUserId());
            assertThat(addCartRequestArgumentCaptor.getValue().getQuantity()).isEqualTo(1);
            assertThat(addCartRequestArgumentCaptor.getValue().getProductDetailId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("인증이 안된 유저는 장바구니에 상품을 추가할 수 없다.")
        public void addCart_noAuth() throws Exception {
            // given
            String addCartRequestStr = """
                { "productDetailId" : 1, "quantity" : 1}
                """;

            // when
            ResultActions perform = mockMvc.perform(MockMvcRequestBuilders
                .post("/api/cart")
                .contentType(MediaType.APPLICATION_JSON)
                .content(addCartRequestStr));

            // then
            perform.andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("장바구니에 추가할 상품 정보 (바디) 가 없다면 INVALID_REQUEST_BODY")
        public void addCart_noBody() throws Exception {

            // when
            ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.post("/api/cart"));

            // then
            perform
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(ErrorCode.INVALID_REQUEST_BODY.getCode()));
        }
    }
}
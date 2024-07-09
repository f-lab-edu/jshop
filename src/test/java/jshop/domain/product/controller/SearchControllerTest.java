package jshop.domain.product.controller;

import static jshop.utils.MockSecurityContextUtil.mockUserSecurityContext;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jshop.domain.address.dto.CreateAddressRequest;
import jshop.domain.product.service.SearchService;
import jshop.global.controller.GlobalExceptionHandler;
import jshop.utils.config.TestSecurityConfig;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest(SearchController.class)
@Import({TestSecurityConfig.class, GlobalExceptionHandler.class})
@DisplayName("[단위 테스트] SearchController")
public class SearchControllerTest {

    private static final JSONObject createAddressRequestJson = new JSONObject();

    @MockBean
    private SearchService searchService;

    @Autowired
    private MockMvc mockMvc;

    @Captor
    private ArgumentCaptor<CreateAddressRequest> createAddressRequestCaptor;

    @Captor
    private ArgumentCaptor<Long> userIdCaptor;

    @Nested
    class SearchProductDetail {

        @Test
        @DisplayName("상품 검색시 cursor, size, query 정보가 필요하다")
        public void searchProduct_success() throws Exception {
            // given
            ResultActions perform = mockMvc.perform(MockMvcRequestBuilders
                .get("/api/search?cursor=123&size=20&query=아이폰")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createAddressRequestJson.toString()));
            // when

            verify(searchService, times(1)).searchProductDetail(123L, "아이폰", 20);
            // then
        }

        @Test
        @DisplayName("상품 검색시 cursor, size 정보가 없다면 기본 값이 사용된다")
        public void searchProduct_defaultValue() throws Exception {
            // when
            ResultActions perform = mockMvc.perform(MockMvcRequestBuilders
                .get("/api/search?query=아이폰")
                .with(mockUserSecurityContext())
                .contentType(MediaType.APPLICATION_JSON)
                .content(createAddressRequestJson.toString()));

            // then
            verify(searchService, times(1)).searchProductDetail(Long.MAX_VALUE, "아이폰", 30);
        }

        @Test
        @DisplayName("상품 검색시 query 정보가 없다면 NO_SEARCH_QUERY 발생")
        public void searchProduct_defaultQuery() throws Exception {
            // when
            ResultActions perform = mockMvc.perform(MockMvcRequestBuilders
                .get("/api/search")
                .with(mockUserSecurityContext())
                .contentType(MediaType.APPLICATION_JSON)
                .content(createAddressRequestJson.toString()));

            // then
            perform.andExpect(status().isBadRequest());
        }
    }
}
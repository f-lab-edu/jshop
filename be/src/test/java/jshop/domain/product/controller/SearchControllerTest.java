package jshop.domain.product.controller;

import static jshop.utils.SecurityContextUtil.userSecurityContext;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Optional;
import jshop.domain.address.dto.CreateAddressRequest;
import jshop.domain.product.service.ProductService;
import jshop.global.controller.GlobalExceptionHandler;
import jshop.utils.TestSecurityConfig;
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
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest(SearchController.class)
@Import({TestSecurityConfig.class, GlobalExceptionHandler.class})
@DisplayName("SearchController Serviee 테스트")
public class SearchControllerTest {

    private static final JSONObject createAddressRequestJson = new JSONObject();

    @MockBean
    private ProductService productService;

    @MockBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

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
                .with(userSecurityContext())
                .contentType(MediaType.APPLICATION_JSON)
                .content(createAddressRequestJson.toString()));
            // when

            verify(productService, times(1)).searchProductDetail(123L, Optional.of("아이폰"), 20);
            // then
        }

        @Test
        @DisplayName("상품 검색시 cursor, size 정보가 없다면 기본 값이 사용된다")
        public void searchProduct_defaultValue() throws Exception {
            // given
            ResultActions perform = mockMvc.perform(MockMvcRequestBuilders
                .get("/api/search?query=아이폰")
                .with(userSecurityContext())
                .contentType(MediaType.APPLICATION_JSON)
                .content(createAddressRequestJson.toString()));
            // when

            verify(productService, times(1)).searchProductDetail(Long.MAX_VALUE, Optional.of("아이폰"), 10);
            // then
        }

        @Test
        @DisplayName("상품 검색시 query 정보가 없다면 null 값이 사용된다")
        public void searchProduct_defaultQuery() throws Exception {
            // given
            ResultActions perform = mockMvc.perform(MockMvcRequestBuilders
                .get("/api/search")
                .with(userSecurityContext())
                .contentType(MediaType.APPLICATION_JSON)
                .content(createAddressRequestJson.toString()));
            // when

            verify(productService, times(1)).searchProductDetail(Long.MAX_VALUE, Optional.empty(), 10);
            // then
        }
    }
}
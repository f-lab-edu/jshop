package jshop.domain.product.controller;

import static jshop.utils.MockSecurityContextUtil.mockUserSecurityContext;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jshop.domain.product.dto.CreateProductDetailRequest;
import jshop.domain.product.service.ProductService;
import jshop.global.common.ErrorCode;
import jshop.global.controller.GlobalExceptionHandler;
import jshop.utils.TestSecurityConfig;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


@WebMvcTest(ProductController.class)
@Import({TestSecurityConfig.class, GlobalExceptionHandler.class})
@DisplayName("ProductController Controller 테스트")
class ProductControllerTest {

    @MockBean
    private ProductService productService;

    @MockBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Autowired
    private MockMvc mockMvc;

    @Nested
    @DisplayName("상품 생성 검증")
    class CreateProduct {

        @Test
        @DisplayName("상품 추가시 상품생성 서비스로 요청을 넘겨준다")
        public void createProduct() throws Exception {
            // given
            JSONObject requestBody = new JSONObject();
            requestBody.put("name", "test");
            requestBody.put("categoryId", 1L);
            requestBody.put("manufacturer", "apple");
            requestBody.put("description", "asdf");

            JSONObject attributes = new JSONObject();
            attributes.put("attr1", new JSONArray(List.of("1", "2")));
            requestBody.put("attributes", attributes);

            // when
            ResultActions perform = mockMvc.perform(MockMvcRequestBuilders
                .post("/api/products")
                .with(mockUserSecurityContext())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody.toString()));

            // then
            perform.andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("자신 상품 가져오기 검증")
    class GetOwnProducts {

        @Test
        @DisplayName("상품 페이지와 페이지 크기를 서비스로 넘겨준다.")
        public void getOwnProducts() throws Exception {
            // when
            ResultActions perform = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/products?page=0&size=10").with(mockUserSecurityContext()));

            // then
            perform.andExpect(status().isOk());
            verify(productService, times(1)).getOwnProducts(1L, 0, 10);
        }

        @Test
        @DisplayName("상품 페이지와 페이지 크기가 없다면 기본 값을 넘겨준다.")
        public void getOwnProducts_defaultValue() throws Exception {
            // when
            ResultActions perform = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/products").with(mockUserSecurityContext()));

            // then
            perform.andExpect(status().isOk());
            verify(productService, times(1)).getOwnProducts(1L, 0, 10);
        }
    }

    @Nested
    @DisplayName("상세 상품 추가하기 검증")
    class CreateProductDetail {

        @Test
        @DisplayName("사용자는 자신의 상품에 상세 상품을 추가할 수 있다.")
        public void createProductDetail_success() throws Exception {
            // given
            JSONObject requestBody = new JSONObject();

            requestBody.put("price", 1000L);
            JSONObject attribute = new JSONObject();
            attribute.put("color", "red");
            attribute.put("size", "95");
            requestBody.put("attribute", attribute);

            Map<String, String> attributeMap = new HashMap<>();
            attributeMap.put("color", "red");
            attributeMap.put("size", "95");

            CreateProductDetailRequest createProductDetailRequest = CreateProductDetailRequest
                .builder().attribute(attributeMap).price(1000L).build();

            // when
            ResultActions perform = mockMvc.perform(MockMvcRequestBuilders
                .post("/api/products/1/details")
                .with(mockUserSecurityContext())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody.toString()));
            // then

            verify(productService, times(1)).createProductDetail(createProductDetailRequest, 1L);
            perform.andExpect(status().isOk());
        }

        @Test
        @DisplayName("상세 상품 추가시 바디가 없다면 INVALID_REQUEST_BODY 예외가 터진다.")
        public void createProductDetail_noBody() throws Exception {

            // when
            ResultActions perform = mockMvc.perform(MockMvcRequestBuilders
                .post("/api/products/1/details")
                .with(mockUserSecurityContext())
                .contentType(MediaType.APPLICATION_JSON));

            // then
            perform
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(ErrorCode.INVALID_REQUEST_BODY.getCode()));
        }

        @Test
        @DisplayName("상세상품 추가시 바디 타입이 잘못되면 BAD_REQUEST가 터진다.")
        public void createProductDetail_invalidBody() throws Exception {
            // given
            JSONObject requestBody = new JSONObject();

            requestBody.put("id", 1000L);
            JSONObject attribute = new JSONObject();
            attribute.put("color", "red");
            attribute.put("size", "95");
            requestBody.put("attribute", attribute);

            // when
            ResultActions perform = mockMvc.perform(MockMvcRequestBuilders
                .post("/api/products/1/details")
                .with(mockUserSecurityContext())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody.toString()));

            // then
            perform
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(ErrorCode.BAD_REQUEST.getCode()));
        }
    }
}
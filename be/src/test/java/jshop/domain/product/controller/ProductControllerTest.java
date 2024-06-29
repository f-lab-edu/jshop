package jshop.domain.product.controller;

import static jshop.utils.MockSecurityContextUtil.mockUserSecurityContext;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jshop.domain.product.dto.CreateProductDetailRequest;
import jshop.domain.product.dto.CreateProductRequest;
import jshop.domain.product.dto.UpdateProductDetailRequest;
import jshop.domain.product.dto.UpdateProductDetailStockRequest;
import jshop.domain.product.service.ProductService;
import jshop.global.common.ErrorCode;
import jshop.global.controller.GlobalExceptionHandler;
import jshop.utils.config.TestSecurityConfig;
import jshop.utils.config.TestSecurityConfigWithoutMethodSecurity;
import org.json.JSONArray;
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


@WebMvcTest(ProductController.class)
@Import({TestSecurityConfigWithoutMethodSecurity.class, GlobalExceptionHandler.class})
@DisplayName("[단위 테스트] ProductController")
class ProductControllerTest {

    @MockBean
    private ProductService productService;

    @MockBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Autowired
    private MockMvc mockMvc;

    @Captor
    private ArgumentCaptor<CreateProductRequest> createProductRequestArgumentCaptor;

    @Captor
    private ArgumentCaptor<Long> userIdArgumentCaptor;

    @Nested
    @DisplayName("상품 생성 검증")
    class CreateProduct {

        @Test
        @DisplayName("상품 추가시 상품생성 서비스로 요청과 현재 유저 ID를 넘겨준다")
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
            Map<String, List<String>> attrubutesMap = new HashMap<>();
            attrubutesMap.put("attr1", List.of("1", "2"));

            // when
            ResultActions perform = mockMvc.perform(MockMvcRequestBuilders
                .post("/api/products")
                .with(mockUserSecurityContext())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody.toString()));

            // then
            perform.andExpect(status().isOk());
            verify(productService, times(1)).createProduct(createProductRequestArgumentCaptor.capture(),
                userIdArgumentCaptor.capture());
            CreateProductRequest createProductRequest = createProductRequestArgumentCaptor.getValue();

            assertThat(userIdArgumentCaptor.getValue()).isEqualTo(1L);
            assertAll("상품 생성 요청 검증", () -> assertThat(createProductRequest.getName()).isEqualTo("test"),
                () -> assertThat(createProductRequest.getAttributes()).isEqualTo(attrubutesMap),
                () -> assertThat(createProductRequest.getName()).isEqualTo("test"),
                () -> assertThat(createProductRequest.getCategoryId()).isEqualTo(1L),
                () -> assertThat(createProductRequest.getManufacturer()).isEqualTo("apple"),
                () -> assertThat(createProductRequest.getDescription()).isEqualTo("asdf"));
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

    @Nested
    @DisplayName("상세 상품 수정 검증")
    class UpdateProductDetail {

        @Test
        @DisplayName("상세 상품 수정시, 상품의 id, 상세 상품의 id, 수정할 상세 상품 정보가 필요함")
        public void updateProductDetail_success() throws Exception {
            // given
            String updateProductDetailRequestStr = """
                {"price" : 1000}
                """;
            UpdateProductDetailRequest updateProductDetailRequest = UpdateProductDetailRequest
                .builder().price(1000L).build();

            // when
            ResultActions perform = mockMvc.perform(MockMvcRequestBuilders
                .put("/api/products/1/details/1")
                .with(mockUserSecurityContext())
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateProductDetailRequestStr));
            // then

            verify(productService, times(1)).updateProductDetail(1L, updateProductDetailRequest);
        }

        @Test
        @DisplayName("상세 상품 수정시 수정할 상세 상품 정보가 없다면 INVALID_REQUEST_BODY")
        public void updateProductDetail_noBody() throws Exception {
            // when
            ResultActions perform = mockMvc.perform(
                MockMvcRequestBuilders.put("/api/products/1/details/1").with(mockUserSecurityContext()));
            // then

            perform
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(ErrorCode.INVALID_REQUEST_BODY.getCode()));
        }
    }

    @Nested
    @DisplayName("상세 상품 재고 변경 검증")
    class UpdateProductDetailStock {

        @Test
        @DisplayName("상세 상품 재고 변경시, 상품id, 상세 상품 id, 변경 수량을 줘야 한다")
        public void updateProductDetailStock_success() throws Exception {
            // given
            String updateProductDetailStockRequestStr = """
                { "quantity" : 10}
                """;

            // when
            ResultActions perform = mockMvc.perform(MockMvcRequestBuilders
                .patch("/api/products/1/details/1")
                .with(mockUserSecurityContext())
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateProductDetailStockRequestStr));
            // then

            verify(productService, times(1)).updateProductDetailStock(1L, 10);
        }

        @Test
        @DisplayName("상세 상품 재고 변경시, 변경 수량 정보가 없다면 INVALID_REQUEST_BODY")
        public void updateProductDetailStock_noBody() throws Exception {
            // given

            // when
            ResultActions perform = mockMvc.perform(
                MockMvcRequestBuilders.patch("/api/products/1/details/1").with(mockUserSecurityContext()));

            // then
            perform
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(ErrorCode.INVALID_REQUEST_BODY.getCode()));
        }
    }

    @Nested
    @DisplayName("상세 상품 삭제 겸증")
    class DeleteProductDetail {

        @Test
        @DisplayName("상세 상품 삭제시, 상품 id, 상세 상품 id가 전달되어야 함")
        public void deleteProductDetail_success() throws Exception {
            // when
            mockMvc.perform(MockMvcRequestBuilders.delete("/api/products/1/details/1").with(mockUserSecurityContext()));
            // then
            verify(productService, times(1)).deleteProductDetail(1L);
        }
    }
}
package jshop.domain.product.controller;

import static jshop.utils.SecurityContextUtil.userSecurityContext;
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
class ProductControllerTest {

    @MockBean
    private ProductService productService;

    @MockBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void 상품추가() throws Exception {
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
            .with(userSecurityContext())
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody.toString()));

        // then
        perform.andExpect(status().isOk());
    }

    @Test
    public void 유저상품가져오기() throws Exception {
        // given

        // when
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders
            .get("/api/products?page=0&size=10")
            .with(userSecurityContext()));

        // then
        perform.andExpect(status().isOk());
        verify(productService, times(1)).getOwnProducts(1L, 0, 10);
    }

    @Test
    public void 유저상품가져오기_페이지번호없을때() throws Exception {
        // given

        // when
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders
            .get("/api/products")
            .with(userSecurityContext()));

        // then
        perform.andExpect(status().isOk());
        verify(productService, times(1)).getOwnProducts(1L, 0, 10);
    }

    @Test
    public void 상세상품추가() throws Exception {
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
            .with(userSecurityContext())
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody.toString()));
        // then

        verify(productService, times(1)).createProductDetail(createProductDetailRequest, 1L, 1L);
        perform.andExpect(status().isOk());
    }

    @Test
    public void 상세상품추가_잘못된요청_바디없음() throws Exception {
        // given

        // when
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders
            .post("/api/products/1/details")
            .with(userSecurityContext())
            .contentType(MediaType.APPLICATION_JSON));

        // then
        perform
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value(ErrorCode.INVALID_REQUEST_BODY.toString()));
    }

    @Test
    public void 상세상품추가_잘못된요청_잘못된바디() throws Exception {
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
            .with(userSecurityContext())
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody.toString()));

        // then
        perform
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value(ErrorCode.BAD_REQUEST.toString()));
    }
}
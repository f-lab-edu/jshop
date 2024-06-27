package jshop.domain.category.controller;

import static jshop.utils.MockSecurityContextUtil.mockUserSecurityContext;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jshop.domain.category.dto.CreateCategoryRequest;
import jshop.domain.category.service.CategoryService;
import jshop.global.common.ErrorCode;
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


@WebMvcTest(CategoryController.class)
@Import({TestSecurityConfig.class, GlobalExceptionHandler.class})
@DisplayName("CategoryController Controller 테스트")
class CategoryControllerTest {

    @MockBean
    private CategoryService categoryService;

    @MockBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Autowired
    private MockMvc mockMvc;

    @Captor
    private ArgumentCaptor<CreateCategoryRequest> createCategoryRequestArgumentCaptor;

    @Captor
    private ArgumentCaptor<String> userRoleArgumentCaptor;

    @Nested
    @DisplayName("카테고리 생성 테스트")
    class CreateCategory {

        @Test
        @DisplayName("카테고리 생성 서비스로 요청과 권한을 넘겨줌")
        public void createCategory_success() throws Exception {
            // given
            JSONObject createCategoryRequestJson = new JSONObject();
            createCategoryRequestJson.put("name", "전자기기");

            // when
            ResultActions perform = mockMvc.perform(MockMvcRequestBuilders
                .post("/api/categories")
                .with(mockUserSecurityContext())
                .contentType(MediaType.APPLICATION_JSON)
                .content(createCategoryRequestJson.toString()));

            // then
            perform.andExpect(status().isOk());
            verify(categoryService, times(1)).createCategory(createCategoryRequestArgumentCaptor.capture(),
                userRoleArgumentCaptor.capture());
            assertThat(userRoleArgumentCaptor.getValue()).isEqualTo("ROLE_USER");
            assertThat(createCategoryRequestArgumentCaptor.getValue().getName()).isEqualTo("전자기기");
        }

        @Test
        @DisplayName("권한이 없다면 401을 내려줌")
        public void createCategory_401() throws Exception {
            // given
            JSONObject createCategoryRequestJson = new JSONObject();
            createCategoryRequestJson.put("name", "전자기기");

            // when

            ResultActions perform = mockMvc.perform(MockMvcRequestBuilders
                .post("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createCategoryRequestJson.toString()));

            // then
            perform
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorCode").value(ErrorCode.JWT_USER_NOT_FOUND.getCode()));
        }
    }

}
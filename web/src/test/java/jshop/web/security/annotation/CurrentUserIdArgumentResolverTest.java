package jshop.web.security.annotation;

import static jshop.web.config.MockSecurityContextUtil.mockUserSecurityContext;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jshop.common.exception.ErrorCode;
import jshop.web.config.TestSecurityConfig;
import jshop.web.controller.GlobalExceptionHandler;
import jshop.web.helpers.MockController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest(MockController.class)
@Import({TestSecurityConfig.class, GlobalExceptionHandler.class})
@DisplayName("[단위 테스트] @CurrentUserId")
class CurrentUserIdArgumentResolverTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Test
    @DisplayName("JWT에 principal이 있을때 컨트롤러 파라미터에서 userId 제공")
    public void getUserId() throws Exception {
        // when
        ResultActions perform = mockMvc.perform(
            MockMvcRequestBuilders.get("/api/userid").with(mockUserSecurityContext()));

        // then
        perform.andExpect(status().isOk()).andExpect(jsonPath("$.userid").value(1L));
    }

    @Test
    @DisplayName("JWT에 principal이 없다면 JWT_USER_NOT_FOUND 예외")
    public void getUserId_noPrincipal() throws Exception {
        // when
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.get("/api/userid"));

        // then
        perform
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.errorCode").value(ErrorCode.JWT_USER_NOT_FOUND.getCode()));
    }
}
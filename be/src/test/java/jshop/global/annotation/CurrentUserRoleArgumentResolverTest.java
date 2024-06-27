package jshop.global.annotation;

import static jshop.utils.SecurityContextUtil.userSecurityContext;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jshop.global.common.ErrorCode;
import jshop.global.controller.GlobalExceptionHandler;
import jshop.utils.MockController;
import jshop.utils.TestSecurityConfig;
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
@DisplayName("@CurrentUserRole Annotation 테스트")
class CurrentUserRoleArgumentResolverTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Test
    @DisplayName("JWT에 principal이 있을때 컨트롤러 파라미터에서 userRole 제공")
    public void getUserId() throws Exception {
        // when
        ResultActions perform = mockMvc.perform(
            MockMvcRequestBuilders.get("/api/userrole").with(userSecurityContext()));

        // then
        perform.andExpect(status().isOk()).andExpect(jsonPath("$.userRole").value("ROLE_USER"));
    }

    @Test
    @DisplayName("JWT에 principal이 없다면 JWT_USER_NOT_FOUND 예외")
    public void getUserId_noPrincipal() throws Exception {
        // when
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.get("/api/userrole"));

        // then
        perform
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.errorCode").value(ErrorCode.JWT_USER_NOT_FOUND.getCode()));
    }
}